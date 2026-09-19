package com.algonix.server.repository;

import com.algonix.server.entity.JobOpening;
import com.algonix.server.entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobOpeningRepository extends JpaRepository<JobOpening, UUID> {

    Page<JobOpening> findByCompany_IdAndStatus(UUID companyId, JobStatus status, Pageable pageable);

    Page<JobOpening> findByCompany_Id(UUID companyId, Pageable pageable);

    Optional<JobOpening> findBySourceAndExternalJobId(String source, String externalJobId);

    List<JobOpening> findByCompany_IdAndSource(UUID companyId, String source);
}
