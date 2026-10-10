package com.borrowbox.controller;

import com.borrowbox.dto.common.ApiResponse;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userDetails.getUsername())));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {
        UserDto updated = userService.updateProfile(
                userDetails.getUsername(),
                request.getFullName(),
                request.getPhone(),
                request.getAvatarUrl()
        );
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    public static class UpdateProfileRequest {
        private String fullName;
        private String phone;
        private String avatarUrl;

        public UpdateProfileRequest() {}

        public UpdateProfileRequest(String fullName, String phone, String avatarUrl) {
            this.fullName = fullName;
            this.phone = phone;
            this.avatarUrl = avatarUrl;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
