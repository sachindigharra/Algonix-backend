package com.algonix.server.service;

import com.algonix.server.dto.RegisterRequest;

public interface AuthService  {
    void register(RegisterRequest request);
}
