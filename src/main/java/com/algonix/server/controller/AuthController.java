package com.algonix.server.controller;

import com.algonix.server.dto.LoginRequest;
import com.algonix.server.dto.RegisterRequest;
import com.algonix.server.service.AuthService;
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
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        logger.info("Registering user with email: {}", request.getEmail());
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User Registered Successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        // Authentication is handled by Spring Security, so we just return a success message
        logger.info("User logging in system:{}", request.getEmail());

        return ResponseEntity.ok(authService.login(request,response));
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
    public ResponseEntity<String> logout() {
        // Logout logic would go here
        logger.info("User logging out system");
        return ResponseEntity.ok("User Logged Out Successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        // Logic to get the current authenticated user would go here
        logger.info("Fetching current authenticated user");
        return ResponseEntity.ok("Current User Details");
    }
}