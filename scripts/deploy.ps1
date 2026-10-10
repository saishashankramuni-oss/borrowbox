# ==============================================================================
# BorrowBox Cloud Deployment Automation Helper (Windows PowerShell)
# ==============================================================================

param (
    [switch]$OpenDashboards,
    [switch]$GenerateSecret,
    [string]$UpdateRenderUrl
)

Set-Alias -Name git -Value 'C:\Program Files\Git\cmd\git.exe' -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   BorrowBox Cloud Deployment Helper" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan

# 1. Generate Secure JWT Secret
if ($GenerateSecret -or ($PSBoundParameters.Count -eq 0)) {
    $jwtSecret = -join ((1..32) | ForEach-Object { '{0:x2}' -f (Get-Random -Max 256) })
    Write-Host ""
    Write-Host "[+] Generated Secure 256-Bit Production JWT Secret:" -ForegroundColor Green
    Write-Host "    $jwtSecret" -ForegroundColor Yellow
    Write-Host "    (Copy this into your Render Environment Variable 'JWT_SECRET')" -ForegroundColor Gray
}

# 2. Open Direct Cloud Provider Deployment Portals
if ($OpenDashboards) {
    Write-Host ""
    Write-Host "[+] Opening Cloud Provider Dashboards in your default browser..." -ForegroundColor Green
    Write-Host "    1. Railway MySQL Dashboard" -ForegroundColor Gray
    Start-Process "https://railway.app/new"
    Write-Host "    2. Render Web Service Dashboard" -ForegroundColor Gray
    Start-Process "https://dashboard.render.com/select-repo?type=web"
    Write-Host "    3. Netlify New Site Dashboard" -ForegroundColor Gray
    Start-Process "https://app.netlify.com/start"
}

# 3. Update Render URL across Netlify configuration files and auto-push
if ($UpdateRenderUrl) {
    $cleanUrl = $UpdateRenderUrl.TrimEnd('/')
    Write-Host ""
    Write-Host "[+] Updating Render URL across project to: $cleanUrl" -ForegroundColor Green

    # Update netlify.toml
    $tomlPath = Join-Path $PSScriptRoot "..\netlify.toml"
    if (Test-Path $tomlPath) {
        $tomlContent = [System.IO.File]::ReadAllText($tomlPath)
        $tomlContent = $tomlContent -replace 'https://[a-zA-Z0-9.-]+\.onrender\.com', $cleanUrl
        $tomlContent = $tomlContent.Replace('https://your-service.onrender.com', $cleanUrl)
        [System.IO.File]::WriteAllText($tomlPath, $tomlContent)
        Write-Host "    Updated netlify.toml" -ForegroundColor Gray
    }

    # Update frontend/_redirects
    $redirectsPath = Join-Path $PSScriptRoot "..\frontend\_redirects"
    if (Test-Path $redirectsPath) {
        $redirectsContent = [System.IO.File]::ReadAllText($redirectsPath)
        $redirectsContent = $redirectsContent -replace 'https://[a-zA-Z0-9.-]+\.onrender\.com', $cleanUrl
        $redirectsContent = $redirectsContent.Replace('https://your-service.onrender.com', $cleanUrl)
        [System.IO.File]::WriteAllText($redirectsPath, $redirectsContent)
        Write-Host "    Updated frontend/_redirects" -ForegroundColor Gray
    }

    # Update backend static _redirects
    $staticRedirectsPath = Join-Path $PSScriptRoot "..\backend\src\main\resources\static\_redirects"
    if (Test-Path $staticRedirectsPath) {
        $staticRedirectsContent = [System.IO.File]::ReadAllText($staticRedirectsPath)
        $staticRedirectsContent = $staticRedirectsContent -replace 'https://[a-zA-Z0-9.-]+\.onrender\.com', $cleanUrl
        $staticRedirectsContent = $staticRedirectsContent.Replace('https://your-service.onrender.com', $cleanUrl)
        [System.IO.File]::WriteAllText($staticRedirectsPath, $staticRedirectsContent)
        Write-Host "    Updated static/_redirects" -ForegroundColor Gray
    }

    # Commit and Push
    Write-Host ""
    Write-Host "[+] Committing and pushing updated URLs to GitHub..." -ForegroundColor Green
    & "C:\Program Files\Git\cmd\git.exe" add netlify.toml frontend/_redirects backend/src/main/resources/static/_redirects
    & "C:\Program Files\Git\cmd\git.exe" commit -m "chore: point Netlify proxy to active Render URL $cleanUrl"
    & "C:\Program Files\Git\cmd\git.exe" push origin main
    Write-Host ""
    Write-Host "[OK] Pushed successfully! Netlify will automatically redeploy with the updated backend proxy." -ForegroundColor Green
}

Write-Host ""
Write-Host "Usage Examples:" -ForegroundColor Cyan
Write-Host "  .\scripts\deploy.ps1 -OpenDashboards" -ForegroundColor Yellow
Write-Host "  .\scripts\deploy.ps1 -UpdateRenderUrl https://your-service.onrender.com" -ForegroundColor Yellow
Write-Host "  .\scripts\verify-deployment.ps1 -BackendUrl https://your-service.onrender.com -FrontendUrl https://your-site.netlify.app" -ForegroundColor Yellow
Write-Host ""
