import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class GenericEndpointTest {

    private static final String ENDPOINT_FILES_PROPERTY = "endpoint.test.files";
    private static final String BASE_URL_PROPERTY = "endpoint.test.base-url";
    private static final String JDBC_URL_PROPERTY = "endpoint.test.jdbc-url";
    private static final String JDBC_USERNAME_PROPERTY = "endpoint.test.jdbc-username";
    private static final String JDBC_PASSWORD_PROPERTY = "endpoint.test.jdbc-password";
    private static final String CONNECT_TIMEOUT_PROPERTY = "endpoint.test.connect-timeout-seconds";
    private static final String REQUEST_TIMEOUT_PROPERTY = "endpoint.test.request-timeout-seconds";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(longProperty(CONNECT_TIMEOUT_PROPERTY, 5)))
            .build();
    private final String baseUrl = System.getProperty(BASE_URL_PROPERTY, "").trim();

    @TestFactory
    Stream<DynamicTest> endpointTests() {
        assumeTrue(!baseUrl.isBlank(),
                "Set -D" + BASE_URL_PROPERTY + "=http://localhost:8080 and start the server before running GenericEndpointTest");

        return endpointFiles().stream().flatMap(this::buildSuiteTests);
    }

    private List<String> endpointFiles() {
        String configuredFiles = System.getProperty(
                ENDPOINT_FILES_PROPERTY,
                "classpath:endpoints/users.endpoints.json"
        );

        return Arrays.stream(configuredFiles.split(","))
                .map(String::trim)
                .filter(path -> !path.isBlank())
                .toList();
    }

    private Stream<DynamicTest> buildSuiteTests(String suitePath) {
        EndpointSuite suite = readJsonResource(suitePath, EndpointSuite.class);
        if (suite.tests == null || suite.tests.isEmpty()) {
            return Stream.empty();
        }

        return suite.tests.stream().map(endpoint ->
                DynamicTest.dynamicTest(testName(suitePath, endpoint), () -> runEndpointTest(suite, endpoint))
        );
    }

    private void runEndpointTest(EndpointSuite suite, EndpointCase endpoint) throws Exception {
        runSqlScripts(suite.beforeEachSql);
        runSqlScripts(endpoint.beforeEachSql);

        HttpRequest request = buildRequest(endpoint);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(endpoint.expectedStatus, response.statusCode(),
                () -> endpoint.name + " expected status " + endpoint.expectedStatus + " but got "
                        + response.statusCode() + " with body: " + response.body());
        assertHeaders(endpoint, response);
        assertBody(endpoint, response);
    }

    private HttpRequest buildRequest(EndpointCase endpoint) throws Exception {
        String method = endpoint.method.toUpperCase(Locale.ROOT);
        URI uri = buildUri(endpoint);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(longProperty(REQUEST_TIMEOUT_PROPERTY, 30)))
                .header("Accept", "application/json");

        endpoint.headers.forEach(builder::header);

        if (endpoint.requestBody != null) {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(endpoint.requestBody)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return builder.build();
    }

    private URI buildUri(EndpointCase endpoint) {
        String rawUrl = endpoint.url == null ? "" : endpoint.url.trim();
        if (rawUrl.isBlank()) {
            throw new IllegalArgumentException("Endpoint url is required for test '" + endpoint.name + "'");
        }

        String resolved = rawUrl.startsWith("http://") || rawUrl.startsWith("https://")
                ? rawUrl
                : joinUrl(baseUrl, rawUrl);

        if (endpoint.queryParams.isEmpty()) {
            return URI.create(resolved);
        }

        StringBuilder query = new StringBuilder();
        endpoint.queryParams.forEach((key, value) -> {
            if (query.length() > 0) {
                query.append('&');
            }
            query.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
            query.append('=');
            query.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        });

        return URI.create(resolved + (resolved.contains("?") ? "&" : "?") + query);
    }

    private String joinUrl(String baseUrl, String path) {
        boolean baseEndsWithSlash = baseUrl.endsWith("/");
        boolean pathStartsWithSlash = path.startsWith("/");

        if (baseEndsWithSlash && pathStartsWithSlash) {
            return baseUrl + path.substring(1);
        }
        if (!baseEndsWithSlash && !pathStartsWithSlash) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private void assertHeaders(EndpointCase endpoint, HttpResponse<String> response) {
        endpoint.expectedHeaders.forEach((name, expectedValue) ->
                assertEquals(expectedValue, response.headers().firstValue(name).orElse(null),
                        endpoint.name + " header " + name)
        );
    }

    private void assertBody(EndpointCase endpoint, HttpResponse<String> response) throws Exception {
        if (endpoint.expectedBody == null) {
            return;
        }

        String responseBody = response.body() == null ? "" : response.body();
        JsonNode expectedJson = objectMapper.valueToTree(endpoint.expectedBody);

        if (responseBody.isBlank() && expectedJson.isObject() && expectedJson.isEmpty()) {
            return;
        }

        JsonNode actualJson = responseBody.isBlank()
                ? MissingNode.getInstance()
                : objectMapper.readTree(responseBody);

        assertEquals(expectedJson, actualJson, endpoint.name + " response body");
    }

    private void runSqlScripts(List<String> scriptPaths) {
        if (scriptPaths == null || scriptPaths.isEmpty()) {
            return;
        }

        DataSource dataSource = buildDataSource();
        if (dataSource == null) {
            throw new IllegalStateException(
                    "Set -D" + JDBC_URL_PROPERTY + "=jdbc:... when beforeEachSql is configured."
            );
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        scriptPaths.stream()
                .map(resourceLoader::getResource)
                .forEach(populator::addScript);
        populator.execute(dataSource);
    }

    private DataSource buildDataSource() {
        String jdbcUrl = System.getProperty(JDBC_URL_PROPERTY, "").trim();
        if (jdbcUrl.isBlank()) {
            return null;
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(System.getProperty(JDBC_USERNAME_PROPERTY, ""));
        dataSource.setPassword(System.getProperty(JDBC_PASSWORD_PROPERTY, ""));
        return dataSource;
    }

    private <T> T readJsonResource(String path, Class<T> type) {
        Resource resource = resourceLoader.getResource(path);

        try (InputStream inputStream = resource.getInputStream()) {
            assertNotNull(inputStream, "Could not open endpoint file: " + path);
            return objectMapper.readValue(inputStream, type);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read endpoint file: " + path, exception);
        }
    }

    private long longProperty(String propertyName, long defaultValue) {
        String value = System.getProperty(propertyName);
        return value == null || value.isBlank() ? defaultValue : Long.parseLong(value);
    }

    private String testName(String suitePath, EndpointCase endpoint) {
        String label = endpoint.name == null || endpoint.name.isBlank()
                ? endpoint.method + " " + endpoint.url
                : endpoint.name;
        return suitePath + " :: " + label;
    }

    static class EndpointSuite {
        public List<String> beforeEachSql = List.of();
        public List<EndpointCase> tests = List.of();
    }

    static class EndpointCase {
        public String name;
        public String method;
        public String url;
        public Map<String, String> headers = Map.of();
        public Map<String, String> queryParams = Map.of();
        public Object requestBody;
        public int expectedStatus;
        public Object expectedBody;
        public Map<String, String> expectedHeaders = Map.of();
        public List<String> beforeEachSql = List.of();
    }
}
