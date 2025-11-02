package com.quizbackend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "participations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "guest_id")
    private Integer guestId;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", insertable = false, updatable = false)
    private Guest guest;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}