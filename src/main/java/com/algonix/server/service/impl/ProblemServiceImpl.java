package com.algonix.server.service.impl;

import com.algonix.server.dto.CreateProblemRequest;
import com.algonix.server.dto.ProblemResponse;
import com.algonix.server.entity.Problem;
import com.algonix.server.entity.User;
import com.algonix.server.exception.ResourceNotFoundException;
import com.algonix.server.mapper.ProblemMapper;
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
    private final ProblemMapper problemMapper;

    @Override
    public ProblemResponse createProblem(UUID userId, CreateProblemRequest request) {

        log.info("Creating problem {}", request.getTitle());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Problem problem = problemMapper.toEntity(request);
        problem.setUser(user);
        Problem saved = problemRepository.save(problem);

        log.info("Problem created successfully {}", saved.getId());

        return problemMapper.toResponseDto(problem);
    }

    @Override
    public ProblemResponse getProblem(UUID id) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Problem not found"));

        return problemMapper.toResponseDto(problem);
    }

    @Override
    public List<ProblemResponse> getAllProblems(UUID userId) {
        List<Problem> problems = problemRepository.findByUserId(userId);
        log.info("Fetched {} problems for user {}", problems.size(), userId);
        return problems.stream()
                .map(problemMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ProblemResponse> getAllProblems() {
        List<Problem> problems = problemRepository.findAll();
        log.info("Fetched {} problems", problems.size());
        return problems.stream()
                .map(problemMapper::toResponseDto)
                .toList();
    }

    @Override
    public ProblemResponse updateProblem(UUID id, CreateProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setPlatform(request.getPlatform());
        problem.setStatus(request.getStatus());
        problem.setUrl(request.getUrl());

        Problem updated = problemRepository.save(problem);
        log.info("Updated problem {}", id);
        return problemMapper.toResponseDto(updated);
    }

    @Override
    public void deleteProblem(UUID id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        log.info("Deleting problem {}", id);
        problemRepository.delete(problem);
    }

}
