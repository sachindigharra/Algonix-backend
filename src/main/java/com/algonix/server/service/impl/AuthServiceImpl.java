package com.algonix.server.service.impl;

import com.algonix.server.dto.LoginRequest;
import com.algonix.server.dto.RegisterRequest;
import com.algonix.server.entity.Role;
import com.algonix.server.entity.User;
import com.algonix.server.exception.DuplicateResourceException;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.service.AuthService;
import com.algonix.server.util.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public void register(RegisterRequest request) {
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
            userRepository.save(user);
            log.info("User registered {}", request.getEmail());
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
    public String login(LoginRequest request) {

        log.debug("Validate the credentials for user: {}", request.getEmail());
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

        } catch (Exception e) {
            log.error("Authentication failed for user: {}. Error: {}", request.getEmail(), e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        log.debug("Login successful for user: {}", request.getEmail());
        return token;

    }

}
