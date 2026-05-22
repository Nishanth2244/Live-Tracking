package com.Project.Mechanic.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; 
    private Long mechanicId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    private String problem;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point breakdownLocation; 

    private LocalDateTime createdAt;
    
    private Double serviceCharge;
    private Double partsCost;
    private Double extraCharges;
    private Double totalAmount;
    private String billingDetails;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}