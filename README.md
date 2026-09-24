# GitHubCredentialTesting

This Maven Java project is a Playwright + Cucumber test framework that uses a local config file or environment variables for login credentials and target URL.

## Framework structure

```text
GitHubCredentialTesting
├── src
│   ├── main/java/com/tavant/githubcredentialtesting/config
│   │   ├── AgentSettings.java
│   │   └── AgentSettingsLoader.java
│   └── test
│       ├── java
│       │   ├── constants/FrameworkConstants.java
│       │   ├── hooks/Hooks.java
│       │   ├── pages/LoginPage.java
│       │   ├── runners/TestRunner.java
│       │   ├── stepdefinitions/LoginSteps.java
│       │   └── utils
│       │       ├── ConfigReader.java
│       │       ├── EnvironmentManager.java
│       │       └── PlaywrightFactory.java
│       └── resources
│           ├── config.properties
│           └── features/login.feature
└── .github/workflows/playwright.yml
```

## What this framework does

- Keeps local credentials out of Git by ignoring `config/agent.properties`.
- Loads `USERNAME` / `PASSWORD` / `URL` or `AGENT_USERNAME` / `AGENT_PASSWORD` / `TARGET_URL`.
- Uses Playwright page objects and Cucumber steps to run the login flow.
- Runs unit tests with `mvn test` and runs the UI suite with `mvn verify` or `./scripts/run-agent.ps1`.

## Configuration

1. Copy `config/agent.properties.example` to `config/agent.properties`.
2. Fill in the values for the target login application.
3. Update `src/test/resources/config.properties` with the correct login page selectors for your site.

Example:

```properties
agent.username=your-application-username
agent.password=your-application-password-or-token
target.url=https://your-app.example.com/login.action
```

Environment variables override the file:

- `AGENT_USERNAME`
- `AGENT_PASSWORD`
- `TARGET_URL`

The app also accepts these alternate names when you are integrating with an existing environment that already publishes them:

- `USERNAME`
- `PASSWORD`
- `URL`

Test framework settings live in `src/test/resources/config.properties`, for example:

```properties
browser=chromium
headless=true
navigation.timeout.ms=30000
assertion.timeout.ms=10000
login.username.selector=input[name='username']
login.password.selector=input[name='password']
login.submit.selector=button[type='submit']
login.success.selector=
post.login.url.contains=
```

Set either `login.success.selector` or `post.login.url.contains` so the framework can confirm login success.

## Run locally

Install the Playwright browser and execute the UI suite:

```powershell
./scripts/run-agent.ps1
```

Skip browser installation if it is already installed:

```powershell
./scripts/run-agent.ps1 -SkipBrowserInstall
```

Run only the fast unit tests:

```powershell
mvn test
```

Run the full UI suite directly with Maven:

```powershell
mvn verify
```

## Run in GitHub Actions

- Store `USERNAME` and `URL` as GitHub Variables.
- Store `PASSWORD` as a GitHub Secret.
- Use `.github/workflows/playwright.yml` and provide the environment name when you dispatch the workflow.

## Repository setup

```powershell
git remote add origin <your-github-repo-url>
```