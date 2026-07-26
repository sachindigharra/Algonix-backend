package com.algonix.server.service.impl;

import com.algonix.server.dto.BulkUpdateProblemRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public List<ProblemResponse> upsertProblems(UUID userId, List<CreateProblemRequest> requests) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Problem> existing = problemRepository.findByUserId(userId);
        Map<String, Problem> byTitle = existing.stream()
                .collect(Collectors.toMap(p -> p.getTitle().toLowerCase(), p -> p));

        List<ProblemResponse> result = new ArrayList<>();

        for (CreateProblemRequest req : requests) {
            String key = req.getTitle().toLowerCase();
            Problem p = byTitle.get(key);
            if (p == null) {
                p = problemMapper.toEntity(req);
                p.setUser(user);
            } else {
                // basic update behavior
                p.setDifficulty(req.getDifficulty());
                p.setPlatform(req.getPlatform());
                p.setStatus(req.getStatus());
                p.setUrl(req.getUrl());
            }
            Problem saved = problemRepository.save(p);
            result.add(problemMapper.toResponseDto(saved));
        }

        return result;
    }

    @Override
    public String bulkInsertProblems(UUID userId, List<CreateProblemRequest> requests) {
        log.info("Bulk inserting {} problems for user{} by uploading a csv file",requests.size(),userId);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("user not found with id: "+userId));
        // problem add by the user may be already present in system
        // If its present then we will update company fields otherwise we will create new problem
        List<Problem>problems = new ArrayList<>();
        for(CreateProblemRequest req:requests){
            Problem problem = problemRepository.findByTitleIgnoreCase(req.getTitle()).orElse(null);
            if(problem!=null){
                List<String>companies = problem.getCompanies();
                companies.add(req.getCompany());
                problem.setCompanies(new ArrayList<>(companies));
            }
            else{
                problem  = problemMapper.toEntity(req);
                problem.setUser(user);
            }
            problems.add(problem);
        }
        // Save all problems in bulk
        List<Problem> savedProblems = problemRepository.saveAll(problems);
        log.info("Bulk insert completed for {} problems for user {}",savedProblems.size(),userId);
        return "Bulk Insert Complete";

    }

    @Override
    public void bulkUpdateProblems(UUID userId, List<BulkUpdateProblemRequest> requests) {
        for (BulkUpdateProblemRequest req : requests) {
            problemRepository.findById(req.getId()).ifPresent(problem -> {
                if (problem.getUser() == null || !problem.getUser().getId().equals(userId)) {
                    // skip updates for problems not owned by the user
                    return;
                }
                if (req.getStatus() != null) {
                    problem.setStatus(req.getStatus());
                }
                if (req.getSolvedDate() != null) {
                    problem.setSolvedDate(req.getSolvedDate());
                }
                problemRepository.save(problem);
            });
        }
    }

}
