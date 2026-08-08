package com.algonix.server.mapper;

import com.algonix.server.dto.CreateProblemRequest;
import com.algonix.server.dto.ProblemResponse;
import com.algonix.server.entity.Difficulty;
import com.algonix.server.entity.Problem;
import com.algonix.server.entity.ProblemStatus;
import org.springframework.stereotype.Component;

//@Mapper(componentModel = "spring")
@Component
public class ProblemMapper {

    public ProblemResponse toResponseDto(Problem problem){
        if (problem == null) {
            return null;
        }

        ProblemResponse response = new ProblemResponse();
        response.setId(problem.getId());
        response.setTitle(problem.getTitle());
        response.setPlatform(problem.getPlatform() != null ? problem.getPlatform().toString() : null);
        response.setDifficulty(problem.getDifficulty() != null ? problem.getDifficulty().toString() : null);
        response.setStatus(problem.getStatus() != null ? problem.getStatus().toString() : null);
        response.setTags(problem.getTags());
        response.setCompanies(problem.getCompanies());
        response.setPatterns(problem.getPatterns());
        response.setNotes(problem.getNotes());
        response.setApproaches(problem.getApproaches());
        response.setTimeComplexity(problem.getTimeComplexity());
        response.setSpaceComplexity(problem.getSpaceComplexity());
        response.setUrl(problem.getUrl());
        response.setSheet(problem.getSheet() != null ? problem.getSheet().toString() : null);
        response.setRevisionDates(problem.getRevisionDates());
        return response;
    }

    public Problem toEntity(CreateProblemRequest request){
        if (request == null) {
            return null;
        }

        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setPlatform(request.getPlatform());
        problem.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : Difficulty.EASY);
        problem.setStatus(request.getStatus() != null ? request.getStatus() : ProblemStatus.TODO);
        problem.setTags(request.getTags());
        problem.setCompanies(request.getCompanies());
        problem.setPatterns(request.getPatterns());
        problem.setNotes(request.getNotes());
        problem.setApproaches(request.getApproaches());
        problem.setTimeComplexity(request.getTimeComplexity());
        problem.setSpaceComplexity(request.getSpaceComplexity());
        problem.setUrl(request.getUrl());
        problem.setSheet(request.getSheet());
        problem.setRevisionDates(request.getRevisionDates());

        return problem;
    }
}
