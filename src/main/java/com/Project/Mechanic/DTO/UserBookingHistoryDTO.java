package com.Project.Mechanic.DTO;

import com.Project.Mechanic.Entity.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBookingHistoryDTO {
    private Long bookingId;
    private LocalDateTime bookedTime;
    private String problem;
    private BookingStatus status;
    private String mechanicName;
    private String mechanicPhone;
    private Double totalAmount;
}