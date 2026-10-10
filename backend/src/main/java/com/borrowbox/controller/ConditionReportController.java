package com.borrowbox.controller;

import com.borrowbox.dto.common.ApiResponse;
import com.borrowbox.dto.condition.ConditionReportDto;
import com.borrowbox.service.ConditionReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings/{bookingId}/condition-reports")
public class ConditionReportController {

    private final ConditionReportService reportService;

    public ConditionReportController(ConditionReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConditionReportDto>> createReport(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ConditionReportDto.Request request) {
        ConditionReportDto created = reportService.createReport(bookingId, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Condition report filed successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConditionReportDto>>> getReports(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ConditionReportDto> reports = reportService.getReportsByBooking(bookingId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
}
