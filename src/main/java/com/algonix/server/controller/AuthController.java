package com.algonix.server.controller;

import com.algonix.server.dto.ApiResponse;
import com.algonix.server.dto.AuthResponse;
import com.algonix.server.dto.LoginRequest;
import com.algonix.server.dto.RegisterRequest;
import com.algonix.server.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        logger.info("Registering user with email: {}", request.getEmail());
        AuthResponse user=authService.register(request,response);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                .data(user)
                .error(null)
                .build());
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        // Authentication is handled by Spring Security, so we just return a success message
        logger.info("User logging in system:{}", request.getEmail());

        AuthResponse user = authService.login(request,response);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .data(user)
                        .error(null)
                        .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken() {
        // Token refresh logic would go here
        logger.info("Generate new access token for Already logged in  User");
        return ResponseEntity.ok("Token Refreshed Successfully");
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword() {
        // Password update logic would go here
        logger.info("User updating password");
        return ResponseEntity.ok("Password Updated Successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {
        logger.info("User logging out system");
        authService.logout(request, response);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data("Logged out successfully")
                .error(null)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        logger.info("Fetching current authenticated user");
        try {
            AuthResponse user = authService.getCurrentUser(request);
            return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                    .data(user)
                    .error(null)
                    .build());
        } catch (Exception e) {
            logger.error("Failed to fetch user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<String>builder()
                            .data(null)
                            .error(e.getMessage())
                            .build());
        }
    }

}