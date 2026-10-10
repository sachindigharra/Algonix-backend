package com.algonix.server.repository;

import com.algonix.server.entity.Problem;
import com.algonix.server.repository.projection.CompanyProjection;
import com.algonix.server.repository.projection.PatternProjection;
import com.algonix.server.repository.projection.TagProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {


    List<Problem> findByCreatedById(UUID userId);

    List<Problem> findAll();

    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) = LOWER(:title)")
    Optional<Problem> findByTitleIgnoreCase(@Param("title") String title);

    List<Problem> findByCompanies_NameIgnoreCase(String companyName);

    // Bulk projections — one query per collection type, regardless of problem count
    @Query("SELECT p.id AS problemId, t AS tag FROM Problem p JOIN p.tags t WHERE p.id IN :ids")
    List<TagProjection> findTagsByProblemIds(@Param("ids") List<UUID> ids);

    @Query("SELECT p.id AS problemId, pat AS pattern FROM Problem p JOIN p.patterns pat WHERE p.id IN :ids")
    List<PatternProjection> findPatternsByProblemIds(@Param("ids") List<UUID> ids);

    @Query("SELECT p.id AS problemId, c.name AS companyName FROM Problem p JOIN p.companies c WHERE p.id IN :ids")
    List<CompanyProjection> findCompaniesByProblemIds(@Param("ids") List<UUID> ids);

}
