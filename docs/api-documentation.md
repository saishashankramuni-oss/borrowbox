# BorrowBox REST API Documentation

Base URL: `/api/v1`

## 1. Authentication Endpoints (`/api/v1/auth`)
- `POST /api/v1/auth/register`: Register new user (`ROLE_RENTER` or `ROLE_OWNER`).
- `POST /api/v1/auth/login`: Authenticate user and return JWT bearer token.
- `POST /api/v1/auth/logout`: Invalidate session on client.
- `GET /api/v1/auth/me`: Get current authenticated user details.

## 2. Categories (`/api/v1/categories`)
- `GET /api/v1/categories`: Get list of active categories.
- `GET /api/v1/categories/{id}`: Get category details by ID.

## 3. Equipment (`/api/v1/equipment`)
- `GET /api/v1/equipment`: Search & filter equipment.
  - Query Parameters: `query`, `categoryId`, `categorySlug`, `location`, `minPrice`, `maxPrice`, `startDate`, `endDate`, `sortBy`, `page`, `size`.
- `GET /api/v1/equipment/{id}`: Get equipment details, gallery images, owner profile, and reviews.
- `GET /api/v1/equipment/my-listings`: [Auth Owner] Get owner's listings.
- `POST /api/v1/equipment`: [Auth Owner] Create new equipment listing.
- `PUT /api/v1/equipment/{id}`: [Auth Owner] Update existing listing.
- `PATCH /api/v1/equipment/{id}/status`: [Auth Owner] Update listing status (`AVAILABLE`, `PAUSED`, `MAINTENANCE`).
- `POST /api/v1/equipment/upload-image`: [Auth] Upload photo asset to server storage.

## 4. Bookings (`/api/v1/bookings`)
- `GET /api/v1/bookings/estimate`: Calculate price breakdown for given equipment & dates.
- `POST /api/v1/bookings`: [Auth Renter] Request new booking.
- `GET /api/v1/bookings/my-rentals`: [Auth Renter] Get renter's active & past bookings.
- `GET /api/v1/bookings/my-requests`: [Auth Owner] Get incoming booking requests.
- `GET /api/v1/bookings/{id}`: [Auth] Get booking details & status history.
- `POST /api/v1/bookings/{id}/approve`: [Auth Owner] Approve pending booking request.
- `POST /api/v1/bookings/{id}/reject`: [Auth Owner] Reject booking request.
- `POST /api/v1/bookings/{id}/confirm-pickup`: [Auth] Confirm handover & activate rental.
- `POST /api/v1/bookings/{id}/request-return`: [Auth Renter] Initiate return request.
- `POST /api/v1/bookings/{id}/confirm-return`: [Auth Owner] Confirm equipment return & complete rental.
- `POST /api/v1/bookings/{id}/cancel`: [Auth] Cancel booking.

## 5. Condition Reports (`/api/v1/bookings/{bookingId}/condition-reports`)
- `POST /api/v1/bookings/{bookingId}/condition-reports`: File inspection report (`BEFORE_HANDOVER` or `AFTER_RETURN`).
- `GET /api/v1/bookings/{bookingId}/condition-reports`: Get inspection reports for a booking.

## 6. Wishlist (`/api/v1/wishlist`)
- `GET /api/v1/wishlist`: Get user's saved wishlist items.
- `POST /api/v1/wishlist/{equipmentId}`: Toggle equipment in wishlist.

## 7. Reviews (`/api/v1/reviews`)
- `POST /api/v1/reviews/booking/{bookingId}`: [Auth Renter] Post review for a completed rental.
- `GET /api/v1/reviews/equipment/{equipmentId}`: Get verified reviews for an equipment item.

## 8. Notifications (`/api/v1/notifications`)
- `GET /api/v1/notifications`: List user notifications.
- `GET /api/v1/notifications/unread-count`: Get unread count.
- `PATCH /api/v1/notifications/{id}/read`: Mark single notification as read.
- `POST /api/v1/notifications/read-all`: Mark all notifications as read.

## 9. Administration (`/api/v1/admin`)
- `GET /api/v1/admin/overview`: [Admin] Get platform metrics overview.
- `GET /api/v1/admin/users`: [Admin] List all user accounts.
- `PATCH /api/v1/admin/users/{id}/status`: [Admin] Suspend or activate user account.
- `GET /api/v1/admin/disputes`: [Admin] List open disputes.
- `PATCH /api/v1/admin/disputes/{id}`: [Admin] Resolve dispute.
