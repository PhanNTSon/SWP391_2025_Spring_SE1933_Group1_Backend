package com.se1933g01.steamclonebackend.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.se1933g01.steamclonebackend.exception.ContentModerationException;

@Component
public class GeminiContentModerator {

  @Value("${gemini.key}")
  private String API_KEY;

  public boolean isViolating(String inputText) {
    try {
      if (inputText == null || inputText.isBlank())
        return false;
      String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
          + this.API_KEY;

      String json = """
          {
            "contents": [
              {
                "role": "user",
                "parts": [
                  { "text": "Is the following message toxic, hateful, violent, or inappropriate? Answer only true or false. Message: \\"%s\\"" }
                ]
              }
            ]
          }
          """
          .formatted(inputText.replace("\"", "\\\""));

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json))
          .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      String body = response.body().toLowerCase();
      // Xử lý đơn giản: kiểm tra chuỗi phản hồi có "true" hay không
      return body.contains("true");
    } catch (IOException | InterruptedException | IllegalArgumentException | NullPointerException e) {
      throw new ContentModerationException("Failed to check moderation content", e);
    }
  }
}
