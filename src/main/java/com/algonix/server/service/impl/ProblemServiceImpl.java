package com.algonix.server.service.impl;

import com.algonix.server.dto.request.CreateProblemRequest;
import com.algonix.server.dto.request.UpdateUserProblemRequest;
import com.algonix.server.dto.request.UserProblemStatusResponse;
import com.algonix.server.dto.response.BulkImportResponse;
import com.algonix.server.dto.response.CompanyProblemResponse;
import com.algonix.server.dto.response.ProblemResponse;
import com.algonix.server.dto.response.UserProblemResponse;
import com.algonix.server.entity.*;
import com.algonix.server.exception.ResourceNotFoundException;
import com.algonix.server.mapper.ProblemMapper;
import com.algonix.server.repository.CompanyRepository;
import com.algonix.server.repository.ProblemCompanyMetadataRepository;
import com.algonix.server.repository.ProblemRepository;
import com.algonix.server.repository.UserProblemRepository;
import com.algonix.server.repository.UserRepository;
import com.algonix.server.service.ProblemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ProblemMapper problemMapper;
    private final UserProblemRepository userProblemRepository;
    private final ProblemCompanyMetadataRepository metadataRepository;

    @Override
    public ProblemResponse createProblem(UUID userId, CreateProblemRequest request) {

        log.info("Creating problem {}", request.getTitle());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Problem problem = problemMapper.toEntity(request);
        problem.setCreatedBy(user);
        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setPlatform(request.getPlatform());
        problem.setUrl(request.getUrl());
        problem.setTags(request.getTags() != null ? request.getTags() : List.of());
        Problem saved = problemRepository.save(problem);

        log.info("Problem created successfully {}", saved.getId());

        return problemMapper.toResponseDto(problem);
    }

    @Override
    public ProblemResponse getProblem(UUID problemId) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Problem not found"));

        return problemMapper.toResponseDto(problem);
    }

    @Override
    public List<ProblemResponse> getAllProblems(UUID userId) {
        List<Problem> problems = problemRepository.findByCreatedById(userId);
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
    public ProblemResponse updateProblem(UUID problemId,UUID userId, CreateProblemRequest request) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Problem not found"));

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        boolean isOwner = problem.getCreatedBy()
                .getId()
                .equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to update this problem"
            );
        }
        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setPlatform(request.getPlatform());
        problem.setUrl(request.getUrl());

        Problem updated = problemRepository.save(problem);
        log.info("Updated problem {}", problemId);
        return problemMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteProblem(UUID problemId,UUID userId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        boolean isOwner = problem.getCreatedBy()
                .getId()
                .equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this problem: "+problem.getId()
            );
        }
        log.info("Deleting problem {}", problemId);
        userProblemRepository.deleteByProblemId(problemId);
        problemRepository.deleteById(problemId);
    }

    @Override
    public List<ProblemResponse> upsertProblems(UUID userId, List<CreateProblemRequest> requests) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Problem> existing = problemRepository.findByCreatedById(userId);
        Map<String, Problem> byTitle = existing.stream()
                .collect(Collectors.toMap(p -> p.getTitle().toLowerCase(), p -> p));

        List<ProblemResponse> result = new ArrayList<>();

        for (CreateProblemRequest req : requests) {
            String key = req.getTitle().toLowerCase();
            Problem p = byTitle.get(key);
            if (p == null) {
                p = problemMapper.toEntity(req);
                p.setCreatedBy(user);
            } else {
                // basic update behavior
                p.setDifficulty(req.getDifficulty());
                p.setPlatform(req.getPlatform());
                p.setUrl(req.getUrl());
            }
            Problem saved = problemRepository.save(p);
            result.add(problemMapper.toResponseDto(saved));
        }

        return result;
    }

    @Override
    @Transactional
    public BulkImportResponse bulkImportProblems(
            UUID adminId,
            String company,
            String Bucket,
            List<CreateProblemRequest> requests) {

        log.info(
                "Starting bulk problem import: adminId={}, totalProblems={}",
                adminId,
                requests.size()
        );

        User admin = userRepository.findById(adminId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin not found with id: " + adminId
                        ));

        int created = 0;
        int updated = 0;
        int skipped = 0;

        List<String> errors = new ArrayList<>();

        for (CreateProblemRequest request : requests) {

            try {
                Optional<Problem> existingProblem =
                        problemRepository.findByTitleIgnoreCase(
                                request.getTitle().trim()
                        );
                Problem problem;
                if (existingProblem.isPresent()) {

                    problem = existingProblem.get();

                    mergeCompanies(problem, request.getCompanies());

                    problemRepository.save(problem);

                    updated++;

                } else {

                     problem = problemMapper.toEntity(request);

                    problem.setCreatedBy(admin);

                    problemRepository.save(problem);

                    created++;
                }
                savePreparationMetadata(problem, company, Bucket != null ? PreparationBucket.valueOf(Bucket.toUpperCase()) : null);
            } catch (Exception e) {

                log.error(
                        "Failed to import problem: {}",
                        request.getTitle(),
                        e
                );

                errors.add(request.getTitle());
            }
        }

        log.info(
                "Bulk problem import completed: created={}, updated={}, failed={}",
                created,
                updated,
                errors.size()
        );

        return BulkImportResponse.builder()
                .total(requests.size())
                .created(created)
                .updated(updated)
                .failed(errors.size())
                .failedProblems(errors)
                .build();
    }

    private void mergeCompanies(
            Problem problem,
            List<String> importedCompanies) {

        if (importedCompanies == null || importedCompanies.isEmpty()) {
            return;
        }

        Set<String> companyNames = new LinkedHashSet<>();

        if (problem.getCompanies() != null) {
            problem.getCompanies().stream()
                    .map(Company::getName)
                    .forEach(companyNames::add);
        }

        importedCompanies.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .forEach(companyNames::add);

        List<Company> mergedCompanies = companyNames.stream()
                .map(name -> companyRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> {
                            Company company = new Company();
                            company.setName(name);
                            return companyRepository.save(company);
                        }))
                .toList();

        problem.setCompanies(new ArrayList<>(mergedCompanies));
    }
    private void savePreparationMetadata(
            Problem problem,
            String company,
            PreparationBucket bucket) {

        if (bucket == null) {
            return;
        }

        Company companyEntity = companyRepository.findByNameIgnoreCase(company)
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(company);
                    return companyRepository.save(newCompany);
                });

        Optional<ProblemCompanyMetadata> existing =
                metadataRepository.findByProblem_IdAndCompany_NameIgnoreCase(
                        problem.getId(),
                        companyEntity.getName()
                );

        if (existing.isPresent()) {
            existing.get().setPreparationBucket(bucket);
            return;
        }

        ProblemCompanyMetadata metadata =
                new ProblemCompanyMetadata();

        metadata.setProblem(problem);
        metadata.setCompany(companyEntity);
        metadata.setPreparationBucket(bucket);

        metadataRepository.save(metadata);
    }
    public UserProblemResponse updateUserProblem(
            UUID problemId,
            UUID userId,
            UpdateUserProblemRequest request) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Problem not found"));

        UserProblem userProblem =
                userProblemRepository
                        .findByUserIdAndProblemId(userId, problemId)
                        .orElseGet(() -> {

                            UserProblem newUserProblem = new UserProblem();

                            newUserProblem.setUser(
                                    userRepository.getReferenceById(userId)
                            );

                            newUserProblem.setProblem(problem);
                            newUserProblem.setStatus(
                                    ProblemStatus.TODO
                            );

                            return newUserProblem;
                        });

        if (request.getStatus() != null) {
            userProblem.setStatus(request.getStatus());
        }

        if (request.getNotes() != null) {
            userProblem.setNotes(request.getNotes());
        }

        if (request.getApproaches() != null) {
            userProblem.setApproaches(request.getApproaches());
        }

        if (request.getTimeComplexity() != null) {
            userProblem.setTimeComplexity(
                    request.getTimeComplexity()
            );
        }

        if (request.getSpaceComplexity() != null) {
            userProblem.setSpaceComplexity(
                    request.getSpaceComplexity()
            );
        }

        if (request.getSheet() != null) {
            userProblem.setSheet(request.getSheet());
        }

        if (request.getSolvedDate() != null) {
            userProblem.setSolvedDate(
                    request.getSolvedDate()
            );
        }

        if (request.getRevisionDates() != null) {
            userProblem.setRevisionDates(
                    request.getRevisionDates()
            );
        }
        userProblemRepository.save(userProblem);

        return problemMapper.toUserProblemResponse(userProblem);
    }

    public void updateStatus(UUID userId, UUID problemId, ProblemStatus status) {

        UserProblem userProblem =
                userProblemRepository
                        .findByUserIdAndProblemId(userId, problemId)
                        .orElseGet(() -> {

                            UserProblem newUserProblem = new UserProblem();
                            User user = userRepository.findById(userId)
                                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                            Problem problem = problemRepository.findById(problemId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

                            newUserProblem.setProblem(problem);
                            newUserProblem.setUser(user);
                            newUserProblem.setStatus(
                                    ProblemStatus.TODO
                            );

                            return newUserProblem;
                        });
        userProblem.setStatus(status);

        if (status == ProblemStatus.SOLVED) {
            userProblem.setSolvedDate(LocalDate.now());
        } else {
            userProblem.setSolvedDate(null);
        }

        userProblemRepository.save(userProblem);
    }

    @Override
    public List<ProblemResponse> getAllProblemsForUser() {

        List<Problem> problems = problemRepository.findAll();
        return problems.stream()
                .map(problemMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<UserProblemResponse> getAllProblemsForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Problem> problems= problemRepository.findAll();
        List<UserProblemResponse> problemResponses = new ArrayList<>();
        for(Problem problem : problems) {
            UserProblem userProblem = userProblemRepository.findByUserIdAndProblemId(userId, problem.getId()).orElse(null);
            if (userProblem != null) {
                UserProblemResponse response = problemMapper.toUserProblemResponse(userProblem);
                problemResponses.add(response);
            }
        }

        return problemResponses;
    }

    public List<UserProblemStatusResponse> getUserStatuses(UUID userId) {

        return userProblemRepository.findByUserId(userId)
                .stream()
                .map(userProblem ->
                        new UserProblemStatusResponse(
                                userProblem.getProblem().getId(),
                                userProblem.getStatus().toString()
                        )
                )
                .toList();
    }

    @Transactional
    public List<CompanyProblemResponse> getProblemsByCompany(String company) {

        company = company.trim().toUpperCase();

        List<Problem> problems =
                problemRepository.findByCompanies_NameIgnoreCase(company);

        List<ProblemCompanyMetadata> metadata =
                metadataRepository.findByCompany_NameIgnoreCaseAndProblemIn(
                        company,
                        problems
                );

        Map<UUID, PreparationBucket> bucketMap =
                metadata.stream()
                        .collect(Collectors.toMap(
                                m -> m.getProblem().getId(),
                                ProblemCompanyMetadata::getPreparationBucket
                        ));

        String finalCompany = company;
        return problems.stream()
                .map(problem -> {
                    PreparationBucket bucket =
                            bucketMap.getOrDefault(
                                    problem.getId(),
                                    PreparationBucket.MORE_THAN_SIX_MONTHS
                            );

                    return problemMapper.toCompanyProblemResponse(
                            problem,
                            finalCompany,
                            bucket
                    );
                })
                .toList();
    }
}
