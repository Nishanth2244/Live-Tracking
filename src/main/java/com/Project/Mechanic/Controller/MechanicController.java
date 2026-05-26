package com.Project.Mechanic.Controller;

import java.util.List;

import com.Project.Mechanic.DTO.MechanicDashboardDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.MechanicBookingHistoryDTO;
import com.Project.Mechanic.DTO.NearbyMechanicResDTO;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Service.JwtService;
import com.Project.Mechanic.Service.MechanicService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mechanic")
public class MechanicController {
	
	private final MechanicService mechanicService;
	private final JwtService jwtService;
	
	@GetMapping("/get")
	public List<NearbyMechanicResDTO> searchMechNearby(@RequestParam double lat,
													@RequestParam double lon){
		return mechanicService.searchNearbyMechanics(lat, lon);
	}
	
	
	@GetMapping("/history")
    public List<MechanicBookingHistoryDTO> getMechanicBookingHistory(
            @RequestParam BookingStatus status,
            @RequestHeader("Authorization") String token,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size) {

        // Extracting Mechanic ID securely from JWT Token
        Long mechanicId = jwtService.extractUserId(token.substring(7));
        
        Pageable pageable = PageRequest.of(page, size);
        
        return mechanicService.getMechanicHistory(mechanicId, status, pageable);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<MechanicDashboardDTO> getDashboard(@RequestHeader("Authorization") String token) {
        Long mechanicId = jwtService.extractUserId(token.substring(7));
        return ResponseEntity.ok(
                mechanicService.getMechanicDashboard(mechanicId)
        );
    }
}
