package com.algonix.server.dto;

import lombok.*;

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
}
