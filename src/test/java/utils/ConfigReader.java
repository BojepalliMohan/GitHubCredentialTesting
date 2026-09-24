package utils;

import constants.FrameworkConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    public static String get(String key) {
        String systemValue = trimToNull(System.getProperty(key));
        if (systemValue != null) {
            return systemValue;
        }

        String environmentValue = trimToNull(System.getenv(toEnvironmentKey(key)));
        if (environmentValue != null) {
            return environmentValue;
        }

        return trimToNull(PROPERTIES.getProperty(key));
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public static String require(String key) {
        String value = get(key);
        if (value == null) {
            throw new IllegalStateException("Missing required framework setting: " + key);
        }
        return value;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = ConfigReader.class.getClassLoader()
                .getResourceAsStream(FrameworkConstants.TEST_CONFIG_RESOURCE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to find " + FrameworkConstants.TEST_CONFIG_RESOURCE + " on the test classpath.");
            }

            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to load framework configuration.", exception);
        }
    }

    private static String toEnvironmentKey(String key) {
        return key.replace('.', '_').replace('-', '_').toUpperCase();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}