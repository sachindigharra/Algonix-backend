package com.algonix.server.entity;
import jakarta.persistence.*;
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
public class PlatformProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformType platform;

    @Column(nullable = false)
    private String username;

    private String profileUrl;

    private Integer problemsSolved = 0;

    private Integer easySolved = 0;

    private Integer mediumSolved = 0;

    private Integer hardSolved = 0;

    private Integer rating;

    private Integer maxRating;

    private Integer contributions = 0;

    private Integer streak = 0;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
