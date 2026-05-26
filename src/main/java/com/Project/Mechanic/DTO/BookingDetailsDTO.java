package com.Project.Mechanic.DTO;

import com.Project.Mechanic.Entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class BookingDetailsDTO {
    private Long id;
    private LocalDateTime createdAt;
    private String problem;
    private BookingStatus status;

    // Customer Details
    private String customerName;
    private String customerPhone;

    // Location Coordinates
    private double latitude;
    private double longitude;

    private String billingDetails;
    private Double totalAmount;
}
