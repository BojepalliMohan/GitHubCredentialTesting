package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import constants.FrameworkConstants;

import java.util.Locale;

public final class PlaywrightFactory {
    private static final ThreadLocal<PlaywrightSession> SESSION = new ThreadLocal<>();

    private PlaywrightFactory() {
    }

    public static Page initializePage() {
        PlaywrightSession existingSession = SESSION.get();
        if (existingSession != null) {
            return existingSession.page();
        }

        Playwright playwright = Playwright.create();
        Browser browser = resolveBrowserType(playwright).launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(ConfigReader.getBoolean(
                                FrameworkConstants.HEADLESS_PROPERTY,
                                FrameworkConstants.DEFAULT_HEADLESS)));

        BrowserContext browserContext = browser.newContext();
        Page page = browserContext.newPage();
        page.setDefaultNavigationTimeout(ConfigReader.getInt(
                FrameworkConstants.NAVIGATION_TIMEOUT_PROPERTY,
                FrameworkConstants.DEFAULT_NAVIGATION_TIMEOUT_MS));
        page.setDefaultTimeout(ConfigReader.getInt(
                FrameworkConstants.ASSERTION_TIMEOUT_PROPERTY,
                FrameworkConstants.DEFAULT_ASSERTION_TIMEOUT_MS));

        SESSION.set(new PlaywrightSession(playwright, browser, browserContext, page));
        return page;
    }

    public static Page getPage() {
        PlaywrightSession session = SESSION.get();
        if (session == null) {
            throw new IllegalStateException("Playwright has not been initialized. Hooks must run before steps.");
        }
        return session.page();
    }

    public static byte[] takeScreenshot() {
        PlaywrightSession session = SESSION.get();
        if (session == null) {
            return new byte[0];
        }

        return session.page().screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }

    public static void closePage() {
        PlaywrightSession session = SESSION.get();
        if (session == null) {
            return;
        }

        try {
            session.browserContext().close();
        } finally {
            try {
                session.browser().close();
            } finally {
                session.playwright().close();
                SESSION.remove();
            }
        }
    }

    private static BrowserType resolveBrowserType(Playwright playwright) {
        String browserName = ConfigReader.getOrDefault(
                FrameworkConstants.BROWSER_PROPERTY,
                FrameworkConstants.DEFAULT_BROWSER).toLowerCase(Locale.ROOT);

        return switch (browserName) {
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> throw new IllegalArgumentException("Unsupported browser configured: " + browserName);
        };
    }

    private record PlaywrightSession(
            Playwright playwright,
            Browser browser,
            BrowserContext browserContext,
            Page page) {
    }
}