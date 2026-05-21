package com.Project.Mechanic.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.MechanicDistanceProjection;
import com.Project.Mechanic.DTO.NearbyMechanicResDTO;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class MechanicService {
	
	private UserRepository userRepository;

	public List<NearbyMechanicResDTO> searchNearbyMechanics(double userLat, double userLon) {
	    List<MechanicDistanceProjection> mechanics = userRepository.findNearestMechanicsWithDistance(userLon, userLat);
	    
	    return mechanics.stream().map(mechanic -> {
	        NearbyMechanicResDTO dto = new NearbyMechanicResDTO();
	        dto.setId(mechanic.getId());
	        dto.setName(mechanic.getName());
	        dto.setEmail(mechanic.getEmail());
	        dto.setPhoneNo(mechanic.getPhone());
	        dto.setLatitude(mechanic.getLatitude());
	        dto.setLongitude(mechanic.getLongitude());
	        
	        double distanceInKm = mechanic.getDistance() / 1000.0;
	        dto.setDistance(Math.round(distanceInKm * 100.0) / 100.0); 
	        
	        return dto;
	    }).collect(Collectors.toList());
	}
}
