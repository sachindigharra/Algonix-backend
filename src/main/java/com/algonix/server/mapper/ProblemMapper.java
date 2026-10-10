package com.algonix.server.mapper;

import com.algonix.server.dto.request.CreateProblemRequest;
import com.algonix.server.dto.response.CompanyProblemResponse;
import com.algonix.server.dto.response.ProblemResponse;
import com.algonix.server.dto.response.UserProblemResponse;
import com.algonix.server.entity.*;
import com.algonix.server.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//@Mapper(componentModel = "spring")
@Component
@RequiredArgsConstructor
public class ProblemMapper {

    private final CompanyRepository companyRepository;

    // Problem Entity Mapping
    public ProblemResponse toResponseDto(Problem problem){
        if (problem == null) {
            return null;
        }

        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .url(problem.getUrl() != null ? problem.getUrl() : "")
                .platform(problem.getPlatform() != null ? problem.getPlatform().toString().toLowerCase() : "leetcode")
                .difficulty(problem.getDifficulty() != null ? problem.getDifficulty().toString().toLowerCase() : "medium")
                .tags(problem.getTags() != null ? problem.getTags() : List.of())
                .companies(problem.getCompanies() != null
                        ? problem.getCompanies().stream().map(Company::getName).toList()
                        : List.of())
                .patterns(problem.getPatterns() != null ? problem.getPatterns() : List.of())
                .build();
    }
    // DTO Projection Mapping (for bulk queries)
    public ProblemResponse toResponseDto(Problem problem, List<String> tags,
                                         List<String> patterns, List<String> companies) {
        if (problem == null) {
            return null;
        }

        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .url(problem.getUrl() != null ? problem.getUrl() : "")
                .platform(problem.getPlatform() != null ? problem.getPlatform().toString().toLowerCase() : "leetcode")
                .difficulty(problem.getDifficulty() != null ? problem.getDifficulty().toString().toLowerCase() : "medium")
                .tags(tags)
                .companies(companies)
                .patterns(patterns)
                .build();
    }

    public Problem toEntity(CreateProblemRequest request){
        if (request == null) {
            return null;
        }

        Problem problem = new Problem();

        // Metadata fields
        problem.setTitle(request.getTitle());
        problem.setUrl(request.getUrl());
        problem.setPlatform(request.getPlatform());
        problem.setDifficulty(request.getDifficulty());
        problem.setTags(request.getTags() != null ? request.getTags() : List.of());
        problem.setCompanies(resolveCompanies(request.getCompanies()));
        problem.setPatterns(request.getPatterns() != null ? request.getPatterns() : List.of());
        problem.setVisibility(ProblemVisibility.PRIVATE);

        return problem;
    }

    // UserProblem Entity Mapping
    public UserProblemResponse toUserProblemResponse(UserProblem userProblem){
        if (userProblem == null) {
            return null;
        }

        Problem problem = userProblem.getProblem();
        return UserProblemResponse.builder()
                .userProblemId(userProblem.getId())
                .problemId(problem.getId())
                .title(problem.getTitle())
                .url(problem.getUrl() != null ? problem.getUrl() : "")
                .platform(problem.getPlatform() != null ? problem.getPlatform().toString().toLowerCase() : "leetcode")
                .difficulty(problem.getDifficulty() != null ? problem.getDifficulty().toString().toLowerCase() : "medium")
                .tags(problem.getTags() != null ? problem.getTags() : List.of())
                .companies(problem.getCompanies() != null
                        ? problem.getCompanies().stream().map(Company::getName).toList()
                        : List.of())
                .patterns(problem.getPatterns() != null ? problem.getPatterns() : List.of())
                // UserProblem fields
                .status(userProblem.getStatus() != null ? userProblem.getStatus().toString().toLowerCase() : "todo")
                .approaches(userProblem.getApproaches() != null ? userProblem.getApproaches() : List.of())
                .timeComplexity(userProblem.getTimeComplexity() != null ? userProblem.getTimeComplexity() : "")
                .spaceComplexity(userProblem.getSpaceComplexity() != null ? userProblem.getSpaceComplexity() : "")
                .sheet(userProblem.getSheet() != null ? userProblem.getSheet().toString().toLowerCase() : "none")
                .revisionDates(userProblem.getRevisionDates() != null ? userProblem.getRevisionDates() : List.of())
                .notes(userProblem.getNotes() != null ? userProblem.getNotes() : "")
                .solvedDate(userProblem.getSolvedDate())
                .build();
    }

    public UserProblem toUserProblemEntity(CreateProblemRequest request, Problem problem, User user){
        if (request == null || problem == null || user == null) {
            return null;
        }

        UserProblem userProblem = new UserProblem();
        userProblem.setUser(user);
        userProblem.setProblem(problem);
        

        
        // Parse sheet
        if (request.getSheet() != null) {
            try {
                userProblem.setSheet(ProblemSheet.valueOf(request.getSheet().toUpperCase()));
            } catch (IllegalArgumentException e) {
                userProblem.setSheet(null);
            }
        }
        return userProblem;
    }

    public CompanyProblemResponse toCompanyProblemResponse(
            Problem problem,
            String company,
            PreparationBucket bucket) {

        return CompanyProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .url(problem.getUrl())
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags())
                .patterns(problem.getPatterns())
                .company(company)
                .preparationBucket(bucket)
                .build();
    }

    private List<Company> resolveCompanies(List<String> companyNames) {
        if (companyNames == null || companyNames.isEmpty()) {
            return List.of();
        }

        return companyNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .distinct()
                .map(name -> companyRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> {
                            Company company = new Company();
                            company.setName(name);
                            return companyRepository.save(company);
                        }))
                .collect(Collectors.toList());
    }
}
