package utils;

import com.tavant.githubcredentialtesting.config.AgentSettings;
import com.tavant.githubcredentialtesting.config.AgentSettingsLoader;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class EnvironmentManager {
    private static volatile AgentSettings cachedSettings;

    private EnvironmentManager() {
    }

    public static AgentSettings getSettings() {
        if (cachedSettings == null) {
            synchronized (EnvironmentManager.class) {
                if (cachedSettings == null) {
                    try {
                        cachedSettings = new AgentSettingsLoader().load();
                    } catch (IOException exception) {
                        throw new UncheckedIOException("Unable to load credential configuration.", exception);
                    }
                }
            }
        }

        return cachedSettings;
    }
}