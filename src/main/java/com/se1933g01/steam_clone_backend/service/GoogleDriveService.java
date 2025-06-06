package com.se1933g01.steam_clone_backend.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class GoogleDriveService {
    private static final String APPLICATION_NAME = "Steam CLone";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
}
