# ==============================================================================
# BorrowBox Post-Deployment Verification Tool (Windows PowerShell)
# ==============================================================================
param (
    [Parameter(Mandatory=$false)]
    [string]$BackendUrl = "http://localhost:8080",

    [Parameter(Mandatory=$false)]
    [string]$FrontendUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Continue"

Write-Host "`n=======================================================" -ForegroundColor Cyan
Write-Host "   BorrowBox Post-Deployment Verification Suite" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "Target Backend URL : $BackendUrl" -ForegroundColor Yellow
Write-Host "Target Frontend URL: $FrontendUrl`n" -ForegroundColor Yellow

$allPassed = $true

# Helper function
function Test-Endpoint {
    param (
        [string]$Name,
        [string]$Uri,
        [string]$Method = "GET",
        [hashtable]$Headers = @{}
    )
    Write-Host "[-] Testing $Name ($Uri)..." -NoNewline
    try {
        $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $Headers -TimeoutSec 15
        Write-Host " [PASS]" -ForegroundColor Green
        return $response
    }
    catch {
        Write-Host " [FAIL]" -ForegroundColor Red
        Write-Host "    Error: $_" -ForegroundColor DarkRed
        $script:allPassed = $false
        return $null
    }
}

# 1. Health Probe
$health = Test-Endpoint -Name "Public Health Check" -Uri "$BackendUrl/health"
if ($health) {
    Write-Host "    Status: $($health.status) | Service: $($health.service)" -ForegroundColor Gray
}

# 2. Actuator Health Probe
$actuator = Test-Endpoint -Name "Spring Actuator Health" -Uri "$BackendUrl/actuator/health"
if ($actuator) {
    Write-Host "    Actuator Status: $($actuator.status)" -ForegroundColor Gray
}

# 3. Public Categories
$categories = Test-Endpoint -Name "Categories Catalog" -Uri "$BackendUrl/api/v1/categories"
if ($categories -and $categories.data) {
    Write-Host "    Categories Count: $($categories.data.Count)" -ForegroundColor Gray
}

# 4. Public Equipment Listings
$equipment = Test-Endpoint -Name "Equipment Listings" -Uri "$BackendUrl/api/v1/equipment?size=5"
if ($equipment -and $equipment.data) {
    Write-Host "    Equipment Total: $($equipment.data.totalElements)" -ForegroundColor Gray
}

# 5. Public Booking Pricing Estimate
$estimate = Test-Endpoint -Name "Pricing Estimate Engine" -Uri "$BackendUrl/api/v1/bookings/estimate?equipmentId=1&startDate=2026-11-01&endDate=2026-11-05"
if ($estimate -and $estimate.data) {
    Write-Host "    Estimate Available: $($estimate.data.available) | Total: ₹$($estimate.data.totalAmount)" -ForegroundColor Gray
}

# 6. CORS Options Pre-flight Verification
Write-Host "[-] Testing CORS Pre-flight for origin ($FrontendUrl)..." -NoNewline
try {
    $corsReq = [System.Net.HttpWebRequest]::Create("$BackendUrl/api/v1/equipment")
    $corsReq.Method = "OPTIONS"
    $corsReq.Headers.Add("Origin", $FrontendUrl)
    $corsReq.Headers.Add("Access-Control-Request-Method", "GET")
    $corsResp = $corsReq.GetResponse()
    $allowOrigin = $corsResp.Headers.Get("Access-Control-Allow-Origin")
    if ($allowOrigin) {
        Write-Host " [PASS]" -ForegroundColor Green
        Write-Host "    Access-Control-Allow-Origin: $allowOrigin" -ForegroundColor Gray
    } else {
        Write-Host " [WARN: Header Missing]" -ForegroundColor Yellow
    }
    $corsResp.Close()
} catch {
    Write-Host " [FAIL]" -ForegroundColor Red
    Write-Host "    Error: $_" -ForegroundColor DarkRed
    $allPassed = $false
}

Write-Host "`n-------------------------------------------------------" -ForegroundColor Cyan
if ($allPassed) {
    Write-Host "RESULT: ALL DEPLOYMENT CHECKS PASSED!" -ForegroundColor Green
} else {
    Write-Host "RESULT: SOME CHECKS FAILED OR WARNED. Review messages above." -ForegroundColor Yellow
}
Write-Host "-------------------------------------------------------`n" -ForegroundColor Cyan
