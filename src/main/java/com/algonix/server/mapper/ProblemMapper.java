package com.algonix.server.mapper;

import com.algonix.server.dto.request.CreateProblemRequest;
import com.algonix.server.dto.response.ProblemResponse;
import com.algonix.server.dto.response.UserProblemResponse;
import com.algonix.server.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

//@Mapper(componentModel = "spring")
@Component
public class ProblemMapper {

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
                .companies(problem.getCompanies() != null ? problem.getCompanies() : List.of())
                .patterns(problem.getPatterns() != null ? problem.getPatterns() : List.of())
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
        problem.setCompanies(request.getCompanies() != null ? request.getCompanies() : List.of());
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
                .companies(problem.getCompanies() != null ? problem.getCompanies() : List.of())
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
}
