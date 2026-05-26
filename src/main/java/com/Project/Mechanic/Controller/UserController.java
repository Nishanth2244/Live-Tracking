package com.Project.Mechanic.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.ProfileUpdateDTO;
import com.Project.Mechanic.DTO.TicketRequestDTO;
import com.Project.Mechanic.DTO.UserBookingHistoryDTO;
import com.Project.Mechanic.DTO.UserProfileResDTO;
import com.Project.Mechanic.DTO.UserTicketViewDTO;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Service.JwtService;
import com.Project.Mechanic.Service.SupportService;
import com.Project.Mechanic.Service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	
	private final JwtService jwtService;
	private final UserService userService;
	private final SupportService supportService;
	
	@GetMapping("/profile")
    public UserProfileResDTO getMyProfile(@RequestHeader("Authorization") String token) {
        // Bearer prefix split logic
		
		log.info("Token received to get Profile: {}", token);
        Long userId = jwtService.extractUserId(token.substring(7));
        
        UserProfileResDTO profile = userService.getUserProfile(userId);
        return (profile);
    }
	
	
	@PutMapping("/profile/update")
	public String updateProfile(@RequestHeader ("Authorization") String token,
								@RequestBody ProfileUpdateDTO profileUpdateDTO) {
		
        Long userId = jwtService.extractUserId(token.substring(7));
        userService.updateProfile(userId, profileUpdateDTO);
        return "Profile Updated Succesfully";
	}
	
	
	@GetMapping("/history")
	public List<UserBookingHistoryDTO> getUserBookingHistory(
	        @RequestParam BookingStatus status,
	        @RequestHeader("Authorization") String token) {

	    Long userId = jwtService.extractUserId(token.substring(7));
	    
	    List<UserBookingHistoryDTO> history = userService.getUserHistory(userId, status);
	    
	    log.info("Fetching the Booking History of User: {}", userId);
	    return history;
	}

	@PostMapping("/support/raise")
    public String raiseTicket(@RequestBody TicketRequestDTO dto, 
    							@RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token.substring(7));
        return supportService.raiseTicket(userId, dto);
    }

    @GetMapping("/support/my-tickets")
    public List<UserTicketViewDTO> getMyTickets(@RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token.substring(7));
        return supportService.getUserTickets(userId);
    }
	
	

}

