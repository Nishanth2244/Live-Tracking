package com.Project.Mechanic.Controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.BookingRequestDTO;
import com.Project.Mechanic.Service.BookingService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/booking")
public class BookingController {
	
	private final BookingService bookingService;
	
	
	@PostMapping("/mechanic")
	public String createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
		
	    bookingService.booking(bookingRequestDTO);
		return "Booking created and mechanic notified!";
	}
	
	
	@PatchMapping("/accept/{bookingId}")
	public String acceptBooking(@PathVariable Long bookingId,
								@RequestParam Long mechanicId) {
		
		return bookingService.accept(bookingId, mechanicId);
	}
	
	
	
	@PatchMapping("/complete/{bookingId}")
	public String completeBooking(@PathVariable Long bookingId,
			@RequestParam Long mechanicId) {
		
		return bookingService.completeBooking(bookingId, mechanicId);
	}

}
