package com.borrowbox.service;

import com.borrowbox.dto.notification.NotificationDto;
import com.borrowbox.entity.Notification;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.NotificationType;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.repository.NotificationRepository;
import com.borrowbox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createNotification(User user, String title, String message, NotificationType type, Long relatedBookingId) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .relatedBookingId(relatedBookingId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Notification not found for this user");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                .collect(Collectors.toList());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    public NotificationDto mapToDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .userId(n.getUser().getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .relatedBookingId(n.getRelatedBookingId())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
