package com.algonix.server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@Builder

public class AuthResponse {
    private UUID userId;

    private String email;

    private String fullName;
}


