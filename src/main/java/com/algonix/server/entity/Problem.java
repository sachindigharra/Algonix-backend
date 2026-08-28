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


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "problems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "problems_unique",
                        columnNames = {"title", "platform", "difficulty"}
                )
        }
)
public class Problem {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * User who created this problem (ADMIN for global, USER for personal).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformType platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @ElementCollection
    @CollectionTable(
            name = "problem_tags",
            joinColumns = @JoinColumn(name = "problem_id")
    )
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "problem_companies",
            joinColumns = @JoinColumn(name = "problem_id")
    )
    @Column(name = "company")
    private List<String> companies = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "problem_patterns",
            joinColumns = @JoinColumn(name = "problem_id")
    )
    @Column(name = "pattern")
    private List<String> patterns = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String url;

    /**
     * PUBLIC  -> visible to other users
     * PRIVATE -> visible only to creator/admin
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemVisibility visibility;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}