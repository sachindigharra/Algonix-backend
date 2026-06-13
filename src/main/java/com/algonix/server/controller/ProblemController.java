package com.algonix.server.controller;

import com.algonix.server.dto.CreateProblemRequest;
import com.algonix.server.dto.ProblemResponse;
import com.algonix.server.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(
            @RequestParam UUID userId,
            @Valid @RequestBody CreateProblemRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        problemService.createProblem(
                                userId,
                                request
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblem(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                problemService.getProblem(id)
        );
    }
    @GetMapping("/health")
    public ResponseEntity<String> gethealth() {
        return ResponseEntity.ok("Health check passed");
    }
}