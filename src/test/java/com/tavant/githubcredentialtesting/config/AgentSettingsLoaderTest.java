package com.tavant.githubcredentialtesting.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgentSettingsLoaderTest {
    @Test
    void loadsSettingsFromPropertiesFile() throws IOException {
        Path configFile = Files.createTempFile("agent-settings", ".properties");
        Files.writeString(configFile, String.join(System.lineSeparator(),
                "agent.username=test-user",
                "agent.password=test-token",
                "target.url=https://api.github.com/user"));

        try {
            AgentSettings settings = new AgentSettingsLoader().load(configFile, Map.of());

            assertEquals("test-user", settings.username());
            assertEquals("test-token", settings.password());
            assertEquals("https://api.github.com/user", settings.targetUrl());
        } finally {
            Files.deleteIfExists(configFile);
        }
    }

    @Test
    void supportsAlternateEnvironmentVariableNames() {
        Properties properties = new Properties();
        properties.setProperty("agent.username", "file-user");
        properties.setProperty("agent.password", "file-password");
        properties.setProperty("target.url", "https://from-file.example");

        AgentSettings settings = new AgentSettingsLoader().fromSources(Map.of(
                "USERNAME", "env-user",
                "PASSWORD", "env-password",
                "URL", "https://example.com/login.action"), properties);

        assertEquals("env-user", settings.username());
        assertEquals("env-password", settings.password());
        assertEquals("https://example.com/login.action", settings.targetUrl());
    }

    @Test
    void prefersAgentPrefixedEnvironmentVariablesWhenBothArePresent() {
        AgentSettings settings = new AgentSettingsLoader().fromSources(Map.of(
                "AGENT_USERNAME", "agent-user",
                "AGENT_PASSWORD", "agent-password",
                "AGENT_TARGET_URL_UNUSED", "ignored",
                "TARGET_URL", "https://primary.example",
                "USERNAME", "fallback-user",
                "PASSWORD", "fallback-password",
                "URL", "https://fallback.example"), new Properties());

        assertEquals("agent-user", settings.username());
        assertEquals("agent-password", settings.password());
        assertEquals("https://primary.example", settings.targetUrl());
    }
}