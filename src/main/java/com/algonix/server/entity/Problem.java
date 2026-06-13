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
        name = "problems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "problems_user_title_unique",
                        columnNames = {"user_id","title"}
                )
        }
)
public class Problem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Enumerated(EnumType.STRING)
    private PlatformType platform;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    private ProblemStatus status;

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    private List<String> companies = new ArrayList<>();

    @ElementCollection
    private List<String> patterns = new ArrayList<>();
    
    @Lob
    @Column(columnDefinition = "text")
    private String notes;

    @ElementCollection
    @Column(columnDefinition = "text")
    private List<String> approaches = new ArrayList<>();

    private String timeComplexity;

    private String spaceComplexity;

    private String url;

    @Enumerated(EnumType.STRING)
    private ProblemSheet sheet;

    private LocalDate solvedDate;

    @ElementCollection
    private List<LocalDate> revisionDates = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
