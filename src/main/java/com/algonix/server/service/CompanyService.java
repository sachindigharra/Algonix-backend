package com.algonix.server.service;

import com.algonix.server.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    List<CompanyResponse> getAllCompanies();

    CompanyResponse getCompanyById(UUID companyId);

    CompanyResponse createCompany(CompanyResponse request);

    CompanyResponse updateCompany(UUID companyId, CompanyResponse request);

    void deleteCompany(UUID companyId);
}
