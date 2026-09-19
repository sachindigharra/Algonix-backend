package com.algonix.server.service.impl;

import com.algonix.server.dto.response.JobOpeningResponse;
import com.algonix.server.mapper.JobOpeningMapper;
import com.algonix.server.repository.JobOpeningRepository;
import com.algonix.server.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobOpeningRepository jobOpeningRepository;
    private final JobOpeningMapper jobOpeningMapper;

    @Override
    public Page<JobOpeningResponse> getJobsByCompany(UUID companyId, Pageable pageable) {
        return jobOpeningRepository.findByCompany_Id(companyId, pageable)
                .map(jobOpeningMapper::toResponse);
    }
}
