package com.borrowbox.service;

import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.User;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("null")
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public UserDto updateProfile(String email, String fullName, String phone, String avatarUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (fullName != null) {
            if (fullName.isBlank()) {
                throw new IllegalArgumentException("Full name cannot be blank.");
            }
            user.setFullName(fullName.trim());
        }
        if (phone != null) {
            String trimmedPhone = phone.trim();
            if (trimmedPhone.isEmpty()) {
                user.setPhone(null);
            } else {
                user.setPhone(trimmedPhone);
            }
        }
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            user.setAvatarUrl(avatarUrl.trim());
        }

        user = userRepository.save(user);
        return authService.mapToUserDto(user);
    }
}
