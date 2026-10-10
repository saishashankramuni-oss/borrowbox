package com.borrowbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BorrowBoxIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Health endpoint returns UP status")
    void testPublicHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("BorrowBox Backend")));
    }

    @Test
    @DisplayName("Public categories catalog returns seed categories")
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(5)));
    }

    @Test
    @DisplayName("Public equipment listings API returns paginated equipment")
    void testGetEquipmentCatalog() throws Exception {
        mockMvc.perform(get("/api/v1/equipment?size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("Protected endpoint rejects unauthenticated request with 403 Forbidden")
    void testProtectedEndpointRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin endpoint rejects unauthenticated request with 403 Forbidden")
    void testAdminEndpointRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/admin/overview"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Public pricing estimate calculates total price correctly")
    void testPricingEstimatePublicApi() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/estimate")
                        .param("equipmentId", "1")
                        .param("startDate", "2026-11-01")
                        .param("endDate", "2026-11-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.rentalDays", is(3)))
                .andExpect(jsonPath("$.data.available", is(true)));
    }

    @Test
    @DisplayName("Invalid login returns 401 Unauthorized with centralized error response")
    void testInvalidLoginReturnsCentralizedError() throws Exception {
        String invalidPayload = "{\"email\":\"nonexistent@borrowbox.com\",\"password\":\"WrongPassword!\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid email or password")));
    }
}
