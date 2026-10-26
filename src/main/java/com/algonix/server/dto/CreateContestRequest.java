package com.algonix.server.dto;

import com.algonix.server.entity.PlatformType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateContestRequest {

    @NotBlank
    private String name;

    private PlatformType platform;

    private Instant startTime;

    private Integer durationMinutes;

    private String url;

    private Boolean participated;

    private Integer rank;

    private Integer problemsSolved;

    private Integer ratingChange;

    private String notes;
}

