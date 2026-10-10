package com.borrowbox.service;

import com.borrowbox.dto.auth.AuthRequest;
import com.borrowbox.dto.auth.AuthResponse;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.AccountStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.BadRequestException;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.security.JwtUtils;
import com.borrowbox.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponse register(AuthRequest.Register request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered in BorrowBox!");
        }

        if (request.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("Registration with ADMIN role is not permitted.");
        }

        Role userRole = request.getRole() != null ? request.getRole() : Role.ROLE_RENTER;

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .accountStatus(AccountStatus.ACTIVE)
                .avatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80")
                .build();

        user = userRepository.save(user);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String token = jwtUtils.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserDto(user))
                .build();
    }

    public AuthResponse login(AuthRequest.Login request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserDto(user))
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserDto(user);
    }

    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
