package com.se1933g01.steamclonebackend.config;

import com.google.api.services.drive.Drive;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory; // ✅ Use JacksonFactory
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); // Use GsonFactory instead of deprecated JacksonFactory

    @Bean
    public Drive googleDrive() {
        try {
            InputStream in = GoogleDriveConfig.class.getClassLoader().getResourceAsStream("credentials.json");
            if (in == null) {
                throw new FileNotFoundException("Resource not found: credentials.json");
            }

            GoogleCredential credentials = GoogleCredential.fromStream(in)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive.file"));

            return new Drive.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
                    JSON_FACTORY, // ✅ Use JacksonFactory instead
                    credentials)
                    .setApplicationName("MyApp")
                    .build();
        } catch (Exception e) {
            e.printStackTrace(); // Logs the actual error
            throw new IllegalArgumentException("Invalid config for Google Drive", e);

        }
    }
}
