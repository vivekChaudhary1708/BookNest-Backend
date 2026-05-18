package com.booknest.authuserservice.controller;

import com.booknest.authuserservice.config.JwtUtil;
import com.booknest.authuserservice.dto.AuthResponse;
import com.booknest.authuserservice.dto.ChangePasswordRequest;
import com.booknest.authuserservice.dto.LoginRequest;
import com.booknest.authuserservice.dto.RegisterRequest;
import com.booknest.authuserservice.dto.UpdateProfileRequest;
import com.booknest.authuserservice.dto.UserDto;
import com.booknest.authuserservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    private UserDto dummyUserDto;
    private AuthResponse dummyAuthResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        dummyUserDto = new UserDto(
                "user123",
                "John Doe",
                "john@example.com",
                "1234567890",
                "CUSTOMER",
                false,
                Instant.now()
        );
        dummyAuthResponse = new AuthResponse("dummy-jwt-token", dummyUserDto);
    }

    // 1. testRegister_Success
    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setMobile("1234567890");

        when(authService.register(any(RegisterRequest.class))).thenReturn(dummyAuthResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }

    // 2. testRegister_Failure
    @Test
    void testRegister_Failure() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    // 3. testLogin_Success
    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenReturn(dummyAuthResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"));
    }

    // 4. testLogin_Failure
    @Test
    void testLogin_Failure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrong");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // 5. testMe_Success
    @Test
    void testMe_Success() throws Exception {
        String token = "valid-token";
        when(jwtUtil.extractEmail(token)).thenReturn("john@example.com");
        when(authService.me("john@example.com")).thenReturn(dummyUserDto);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    // 6. testMe_MissingHeader
    @Test
    void testMe_MissingHeader() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }

    // 7. testUpdateProfile_Success
    @Test
    void testUpdateProfile_Success() throws Exception {
        String token = "valid-token";
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setName("John Updated");
        req.setMobile("9876543210");

        UserDto updatedUser = new UserDto("user123", "John Updated", "john@example.com", "9876543210", "CUSTOMER", false, Instant.now());

        when(jwtUtil.extractEmail(token)).thenReturn("john@example.com");
        when(authService.updateProfile("john@example.com", "John Updated", "9876543210")).thenReturn(updatedUser);

        mockMvc.perform(put("/auth/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    // 8. testChangePassword_Success
    @Test
    void testChangePassword_Success() throws Exception {
        String token = "valid-token";
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("oldPass");
        req.setNewPassword("newPass");

        when(jwtUtil.extractEmail(token)).thenReturn("john@example.com");

        mockMvc.perform(put("/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // 9. testListUsers_Success
    @Test
    void testListUsers_Success() throws Exception {
        when(authService.listUsers()).thenReturn(List.of(dummyUserDto));

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    // 10. testBlockUser_Success
    @Test
    void testBlockUser_Success() throws Exception {
        UserDto blockedUser = new UserDto("user123", "John Doe", "john@example.com", "1234567890", "CUSTOMER", true, Instant.now());
        when(authService.blockUser("user123")).thenReturn(blockedUser);

        mockMvc.perform(put("/auth/users/user123/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(true));
    }

    // 11. testDeleteUser_Success
    @Test
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/auth/users/user123"))
                .andExpect(status().isNoContent());
    }
}
