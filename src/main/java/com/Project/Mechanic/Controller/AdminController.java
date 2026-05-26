package com.Project.Mechanic.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.AdminBookingHistoryDTO;
import com.Project.Mechanic.DTO.AdminDashboardDTO;
import com.Project.Mechanic.DTO.AdminReplyDTO;
import com.Project.Mechanic.DTO.AdminTicketViewDTO;
import com.Project.Mechanic.DTO.NearbyMechanicResDTO;
import com.Project.Mechanic.DTO.UserProfileResDTO;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Entity.Roles;
import com.Project.Mechanic.Entity.TicketStatus;
import com.Project.Mechanic.Service.AdminService;
import com.Project.Mechanic.Service.SupportService;

import lombok.AllArgsConstructor;


@CrossOrigin(originPatterns = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
	
	private final AdminService adminService;
	private final SupportService supportService;
	
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

	
	@GetMapping("/dashboard-stats")
	public AdminDashboardDTO getDashboardStats() {
	    return adminService.getDashboardStats();
	}
	
	@PatchMapping("/toggle-access")
	public String toggleAccess(@RequestParam Long userId, @RequestParam boolean hasAccess) {
	    return adminService.toggleUserAccess(userId, hasAccess);
	}
	
	@GetMapping("/users")
	public List<UserProfileResDTO> getUsersByRole(@RequestParam Roles role) {
	    return adminService.getUsersByRole(role);
	}
	
	
	@GetMapping("/support/tickets")
    public List<AdminTicketViewDTO> getPendingTickets(@RequestParam TicketStatus status) {
        return supportService.getTicketsForAdmin(status);
    }

    @PatchMapping("/support/resolve/{ticketId}")
    public String resolveTicket(@PathVariable Long ticketId, @RequestBody AdminReplyDTO dto) {
        return supportService.resolveTicket(ticketId, dto);
    }
}
