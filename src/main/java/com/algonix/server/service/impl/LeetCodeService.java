package com.algonix.server.service.impl;

import com.algonix.server.client.LeetCodeGraphQLClient;
import com.algonix.server.dto.*;
import com.algonix.server.entity.PlatformProfile;
import com.algonix.server.entity.PlatformType;
import com.algonix.server.service.CodingPlatformService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class LeetCodeService implements CodingPlatformService {

    private final LeetCodeGraphQLClient client;
    @Override
    public PlatformType getPlatform() {
        return PlatformType.LEETCODE;
    }

    @Override
    public PlatformProfile fetchProfile(String username) {
        // Implement the logic to fetch the profile from LeetCode
        return null; // Replace with actual implementation
    }

    @Override
    public List<SyncedContestData> fetchContests(String username) {
        // Implement the logic to fetch contests from LeetCode
        return null; // Replace with actual implementation
    }

    @Override
    public SubmissionCalendarData fetchSubmissionHistory(String username) {
        return null;
    }

    @Override
    public List<SolvedProblemData> fetchSolvedProblems(String username) {
        return List.of();
    }

    @Override
    public void syncProfile(Long userId) {

    }

    @Override
    public PlatformSyncData sync(String username) {
        return null;
    }

        // Fetch GraphQL response

//        LeetCodeGraphQLResponse response =
//                client.getProfile(username);
//
//        return mapper.toPlatformSyncData(response);


}
