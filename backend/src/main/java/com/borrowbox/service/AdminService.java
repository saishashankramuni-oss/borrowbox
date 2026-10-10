package com.borrowbox.service;

import com.borrowbox.dto.admin.AdminOverviewDto;
import com.borrowbox.dto.admin.DisputeDto;
import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.Dispute;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.AccountStatus;
import com.borrowbox.entity.enums.DisputeStatus;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class AdminService {

    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;
    private final DisputeRepository disputeRepository;
    private final AuthService authService;
    private final EquipmentService equipmentService;
    private final BookingService bookingService;

    public AdminService(UserRepository userRepository, EquipmentRepository equipmentRepository, BookingRepository bookingRepository, DisputeRepository disputeRepository, AuthService authService, EquipmentService equipmentService, BookingService bookingService) {
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.bookingRepository = bookingRepository;
        this.disputeRepository = disputeRepository;
        this.authService = authService;
        this.equipmentService = equipmentService;
        this.bookingService = bookingService;
    }

    @Transactional(readOnly = true)
    public AdminOverviewDto getOverview() {
        long totalUsers = userRepository.count();
        long totalOwners = userRepository.findAll().stream().filter(u -> u.getRole() == Role.ROLE_OWNER).count();
        long totalRenters = userRepository.findAll().stream().filter(u -> u.getRole() == Role.ROLE_RENTER).count();

        long totalEquipment = equipmentRepository.count();
        long activeEquipment = equipmentRepository.findAll().stream().filter(e -> e.getStatus() == EquipmentStatus.AVAILABLE).count();

        long totalBookings = bookingRepository.count();
        long activeBookings = bookingRepository.findAll().stream().filter(b -> b.getBookingStatus().blocksAvailability()).count();

        long openDisputes = disputeRepository.countByStatus(DisputeStatus.OPEN);

        BigDecimal grossVolume = bookingRepository.findAll().stream()
                .map(b -> b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminOverviewDto.builder()
                .totalUsers(totalUsers)
                .totalOwners(totalOwners)
                .totalRenters(totalRenters)
                .totalEquipment(totalEquipment)
                .activeEquipment(activeEquipment)
                .totalBookings(totalBookings)
                .activeBookings(activeBookings)
                .openDisputes(openDisputes)
                .totalGrossVolume(grossVolume)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(authService::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUserStatus(Long userId, AccountStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        user.setAccountStatus(status);
        return authService.mapToUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<EquipmentDto> getAllListings() {
        return equipmentRepository.findAll()
                .stream()
                .map(equipmentService::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisputeDto> getAllDisputes() {
        return disputeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDisputeDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DisputeDto resolveDispute(Long disputeId, DisputeStatus status, String resolutionNotes) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found with ID: " + disputeId));

        dispute.setStatus(status);
        dispute.setResolutionNotes(resolutionNotes);
        return mapToDisputeDto(disputeRepository.save(dispute));
    }

    public DisputeDto mapToDisputeDto(Dispute d) {
        return DisputeDto.builder()
                .id(d.getId())
                .booking(bookingService.mapToDto(d.getBooking()))
                .raisedBy(authService.mapToUserDto(d.getRaisedBy()))
                .reason(d.getReason())
                .description(d.getDescription())
                .status(d.getStatus())
                .resolutionNotes(d.getResolutionNotes())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
