package com.algonix.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalJob {
    private String externalJobId;
    private String title;
    private String jobUrl;
    private String location;
    private String country;
    private String employmentType;
    private Integer experienceMin;
    private Integer experienceMax;
    private String description;
    private List<String> skills;
    private Instant postedAt;
    private Instant expiresAt;
    private String source; // provider name
}
