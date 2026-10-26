package com.algonix.server.mapper;

import com.algonix.server.dto.CreateContestRequest;
import com.algonix.server.dto.ContestResponse;
import com.algonix.server.entity.Contest;
import org.springframework.stereotype.Component;

@Component
public class ContestMapper {

    public ContestResponse toResponseDto(Contest contest) {
        if (contest == null) {
            return null;
        }

        ContestResponse response = new ContestResponse();
        response.setId(contest.getId());
        response.setName(contest.getName());
        response.setPlatform(contest.getPlatform() != null ? contest.getPlatform().toString() : null);
        response.setStartTime(contest.getStartTime());
        response.setDurationMinutes(contest.getDurationMinutes());
        response.setUrl(contest.getUrl());
        response.setParticipated(contest.getParticipated());
        response.setRank(contest.getRank());
        response.setProblemsSolved(contest.getProblemsSolved());
        response.setRatingChange(contest.getRatingChange());
        response.setNotes(contest.getNotes());
        return response;
    }

    public Contest toEntity(CreateContestRequest request) {
        if (request == null) {
            return null;
        }

        Contest contest = new Contest();
        contest.setName(request.getName());
        contest.setPlatform(request.getPlatform());
        contest.setStartTime(request.getStartTime());
        contest.setDurationMinutes(request.getDurationMinutes());
        contest.setUrl(request.getUrl());
        contest.setParticipated(request.getParticipated() != null ? request.getParticipated() : false);
        contest.setRank(request.getRank());
        contest.setProblemsSolved(request.getProblemsSolved());
        contest.setRatingChange(request.getRatingChange());
        contest.setNotes(request.getNotes());
        return contest;
    }
}

