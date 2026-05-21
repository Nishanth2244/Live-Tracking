package com.Project.Mechanic.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.NearbyMechanicResDTO;
import com.Project.Mechanic.Service.MechanicService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mechanic")
public class MechanicController {
	
	private final MechanicService mechanicService;
	
	@GetMapping("/get")
	public List<NearbyMechanicResDTO> searchMechNearby(@RequestParam double lat,
													@RequestParam double lon){
		return mechanicService.searchNearbyMechanics(lat, lon);
	}
}
