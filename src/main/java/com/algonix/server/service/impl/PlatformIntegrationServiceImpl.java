package com.algonix.server.service.impl;

import com.algonix.server.config.CodingPlatformFactory;
import com.algonix.server.dto.ConnectProfileRequest;
import com.algonix.server.dto.PlatformSyncData;
import com.algonix.server.entity.PlatformProfile;
import com.algonix.server.entity.PlatformType;
import com.algonix.server.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PlatformIntegrationServiceImpl implements PlatformIntegrationService {

    private final CodingPlatformFactory codingPlatformFactory;
    private final PlatformProfileService platformProfileService;

    @Override
    public void connectProfile(UUID userId, ConnectProfileRequest request) {

        // Get platform implementation
        CodingPlatformService platformService =
                codingPlatformFactory.getService(request.getPlatform());

        // Fetch latest data from external platform
        PlatformSyncData syncData =
                platformService.sync(request.getUsername());

        // Save everything
        persist(userId, syncData);
    }

    @Override
    public void syncProfile(UUID userId, PlatformType platform) {

        // Read stored username
        String username =
                platformProfileService.getPlatformUsername(userId, platform);

        // Get implementation
        CodingPlatformService platformService =
                codingPlatformFactory.getService(platform);

        // Fetch latest data
        PlatformSyncData syncData =
                platformService.sync(username);

        // Update database
        persist(userId, syncData);
    }

    /**
     * Persist all synchronized data.
     */
    private PlatformProfile persist(UUID userId, PlatformSyncData syncData) {

        // 1. Profile
        if (syncData.getProfile() != null) {

            return platformProfileService.saveOrUpdateProfile(
                    userId,
                    syncData.getProfile()
            );
        }

        /*// 2. Solved Problems
        if (syncData.getSolvedProblems() != null &&
                !syncData.getSolvedProblems().isEmpty()) {

            problemService.syncProblems(
                    userId,
                    syncData.getSolvedProblems()
            );
        }

        // 3. Contests
        if (syncData.getContests() != null &&
                !syncData.getContests().isEmpty()) {

            contestService.syncContests(
                    userId,
                    syncData.getContests()
            );
        }*/

        // 4. Badges (Future)
        /*
        if (syncData.getBadges() != null &&
                !syncData.getBadges().isEmpty()) {

            badgeService.syncBadges(
                    userId,
                    syncData.getBadges()
            );
        }
        */

        // 5. Submission Calendar (Future)
        /*
        if(syncData.getSubmissionCalendar()!=null){
            ...
        }
        */
        return null;
    }
}