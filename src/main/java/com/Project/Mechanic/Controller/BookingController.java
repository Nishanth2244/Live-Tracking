package com.Project.Mechanic.Controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.BookingRequestDTO;
import com.Project.Mechanic.Service.BookingService;
import com.Project.Mechanic.Service.JwtService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/booking")
public class BookingController {
	
	private final BookingService bookingService;
	private final JwtService jwtService;
	
	
	@PostMapping("/mechanic")
	public String createBooking(@RequestBody BookingRequestDTO bookingRequestDTO,
								@RequestHeader("Authorization") String token) {
		
		Long userId = jwtService.extractUserId(token.substring(7));
		
	    bookingService.booking(bookingRequestDTO, userId);
		return "Booking created and mechanic notified!";
	}
	
	
	@PatchMapping("/accept/{bookingId}")
	public String acceptBooking(@PathVariable Long bookingId,
								@RequestHeader("Authorization") String token) {
		
		Long mechanicId = jwtService.extractUserId(token.substring(7));
		return bookingService.accept(bookingId, mechanicId);
	}
	
	
	
	@PatchMapping("/complete/{bookingId}")
	public String completeBooking(@PathVariable Long bookingId,
									@RequestHeader("Authorization") String token) {
		
		Long mechanicId = jwtService.extractUserId(token.substring(7));
		return bookingService.completeBooking(bookingId, mechanicId);
	}
	
	
	

}
