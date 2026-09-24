package com.tavant.githubcredentialtesting.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AgentSettingsLoader {
    private static final String DEFAULT_CONFIG_PATH = "config/agent.properties";

    public AgentSettings load() throws IOException {
        return load(resolveConfigPath());
    }

    public AgentSettings load(Path configPath) throws IOException {
        Properties properties = new Properties();

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            }
        }

        String username = firstNonBlank(System.getenv("AGENT_USERNAME"), properties.getProperty("agent.username"));
        String password = firstNonBlank(System.getenv("AGENT_PASSWORD"), properties.getProperty("agent.password"));
        String targetUrl = firstNonBlank(System.getenv("TARGET_URL"), properties.getProperty("target.url"));

        validateRequiredValue("agent username", username);
        validateRequiredValue("agent password or token", password);
        validateRequiredValue("target URL", targetUrl);

        return new AgentSettings(username, password, targetUrl);
    }

    private Path resolveConfigPath() {
        return Path.of(System.getProperty("agentConfig", DEFAULT_CONFIG_PATH));
    }

    private String firstNonBlank(String firstValue, String secondValue) {
        if (firstValue != null && !firstValue.isBlank()) {
            return firstValue.trim();
        }
        if (secondValue != null && !secondValue.isBlank()) {
            return secondValue.trim();
        }
        return null;
    }

    private void validateRequiredValue(String label, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + label + ". Configure env vars or config/agent.properties.");
        }
    }
}