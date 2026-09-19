package com.algonix.server.repository;

import com.algonix.server.entity.PreparationBucket;
import com.algonix.server.entity.Problem;
import com.algonix.server.entity.ProblemCompanyMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemCompanyMetadataRepository
        extends JpaRepository<ProblemCompanyMetadata, UUID> {

    @Query("""
        SELECT pcm.problem
        FROM ProblemCompanyMetadata pcm
        WHERE LOWER(pcm.company.name) = LOWER(:company)
          AND pcm.preparationBucket = :bucket
    """)
    List<Problem> findProblemsByCompanyAndBucket(
            @Param("company") String company,
            @Param("bucket") PreparationBucket bucket
    );

    Optional<ProblemCompanyMetadata> findByProblem_IdAndCompany_NameIgnoreCase(
            UUID problemId,
            String companyName
    );

    List<ProblemCompanyMetadata> findByCompany_NameIgnoreCaseAndProblemIn(String company, List<Problem> problems);
}
