package com.tavant.githubcredentialtesting.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            AgentSettings settings = new AgentSettingsLoader().load(configFile);

            assertEquals("test-user", settings.username());
            assertEquals("test-token", settings.password());
            assertEquals("https://api.github.com/user", settings.targetUrl());
        } finally {
            Files.deleteIfExists(configFile);
        }
    }
}