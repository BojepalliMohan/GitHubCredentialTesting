package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.LoginPage;
import utils.PlaywrightFactory;

public class LoginSteps {
    @Given("the user opens the login page")
    public void theUserOpensTheLoginPage() {
        loginPage().open();
    }

    @When("the user logs in with configured credentials")
    public void theUserLogsInWithConfiguredCredentials() {
        loginPage().loginWithConfiguredCredentials();
    }

    @Then("the user should reach the authenticated area")
    public void theUserShouldReachTheAuthenticatedArea() {
        loginPage().assertLoginSucceeded();
    }

    private LoginPage loginPage() {
        return new LoginPage(PlaywrightFactory.getPage());
    }
}