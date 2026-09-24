package com.tavant.githubcredentialtesting.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public final class AgentSettingsLoader {
    private static final String DEFAULT_CONFIG_PATH = "config/agent.properties";

    public AgentSettings load() throws IOException {
        return load(resolveConfigPath());
    }

    public AgentSettings load(Path configPath) throws IOException {
        return load(configPath, System.getenv());
    }

    AgentSettings load(Path configPath, Map<String, String> environment) throws IOException {
        Properties properties = new Properties();

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            }
        }

        return fromSources(environment, properties);
    }

    AgentSettings fromSources(Map<String, String> environment, Properties properties) {
        String username = firstNonBlank(
                environment.get("AGENT_USERNAME"),
                environment.get("USERNAME"),
                properties.getProperty("agent.username"));
        String password = firstNonBlank(
                environment.get("AGENT_PASSWORD"),
                environment.get("PASSWORD"),
                properties.getProperty("agent.password"));
        String targetUrl = firstNonBlank(
                environment.get("TARGET_URL"),
                environment.get("URL"),
                properties.getProperty("target.url"));

        validateRequiredValue("agent username", username);
        validateRequiredValue("agent password or token", password);
        validateRequiredValue("target URL", targetUrl);

        return new AgentSettings(username, password, targetUrl);
    }

    private Path resolveConfigPath() {
        return Path.of(System.getProperty("agentConfig", DEFAULT_CONFIG_PATH));
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private void validateRequiredValue(String label, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + label + ". Configure env vars or config/agent.properties.");
        }
    }
}