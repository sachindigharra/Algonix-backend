package com.algonix.server.service.impl;

import com.algonix.server.dto.response.AuthResponse;
import com.algonix.server.dto.request.LoginRequest;
import com.algonix.server.dto.request.RegisterRequest;
import com.algonix.server.entity.Role;
import com.algonix.server.entity.User;
import com.algonix.server.exception.DuplicateResourceException;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.security.CustomUserPrincipal;
import com.algonix.server.service.AuthService;
import com.algonix.server.util.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final CustomUserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;


    @Override
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException(
                        "Email already registered");
            }

            User user = new User();

            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());

            user.setPasswordHash(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
            user.setRole(Role.ROLE_USER);
            user =userRepository.save(user);
            log.info("User registered {}", request.getEmail());

            // Auto-login after registration
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(request.getEmail());
            loginRequest.setPassword(request.getPassword());

            // call login method to authenticate and generate cookie
            login(loginRequest, response);

            return AuthResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();
        }
        catch (DuplicateResourceException e) {
            log.error("Error that email is already registered: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("Error occurred during user registration: {}", e.getMessage());
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }


    }

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {

        log.debug("Validate the credentials for user: {}", request.getEmail());

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserPrincipal  userDetails =  userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Create Cookie for User : {}",request.getEmail());
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Set to true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
        log.debug("Login successful for user: {}", request.getEmail());
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();

    }

    @Override
    public AuthResponse getCurrentUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.warn("No cookies found in request");
            throw new RuntimeException("User not authenticated - no token found");
        }

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                String token = cookie.getValue();
                
                if (StringUtils.isEmpty(token)) {
                    log.warn("JWT token is empty");
                    throw new RuntimeException("User not authenticated - invalid token");
                }

                if (jwtService.isTokenExpired(token)) {
                    log.warn("JWT token is expired");
                    throw new RuntimeException("User not authenticated - token expired");
                }

                try {
                    String email = jwtService.extractUsername(token);
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> {
                                log.error("User not found for email: {}", email);
                                return new RuntimeException("User not found");
                            });

                    log.info("Successfully retrieved user: {}", email);
                    return AuthResponse.builder()
                            .userId(user.getId())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .build();
                } catch (Exception e) {
                    log.error("Failed to extract user from token: {}", e.getMessage());
                    throw new RuntimeException("User not authenticated - " + e.getMessage());
                }
            }
        }

        log.warn("JWT cookie not found in request");
        throw new RuntimeException("User not authenticated - no JWT token");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String username = null;
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    
                    if (StringUtils.isEmpty(token) || jwtService.isTokenExpired(token)) {
                        log.info("Token already expired or invalid, clearing cookie");
                    } else {
                        try {
                            username = jwtService.extractUsername(token);
                            log.info("User: {} logged out successfully", username);
                        } catch (Exception e) {
                            log.warn("Could not extract username from token: {}", e.getMessage());
                        }
                    }
                    break;
                }
            }
        }

        // Clear the JWT cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        if (username == null) {
            log.info("Logout completed - no valid username in token");
        }
    }
}
