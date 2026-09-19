package com.algonix.server.integration;

import com.algonix.server.dto.ExternalJob;
import com.algonix.server.entity.CompanyJobSource;

import java.util.List;

public interface JobProvider {
    /**
     * Fetch jobs for the given CompanyJobSource.
     * Implementations should return normalized ExternalJob objects.
     */
    List<ExternalJob> fetchJobs(CompanyJobSource source);
}
