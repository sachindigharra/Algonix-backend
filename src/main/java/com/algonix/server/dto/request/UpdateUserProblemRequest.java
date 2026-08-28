package com.algonix.server.dto.request;

import com.algonix.server.entity.ProblemSheet;
import com.algonix.server.entity.ProblemStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdateUserProblemRequest {

    private ProblemStatus status;

    private String notes;

    private List<String> approaches;

    private String timeComplexity;

    private String spaceComplexity;

    private ProblemSheet sheet;

    private LocalDate solvedDate;

    private List<LocalDate> revisionDates;
}