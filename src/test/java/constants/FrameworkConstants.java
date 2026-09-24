package constants;

public final class FrameworkConstants {
    public static final String TEST_CONFIG_RESOURCE = "config.properties";
    public static final String DEFAULT_BROWSER = "chromium";
    public static final boolean DEFAULT_HEADLESS = true;
    public static final int DEFAULT_NAVIGATION_TIMEOUT_MS = 30_000;
    public static final int DEFAULT_ASSERTION_TIMEOUT_MS = 10_000;

    public static final String BROWSER_PROPERTY = "browser";
    public static final String HEADLESS_PROPERTY = "headless";
    public static final String NAVIGATION_TIMEOUT_PROPERTY = "navigation.timeout.ms";
    public static final String ASSERTION_TIMEOUT_PROPERTY = "assertion.timeout.ms";
    public static final String LOGIN_USERNAME_SELECTOR_PROPERTY = "login.username.selector";
    public static final String LOGIN_PASSWORD_SELECTOR_PROPERTY = "login.password.selector";
    public static final String LOGIN_SUBMIT_SELECTOR_PROPERTY = "login.submit.selector";
    public static final String LOGIN_SUCCESS_SELECTOR_PROPERTY = "login.success.selector";
    public static final String POST_LOGIN_URL_CONTAINS_PROPERTY = "post.login.url.contains";

    private FrameworkConstants() {
    }
}