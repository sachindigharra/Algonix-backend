package com.algonix.server.service;

import com.algonix.server.dto.BulkUpdateProblemRequest;
import com.algonix.server.dto.CreateProblemRequest;
import com.algonix.server.dto.ProblemResponse;

import java.util.List;
import java.util.UUID;

public interface ProblemService {

    ProblemResponse createProblem(
            UUID userId,
            CreateProblemRequest request);

    ProblemResponse getProblem(UUID id);

    List<ProblemResponse> getAllProblems(UUID userId);

    void deleteProblem(UUID id);

    ProblemResponse updateProblem(UUID id, CreateProblemRequest request);

    List<ProblemResponse> getAllProblems();

    // Bulk and sync related operations
    List<ProblemResponse> upsertProblems(UUID userId, List<CreateProblemRequest> requests);

    String bulkInsertProblems(UUID userId, List<CreateProblemRequest> requests);

    void bulkUpdateProblems(UUID userId, List<BulkUpdateProblemRequest> requests);
}
