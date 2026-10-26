package com.algonix.server.repository;

import com.algonix.server.entity.PlatformProfile;
import com.algonix.server.entity.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlatformProfileRepository extends JpaRepository<PlatformProfile, UUID> {

    Optional<PlatformProfile> findByUserIdAndPlatform(UUID userId, PlatformType platform);
}
