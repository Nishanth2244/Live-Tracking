package com.Project.Mechanic.DTO;

import lombok.Data;

@Data
public class BookingRequestDTO {
	
	private Long userId;
	private Long mechanicId;
	private String problem;
	private double lat;
	private double lon;

}
