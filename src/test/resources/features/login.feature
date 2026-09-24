Feature: Login

  Scenario: Successful login with configured credentials
    Given the user opens the login page
    When the user logs in with configured credentials
    Then the user should reach the authenticated area