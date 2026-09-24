param(
    [string]$ConfigPath = "config/agent.properties",
    [switch]$SkipBrowserInstall
)

$projectRoot = Split-Path -Parent $PSScriptRoot

if ([System.IO.Path]::IsPathRooted($ConfigPath)) {
    $resolvedConfigPath = $ConfigPath
} else {
    $resolvedConfigPath = Join-Path $projectRoot $ConfigPath
}

$hasPrimaryEnvSettings = $env:AGENT_USERNAME -and $env:AGENT_PASSWORD -and $env:TARGET_URL
$hasFallbackEnvSettings = $env:USERNAME -and $env:PASSWORD -and $env:URL

if (-not $hasPrimaryEnvSettings -and -not $hasFallbackEnvSettings -and -not (Test-Path $resolvedConfigPath)) {
    Write-Error "Create $resolvedConfigPath from config/agent.properties.example or set AGENT_USERNAME, AGENT_PASSWORD, TARGET_URL or USERNAME, PASSWORD, URL."
    exit 1
}

Push-Location $projectRoot
try {
    if (-not $SkipBrowserInstall) {
        & mvn '-q' '-DskipTests' '-Dexec.classpathScope=test' 'exec:java' '-Dexec.mainClass=com.microsoft.playwright.CLI' '-Dexec.args=install chromium'
        if ($LASTEXITCODE -ne 0) {
            exit $LASTEXITCODE
        }
    }

    & mvn '-q' "-DagentConfig=$resolvedConfigPath" 'verify'
    exit $LASTEXITCODE
} finally {
    Pop-Location
}