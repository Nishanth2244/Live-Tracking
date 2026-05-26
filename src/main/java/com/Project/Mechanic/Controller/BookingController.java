package com.Project.Mechanic.Controller;

import com.Project.Mechanic.DTO.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Project.Mechanic.Service.BookingService;
import com.Project.Mechanic.Service.JwtService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
    @PatchMapping("/reject/{bookingId}")
    public String rejectBooking(@PathVariable Long bookingId,
                                @RequestHeader("Authorization") String token) {

        Long mechanicId = jwtService.extractUserId(token.substring(7));
        return bookingService.reject(bookingId, mechanicId);
    }

	@PostMapping("/generate-bill")
	public String generateBill(@RequestBody BillRequestDTO dto, 
								@RequestHeader("Authorization") String token) {
	    Long mechanicId = jwtService.extractUserId(token.substring(7));
	    return bookingService.generateBill(dto, mechanicId);
	}
	
	
	@PatchMapping("/complete/{bookingId}")
	public String completeBooking(@PathVariable Long bookingId,
									@RequestHeader("Authorization") String token) {
		
		Long mechanicId = jwtService.extractUserId(token.substring(7));
		return bookingService.completeBooking(bookingId, mechanicId);
	}
    @GetMapping("/upcoming")
    public List<BookingDetailsDTO> upcomingBookings(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size){
        return bookingService.upcomingBookings(page,size);
    }
    @GetMapping("/completed")
    public List<BookingDetailsDTO> completedBookings(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        return bookingService.completedBookings(page,size);
    }
    @GetMapping("/dashboard")
    public DashboardDTO getDashboardData(){
        return bookingService.getDashboardData();
    }
    @GetMapping("/weekly-sales")
    public ResponseEntity<List<WeeklySalesDTO>> getWeeklySales() {

        return ResponseEntity.ok(
                bookingService.getWeeklySales()
        );
    }
    @GetMapping("/weekly-finance")
    public ResponseEntity<List<WeeklyFinanceDTO>> getWeeklyFinance() {
        return ResponseEntity.ok(
                bookingService.getWeeklyFinance()
        );
    }
}
