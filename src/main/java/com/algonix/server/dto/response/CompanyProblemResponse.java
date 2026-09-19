package com.algonix.server.dto.response;

import com.algonix.server.entity.Difficulty;
import com.algonix.server.entity.PreparationBucket;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
public class CompanyProblemResponse {

    private UUID id;
    private String title;
    private String url;
    private Difficulty difficulty;
    private List<String> tags;
    private List<String> patterns;

    private String company;
    private PreparationBucket preparationBucket;
}
