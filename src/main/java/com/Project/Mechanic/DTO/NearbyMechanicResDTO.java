package com.Project.Mechanic.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NearbyMechanicResDTO {
	
	private Long id;
	private String name;
	private String phoneNo;
	private String email;
	private double latitude;
    private double longitude;
    private double distance;
    private String exp;
}
