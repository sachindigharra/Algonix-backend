package com.algonix.server.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ContestResponse {

    private UUID id;

    private String name;

    private String platform;

    private Instant startTime;

    private Integer durationMinutes;

    private String url;

    private Boolean participated;

    private Integer rank;

    private Integer problemsSolved;

    private Integer ratingChange;

    private String notes;
}

