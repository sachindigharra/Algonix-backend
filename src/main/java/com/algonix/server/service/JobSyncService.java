package com.algonix.server.service;

import com.algonix.server.dto.ExternalJob;
import com.algonix.server.entity.CompanyJobSource;

import java.util.List;

public interface JobSyncService {
    /**
     * Sync (upsert) jobs for the provided company job source.
     * Returns number of upserts performed (created + updated).
     */
    int syncJobsForSource(CompanyJobSource source, List<ExternalJob> jobs);
}
