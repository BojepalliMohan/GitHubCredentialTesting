package com.tavant.githubcredentialtesting;

import com.tavant.githubcredentialtesting.config.AgentSettings;
import com.tavant.githubcredentialtesting.config.AgentSettingsLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class App {
    private static final int MAX_BODY_PREVIEW = 400;

    private App() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AgentSettings settings = new AgentSettingsLoader().load();
        URI targetUri = URI.create(settings.targetUrl());
        String authorizationHeader = buildAuthorizationHeader(targetUri, settings);

        HttpRequest request = HttpRequest.newBuilder(targetUri)
                .header("Accept", "application/json")
                .header("Authorization", authorizationHeader)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.printf("Using agent '%s' against %s%n", settings.username(), settings.targetUrl());
        System.out.printf("Authorization mode: %s%n", isGitHubHost(targetUri) ? "Bearer token" : "Basic auth");
        System.out.printf("Response status: %d%n", response.statusCode());
        System.out.println(preview(response.body()));
    }

    static String buildAuthorizationHeader(URI targetUri, AgentSettings settings) {
        if (isGitHubHost(targetUri)) {
            return "Bearer " + settings.password();
        }

        String credentials = settings.username() + ":" + settings.password();
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }

    private static boolean isGitHubHost(URI targetUri) {
        String host = targetUri.getHost();
        return host != null && (host.equalsIgnoreCase("github.com")
                || host.equalsIgnoreCase("api.github.com")
                || host.endsWith(".github.com"));
    }

    private static String preview(String body) {
        if (body == null || body.isBlank()) {
            return "Response body is empty.";
        }
        if (body.length() <= MAX_BODY_PREVIEW) {
            return body;
        }
        return body.substring(0, MAX_BODY_PREVIEW) + System.lineSeparator() + "... output truncated ...";
    }
}