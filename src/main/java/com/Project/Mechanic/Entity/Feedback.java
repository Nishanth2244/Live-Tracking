package com.Project.Mechanic.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(
            name = "booking_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true
    )
    private Booking booking;

    private Long userId;

    private Long mechanicId;

    private Integer rating;

    @Column(length = 1000)
    private String review;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
    }
}