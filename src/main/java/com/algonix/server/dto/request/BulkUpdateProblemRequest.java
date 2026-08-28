package com.algonix.server.dto.request;

import com.algonix.server.entity.ProblemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class BulkUpdateProblemRequest {

    @NotNull
    private UUID id;

    // Optional - status to set (e.g., SOLVED)
    private ProblemStatus status;

    // Optional - solved date (for sync imports)
    private LocalDate solvedDate;
}

