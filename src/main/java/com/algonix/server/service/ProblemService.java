package com.algonix.server.service;

import com.algonix.server.dto.request.CreateProblemRequest;
import com.algonix.server.dto.request.UpdateUserProblemRequest;
import com.algonix.server.dto.request.UserProblemStatusResponse;
import com.algonix.server.dto.response.BulkImportResponse;
import com.algonix.server.dto.response.ProblemResponse;
import com.algonix.server.dto.response.UserProblemResponse;
import com.algonix.server.entity.ProblemStatus;

import java.util.List;
import java.util.UUID;

public interface ProblemService {

    ProblemResponse createProblem(
            UUID userId,
            CreateProblemRequest request);

    ProblemResponse getProblem(UUID problemId);

    List<ProblemResponse> getAllProblems(UUID userId);

    void deleteProblem(UUID problemId,UUID userId);

    ProblemResponse updateProblem(UUID problemId,UUID userID ,CreateProblemRequest request);

    List<ProblemResponse> getAllProblems();

    // Bulk and sync related operations
    List<ProblemResponse> upsertProblems(UUID userId, List<CreateProblemRequest> requests);

    BulkImportResponse bulkImportProblems(UUID userId, List<CreateProblemRequest> requests);

    UserProblemResponse updateUserProblem(UUID problemId, UUID userId, UpdateUserProblemRequest request);

     void updateStatus(UUID userId, UUID problemId, ProblemStatus status);

     List<ProblemResponse> getAllProblemsForUser();

     List<UserProblemResponse> getAllProblemsForUser(UUID userId);

    List<UserProblemStatusResponse> getUserStatuses(UUID userId);
}
