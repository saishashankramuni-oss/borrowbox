package com.borrowbox.config;

import com.borrowbox.security.JwtAuthFilter;
import com.borrowbox.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Value("${borrowbox.cors.allowed-origins:https://lambent-puffpuff-321850.netlify.app,https://*.netlify.app,https://*.onrender.com,http://localhost:3000,http://localhost:5500,http://localhost:8080,http://127.0.0.1:5500,http://127.0.0.1:8080}")
    private String allowedOrigins;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> exactOrigins = new java.util.ArrayList<>();
        List<String> patternOrigins = new java.util.ArrayList<>();
        for (String origin : allowedOrigins.split(",")) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                if (trimmed.contains("*")) {
                    patternOrigins.add(trimmed);
                } else {
                    exactOrigins.add(trimmed);
                }
            }
        }

        if (!exactOrigins.isEmpty()) {
            configuration.setAllowedOrigins(exactOrigins);
        }
        if (!patternOrigins.isEmpty()) {
            configuration.setAllowedOriginPatterns(patternOrigins);
        }

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public Static Resources & Frontend Pages
                .requestMatchers("/", "/index.html", "/explore.html", "/equipment-details.html",
                                 "/login.html", "/register.html", "/about.html", "/contact.html",
                                 "/renter-dashboard.html", "/owner-dashboard.html", "/add-equipment.html",
                                 "/edit-equipment.html", "/booking-details.html", "/wishlist.html",
                                 "/notifications.html", "/profile.html", "/admin-dashboard.html",
                                 "/css/**", "/js/**", "/assets/**", "/uploads/**", "/h2-console/**").permitAll()
                // Health Check Endpoints
                .requestMatchers("/health", "/api/v1/health", "/actuator/health", "/actuator/info").permitAll()
                // Public REST APIs
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/equipment/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/bookings/estimate").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/equipment/**").permitAll()
                // Admin REST APIs
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                // Protected REST APIs
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Required for H2 console frame options
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
