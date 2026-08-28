package com.algonix.server.controller;

import com.algonix.server.dto.request.BulkUpdateProblemRequest;
import com.algonix.server.dto.request.CreateProblemRequest;
import com.algonix.server.dto.request.UserProblemStatusResponse;
import com.algonix.server.dto.request.UpdateUserProblemRequest;
import com.algonix.server.dto.response.BulkImportResponse;
import com.algonix.server.dto.response.ProblemResponse;
import com.algonix.server.dto.response.UserProblemResponse;
import com.algonix.server.entity.ProblemStatus;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.security.CustomUserPrincipal;
import com.algonix.server.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final UserRepository userRepository;


    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(Authentication authentication,
                                                         @Valid @RequestBody CreateProblemRequest request) {
        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        UUID userId = principal.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(problemService.createProblem(userId, request));
    }

    @GetMapping("/{problemId}")
    public ResponseEntity<ProblemResponse> getProblem(
            @PathVariable UUID problemId) {

        return ResponseEntity.ok(
                problemService.getProblem(problemId)
        );
    }
    // that method is used to get all problems support guest user also
    @GetMapping
    public ResponseEntity<?> getAllProblems() {
        return ResponseEntity.ok(
                problemService.getAllProblems());

    }
    @GetMapping("/mine")
    public ResponseEntity<List<ProblemResponse>> getAllProblemsForUser(Authentication authentication){
        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();
        UUID userId = principal.getUserId();
        return ResponseEntity.ok(problemService.getAllProblems(userId));
    }

    @PutMapping("/{problemId}")
    public ResponseEntity<ProblemResponse> updateProblem(@PathVariable UUID problemId,
                                                         Authentication authentication,
                                                         @Valid @RequestBody CreateProblemRequest request) {
        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();
        UUID userId = principal.getUserId();
        return ResponseEntity.ok(
                problemService.updateProblem(problemId,userId,request));
    }

    @DeleteMapping("/{problemId}")
    public ResponseEntity<UserProblemResponse> deleteProblem(@PathVariable UUID problemId,Authentication authentication) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();
        UUID userId = principal.getUserId();
        problemService.deleteProblem(problemId,userId);
        return ResponseEntity.ok(
                UserProblemResponse.builder()
                        .problemId(problemId)
                        .status("Problem deleted successfully")
                        .build()
        );
    }

    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("Health check passed");
    }

    @PostMapping("/upsert")
    public ResponseEntity<List<ProblemResponse>> upsertProblems(
            @Valid @RequestBody List<CreateProblemRequest> requests) {

        return ResponseEntity.ok(
                null);
    }

    @PostMapping("/bulk-import")
    public ResponseEntity<BulkImportResponse> bulkImport(
            @Valid @RequestBody List<CreateProblemRequest> requests,Authentication authentication) {
        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();
        UUID userId = principal.getUserId();
        return ResponseEntity.ok(
                problemService.bulkImportProblems(userId, requests)
        );
    }

    @PatchMapping("/bulk-update")
    public ResponseEntity<Void> bulkUpdate(
            @Valid @RequestBody List<BulkUpdateProblemRequest> requests) {

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{problemId}/tracking")
    public ResponseEntity<UserProblemResponse> updateTracking(
            @PathVariable UUID problemId,
            @RequestBody UpdateUserProblemRequest request,
            Authentication authentication) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        UUID userId = principal.getUserId();

        return ResponseEntity.ok(
                problemService.updateUserProblem(
                        problemId,
                        userId,
                        request
                )
        );
    }
    @PatchMapping("/{problemId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID problemId,
            @RequestBody UserProblemStatusResponse request,
            Authentication authentication) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        problemService.updateStatus(
                principal.getUserId(),
                problemId,
                ProblemStatus.valueOf(request.getStatus())
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<List<UserProblemStatusResponse>> getUserStatuses(
            Authentication authentication) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(
                problemService.getUserStatuses(principal.getUserId())
        );
    }

}