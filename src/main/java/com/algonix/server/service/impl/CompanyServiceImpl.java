package com.algonix.server.service.impl;

import com.algonix.server.dto.response.CompanyResponse;
import com.algonix.server.entity.Company;
import com.algonix.server.exception.ResourceNotFoundException;
import com.algonix.server.mapper.CompanyMapper;
import com.algonix.server.repository.CompanyRepository;
import com.algonix.server.service.CompanyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toResponse)
                .toList();
    }

    @Override
    public CompanyResponse getCompanyById(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with id: " + companyId));

        return companyMapper.toResponse(company);
    }

    @Override
    public CompanyResponse createCompany(CompanyResponse request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }

        if (companyRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new IllegalArgumentException("Company already exists");
        }

        Company company = companyMapper.toEntity(request);
        Company saved = companyRepository.save(company);
        log.info("Created company with id {}", saved.getId());
        return companyMapper.toResponse(saved);
    }

    @Override
    public CompanyResponse updateCompany(UUID companyId, CompanyResponse request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with id: " + companyId));

        if (request == null) {
            throw new IllegalArgumentException("Company payload is required");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            company.setName(request.getName().trim());
        }
        company.setDescription(request.getDescription());
        company.setCarrierPageUrl(request.getWebsite());
        company.setLogoUrl(request.getLogoUrl());

        Company updated = companyRepository.save(company);
        log.info("Updated company with id {}", updated.getId());
        return companyMapper.toResponse(updated);
    }

    @Override
    public void deleteCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found with id: " + companyId));

        companyRepository.delete(company);
        log.info("Deleted company with id {}", companyId);
    }
}
