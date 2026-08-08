package com.algonix.server.service;

import com.algonix.server.dto.PlatformSyncData;
import com.algonix.server.dto.SolvedProblemData;
import com.algonix.server.dto.SubmissionCalendarData;
import com.algonix.server.dto.SyncedContestData;
import com.algonix.server.entity.PlatformProfile;
import com.algonix.server.entity.PlatformType;

import java.util.List;

public interface CodingPlatformService {

    PlatformType getPlatform();

    PlatformSyncData sync(String username);

    PlatformProfile fetchProfile(String username);

    List<SyncedContestData> fetchContests(String username);

     SubmissionCalendarData fetchSubmissionHistory(String username);

    List<SolvedProblemData> fetchSolvedProblems(String username);

    void syncProfile(Long userId);
}