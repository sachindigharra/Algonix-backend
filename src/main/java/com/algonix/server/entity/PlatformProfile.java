package com.algonix.server.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "platform_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_platform",
                        columnNames = {"user_id", "platform"}
                )
        }
)
@Getter
@Setter
@Builder

public class PlatformProfile {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Owner of this platform profile.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * LEETCODE, CODEFORCES, CODECHEF...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlatformType platform;

    /**
     * Username/Handle on the coding platform.
     */
    @Column(nullable = false, length = 100)
    private String platformUsername;

    /**
     * Public profile URL.
     */
    private String profileUrl;

    /**
     * Public display name.
     */
    private String displayName;

    /**
     * Profile picture/avatar.
     */
    private String avatarUrl;

    // ======================================================
    // Problem Statistics
    // ======================================================

    private Integer totalSolved = 0;

    private Integer easySolved = 0;

    private Integer mediumSolved = 0;

    private Integer hardSolved = 0;

    // ======================================================
    // Contest Statistics
    // ======================================================

    private Integer contestRating;

    private Integer maxContestRating;

    private Integer contestAttended = 0;

    private Integer globalRanking;

    // ======================================================
    // Activity Statistics
    // ======================================================

    private Integer totalSubmissions = 0;

    private Integer activeDays = 0;

    private Integer currentStreak = 0;

    private Integer maxStreak = 0;

    private Integer contributions = 0;

    private Double acceptanceRate;

    // ======================================================
    // Sync Information
    // ======================================================

    /**
     * Whether this platform is currently connected.
     */
    private Boolean connected = true;

    /**
     * Last successful synchronization time.
     */
    private Instant lastSyncedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}