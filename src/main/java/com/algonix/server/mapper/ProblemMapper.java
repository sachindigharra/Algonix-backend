package com.algonix.server.mapper;

import com.algonix.server.dto.CreateProblemRequest;
import com.algonix.server.dto.ProblemResponse;
import com.algonix.server.entity.Problem;

//@Mapper(componentModel = "spring")
public class ProblemMapper {

    public ProblemResponse toResponseDto(Problem problem){
        if (problem == null) {
            return null;
        }

        ProblemResponse response = new ProblemResponse();
        response.setId(problem.getId());
        response.setTitle(problem.getTitle());
        response.setPlatform(problem.getPlatform().toString());
        response.setDifficulty(problem.getDifficulty().toString());
        response.setStatus(problem.getStatus().toString());

        return response;
    }

    Problem toEntity(CreateProblemRequest request){
        if (request == null) {
            return null;
        }

        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setPlatform(request.getPlatform());
        problem.setDifficulty(request.getDifficulty());
        problem.setStatus(request.getStatus());

        return problem;
    }

    /*void updateEntityFromDto(
            UpdateProblemRequestDto request,
            @MappingTarget Problem problem
    );*/
}
