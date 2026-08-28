package com.algonix.server.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserProblemResponse {

    private UUID userProblemId;

    // Problem Metadata
    private UUID problemId;
    private String title;
    private String url;
    private String platform;
    private String difficulty;
    private List<String> tags;
    private List<String> companies;
    private List<String> patterns;

    // UserProblem Data
    private String status;
    private List<String> approaches;
    private String timeComplexity;
    private String spaceComplexity;
    private String sheet;
    private List<LocalDate> revisionDates;
    private String notes;
    private LocalDate solvedDate;
}
