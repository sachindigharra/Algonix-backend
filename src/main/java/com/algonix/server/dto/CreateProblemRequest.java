package com.algonix.server.dto;

import com.algonix.server.entity.Difficulty;
import com.algonix.server.entity.PlatformType;
import com.algonix.server.entity.ProblemStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProblemRequest {

    @NotBlank
    private String title;

    private PlatformType platform;

    private Difficulty difficulty;

    private ProblemStatus status;

    private String Company;

    private String url;
}
