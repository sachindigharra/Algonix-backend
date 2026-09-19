package com.algonix.server.service;

import com.algonix.server.dto.response.JobOpeningResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JobService {
    Page<JobOpeningResponse> getJobsByCompany(UUID companyId, Pageable pageable);
}
