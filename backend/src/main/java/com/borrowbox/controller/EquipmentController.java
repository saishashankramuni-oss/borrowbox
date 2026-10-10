package com.borrowbox.controller;

import com.borrowbox.dto.common.ApiResponse;
import com.borrowbox.dto.equipment.EquipmentCreateRequest;
import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.dto.equipment.EquipmentSearchFilter;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.service.EquipmentService;
import com.borrowbox.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final FileStorageService fileStorageService;

    public EquipmentController(EquipmentService equipmentService, FileStorageService fileStorageService) {
        this.equipmentService = equipmentService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EquipmentDto>>> searchEquipment(EquipmentSearchFilter filter) {
        Page<EquipmentDto> result = equipmentService.searchEquipment(filter);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipmentDto>> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getEquipmentById(id)));
    }

    @GetMapping("/my-listings")
    public ResponseEntity<ApiResponse<List<EquipmentDto>>> getMyListings(@AuthenticationPrincipal UserDetails userDetails) {
        List<EquipmentDto> listings = equipmentService.getMyListings(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(listings));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EquipmentDto>> createEquipment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EquipmentCreateRequest request) {
        EquipmentDto created = equipmentService.createEquipment(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Equipment listing created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipmentDto>> updateEquipment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EquipmentCreateRequest request) {
        EquipmentDto updated = equipmentService.updateEquipment(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Equipment listing updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EquipmentDto>> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StatusUpdateRequest request) {
        EquipmentDto updated = equipmentService.updateStatus(id, userDetails.getUsername(), request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Listing status updated", updated));
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String filePath = fileStorageService.storeFile(file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", filePath));
    }

    public static class StatusUpdateRequest {
        private EquipmentStatus status;

        public StatusUpdateRequest() {}

        public StatusUpdateRequest(EquipmentStatus status) {
            this.status = status;
        }

        public EquipmentStatus getStatus() {
            return status;
        }

        public void setStatus(EquipmentStatus status) {
            this.status = status;
        }
    }
}
