package com.algonix.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequest {

    @NotBlank
    private String fullName;

    private String avatarUrl;

    private String bio;
}
