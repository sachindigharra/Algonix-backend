package com.algonix.server.mapper;

import com.algonix.server.dto.response.JobOpeningResponse;
import com.algonix.server.entity.JobOpening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobOpeningMapper {

    public JobOpeningResponse toResponse(JobOpening job) {
        if (job == null) return null;

        return JobOpeningResponse.builder()
                .id(job.getId())
                .companyId(job.getCompany() != null ? job.getCompany().getId() : null)
                .externalJobId(job.getExternalJobId())
                .title(job.getTitle())
                .jobUrl(job.getJobUrl())
                .location(job.getLocation())
                .country(job.getCountry())
                .employmentType(job.getEmploymentType() != null ? job.getEmploymentType().name() : null)
                .experienceMin(job.getExperienceMin())
                .experienceMax(job.getExperienceMax())
                .description(job.getDescription())
                .skills(job.getSkills())
                .postedAt(job.getPostedAt())
                .expiresAt(job.getExpiresAt())
                .source(job.getSource())
                .status(job.getStatus() != null ? job.getStatus().name() : null)
                .fetchedAt(job.getFetchedAt())
                .build();
    }
}
