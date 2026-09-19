package com.algonix.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "job_openings", uniqueConstraints = @UniqueConstraint(name = "uk_job_source_external", columnNames = {"source", "external_job_id"}))
@Getter
@Setter
@NoArgsConstructor
public class JobOpening {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "external_job_id")
    private String externalJobId;

    @Column(nullable = false)
    private String title;

    @Column(name = "job_url")
    private String jobUrl;

    private String location;

    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Column(name = "experience_min")
    private Integer experienceMin;

    @Column(name = "experience_max")
    private Integer experienceMax;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Column(name = "posted_at")
    private Instant postedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    private String source;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.ACTIVE;

    @Column(name = "fetched_at")
    private Instant fetchedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
