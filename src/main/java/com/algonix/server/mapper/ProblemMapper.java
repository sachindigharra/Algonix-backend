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
        response.setPlatform(problem.getPlatform().toString());
        response.setDifficulty(problem.getDifficulty().toString());
        response.setStatus(problem.getStatus().toString());
        response.setUrl(problem.getUrl());
        return response;
    }

    public Problem toEntity(CreateProblemRequest request){
        if (request == null) {
            return null;
        }

        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setPlatform(request.getPlatform());
        problem.setDifficulty(Difficulty.EASY==request.getDifficulty() ? Difficulty.EASY :
                Difficulty.MEDIUM == request.getDifficulty() ? Difficulty.MEDIUM :
                        Difficulty.HARD);
        problem.setStatus(ProblemStatus.SOLVED==request.getStatus() ? ProblemStatus.SOLVED :
                ProblemStatus.ATTEMPTED == request.getStatus() ? ProblemStatus.ATTEMPTED :
                        ProblemStatus.TODO);
        problem.setUrl(request.getUrl());

        return problem;
    }

    /*void updateEntityFromDto(
            UpdateProblemRequestDto request,
            @MappingTarget Problem problem
    );*/
}
