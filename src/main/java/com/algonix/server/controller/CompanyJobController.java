package com.algonix.server.controller;

import com.algonix.server.dto.response.JobOpeningResponse;
import com.algonix.server.service.JobService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class CompanyJobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<Page<JobOpeningResponse>> getCompanyJobs(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobOpeningResponse> jobs = jobService.getJobsByCompany(companyId, pageable);
        return ResponseEntity.ok(jobs);
    }
}
