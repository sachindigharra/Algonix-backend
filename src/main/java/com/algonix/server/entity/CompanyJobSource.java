package com.algonix.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "company_job_sources", uniqueConstraints = @UniqueConstraint(name = "uk_company_provider_external", columnNames = {"company_id", "provider", "external_company_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CompanyJobSource {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String provider; // e.g., "example-jobs-api"

    @Column(name = "external_company_id")
    private String externalCompanyId;

    @Column(name = "external_company_name")
    private String externalCompanyName;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson; // optional provider config

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
