package com.algonix.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolvedProblemData {

    private String platformProblemId;

    private String title;

    private String problemUrl;

    private String difficulty;

    private List<String> tags;

    private LocalDateTime solvedAt;

    private Integer submissionCount;
}