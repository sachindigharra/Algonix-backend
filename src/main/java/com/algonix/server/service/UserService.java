package com.algonix.server.service;

import com.algonix.server.dto.request.UpdateUserProfileRequest;
import com.algonix.server.dto.response.UserProfileResponse;

import java.util.UUID;

public interface UserService {

    UserProfileResponse getUser(UUID userId);

    UserProfileResponse updateProfile(
            UUID userId,
            UpdateUserProfileRequest request);

    void deleteUser(UUID userId);
}