package com.Project.Mechanic.DTO;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserTicketViewDTO {
    private Long ticketId;
    private Long bookingId;
    private String subject;
    private String description;
    private String adminReply;
    private String status;
    private LocalDateTime createdAt;
}