package com.algonix.server.service;

import com.algonix.server.dto.ConnectProfileRequest;
import com.algonix.server.entity.PlatformType;

import java.util.UUID;

public interface PlatformIntegrationService {

    void connectProfile(UUID userId, ConnectProfileRequest request);

    void syncProfile(UUID userId, PlatformType platform);

}