package com.algonix.server.repository;

import com.algonix.server.entity.UserProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProblemRepository
        extends JpaRepository<UserProblem, UUID> {

    Optional<UserProblem> findByUserIdAndProblemId(
            UUID userId,
            UUID problemId
    );

    List<UserProblem> findByUserId(UUID userId);

    void deleteByProblemId(UUID problemId);
}