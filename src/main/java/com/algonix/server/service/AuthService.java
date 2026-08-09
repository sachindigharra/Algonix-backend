package com.algonix.server.service;

import com.algonix.server.dto.AuthResponse;
import com.algonix.server.dto.LoginRequest;
import com.algonix.server.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService  {
    AuthResponse register(RegisterRequest request, HttpServletResponse response);

    AuthResponse login(LoginRequest request, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);

    AuthResponse getCurrentUser(HttpServletRequest request);
}
