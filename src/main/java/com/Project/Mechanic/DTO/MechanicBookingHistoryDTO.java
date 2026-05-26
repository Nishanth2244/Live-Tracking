package com.Project.Mechanic.DTO;

import com.Project.Mechanic.Entity.BookingStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MechanicBookingHistoryDTO {
    private Long bookingId;
    private LocalDateTime bookedTime;
    private String problem;
    private BookingStatus status;
    
    // Customer Details
    private String customerName;
    private String customerPhone;
    
    // Location Coordinates
    private double latitude;
    private double longitude;
    
    private Double totalAmount; 
}