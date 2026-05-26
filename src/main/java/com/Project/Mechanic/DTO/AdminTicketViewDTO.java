package com.Project.Mechanic.DTO;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminTicketViewDTO {
    private Long ticketId;
    private String subject;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    
    // User Details
    private String userName;
    private String userPhone;
    
    // Booking Details
    private Long bookingId;
    private String bookingProblem;
    private Double totalAmount;
}