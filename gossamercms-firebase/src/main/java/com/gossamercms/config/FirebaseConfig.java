package com.gossamercms.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.io.IOException;

/**
 * FirebaseConfig now supports a Spring property instead of a hard-coded path.
 * It reads firebase.credentials-location from application.yml, and if that is blank it falls back to GOOGLE_APPLICATION_CREDENTIALS.
 * Use this in your app’s application.yml:
  firebase:
    credentials-location: ${FIREBASE_CREDENTIALS_LOCATION:}
 * Then each developer can set their own local path:
 * export FIREBASE_CREDENTIALS_LOCATION=/home/dave/workspace/java/cedar-demo/src/main/resources/cedar-medical-firebase-adminsdk-fbsvc-89f15d434a.json
 * Or put the absolute path directly in application.yml:
 * firebase:
 *   credentials-location: file:/home/dave/workspace/java/cedar-demo/src/main/resources/cedar-medical-firebase-adminsdk-fbsvc-89f15d434a.json
 * The code change is in gossamercms-firebase:
 * •
 * FirebaseConfig now loads credentials from the configured location
 * •
 * FirebaseProperties was added for typed config binding
 * •
 * FirebaseAutoConfig enables those properties
 * Use file:/... for local files; classpath:... also works if you ever decide to package the JSON as a resource.
 */
@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private final FirebaseProperties firebaseProperties;
    private final ResourceLoader resourceLoader;

    public FirebaseConfig(FirebaseProperties firebaseProperties, ResourceLoader resourceLoader) {
        this.firebaseProperties = firebaseProperties;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initialize() throws IOException {
        if (!hasConfiguredCredentials()) {
            log.info("Skipping Firebase initialization because no credentials are configured");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(loadCredentials())
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    private boolean hasConfiguredCredentials() {
        String location = firebaseProperties.getCredentialsLocation();
        if (location != null && !location.isBlank()) {
            return true;
        }

        String applicationDefaultCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        return applicationDefaultCredentials != null && !applicationDefaultCredentials.isBlank();
    }

    private GoogleCredentials loadCredentials() throws IOException {
        String location = firebaseProperties.getCredentialsLocation();
        if (location == null || location.isBlank()) {
            return GoogleCredentials.getApplicationDefault();
        }

        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            resource = resourceLoader.getResource("file:" + location);
        }

        if (!resource.exists()) {
            throw new IOException("Firebase credentials file not found: " + location);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return GoogleCredentials.fromStream(inputStream);
        }
    }
}
