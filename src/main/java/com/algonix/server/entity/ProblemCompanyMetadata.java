package com.algonix.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "problem_company_metadata",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_problem_company",
                columnNames = {"problem_id", "company_id"}
        )
)
public class ProblemCompanyMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "preparation_bucket", nullable = false)
    private PreparationBucket preparationBucket;
}
