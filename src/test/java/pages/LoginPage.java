package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tavant.githubcredentialtesting.config.AgentSettings;
import constants.FrameworkConstants;
import utils.ConfigReader;
import utils.EnvironmentManager;

import java.util.concurrent.TimeUnit;

public final class LoginPage {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate(EnvironmentManager.getSettings().targetUrl());
    }

    public void loginWithConfiguredCredentials() {
        AgentSettings settings = EnvironmentManager.getSettings();

        page.locator(ConfigReader.require(FrameworkConstants.LOGIN_USERNAME_SELECTOR_PROPERTY))
                .fill(settings.username());
        page.locator(ConfigReader.require(FrameworkConstants.LOGIN_PASSWORD_SELECTOR_PROPERTY))
                .fill(settings.password());
        page.locator(ConfigReader.require(FrameworkConstants.LOGIN_SUBMIT_SELECTOR_PROPERTY))
                .click();
    }

    public void assertLoginSucceeded() {
        int timeoutMs = ConfigReader.getInt(
                FrameworkConstants.ASSERTION_TIMEOUT_PROPERTY,
                FrameworkConstants.DEFAULT_ASSERTION_TIMEOUT_MS);
        String successSelector = ConfigReader.get(FrameworkConstants.LOGIN_SUCCESS_SELECTOR_PROPERTY);
        String expectedUrlFragment = ConfigReader.get(FrameworkConstants.POST_LOGIN_URL_CONTAINS_PROPERTY);

        if (expectedUrlFragment == null && successSelector == null) {
            throw new IllegalStateException(
                    "Configure either post.login.url.contains or login.success.selector in src/test/resources/config.properties.");
        }

        if (expectedUrlFragment != null) {
            waitForUrl(expectedUrlFragment, timeoutMs);
        }

        if (successSelector != null) {
            Locator successLocator = page.locator(successSelector).first();
            successLocator.waitFor(new Locator.WaitForOptions().setTimeout((double) timeoutMs));
        }
    }

    private void waitForUrl(String expectedUrlFragment, int timeoutMs) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);

        while (System.nanoTime() < deadline) {
            if (page.url().contains(expectedUrlFragment)) {
                return;
            }

            page.waitForTimeout(250);
        }

        throw new AssertionError("Expected current URL to contain '" + expectedUrlFragment
                + "' but found '" + page.url() + "'.");
    }
}