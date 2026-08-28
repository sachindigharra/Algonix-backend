package com.algonix.server.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserProfileResponse {

    private UUID userId;

    private String email;

    private String fullName;

    private String avatarUrl;

    private String bio;
}