package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitUntilState;
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
        page.navigate(
            EnvironmentManager.getSettings().targetUrl(),
            new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

    public void loginWithConfiguredCredentials() {
        AgentSettings settings = EnvironmentManager.getSettings();
        String usernameSelector = ConfigReader.require(FrameworkConstants.LOGIN_USERNAME_SELECTOR_PROPERTY);
        String passwordSelector = ConfigReader.require(FrameworkConstants.LOGIN_PASSWORD_SELECTOR_PROPERTY);
        String submitSelector = ConfigReader.require(FrameworkConstants.LOGIN_SUBMIT_SELECTOR_PROPERTY);
        int timeoutMs = ConfigReader.getInt(
            FrameworkConstants.ASSERTION_TIMEOUT_PROPERTY,
            FrameworkConstants.DEFAULT_ASSERTION_TIMEOUT_MS);

        Locator username = page.locator(usernameSelector).first();
        Locator password = page.locator(passwordSelector).first();

        try {
            username.waitFor(new Locator.WaitForOptions().setTimeout((double) timeoutMs));
            password.waitFor(new Locator.WaitForOptions().setTimeout((double) timeoutMs));

            username.fill(settings.username());
            password.fill(settings.password());
            page.locator(submitSelector).first().click();
        } catch (TimeoutError exception) {
            throw new AssertionError(
                "Login form was not found. URL: " + page.url()
                    + ", username selector: " + usernameSelector
                    + ", password selector: " + passwordSelector
                    + ", submit selector: " + submitSelector,
                exception);
        }
    }

    public void assertLoginSucceeded() {
        int timeoutMs = ConfigReader.getInt(
                FrameworkConstants.ASSERTION_TIMEOUT_PROPERTY,
                FrameworkConstants.DEFAULT_ASSERTION_TIMEOUT_MS);
        String successSelector = ConfigReader.get(FrameworkConstants.LOGIN_SUCCESS_SELECTOR_PROPERTY);
        String expectedUrlFragment = ConfigReader.get(FrameworkConstants.POST_LOGIN_URL_CONTAINS_PROPERTY);

        if (expectedUrlFragment != null) {
            waitForUrl(expectedUrlFragment, timeoutMs);
        }

        Locator successLocator = successSelector != null
                ? page.locator(successSelector).first()
                : logoutButton();
        successLocator.waitFor(new Locator.WaitForOptions().setTimeout((double) timeoutMs));
    }

    private Locator logoutButton() {
        return page.locator("//a[@href='javascript:logoutAndReleaseLock()']"
                + " | //a[@href='/saml/logout?local=true']"
                + " | //a[contains(normalize-space(), 'Logout')]"
                + " | //a[contains(normalize-space(), 'Cerrar sesión')]"
                + " | //td[contains(normalize-space(), 'Welcome')]").first();
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