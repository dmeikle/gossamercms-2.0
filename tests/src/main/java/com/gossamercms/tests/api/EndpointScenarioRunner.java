package com.gossamercms.tests.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public class EndpointScenarioRunner {

    private final ObjectMapper objectMapper;

    public EndpointScenarioRunner() {
        this(defaultObjectMapper());
    }

    public EndpointScenarioRunner(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static ObjectMapper defaultObjectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    public static MockMvc buildMockMvc(Object controller, Object... controllerAdvice) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(controllerAdvice)
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .build();
    }

    public ControllerScenarioFile loadScenarioFile(String resourcePath) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Scenario resource not found: " + resourcePath);
            }

            ControllerScenarioFile scenarioFile =
                    objectMapper.readValue(inputStream, ControllerScenarioFile.class);

            if (scenarioFile.scenarios() == null || scenarioFile.scenarios().isEmpty()) {
                throw new IllegalArgumentException("Scenario resource contains no scenarios: " + resourcePath);
            }

            return scenarioFile;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to read scenario resource: " + resourcePath, ex);
        }
    }

    public void runScenario(MockMvc mockMvc, EndpointScenario scenario) throws Exception {
        Objects.requireNonNull(scenario, "scenario");
        Objects.requireNonNull(scenario.expected(), "scenario.expected");

        MvcResult result = mockMvc.perform(buildRequest(scenario)).andReturn();
        int actualStatus = result.getResponse().getStatus();

        if (actualStatus != scenario.expected().status()) {
            throw new AssertionError(
                    "Scenario '%s' expected status %d but received %d. Response body: %s".formatted(
                            scenario.name(),
                            scenario.expected().status(),
                            actualStatus,
                            result.getResponse().getContentAsString()
                    )
            );
        }

        if (scenario.expected().body() != null) {
            String responseBody = result.getResponse().getContentAsString();
            if (responseBody == null || responseBody.isBlank()) {
                throw new AssertionError("Scenario '%s' expected a JSON response body but received none"
                        .formatted(scenario.name()));
            }

            JsonNode actualBody = objectMapper.readTree(responseBody);
            boolean strictJson = Boolean.TRUE.equals(scenario.expected().strictJson());

            if (strictJson) {
                if (!scenario.expected().body().equals(actualBody)) {
                    throw new AssertionError(
                            "Scenario '%s' expected exact JSON body %s but received %s".formatted(
                                    scenario.name(),
                                    scenario.expected().body(),
                                    actualBody
                            )
                    );
                }
            } else {
                assertJsonSubset(scenario.expected().body(), actualBody, "$");
            }
        }
    }

    private MockHttpServletRequestBuilder buildRequest(EndpointScenario scenario) throws IOException {
        MockHttpServletRequestBuilder requestBuilder = switch (scenario.method().toUpperCase()) {
            case "GET" -> MockMvcRequestBuilders.get(scenario.uri());
            case "POST" -> MockMvcRequestBuilders.post(scenario.uri());
            case "PUT" -> MockMvcRequestBuilders.put(scenario.uri());
            case "PATCH" -> MockMvcRequestBuilders.patch(scenario.uri());
            case "DELETE" -> MockMvcRequestBuilders.delete(scenario.uri());
            default -> throw new IllegalArgumentException(
                    "Unsupported HTTP method for scenario '%s': %s".formatted(scenario.name(), scenario.method())
            );
        };

        if (scenario.headers() != null) {
            for (Map.Entry<String, String> header : scenario.headers().entrySet()) {
                requestBuilder.header(header.getKey(), header.getValue());
            }
        }

        if (scenario.currentUser() != null) {
            requestBuilder.requestAttr(
                    CurrentUserArgumentResolver.CURRENT_USER_REQUEST_ATTRIBUTE,
                    scenario.currentUser().toJwtUser()
            );
        }

        if (scenario.requestBody() != null && !scenario.requestBody().isNull()) {
            requestBuilder
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(scenario.requestBody()));
        }

        return requestBuilder.accept(MediaType.APPLICATION_JSON);
    }

    private void assertJsonSubset(JsonNode expected, JsonNode actual, String path) {
        if (expected == null || expected.isNull()) {
            if (actual != null && !actual.isNull()) {
                throw new AssertionError("Expected null at %s but received %s".formatted(path, actual));
            }
            return;
        }

        if (actual == null || actual.isNull()) {
            throw new AssertionError("Expected value at %s but received null".formatted(path));
        }

        if (expected.isObject()) {
            if (!actual.isObject()) {
                throw new AssertionError("Expected object at %s but received %s".formatted(path, actual.getNodeType()));
            }

            expected.fields().forEachRemaining(entry -> {
                JsonNode actualValue = actual.get(entry.getKey());
                if (actualValue == null) {
                    throw new AssertionError("Expected field %s.%s to exist".formatted(path, entry.getKey()));
                }
                assertJsonSubset(entry.getValue(), actualValue, path + "." + entry.getKey());
            });
            return;
        }

        if (expected.isArray()) {
            if (!actual.isArray()) {
                throw new AssertionError("Expected array at %s but received %s".formatted(path, actual.getNodeType()));
            }
            if (actual.size() < expected.size()) {
                throw new AssertionError(
                        "Expected array at %s to contain at least %d items but received %d"
                                .formatted(path, expected.size(), actual.size())
                );
            }

            for (int index = 0; index < expected.size(); index++) {
                assertJsonSubset(expected.get(index), actual.get(index), path + "[" + index + "]");
            }
            return;
        }

        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Expected value %s at %s but received %s".formatted(expected, path, actual)
            );
        }
    }
}
