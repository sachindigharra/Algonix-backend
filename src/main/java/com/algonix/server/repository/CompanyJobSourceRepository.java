package com.algonix.server.repository;

import com.algonix.server.entity.CompanyJobSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyJobSourceRepository extends JpaRepository<CompanyJobSource, UUID> {
    Optional<CompanyJobSource> findByCompany_IdAndProviderAndExternalCompanyId(UUID companyId, String provider, String externalCompanyId);
}
