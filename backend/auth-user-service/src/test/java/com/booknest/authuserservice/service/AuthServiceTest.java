package com.booknest.authuserservice.service;

import com.booknest.authuserservice.config.JwtUtil;
import com.booknest.authuserservice.dto.AuthResponse;
import com.booknest.authuserservice.dto.LoginRequest;
import com.booknest.authuserservice.dto.RegisterRequest;
import com.booknest.authuserservice.model.User;
import com.booknest.authuserservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User dummyUser;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId("user123");
        dummyUser.setName("John Doe");
        dummyUser.setEmail("john@example.com");
        dummyUser.setPassword("encodedPassword");
        dummyUser.setRole("CUSTOMER");
        dummyUser.setBlocked(false);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("rawPassword");
        request.setRole("CUSTOMER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);
        when(jwtUtil.generateToken(dummyUser.getEmail())).thenReturn("dummyToken");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("dummyToken", response.getToken());
        assertEquals("john@example.com", response.getUser().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("rawPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches(request.getPassword(), dummyUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(dummyUser.getEmail())).thenReturn("dummyToken");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("dummyToken", response.getToken());
        assertEquals("john@example.com", response.getUser().getEmail());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrongPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches(request.getPassword(), dummyUser.getPassword())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void testMe_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(dummyUser));
        
        com.booknest.authuserservice.dto.UserDto result = authService.me("john@example.com");
        
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void testMe_NotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            authService.me("notfound@example.com");
        });
    }

    @Test
    void testUpdateProfile_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);
        
        com.booknest.authuserservice.dto.UserDto result = authService.updateProfile("john@example.com", "John Updated", "9876543210");
        
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangePassword_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("oldPass", dummyUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");
        
        authService.changePassword("john@example.com", "oldPass", "newPass");
        
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangePassword_IncorrectOldPassword() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("wrongOldPass", dummyUser.getPassword())).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> {
            authService.changePassword("john@example.com", "wrongOldPass", "newPass");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testListUsers_Success() {
        when(userRepository.findAll()).thenReturn(java.util.List.of(dummyUser));
        
        java.util.List<com.booknest.authuserservice.dto.UserDto> users = authService.listUsers();
        
        assertEquals(1, users.size());
        assertEquals("john@example.com", users.get(0).getEmail());
    }

    @Test
    void testBlockUser_Success() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);
        
        com.booknest.authuserservice.dto.UserDto result = authService.blockUser("user123");
        
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById("user123")).thenReturn(true);
        
        authService.deleteUser("user123");
        
        verify(userRepository, times(1)).deleteById("user123");
    }
}
