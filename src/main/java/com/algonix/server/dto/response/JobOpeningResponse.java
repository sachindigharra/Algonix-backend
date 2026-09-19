package com.algonix.server.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobOpeningResponse {
    private UUID id;
    private UUID companyId;
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
    private String source;
    private String status;
    private Instant fetchedAt;
}
