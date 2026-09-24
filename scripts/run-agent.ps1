param(
    [string]$ConfigPath = "config/agent.properties"
)

$projectRoot = Split-Path -Parent $PSScriptRoot

if ([System.IO.Path]::IsPathRooted($ConfigPath)) {
    $resolvedConfigPath = $ConfigPath
} else {
    $resolvedConfigPath = Join-Path $projectRoot $ConfigPath
}

$hasEnvSettings = $env:AGENT_USERNAME -and $env:AGENT_PASSWORD -and $env:TARGET_URL

if (-not $hasEnvSettings -and -not (Test-Path $resolvedConfigPath)) {
    Write-Error "Create $resolvedConfigPath from config/agent.properties.example or set AGENT_USERNAME, AGENT_PASSWORD, and TARGET_URL."
    exit 1
}

Push-Location $projectRoot
try {
    mvn -q compile exec:java "-DagentConfig=$resolvedConfigPath"
} finally {
    Pop-Location
}