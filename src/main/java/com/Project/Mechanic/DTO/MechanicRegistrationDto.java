package com.Project.Mechanic.DTO;

import lombok.Data;

@Data
public class MechanicRegistrationDto {
	
	private String name;
    private String email;
    private String password;
    private String phone;
    private double latitude;
    private double longitude;
    private String experience;

}