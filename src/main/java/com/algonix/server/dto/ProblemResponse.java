package com.algonix.server.dto;

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
public class ProblemResponse {

    private UUID id;

    private String title;

    private String platform;

    private String difficulty;

    private String status;

    private List<String> tags;

    private List<String> companies;

    private List<String> patterns;

    private String notes;

    private List<String> approaches;

    private String timeComplexity;

    private String spaceComplexity;

    private String url;

    private String sheet;

    private List<LocalDate> revisionDates;
}
