package com.algonix.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncedContestData {

    private String platformContestId;

    private String contestName;

    private String contestUrl;

    private LocalDateTime contestDate;

    private Integer rank;

    private Integer rating;

    private Integer ratingChange;

    private Integer solvedProblems;
}