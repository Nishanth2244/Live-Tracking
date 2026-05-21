package com.Project.Mechanic.Entity;


import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "user_snapshot")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String email;
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Roles roles;
	
	private Boolean approvalStatus;
	
	private String phone;
    private Boolean isAvailable;
    
    @Column(columnDefinition = "geometry(Point, 4326)") // 4326 is standard GPS coordinate system (WGS 84)
    private Point location;
    
    private String experience;
    
}
