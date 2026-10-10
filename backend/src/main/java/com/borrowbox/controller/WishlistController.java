package com.borrowbox.controller;

import com.borrowbox.dto.common.ApiResponse;
import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EquipmentDto>>> getMyWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        List<EquipmentDto> wishlist = wishlistService.getMyWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(wishlist));
    }

    @PostMapping("/{equipmentId}")
    public ResponseEntity<ApiResponse<Boolean>> toggleWishlist(
            @PathVariable Long equipmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean saved = wishlistService.toggleWishlist(userDetails.getUsername(), equipmentId);
        String msg = saved ? "Added to wishlist" : "Removed from wishlist";
        return ResponseEntity.ok(ApiResponse.success(msg, saved));
    }

    @GetMapping("/check/{equipmentId}")
    public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
            @PathVariable Long equipmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        boolean inWishlist = wishlistService.isInWishlist(email, equipmentId);
        return ResponseEntity.ok(ApiResponse.success(inWishlist));
    }
}
