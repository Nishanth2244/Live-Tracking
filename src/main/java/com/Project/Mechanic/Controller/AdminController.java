package com.Project.Mechanic.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.AdminBookingHistoryDTO;
import com.Project.Mechanic.DTO.NearbyMechanicResDTO;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Service.AdminService;

import lombok.AllArgsConstructor;


@CrossOrigin(originPatterns = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
	
	private final AdminService adminService;
	
	@GetMapping("/mechanics")
	public List<NearbyMechanicResDTO> getPending(@RequestParam boolean status){
		return adminService.pendingMech(status);
	}
	
	
	@PatchMapping("/updateStatus")
	public String approveMech(@RequestParam Long id,
								@RequestParam boolean status) {
		return adminService.approveMech(id,status);
	}
	
	
	@GetMapping("/history")
	private List<AdminBookingHistoryDTO> getHistory(@RequestParam BookingStatus status){
		
		return adminService.getHistory(status);
	}

}
