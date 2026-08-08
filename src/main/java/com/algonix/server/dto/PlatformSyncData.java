package com.algonix.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSyncData {

    private PlatformProfileData profile;

    private List<SolvedProblemData> solvedProblems;

    private List<SyncedContestData> contests;

    private List<BadgeData> badges;

    private List<SubmissionCalendarData> submissionCalendar;
}