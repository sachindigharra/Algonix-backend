package com.algonix.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeData {

    private String badgeId;

    private String badgeName;

    private String badgeIconUrl;

    private LocalDate earnedDate;

    private String description;
}