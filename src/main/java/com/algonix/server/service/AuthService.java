package com.algonix.server.service;

import com.algonix.server.dto.LoginRequest;
import com.algonix.server.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService  {
    void register(RegisterRequest request);

    String login(LoginRequest request, HttpServletResponse response);
}
