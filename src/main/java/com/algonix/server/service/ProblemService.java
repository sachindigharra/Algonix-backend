package com.algonix.server.service;

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
}
