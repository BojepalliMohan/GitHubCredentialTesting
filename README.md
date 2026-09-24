# GitHubCredentialTesting

This Maven Java project loads an agent username, secret, and target URL from a local config file or environment variables, then uses them from a scriptable Java entry point.

## What this project does

- Keeps local credentials out of Git by ignoring `config/agent.properties`.
- Loads values from environment variables first, then from `config/agent.properties`.
- Calls the configured URL from Java with either:
  - `Bearer <token>` for GitHub hosts.
  - `Basic <base64(username:password)>` for other hosts.

## Configuration

1. Copy `config/agent.properties.example` to `config/agent.properties`.
2. Fill in the values.

Example:

```properties
agent.username=your-github-username
agent.password=your-token-or-password
target.url=https://api.github.com/user
```

Environment variables override the file:

- `AGENT_USERNAME`
- `AGENT_PASSWORD`
- `TARGET_URL`

## Important note for GitHub

GitHub does not support normal account passwords for API access. If `target.url` points to GitHub, put a Personal Access Token in `agent.password` or `AGENT_PASSWORD`.

## Run

From PowerShell:

```powershell
./scripts/run-agent.ps1
```

Or with Maven directly:

```powershell
mvn -q compile exec:java
```

## Repository setup

This folder is initialized as a local Git repository. If you want a remote GitHub repository later, add it with:

```powershell
git remote add origin <your-github-repo-url>
```