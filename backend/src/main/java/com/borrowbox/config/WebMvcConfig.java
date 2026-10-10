package com.borrowbox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${borrowbox.upload.dir:uploads}")
    private String uploadDir;

    @Value("${borrowbox.cors.allowed-origins:https://lambent-puffpuff-321850.netlify.app,https://*.netlify.app,https://*.onrender.com,http://localhost:3000,http://localhost:5500,http://localhost:8080,http://127.0.0.1:5500,http://127.0.0.1:8080}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        java.util.List<String> exactOrigins = new java.util.ArrayList<>();
        java.util.List<String> patternOrigins = new java.util.ArrayList<>();
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

        var corsRegistration = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        if (!exactOrigins.isEmpty()) {
            corsRegistration.allowedOrigins(exactOrigins.toArray(String[]::new));
        }
        if (!patternOrigins.isEmpty()) {
            corsRegistration.allowedOriginPatterns(patternOrigins.toArray(String[]::new));
        }
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            java.nio.file.Files.createDirectories(uploadPath);
        } catch (Exception ignored) {
        }
        String uploadResourceLocation = uploadPath.toUri().toString();
        if (!uploadResourceLocation.endsWith("/")) {
            uploadResourceLocation += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadResourceLocation);

        java.util.List<String> staticLocations = new java.util.ArrayList<>();
        staticLocations.add("classpath:/static/");

        Path frontendPath = Paths.get("..", "frontend").toAbsolutePath().normalize();
        if (java.nio.file.Files.exists(frontendPath)) {
            String frontendResourceLocation = frontendPath.toUri().toString();
            if (!frontendResourceLocation.endsWith("/")) {
                frontendResourceLocation += "/";
            }
            staticLocations.add(frontendResourceLocation);
        }

        registry.addResourceHandler("/**")
                .addResourceLocations(staticLocations.toArray(String[]::new));
    }
}
