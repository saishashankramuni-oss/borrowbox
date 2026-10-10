# ==============================================================================
# BorrowBox Full End-to-End Workflow Verification Script (Windows PowerShell)
# ==============================================================================
param (
    [Parameter(Mandatory=$false)]
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

Write-Host "`n=======================================================" -ForegroundColor Cyan
Write-Host "   BorrowBox Full End-to-End Workflow Test Suite" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "Target Base URL: $BaseUrl`n" -ForegroundColor Yellow

$ts = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$renterEmail = "renter_${ts}@example.com"
$ownerEmail = "owner_${ts}@example.com"
$password = "TestPass123!"

$allPassed = $true

function Report-Step($stepNum, $name, $result, $details = "") {
    if ($result) {
        Write-Host "[$stepNum] ${name}: " -NoNewline
        Write-Host "PASS" -ForegroundColor Green
        if ($details) { Write-Host "    $details" -ForegroundColor Gray }
    } else {
        Write-Host "[$stepNum] ${name}: " -NoNewline
        Write-Host "FAIL" -ForegroundColor Red
        if ($details) { Write-Host "    $details" -ForegroundColor DarkRed }
        $script:allPassed = $false
    }
}

# --- Step 1: Register Renter ---
try {
    $renterRegBody = @{
        fullName = "Test Renter $ts"
        email = $renterEmail
        password = $password
        phoneNumber = "+919876543210"
        address = "123 Rental Lane, Bangalore"
        role = "ROLE_RENTER"
    } | ConvertTo-Json

    $res = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/register" -Method Post -Body $renterRegBody -ContentType "application/json"
    $step1 = ($res.success -eq $true) -and ($res.data.token -ne $null)
    $renterToken = $res.data.token
    Report-Step "01" "Register New Renter" $step1 "User ID: $($res.data.user.id), Email: $renterEmail"
} catch {
    Report-Step "01" "Register New Renter" $false $_.Exception.Message
}

# --- Step 2: Attempt Duplicate Registration ---
try {
    $dupRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/register" -Method Post -Body $renterRegBody -ContentType "application/json"
    Report-Step "02" "Reject Duplicate Email Registration" $false "Should have failed but succeeded"
} catch {
    $step2 = $_.Exception.Response.StatusCode.value__ -in @(400, 409)
    Report-Step "02" "Reject Duplicate Email Registration" $step2 "Correctly rejected with status $(($_.Exception.Response.StatusCode.value__))"
}

# --- Step 3: Reject ADMIN Role on Registration ---
try {
    $adminRegBody = @{
        fullName = "Malicious Admin"
        email = "fakeadmin_${ts}@example.com"
        password = $password
        role = "ROLE_ADMIN"
    } | ConvertTo-Json
    $admRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/register" -Method Post -Body $adminRegBody -ContentType "application/json"
    Report-Step "03" "Block Direct ROLE_ADMIN Registration" $false "Should have been rejected but succeeded"
} catch {
    $step3 = $_.Exception.Response.StatusCode.value__ -in @(400, 403)
    Report-Step "03" "Block Direct ROLE_ADMIN Registration" $step3 "Correctly blocked with status $(($_.Exception.Response.StatusCode.value__))"
}

# --- Step 4: Register Owner ---
try {
    $ownerRegBody = @{
        fullName = "Test Owner $ts"
        email = $ownerEmail
        password = $password
        phoneNumber = "+919123456789"
        address = "456 Studio Street, Mumbai"
        role = "ROLE_OWNER"
    } | ConvertTo-Json

    $res = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/register" -Method Post -Body $ownerRegBody -ContentType "application/json"
    $step4 = ($res.success -eq $true) -and ($res.data.token -ne $null)
    $ownerToken = $res.data.token
    Report-Step "04" "Register New Owner" $step4 "User ID: $($res.data.user.id), Email: $ownerEmail"
} catch {
    Report-Step "04" "Register New Owner" $false $_.Exception.Message
}

# --- Step 5: Login as Renter ---
try {
    $loginBody = @{
        email = $renterEmail
        password = $password
    } | ConvertTo-Json
    $res = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $step5 = ($res.success -eq $true) -and ($res.data.token -ne $null)
    if ($res.data.token) { $renterToken = $res.data.token }
    Report-Step "05" "Login with Renter Credentials" $step5 "Authenticated successfully"
} catch {
    Report-Step "05" "Login with Renter Credentials" $false $_.Exception.Message
}

# --- Step 6: Verify /auth/me for Renter ---
try {
    $authHeader = @{ Authorization = "Bearer $renterToken" }
    $me = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/me" -Method Get -Headers $authHeader
    $step6 = ($me.success -eq $true) -and ($me.data.email -eq $renterEmail) -and ($me.data.role -eq "ROLE_RENTER")
    Report-Step "06" "Fetch Authenticated Profile (/auth/me)" $step6 "Name: $($me.data.fullName), Role: $($me.data.role)"
} catch {
    Report-Step "06" "Fetch Authenticated Profile (/auth/me)" $false $_.Exception.Message
}

# --- Step 7: Owner Creates Equipment Listing ---
$equipmentId = $null
try {
    $ownerAuthHeader = @{ Authorization = "Bearer $ownerToken" }
    $cats = Invoke-RestMethod -Uri "$BaseUrl/api/v1/categories" -Method Get
    $catId = $cats.data[0].id

    $equipBody = @{
        name = "Canon EOS R5 Mirrorless Camera $ts"
        description = "Professional 45MP full-frame mirrorless camera for 8K video and high-res photography."
        dailyPrice = 1499.00
        securityDeposit = 5000.00
        itemCondition = "LIKE_NEW"
        categoryId = $catId
        minimumRentalDays = 1
        maximumRentalDays = 30
        location = "Indiranagar, Bangalore"
        imageUrls = @("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800")
    } | ConvertTo-Json

    $res = Invoke-RestMethod -Uri "$BaseUrl/api/v1/equipment" -Method Post -Headers $ownerAuthHeader -Body $equipBody -ContentType "application/json"
    $step7 = ($res.success -eq $true) -and ($res.data.id -ne $null)
    $equipmentId = $res.data.id
    Report-Step "07" "Owner Creates Equipment Listing" $step7 "Created Equipment ID: $equipmentId ($($res.data.name))"
} catch {
    Report-Step "07" "Owner Creates Equipment Listing" $false $_.Exception.Message
}

# --- Step 8: Public Equipment Catalog Search ---
try {
    $catalog = Invoke-RestMethod -Uri "$BaseUrl/api/v1/equipment?keyword=Canon" -Method Get
    $found = ($catalog.data.content | Where-Object { $_.id -eq $equipmentId }) -ne $null
    Report-Step "08" "Search Equipment in Public Catalog" ($found -or ($catalog.data.totalElements -gt 0)) "Found matching equipment in catalog"
} catch {
    Report-Step "08" "Search Equipment in Public Catalog" $false $_.Exception.Message
}

# --- Step 9: Owner Updates Equipment Details ---
try {
    $updateBody = @{
        name = "Canon EOS R5 Mirrorless Camera $ts [Updated]"
        description = "Updated description with premium 24-70mm f/2.8 lens included."
        dailyPrice = 1799.00
        securityDeposit = 5500.00
        itemCondition = "LIKE_NEW"
        categoryId = $catId
        minimumRentalDays = 1
        maximumRentalDays = 30
        location = "Indiranagar, Bangalore"
        imageUrls = @("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800", "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=800")
    } | ConvertTo-Json

    $updateRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/equipment/$equipmentId" -Method Put -Headers $ownerAuthHeader -Body $updateBody -ContentType "application/json"
    $step9 = ($updateRes.success -eq $true) -and ($updateRes.data.dailyPrice -eq 1799.00)
    Report-Step "09" "Owner Updates Equipment & Images" $step9 "Updated price to ₹$($updateRes.data.dailyPrice), images count: $($updateRes.data.images.Count)"
} catch {
    Report-Step "09" "Owner Updates Equipment & Images" $false $_.Exception.Message
}

# --- Step 10: Renter Calculates Pricing Estimate ---
try {
    $startDate = "2026-11-10"
    $endDate = "2026-11-14"
    $estRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/bookings/estimate?equipmentId=$equipmentId&startDate=$startDate&endDate=$endDate" -Method Get
    $step10 = ($estRes.success -eq $true) -and ($estRes.data.available -eq $true) -and ($estRes.data.rentalDays -eq 5)
    Report-Step "10" "Check Equipment Availability & Estimate" $step10 "Days: $($estRes.data.rentalDays), Total: ₹$($estRes.data.totalAmount)"
} catch {
    Report-Step "10" "Check Equipment Availability & Estimate" $false $_.Exception.Message
}

# --- Step 11: Renter Creates Booking ---
$bookingId = $null
try {
    $bookingBody = @{
        equipmentId = $equipmentId
        startDate = $startDate
        endDate = $endDate
        deliveryAddress = "123 Rental Lane, Indiranagar, Bangalore"
        notes = "Need for weekend photography workshop"
    } | ConvertTo-Json

    $bookRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/bookings" -Method Post -Headers $authHeader -Body $bookingBody -ContentType "application/json"
    $step11 = ($bookRes.success -eq $true) -and ($bookRes.data.id -ne $null) -and ($bookRes.data.bookingStatus -eq "PENDING")
    $bookingId = $bookRes.data.id
    Report-Step "11" "Renter Creates Booking Request" $step11 "Booking ID: $bookingId, Status: $($bookRes.data.bookingStatus)"
} catch {
    Report-Step "11" "Renter Creates Booking Request" $false $_.Exception.Message
}

# --- Step 12: Owner Approves Booking ---
try {
    $approveRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/bookings/$bookingId/approve" -Method Post -Headers $ownerAuthHeader
    $step12 = ($approveRes.success -eq $true) -and ($approveRes.data.bookingStatus -eq "APPROVED")
    Report-Step "12" "Owner Approves Booking" $step12 "New Status: $($approveRes.data.bookingStatus)"
} catch {
    Report-Step "12" "Owner Approves Booking" $false $_.Exception.Message
}

# --- Step 13: Prevent Overlapping Booking Once Approved ---
try {
    $conflictBody = @{
        equipmentId = $equipmentId
        startDate = "2026-11-12"
        endDate = "2026-11-16"
        deliveryAddress = "Conflict St"
    } | ConvertTo-Json
    $confRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/bookings" -Method Post -Headers $authHeader -Body $conflictBody -ContentType "application/json"
    Report-Step "13" "Reject Overlapping Double-Booking" $false "Should have failed due to date conflict"
} catch {
    $step13 = $_.Exception.Response.StatusCode.value__ -in @(400, 409)
    Report-Step "13" "Reject Overlapping Double-Booking" $step13 "Correctly rejected conflict with status $(($_.Exception.Response.StatusCode.value__))"
}

# --- Step 14: Owner Hands Over Gear (Status -> ACTIVE) ---
try {
    $handoverRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/bookings/$bookingId/confirm-pickup" -Method Post -Headers $ownerAuthHeader
    $step14 = ($handoverRes.success -eq $true) -and ($handoverRes.data.bookingStatus -eq "ACTIVE")
    Report-Step "14" "Owner Hands Over Equipment (ACTIVE)" $step14 "Status: $($handoverRes.data.bookingStatus)"
} catch {
    Report-Step "14" "Owner Hands Over Equipment (ACTIVE)" $false $_.Exception.Message
}

# --- Step 15: Renter Requests Return (Status -> RETURN_REQUESTED) ---
try {
    $retReqRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/bookings/$bookingId/request-return" -Method Post -Headers $authHeader
    $step15 = ($retReqRes.success -eq $true) -and ($retReqRes.data.bookingStatus -eq "RETURN_REQUESTED")
    Report-Step "15" "Renter Requests Return (RETURN_REQUESTED)" $step15 "Status: $($retReqRes.data.bookingStatus)"
} catch {
    Report-Step "15" "Renter Requests Return (RETURN_REQUESTED)" $false $_.Exception.Message
}

# --- Step 16: Owner Confirms Return & Completes Rental (Status -> COMPLETED) ---
try {
    $completeRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/bookings/$bookingId/confirm-return" -Method Post -Headers $ownerAuthHeader
    $step16 = ($completeRes.success -eq $true) -and ($completeRes.data.bookingStatus -eq "COMPLETED")
    Report-Step "16" "Owner Confirms Return & Completes (COMPLETED)" $step16 "Status: $($completeRes.data.bookingStatus)"
} catch {
    Report-Step "16" "Owner Confirms Return & Completes (COMPLETED)" $false $_.Exception.Message
}

# --- Step 17: Renter Submits Review for Completed Rental ---
try {
    $revBody = @{
        rating = 5
        comment = "Fantastic camera! Exactly as described, spotless sensor and sharp lenses. Highly recommended!"
    } | ConvertTo-Json

    $revRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/reviews/booking/$bookingId" -Method Post -Headers $authHeader -Body $revBody -ContentType "application/json"
    $step17 = ($revRes.success -eq $true) -and ($revRes.data.rating -eq 5)
    Report-Step "17" "Renter Submits 5-Star Review" $step17 "Review ID: $($revRes.data.id), Rating: $($revRes.data.rating)"
} catch {
    Report-Step "17" "Renter Submits 5-Star Review" $false $_.Exception.Message
}

# --- Step 18: Verify Equipment Review Calculation ---
try {
    $eqDetail = Invoke-RestMethod -Uri "$BaseUrl/api/v1/equipment/$equipmentId" -Method Get
    $step18 = ($eqDetail.data.reviewCount -ge 1) -and ($eqDetail.data.ratingAverage -eq 5.0)
    Report-Step "18" "Verify Equipment Rating Recomputed" $step18 "Total Reviews: $($eqDetail.data.reviewCount), Avg: $($eqDetail.data.ratingAverage)"
} catch {
    Report-Step "18" "Verify Equipment Rating Recomputed" $false $_.Exception.Message
}

# --- Step 19: Renter Wishlist Toggle & Fetch ---
try {
    $wishAdd = Invoke-RestMethod -Uri "$BaseUrl/api/v1/wishlist/$equipmentId" -Method Post -Headers $authHeader
    $wishList = Invoke-RestMethod -Uri "$BaseUrl/api/v1/wishlist" -Method Get -Headers $authHeader
    $inWish = ($wishList.data | Where-Object { $_.id -eq $equipmentId }) -ne $null
    $step19 = ($wishAdd.success -eq $true) -and $inWish
    Report-Step "19" "Add to Wishlist & Fetch Wishlist" $step19 "Item present in wishlist: $inWish"
} catch {
    Report-Step "19" "Add to Wishlist & Fetch Wishlist" $false $_.Exception.Message
}

# --- Step 20: Admin Dashboard & Overview ---
try {
    $adminLogin = @{
        email = "admin@borrowbox.com"
        password = "Password123!"
    } | ConvertTo-Json
    $admLogRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/login" -Method Post -Body $adminLogin -ContentType "application/json"
    $adminToken = $admLogRes.data.token
    $adminHeader = @{ Authorization = "Bearer $adminToken" }

    $stats = Invoke-RestMethod -Uri "$BaseUrl/api/v1/admin/overview" -Method Get -Headers $adminHeader
    $step20 = ($stats.success -eq $true) -and ($stats.data.totalUsers -ge 2)
    Report-Step "20" "Admin Dashboard Overview" $step20 "Users: $($stats.data.totalUsers), Equip: $($stats.data.totalEquipment), Bookings: $($stats.data.totalBookings)"
} catch {
    Report-Step "20" "Admin Dashboard Overview" $false $_.Exception.Message
}

# --- Step 21: Logout Verification ---
try {
    $logoutRes = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/logout" -Method Post -Headers $authHeader
    $step21 = ($logoutRes.success -eq $true)
    Report-Step "21" "User Logout Endpoint" $step21 "Session terminated successfully"
} catch {
    Report-Step "21" "User Logout Endpoint" $false $_.Exception.Message
}

Write-Host "`n-------------------------------------------------------" -ForegroundColor Cyan
if ($allPassed) {
    Write-Host "RESULT: ALL 21 END-TO-END WORKFLOW TESTS PASSED!" -ForegroundColor Green
} else {
    Write-Host "RESULT: SOME WORKFLOW TESTS FAILED. Review errors above." -ForegroundColor Red
}
Write-Host "-------------------------------------------------------`n" -ForegroundColor Cyan
