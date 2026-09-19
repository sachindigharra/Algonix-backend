package com.algonix.server.service.impl;

import com.algonix.server.dto.ExternalJob;
import com.algonix.server.entity.*;
import com.algonix.server.repository.CompanyJobSourceRepository;
import com.algonix.server.repository.JobOpeningRepository;
import com.algonix.server.repository.CompanyRepository;
import com.algonix.server.service.JobSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSyncServiceImpl implements JobSyncService {

    private final JobOpeningRepository jobOpeningRepository;
    private final CompanyRepository companyRepository;
    private final CompanyJobSourceRepository companyJobSourceRepository;

    @Override
    @Transactional
    public int syncJobsForSource(CompanyJobSource source, List<ExternalJob> jobs) {
        if (source == null || jobs == null) return 0;

        UUID companyId = source.getCompany() != null ? source.getCompany().getId() : null;
        if (companyId == null) {
            // Attempt to find company by name on source or create
            String name = source.getExternalCompanyName();
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Source must have company reference or external company name");
            }
            companyId = companyRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Company c = new Company();
                        c.setName(name.trim());
                        return companyRepository.save(c);
                    }).getId();
        }

        Set<String> incomingIds = new HashSet<>();
        int upserts = 0;

        for (ExternalJob ej : jobs) {
            if (ej.getExternalJobId() == null || ej.getExternalJobId().isBlank()) continue;
            incomingIds.add(ej.getExternalJobId());

            Optional<JobOpening> existing = jobOpeningRepository.findBySourceAndExternalJobId(source.getProvider(), ej.getExternalJobId());

            JobOpening job;
            if (existing.isPresent()) {
                job = existing.get();
                mapExternalToEntity(ej, job, source);
                job.setFetchedAt(Instant.now());
                jobOpeningRepository.save(job);
                upserts++;
            } else {
                job = new JobOpening();
                // set company reference
                Company company = source.getCompany();
                if (company == null) {
                    company = companyRepository.findById(companyId)
                            .orElseThrow(() -> new IllegalStateException("Company missing after creation"));
                }
                job.setCompany(company);
                job.setSource(source.getProvider());
                job.setExternalJobId(ej.getExternalJobId());
                mapExternalToEntity(ej, job, source);
                job.setFetchedAt(Instant.now());
                jobOpeningRepository.save(job);
                upserts++;
            }
        }

        // Mark disappeared jobs for this company+source as STALE
        List<JobOpening> existingForCompanySource = jobOpeningRepository.findByCompany_IdAndSource(companyId, source.getProvider());
        for (JobOpening j : existingForCompanySource) {
            if (j.getExternalJobId() == null) continue;
            if (!incomingIds.contains(j.getExternalJobId()) && j.getStatus() == JobStatus.ACTIVE) {
                j.setStatus(JobStatus.STALE);
                j.setUpdatedAt(Instant.now());
                jobOpeningRepository.save(j);
            }
        }

        log.info("Job sync for source={} completed. upserts={}, totalFetched={}", source.getProvider(), upserts, jobs.size());
        return upserts;
    }

    private void mapExternalToEntity(ExternalJob ej, JobOpening job, CompanyJobSource source) {
        job.setTitle(ej.getTitle() != null ? ej.getTitle() : job.getTitle());
        job.setJobUrl(ej.getJobUrl() != null ? ej.getJobUrl() : job.getJobUrl());
        job.setLocation(ej.getLocation() != null ? ej.getLocation() : job.getLocation());
        job.setCountry(ej.getCountry() != null ? ej.getCountry() : job.getCountry());
        if (ej.getEmploymentType() != null) {
            try {
                job.setEmploymentType(EmploymentType.valueOf(ej.getEmploymentType().toUpperCase()));
            } catch (Exception ignored) {
            }
        }
        job.setExperienceMin(ej.getExperienceMin());
        job.setExperienceMax(ej.getExperienceMax());
        job.setDescription(ej.getDescription() != null ? ej.getDescription() : job.getDescription());
        if (ej.getSkills() != null) job.setSkills(new ArrayList<>(ej.getSkills()));
        job.setPostedAt(ej.getPostedAt());
        job.setExpiresAt(ej.getExpiresAt());
        job.setStatus(JobStatus.ACTIVE);
    }
}
