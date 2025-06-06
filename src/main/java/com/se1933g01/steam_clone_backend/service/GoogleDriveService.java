package com.se1933g01.steam_clone_backend.service;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class GoogleDriveService {
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static Drive getDriveService() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive.file"));
        return new Drive.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.gson.GsonFactory(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("MyApp")
                .build();
    }
}
