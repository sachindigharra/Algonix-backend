package com.algonix.server.service.impl;

import com.algonix.server.dto.CreateProblemRequest;
import com.algonix.server.dto.ProblemResponse;
import com.algonix.server.entity.Problem;
import com.algonix.server.entity.User;
import com.algonix.server.exception.ResourceNotFoundException;
import com.algonix.server.repository.ProblemRepository;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.service.ProblemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    @Override
    public ProblemResponse createProblem(
            UUID userId,
            CreateProblemRequest request) {

        log.info("Creating problem {}", request.getTitle());

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        Problem problem = new Problem();

        problem.setUser(user);
        problem.setTitle(request.getTitle());
        problem.setPlatform(request.getPlatform());
        problem.setDifficulty(request.getDifficulty());
        problem.setStatus(request.getStatus());

        Problem saved = problemRepository.save(problem);

        log.info("Problem created successfully {}", saved.getId());

        return map(saved);
    }

    @Override
    public ProblemResponse getProblem(UUID id) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Problem not found"));

        return map(problem);
    }

    @Override
    public List<ProblemResponse> getAllProblems(UUID userId) {
        return List.of();
    }

    @Override
    public void deleteProblem(UUID id) {

    }

    private ProblemResponse map(Problem problem) {

        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .platform(problem.getPlatform().name())
                .difficulty(problem.getDifficulty().name())
                .status(problem.getStatus().name())
                .build();
    }
}
