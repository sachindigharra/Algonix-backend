package com.algonix.server.client;

import com.algonix.server.dto.UserProfileResponse;

public interface LeetCodeGraphQLClientInter {
    UserProfileResponse getProfile(String username);

   /* ContestRankingResponse getContestRanking(String username);

    List<BadgeResponse> getBadges(String username);

    SubmissionCalendarResponse getSubmissionCalendar(String username);

    SolvedProblemResponse getSolvedProblems(String username);*/
}
