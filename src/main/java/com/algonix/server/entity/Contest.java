package com.algonix.server.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;
import com.algonix.server.entity.PlatformType;

@Entity
@Table(name = "contests")
@Getter
@Setter
public class Contest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformType platform;

    private Instant startTime;

    private Integer durationMinutes;

    private String url;

    private Boolean participated = false;

    private Integer rank;

    private Integer problemsSolved;

    private Integer ratingChange;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
