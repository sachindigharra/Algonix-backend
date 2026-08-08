package com.algonix.server.dto;

import com.algonix.server.entity.Difficulty;
import com.algonix.server.entity.PlatformType;
import com.algonix.server.entity.ProblemSheet;
import com.algonix.server.entity.ProblemStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateProblemRequest {

    @NotBlank
    private String title;

    private PlatformType platform;

    private Difficulty difficulty;

    private ProblemStatus status;

    private List<String> tags;

    private List<String> companies;

    private List<String> patterns;

    private String notes;

    private List<String> approaches;

    private String timeComplexity;

    private String spaceComplexity;

    private String url;

    private ProblemSheet sheet;

    private List<LocalDate> revisionDates;
}
