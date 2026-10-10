package com.borrowbox.controller;

import com.borrowbox.dto.admin.AdminOverviewDto;
import com.borrowbox.dto.admin.DisputeDto;
import com.borrowbox.dto.common.ApiResponse;
import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.enums.AccountStatus;
import com.borrowbox.entity.enums.DisputeStatus;
import com.borrowbox.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AdminOverviewDto>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getOverview()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllUsers()));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusUpdateRequest request) {
        UserDto updated = adminService.updateUserStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("User account status updated", updated));
    }

    @GetMapping("/listings")
    public ResponseEntity<ApiResponse<List<EquipmentDto>>> getAllListings() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllListings()));
    }

    @GetMapping("/disputes")
    public ResponseEntity<ApiResponse<List<DisputeDto>>> getAllDisputes() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllDisputes()));
    }

    @PatchMapping("/disputes/{id}")
    public ResponseEntity<ApiResponse<DisputeDto>> resolveDispute(
            @PathVariable Long id,
            @RequestBody ResolveDisputeRequest request) {
        DisputeDto resolved = adminService.resolveDispute(id, request.getStatus(), request.getResolutionNotes());
        return ResponseEntity.ok(ApiResponse.success("Dispute status updated", resolved));
    }

    public static class UserStatusUpdateRequest {
        private AccountStatus status;

        public UserStatusUpdateRequest() {}

        public UserStatusUpdateRequest(AccountStatus status) {
            this.status = status;
        }

        public AccountStatus getStatus() {
            return status;
        }

        public void setStatus(AccountStatus status) {
            this.status = status;
        }
    }

    public static class ResolveDisputeRequest {
        private DisputeStatus status;
        private String resolutionNotes;

        public ResolveDisputeRequest() {}

        public ResolveDisputeRequest(DisputeStatus status, String resolutionNotes) {
            this.status = status;
            this.resolutionNotes = resolutionNotes;
        }

        public DisputeStatus getStatus() {
            return status;
        }

        public void setStatus(DisputeStatus status) {
            this.status = status;
        }

        public String getResolutionNotes() {
            return resolutionNotes;
        }

        public void setResolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
        }
    }
}
