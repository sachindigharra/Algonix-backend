package com.algonix.server.service.impl;

import com.algonix.server.dto.BadgeData;
import com.algonix.server.dto.PlatformProfileData;
import com.algonix.server.dto.SolvedProblemData;
import com.algonix.server.dto.SyncedContestData;
import com.algonix.server.entity.PlatformProfile;
import com.algonix.server.entity.PlatformType;
import com.algonix.server.entity.User;
import com.algonix.server.exception.ResourceNotFoundException;
import com.algonix.server.repository.PlatformProfileRepository;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.service.PlatformProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional

public class PlatformProfileServiceImpl implements PlatformProfileService {

    private final PlatformProfileRepository repository;
    private final UserRepository userRepository;

    @Override
    public PlatformProfile connectProfile(UUID userId,
                                          PlatformProfileData dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        PlatformProfile profile = PlatformProfile.builder()
                .user(user)
                .platform(dto.getPlatform())
                .platformUsername(dto.getPlatformUsername())
                .displayName(dto.getDisplayName())
                .avatarUrl(dto.getAvatarUrl())
                .build();

        updateStatistics(profile, dto);

        return repository.save(profile);
    }


    @Override
    public void syncSolvedProblems(UUID userId, List<SolvedProblemData> solvedProblems) {

    }

    @Override
    public void syncContests(UUID userId, List<SyncedContestData> contests) {

    }

    @Override
    public void syncBadges(UUID userId, List<BadgeData> badges) {

    }

    @Override
    public String getPlatformUsername(UUID userId, PlatformType platform) {
       PlatformProfile platformProfile = repository.findByUserIdAndPlatform(userId,platform)
               .orElseThrow(()-> new ResourceNotFoundException("Profile not found for userId: " + userId + " and platform: " + platform));
        return platformProfile.getPlatformUsername();
    }

    @Override
    public PlatformProfile saveOrUpdateProfile(UUID userId, PlatformProfileData profile) {
        return null;
    }

    @Override
    public PlatformProfile updateProfile(UUID userId,
                                         PlatformProfileData dto) {

        PlatformProfile profile = repository
                .findByUserIdAndPlatform(userId, dto.getPlatform())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setDisplayName(dto.getDisplayName());
        profile.setAvatarUrl(dto.getAvatarUrl());

        updateStatistics(profile, dto);

        return repository.save(profile);
    }



    private void updateStatistics(PlatformProfile profile,
                                  PlatformProfileData dto) {

        profile.setTotalSolved(dto.getTotalSolved());
        profile.setEasySolved(dto.getEasySolved());
        profile.setMediumSolved(dto.getMediumSolved());
        profile.setHardSolved(dto.getHardSolved());

        profile.setContestRating(dto.getContestRating());
        profile.setContestAttended(dto.getContestAttended());

        profile.setCurrentStreak(dto.getCurrentStreak());
        profile.setMaxStreak(dto.getMaxStreak());

        profile.setActiveDays(dto.getActiveDays());
        profile.setTotalSubmissions(dto.getTotalSubmissions());

        profile.setAcceptanceRate(dto.getAcceptanceRate());

        profile.setGlobalRanking(dto.getGlobalRanking());

        profile.setLastSyncedAt(Instant.now());
    }
}