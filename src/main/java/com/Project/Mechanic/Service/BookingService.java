package com.Project.Mechanic.Service;

import com.Project.Mechanic.DTO.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.ConflictException;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.ExceptionHandler.UnauthorizedException;
import com.Project.Mechanic.Repo.BookingRepository;
import com.Project.Mechanic.Repo.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingService {
	
	private final BookingRepository bookingRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final UserRepository userRepository;

	public void booking(BookingRequestDTO bookingRequestDTO, Long userId) {
		
		Booking booking = new Booking();
		booking.setUserId(userId);
		booking.setMechanicId(bookingRequestDTO.getMechanicId());
		booking.setProblem(bookingRequestDTO.getProblem());
		booking.setStatus(BookingStatus.PENDING);
		
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		Point location = geometryFactory.createPoint(new Coordinate(bookingRequestDTO.getLon(), bookingRequestDTO.getLat()));
		
		booking.setBreakdownLocation(location);
		
		Booking savedBooking = bookingRepository.save(booking);
		
		BookingNotificationDTO dto =
		        BookingNotificationDTO.builder()
		                .bookingId(savedBooking.getId())
		                .userId(savedBooking.getUserId())
		                .mechanicId(savedBooking.getMechanicId())
		                .problem(savedBooking.getProblem())
		                .status(savedBooking.getStatus().name())
		                .latitude(
		                    savedBooking.getBreakdownLocation().getY()
		                )
		                .longitude(
		                    savedBooking.getBreakdownLocation().getX()
		                )
		                .build();
		
		String mechanicId = bookingRequestDTO.getMechanicId().toString();
		
		String destination = "/topic/mechanic/"+ bookingRequestDTO.getMechanicId();
		
		log.info("PUSHING NOTIFICATION TO THIS EXACT LOCATION {}", destination);
		messagingTemplate.convertAndSend(destination, dto);				
		
	}

	public String accept(Long bookingId, Long mechanicId) {
		
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking Id not found to accept"));
		
		if(booking.getStatus() != BookingStatus.PENDING) {
			throw new RuntimeException("Alredy accepted by Other's");
		}
		
		if (!booking.getMechanicId().equals(mechanicId)) {
	        throw new RuntimeException("You are not authorized to accept this booking.");
	    }
		
		booking.setStatus(BookingStatus.ACCEPTED);
	    bookingRepository.save(booking);

	    Users mechanic = userRepository.findById(mechanicId)
	            .orElseThrow(() -> new RuntimeException("Mechanic not found"));
	            
	    mechanic.setIsAvailable(false);
	    userRepository.save(mechanic);
	    
	    BookingNotificationDTO dto = BookingNotificationDTO.builder()
	            .bookingId(booking.getId())
	            .userId(booking.getUserId())
	            .mechanicId(mechanicId)
	            .status(BookingStatus.ACCEPTED.name())
	            .build();
	    
	    String userDestination = "/topic/user/booking/" + booking.getUserId();
	    messagingTemplate.convertAndSend(userDestination, dto);
	    log.info("Booking Confirmation Notification sent to User via {}", userDestination);

		return "Booking Accepted! You are now assigned to this request.";
	}
	
	
	
	@Transactional
	public String generateBill(BillRequestDTO dto, Long mechanicId) {
		
		Booking booking = bookingRepository.findById(dto.getBookingId())
				.orElseThrow(() -> new ResourceNotFoundException("Booking Id not found to generate Bill"));
		
		if(!booking.getMechanicId().equals(mechanicId)) {
			throw new UnauthorizedException("You are not authorized to generate bill");
		}
		
		
		// Calculation & Save
	    double total = (dto.getServiceCharge() != null ? dto.getServiceCharge() : 0) +
	                   (dto.getPartsCost() != null ? dto.getPartsCost() : 0) +
	                   (dto.getExtraCharges() != null ? dto.getExtraCharges() : 0);
	    
	    booking.setServiceCharge(dto.getServiceCharge());
	    booking.setPartsCost(dto.getPartsCost());
	    booking.setExtraCharges(dto.getExtraCharges());
	    booking.setBillingDetails(dto.getBillingDetails());
	    booking.setTotalAmount(total);
	    
	    bookingRepository.save(booking);
	    
	    BookingNotificationDTO notification = BookingNotificationDTO.builder()
	            .bookingId(booking.getId())
	            .userId(booking.getUserId())
	            .mechanicId(mechanicId)
	            .status("BILL_GENERATED") 
	            .problem("Bill Generated: " + dto.getBillingDetails())
	            .totalAmount(total)
	            .build();

	    String userDestination = "/topic/user/billGenerate/" + booking.getUserId();
	    messagingTemplate.convertAndSend(userDestination, notification);
	    log.info("Bill sent to User via {}", userDestination);

	    return "Bill generated and sent to user for Rs. " + total;
	}

	


	@Transactional
	public String completeBooking(Long bookingId, Long mechanicId) {
	    
	    Booking booking = bookingRepository.findById(bookingId)
	            .orElseThrow(() -> new RuntimeException("Booking not found"));
	            
	    if (booking.getStatus() != BookingStatus.ACCEPTED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
	        throw new ConflictException("Cannot complete this booking. Invalid state: " + booking.getStatus());
	    }
	    
	    if (!booking.getMechanicId().equals(mechanicId)) {
	        throw new RuntimeException("You are not authorized to complete this booking.");
	    }

	    booking.setStatus(BookingStatus.COMPLETED);
	    bookingRepository.save(booking);

	    Users mechanic = userRepository.findById(mechanicId)
	            .orElseThrow(() -> new RuntimeException("Mechanic not found"));
	            
	    mechanic.setIsAvailable(true);
	    userRepository.save(mechanic);

	    BookingNotificationDTO dto = BookingNotificationDTO.builder()
	            .bookingId(booking.getId())
	            .userId(booking.getUserId())
	            .mechanicId(mechanicId)
	            .status(BookingStatus.COMPLETED.name())
	            .problem("Thank you for choosing our Mechanics! Your service is successfully completed.") 
	            .build();
	            
	    String userDestination = "/topic/user/complete/" + booking.getUserId();
	    messagingTemplate.convertAndSend(userDestination, dto);
	    log.info("Service completed sent notification to User");
	    return "Booking Completed! You are now available for new requests.";
	}

    public List<BookingDetailsDTO> upcomingBookings(int page,int size) {
        Pageable pageable= PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<Booking> bookings =
                bookingRepository.findByStatus(BookingStatus.PENDING,pageable);

        return bookings.stream().map(b -> {

            // Fetch user using userId
            Users user = userRepository.findById(b.getUserId())
                    .orElse(null);

            Point location = b.getBreakdownLocation();

            return BookingDetailsDTO.builder()
                    .id(b.getId())
                    .createdAt(b.getCreatedAt())
                    .problem(b.getProblem())
                    .status(b.getStatus())

                    .customerName(
                            user != null ? user.getName() : "Unknown"
                    )

                    .customerPhone(
                            user != null ? user.getPhone() : "No Phone"
                    )

                    .latitude(location.getY())
                    .longitude(location.getX())

                    .build();

        }).collect(Collectors.toList());
    }
    public List<BookingDetailsDTO> completedBookings(int page,int size) {
        Pageable pageable= PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<Booking> bookings =
                bookingRepository.findByStatus(BookingStatus.COMPLETED,pageable);

        return bookings.stream().map(b -> {

            // Fetch user using userId
            Users user = userRepository.findById(b.getUserId())
                    .orElse(null);
            Point location = b.getBreakdownLocation();
            return BookingDetailsDTO.builder()
                    .id(b.getId())
                    .createdAt(b.getCreatedAt())
                    .problem(b.getProblem())
                    .status(b.getStatus())
                    .customerName(
                            user != null ? user.getName() : "Unknown"
                    )
                    .customerPhone(
                            user != null ? user.getPhone() : "Unknown"
                    )
                    .latitude(location.getY())
                    .longitude(location.getX())
                    .billingDetails(b.getBillingDetails())
                    .totalAmount(b.getTotalAmount())
                    .build();

        }).collect(Collectors.toList());
    }
    public DashboardDTO getDashboardData() {

        Double expenses = bookingRepository.getTotalExpenses();

        Double profit = bookingRepository.getTotalProfit();

        return DashboardDTO.builder()
                .expenses(expenses)
                .profit(profit)
                .build();
    }
    public List<WeeklySalesDTO> getWeeklySales() {

        List<Object[]> results = bookingRepository.getWeeklySales();

        Map<String, Double> salesMap = new HashMap<>();

        for (Object[] obj : results) {

            String day = obj[0].toString().trim();

            Double sales =((Number) obj[1]).doubleValue();

            salesMap.put(day, sales);
        }
        List<String> days = Arrays.asList(
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        );

        // Build final response
        List<WeeklySalesDTO> response = new ArrayList<>();

        for (String day : days) {

            response.add(
                    new WeeklySalesDTO(
                            day,
                            salesMap.getOrDefault(day, 0.0)
                    )
            );
        }
        return response;
    }
    public List<WeeklyFinanceDTO> getWeeklyFinance() {

        List<Object[]> results =
                bookingRepository.getWeeklyFinance();

        Map<String, Double> profitMap = new HashMap<>();
        Map<String, Double> expenseMap = new HashMap<>();

        // Store DB results
        for (Object[] obj : results) {

            String day = obj[0].toString().trim();

            Double profit =
                    ((Number) obj[1]).doubleValue();

            Double expense =
                    ((Number) obj[2]).doubleValue();

            profitMap.put(day, profit);
            expenseMap.put(day, expense);
        }

        // All week days
        List<String> days = Arrays.asList(
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        );

        // Final response
        List<WeeklyFinanceDTO> response =
                new ArrayList<>();

        for (String day : days) {

            response.add(
                    new WeeklyFinanceDTO(
                            day,
                            profitMap.getOrDefault(day, 0.0),
                            expenseMap.getOrDefault(day, 0.0)
                    )
            );
        }

        return response;
    }
}
