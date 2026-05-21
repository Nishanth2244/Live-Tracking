package com.Project.Mechanic.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.NearbyMechanicResDTO;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class AdminService {
	
	private final UserRepository userRepository;
	
	public List<NearbyMechanicResDTO> mapToDto(List<Users> users){
		
		return users.stream()
				.map(user -> {
					NearbyMechanicResDTO dto = new NearbyMechanicResDTO();
					dto.setId(user.getId());
					dto.setName(user.getName());
					dto.setEmail(user.getEmail());
					dto.setPhoneNo(user.getPhone());
					dto.setExp(user.getExperience());
					if(user.getLocation() != null) {
						dto.setLatitude(user.getLocation().getY());
						dto.setLongitude(user.getLocation().getX());
					}
					return dto;
				}).collect(Collectors.toList());
				
	}

	public List<NearbyMechanicResDTO> pendingMech(boolean status) {
		
		List<Users> users = userRepository.findByApprovalStatus(status);
		
		if(users.isEmpty()) {
			throw new ResourceNotFoundException("No pending mechanics");
		}
		
		return mapToDto(users);
	}

	public String approveMech(Long id, boolean status) {
		
		Users users = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Mech Not found to approve"));
		
		users.setApprovalStatus(status);
		users.setIsAvailable(status);
		
		userRepository.save(users);

		return "Mechanic approved Succesfully";
	}

}
