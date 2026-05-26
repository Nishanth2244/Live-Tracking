package com.Project.Mechanic.DTO;
import lombok.Data;

@Data
public class TicketRequestDTO {
    private Long bookingId;
    private String subject;
    private String description;
}