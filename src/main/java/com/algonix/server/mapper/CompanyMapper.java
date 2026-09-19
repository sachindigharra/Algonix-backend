package com.algonix.server.mapper;

import com.algonix.server.dto.response.CompanyResponse;
import com.algonix.server.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company company) {
        if (company == null) {
            return null;
        }

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .website(company.getCarrierPageUrl())
                .logoUrl(company.getLogoUrl())
                .build();
    }

    public Company toEntity(CompanyResponse response) {
        if (response == null) {
            return null;
        }

        Company company = new Company();
        company.setId(response.getId());
        company.setName(response.getName());
        company.setDescription(response.getDescription());
        company.setCarrierPageUrl(response.getWebsite());
        company.setLogoUrl(response.getLogoUrl());
        return company;
    }
}
