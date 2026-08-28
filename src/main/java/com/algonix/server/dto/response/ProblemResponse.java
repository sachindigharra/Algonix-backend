package com.algonix.server.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProblemResponse {

    private UUID id;

    private String title;

    private String url;

    private String platform;

    private String difficulty;

    private List<String> tags;

    private List<String> companies;

    private List<String> patterns;
}
