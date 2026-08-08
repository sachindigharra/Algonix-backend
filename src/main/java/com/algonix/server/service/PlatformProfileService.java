package com.algonix.server.service;

import com.algonix.server.dto.BadgeData;
import com.algonix.server.dto.PlatformProfileData;
import com.algonix.server.dto.SolvedProblemData;
import com.algonix.server.dto.SyncedContestData;
import com.algonix.server.entity.PlatformProfile;
import com.algonix.server.entity.PlatformType;

import java.util.List;
import java.util.UUID;

public interface PlatformProfileService {

    /**
     * Save a newly connected platform profile.
     */
    PlatformProfile connectProfile(UUID userId, PlatformProfileData profile);

    /**
     * Update profile statistics after synchronization.
     */
    PlatformProfile updateProfile(UUID userId, PlatformProfileData profile);

    /**
     * Save or update solved problems.
     */
    void syncSolvedProblems(
            UUID userId,
            List<SolvedProblemData> solvedProblems
    );

    /**
     * Save or update contests.
     */
    void syncContests(
            UUID userId,
            List<SyncedContestData> contests
    );

    /**
     * Save or update badges.
     */
    void syncBadges(
            UUID userId,
            List<BadgeData> badges
    );

    /**
     * Get connected username/handle.
     */
    String getPlatformUsername(
            UUID userId,
            PlatformType platform
    );

    PlatformProfile saveOrUpdateProfile(UUID userId,PlatformProfileData profile);

}
