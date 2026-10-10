package com.borrowbox;

import com.borrowbox.dto.auth.AuthRequest;
import com.borrowbox.dto.auth.AuthResponse;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.AccountStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.BadRequestException;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.security.JwtUtils;
import com.borrowbox.security.UserDetailsImpl;
import com.borrowbox.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthWorkflowTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Registration succeeds and hashes password with BCrypt")
    void testRegisterSuccess() {
        AuthRequest.Register request = new AuthRequest.Register();
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("SecurePass123!");
        request.setRole(Role.ROLE_RENTER);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashedBCryptPasswordString");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(101L);
            return u;
        });
        when(jwtUtils.generateToken(any(UserDetailsImpl.class))).thenReturn("mock-jwt-token-xyz");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token-xyz", response.getToken());
        assertEquals("john@example.com", response.getUser().getEmail());
        assertEquals(Role.ROLE_RENTER, response.getUser().getRole());
        verify(passwordEncoder).encode("SecurePass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Registration fails when email is already registered")
    void testRegisterDuplicateEmailFails() {
        AuthRequest.Register request = new AuthRequest.Register();
        request.setFullName("John Doe");
        request.setEmail("existing@example.com");
        request.setPassword("SecurePass123!");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Registration rejects attempting to assign ADMIN role")
    void testRegisterAdminRoleRejected() {
        AuthRequest.Register request = new AuthRequest.Register();
        request.setFullName("Attacker");
        request.setEmail("attacker@example.com");
        request.setPassword("Password123!");
        request.setRole(Role.ROLE_ADMIN);

        when(userRepository.existsByEmail("attacker@example.com")).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("ADMIN"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Valid login returns JWT and user profile")
    void testLoginSuccess() {
        AuthRequest.Login request = new AuthRequest.Login();
        request.setEmail("user@borrowbox.com");
        request.setPassword("Password123!");

        User user = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("user@borrowbox.com")
                .role(Role.ROLE_RENTER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtils.generateToken(userDetails)).thenReturn("jwt-token-123");
        when(userRepository.findByEmail("user@borrowbox.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("user@borrowbox.com", response.getUser().getEmail());
    }

    @Test
    @DisplayName("Invalid login credentials throws BadCredentialsException")
    void testLoginInvalidCredentials() {
        AuthRequest.Login request = new AuthRequest.Login();
        request.setEmail("user@borrowbox.com");
        request.setPassword("WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
