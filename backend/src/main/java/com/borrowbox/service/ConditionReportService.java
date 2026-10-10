package com.borrowbox.service;

import com.borrowbox.dto.condition.ConditionReportDto;
import com.borrowbox.entity.Booking;
import com.borrowbox.entity.ConditionReport;
import com.borrowbox.entity.ConditionReportImage;
import com.borrowbox.entity.User;
import com.borrowbox.exception.BadRequestException;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.ConditionReportImageRepository;
import com.borrowbox.repository.ConditionReportRepository;
import com.borrowbox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class ConditionReportService {

    private final ConditionReportRepository reportRepository;
    private final ConditionReportImageRepository reportImageRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public ConditionReportService(ConditionReportRepository reportRepository, ConditionReportImageRepository reportImageRepository, BookingRepository bookingRepository, UserRepository userRepository, AuthService authService) {
        this.reportRepository = reportRepository;
        this.reportImageRepository = reportImageRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public ConditionReportDto createReport(Long bookingId, String userEmail, ConditionReportDto.Request request) {
        if (request == null) {
            throw new BadRequestException("Condition report payload is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isRenter = booking.getRenter().getId().equals(user.getId());
        boolean isOwner = booking.getOwner().getId().equals(user.getId());

        if (!isRenter && !isOwner) {
            throw new UnauthorizedAccessException("Only parties associated with this booking can file condition reports.");
        }

        if (request.getInspectionStage() == null) {
            throw new BadRequestException("Inspection stage is required.");
        }

        if (request.getConditionNotes() == null || request.getConditionNotes().isBlank()) {
            throw new BadRequestException("Condition notes are required.");
        }

        reportRepository.findByBookingIdAndInspectionStage(bookingId, request.getInspectionStage())
                .ifPresent(r -> {
                    throw new BadRequestException("Condition report for stage " + request.getInspectionStage() + " already exists for this booking.");
                });

        ConditionReport report = ConditionReport.builder()
                .booking(booking)
                .reporter(user)
                .inspectionStage(request.getInspectionStage())
                .conditionNotes(request.getConditionNotes().trim())
                .damageNotes(request.getDamageNotes() != null ? request.getDamageNotes().trim() : null)
                .confirmedAt(LocalDateTime.now())
                .build();

        report = reportRepository.save(report);

        if (request.getImagePaths() != null && !request.getImagePaths().isEmpty()) {
            List<ConditionReportImage> images = new ArrayList<>();
            for (String imgPath : request.getImagePaths()) {
                images.add(ConditionReportImage.builder()
                        .conditionReport(report)
                        .filePath(imgPath)
                        .build());
            }
            reportImageRepository.saveAll(images);
            report.setImages(images);
        }

        return mapToDto(report);
    }

    @Transactional(readOnly = true)
    public List<ConditionReportDto> getReportsByBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isRenter = booking.getRenter().getId().equals(user.getId());
        boolean isOwner = booking.getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRole().name().equals("ROLE_ADMIN");

        if (!isRenter && !isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("Not authorized to view condition reports for this booking.");
        }

        return reportRepository.findByBookingIdOrderByCreatedAtAsc(bookingId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ConditionReportDto mapToDto(ConditionReport report) {
        List<String> imagePaths = report.getImages().stream()
                .map(ConditionReportImage::getFilePath)
                .collect(Collectors.toList());

        return ConditionReportDto.builder()
                .id(report.getId())
                .bookingId(report.getBooking().getId())
                .reporter(authService.mapToUserDto(report.getReporter()))
                .inspectionStage(report.getInspectionStage())
                .conditionNotes(report.getConditionNotes())
                .damageNotes(report.getDamageNotes())
                .imagePaths(imagePaths)
                .confirmedAt(report.getConfirmedAt())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
