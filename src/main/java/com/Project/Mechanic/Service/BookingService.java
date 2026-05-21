package com.Project.Mechanic.Service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.BookingNotificationDTO;
import com.Project.Mechanic.DTO.BookingRequestDTO;
import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.Repo.BookingRepository;
import com.Project.Mechanic.Repo.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingService {
	
	private final BookingRepository bookingRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final UserRepository userRepository;

	public void booking(BookingRequestDTO bookingRequestDTO) {
		
		Booking booking = new Booking();
		booking.setUserId(bookingRequestDTO.getUserId());
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
		messagingTemplate.convertAndSend(destination, "NEW_REQUEST:" + dto);				
		
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
	    
	    String userDestination = "/topic/user/" + booking.getUserId();
	    messagingTemplate.convertAndSend(userDestination, dto);
	    log.info("Booking Confirmation Notification sent to User");

		return "Booking Accepted! You are now assigned to this request.";
	}
	
	


	@Transactional
	public String completeBooking(Long bookingId, Long mechanicId) {
	    
	    Booking booking = bookingRepository.findById(bookingId)
	            .orElseThrow(() -> new RuntimeException("Booking not found"));
	            
	    if (booking.getStatus() != BookingStatus.ACCEPTED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
	        throw new RuntimeException("Cannot complete this booking. Invalid state: " + booking.getStatus());
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
	            .problem("Total Bill: Rs. 500 (Pay via Razorpay)")
	            .build();
	            
	    String userDestination = "/topic/user/" + booking.getUserId();
	    messagingTemplate.convertAndSend(userDestination, dto);
	    log.info("Service completed sent notification to User");
	    return "Booking Completed! You are now available for new requests.";
	}

}
