package com.booknest.authuserservice.service;

import com.booknest.authuserservice.config.JwtUtil;
import com.booknest.authuserservice.dto.AuthResponse;
import com.booknest.authuserservice.dto.LoginRequest;
import com.booknest.authuserservice.dto.RegisterRequest;
import com.booknest.authuserservice.dto.UserDto;
import com.booknest.authuserservice.model.User;
import com.booknest.authuserservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.Instant;
import java.util.Locale;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void ensureDefaultAdmin() {
        String adminEmail = "admin@booknest.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("BookNest Admin");
            admin.setEmail(adminEmail);
            admin.setMobile("9999999999");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole("ADMIN");
            admin.setBlocked(false);
            admin.setCreatedAt(Instant.now());
            userRepository.save(admin);
        }

        String userAdminEmail = "chaudharyvivek5491@gmail.com";
        if (!userRepository.existsByEmail(userAdminEmail)) {
            User admin2 = new User();
            admin2.setName("Vivek Chaudhary");
            admin2.setEmail(userAdminEmail);
            admin2.setMobile("9999999999");
            admin2.setPassword(passwordEncoder.encode("Vivek123@"));
            admin2.setRole("ADMIN");
            admin2.setBlocked(false);
            admin2.setCreatedAt(Instant.now());
            userRepository.save(admin2);
        }
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        final String requestedRole = request.getRole() == null ? "CUSTOMER" : request.getRole().trim().toUpperCase(Locale.ROOT);
        if (!"CUSTOMER".equals(requestedRole)) {
            // Frontend: customer registration only.
            throw new IllegalArgumentException("Only CUSTOMER registration is allowed");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("CUSTOMER");
        user.setBlocked(false);
        user.setCreatedAt(Instant.now());

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getEmail());
        return new AuthResponse(token, toDto(saved));
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (user.isBlocked()) {
            throw new IllegalArgumentException("Account is blocked");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, toDto(user));
    }

    public UserDto me(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return toDto(user);
    }

    public UserDto updateProfile(String email, String name, String mobile) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) throw new IllegalArgumentException("User not found");
        if (name != null && !name.isBlank()) user.setName(name.trim());
        if (mobile != null) user.setMobile(mobile.trim());
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) throw new IllegalArgumentException("User not found");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User with this email not found");
        }

        // Generate 6-digit OTP (Hardcoded to 123456 for easy testing by user)
        String otp = "123456"; // String.format("%06d", new java.util.Random().nextInt(999999));
        user.setResetOtp(otp);
        user.setResetOtpExpiry(Instant.now().plusSeconds(5 * 60)); // 5 mins expiry
        userRepository.save(user);

        // Send OTP via email
        try {
            emailService.sendOtpEmail(email, otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email. Please try again later.");
        }
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User with this email not found");
        }

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        if (user.getResetOtpExpiry() == null || user.getResetOtpExpiry().isBefore(Instant.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }
        
        return true;
    }

    public void resetPassword(String email, String otp, String newPassword) {
        // First verify the OTP to ensure it's still valid
        verifyOtp(email, otp);

        User user = userRepository.findByEmail(email).orElse(null);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        userRepository.save(user);
    }

    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(AuthService::toDto).toList();
    }

    public UserDto blockUser(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) throw new IllegalArgumentException("User not found");
        user.setBlocked(true);
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) throw new IllegalArgumentException("User not found");
        userRepository.deleteById(id);
    }

    private static UserDto toDto(User u) {
        return new UserDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getMobile(),
                u.getRole(),
                u.isBlocked(),
                u.getCreatedAt()
        );
    }
}