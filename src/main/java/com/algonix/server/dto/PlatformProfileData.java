package com.algonix.server.dto;

import com.algonix.server.entity.PlatformType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformProfileData {

    private PlatformType platform;

    private String platformUsername;

    private String displayName;

    private String avatarUrl;

    private Integer totalSolved;

    private Integer easySolved;

    private Integer mediumSolved;

    private Integer hardSolved;

    private Integer totalSubmissions;

    private Integer activeDays;

    private Integer currentStreak;

    private Integer maxStreak;

    private Integer contestAttended;

    private Integer contestRating;

    private Integer globalRanking;

    private Double acceptanceRate;
}
