package com.algonix.server.dto.request;

import com.algonix.server.entity.Difficulty;
import com.algonix.server.entity.PlatformType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateProblemRequest {

    // Problem Metadata (Mandatory)
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "URL is required")
    private String url;

    @NotNull(message = "Platform is required")
    private PlatformType platform;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    // Problem Metadata (Optional)
    private List<String> tags;
    private List<String> companies;
    private List<String> patterns;
    private String sheet;


}
