package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.EnvironmentManager;
import utils.PlaywrightFactory;

public class Hooks {
    @Before(order = 0)
    public void setUp() {
        EnvironmentManager.getSettings();
        PlaywrightFactory.initializePage();
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = PlaywrightFactory.takeScreenshot();
            if (screenshot.length > 0) {
                scenario.attach(screenshot, "image/png", "failure-screenshot");
            }
        }

        PlaywrightFactory.closePage();
    }
}