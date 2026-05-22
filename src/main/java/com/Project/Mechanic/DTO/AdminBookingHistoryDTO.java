package com.Project.Mechanic.DTO;

import java.time.LocalDateTime;

import com.Project.Mechanic.Entity.BookingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminBookingHistoryDTO {
	
	private Long bookingId;
	private LocalDateTime bookedTime;
	private double lat;
	private double lon;
	private String mechanicName;
	private String problem;
	private BookingStatus status;
	private String userName;

}
