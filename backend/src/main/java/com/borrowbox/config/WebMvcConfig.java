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
import java.util.Arrays;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${borrowbox.upload.dir:uploads}")
    private String uploadDir;

    @Value("${borrowbox.cors.allowed-origins:http://localhost:3000,http://localhost:5500,http://localhost:8080,http://127.0.0.1:5500,http://127.0.0.1:8080}")
    private String allowedOrigins;

    @Override
    @SuppressWarnings("null")
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        java.util.List<String> originList = new java.util.ArrayList<>();
        for (String origin : allowedOrigins.split(",")) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                originList.add(trimmed);
            }
        }
        String[] origins = originList.toArray(new String[0]);

        boolean hasWildcard = originList.contains("*");

        var corsRegistration = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        if (hasWildcard) {
            corsRegistration.allowedOriginPatterns("*");
        } else {
            corsRegistration.allowedOrigins(origins);
        }
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadResourceLocation = uploadPath.toUri().toString();
        if (!uploadResourceLocation.endsWith("/")) {
            uploadResourceLocation += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadResourceLocation);

        Path frontendPath = Paths.get("..", "frontend").toAbsolutePath().normalize();
        String frontendResourceLocation = frontendPath.toUri().toString();
        if (!frontendResourceLocation.endsWith("/")) {
            frontendResourceLocation += "/";
        }

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", frontendResourceLocation);
    }
}
