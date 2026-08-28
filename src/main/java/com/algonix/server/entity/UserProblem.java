package com.algonix.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "user_problems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_problem_unique",
                        columnNames = {"user_id", "problem_id"}
                )
        }
)
public class UserProblem {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * User who is tracking this problem.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Problem being tracked.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    /**
     * User's current status for this problem.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemStatus status;

    /**
     * User's personal notes.
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * User's solution approaches.
     */
    @ElementCollection
    @CollectionTable(
            name = "user_problem_approaches",
            joinColumns = @JoinColumn(name = "user_problem_id")
    )
    @Column(name = "approach", columnDefinition = "TEXT")
    private List<String> approaches = new ArrayList<>();

    /**
     * User's recorded time complexity.
     */
    private String timeComplexity;

    /**
     * User's recorded space complexity.
     */
    private String spaceComplexity;

    /**
     * Sheet/category selected by the user.
     */
    @Enumerated(EnumType.STRING)
    private ProblemSheet sheet;

    /**
     * Date on which THIS USER solved the problem.
     */
    private LocalDate solvedDate;

    /**
     * Revision dates for THIS USER.
     */
    @ElementCollection
    @CollectionTable(
            name = "user_problem_revision_dates",
            joinColumns = @JoinColumn(name = "user_problem_id")
    )
    @Column(name = "revision_date")
    private List<LocalDate> revisionDates = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}