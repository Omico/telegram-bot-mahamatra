function Test-Command($Command = $null) {
    if ($null -eq $Command) {
        return $false
    }
    $oldPreference = $ErrorActionPreference
    $ErrorActionPreference = "stop"
    try { if (Get-Command $Command) { return $true } }
    catch { Write-Host "$Command does not exist"; return $false }
    finally { $ErrorActionPreference = $oldPreference }
}

if (Test-Command("git")) {
    git fetch
    git reset --hard origin/main
}
else {
    Write-Host "Git does not exist."
}

if (Test-Command("docker-compose")) {
    docker-compose up -d --build --force-recreate
}
else {
    Write-Host "Docker does not exist."
}
