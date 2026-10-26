package com.algonix.server.dto;

import com.algonix.server.entity.PlatformType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectProfileRequest {

    @NotNull(message = "Platform is required")
    private PlatformType platform;

    @NotBlank(message = "Username is required")
    private String username;
}