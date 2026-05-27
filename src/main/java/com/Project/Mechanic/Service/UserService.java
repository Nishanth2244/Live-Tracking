package com.Project.Mechanic.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.Project.Mechanic.DTO.UserDashboardDTO;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.ProfileUpdateDTO;
import com.Project.Mechanic.DTO.UserBookingHistoryDTO;
import com.Project.Mechanic.DTO.UserProfileResDTO;
import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.Repo.BookingRepository;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	
	public UserProfileResDTO getUserProfile(Long userId) {
	    Users user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User profile not found for ID: " + userId));

	    UserProfileResDTO dto = new UserProfileResDTO();
	    dto.setId(user.getId());
	    dto.setName(user.getName());
	    dto.setEmail(user.getEmail());
	    dto.setRole(user.getRoles().name());
	    dto.setPhone(user.getPhone());
	    dto.setExperience(user.getExperience());
	    dto.setIsAvailable(user.getIsAvailable());

	    if (user.getLocation() != null) {
	        dto.setLatitude(user.getLocation().getY());
	        dto.setLongitude(user.getLocation().getX());
	    }

	    return dto;
	}

	public void updateProfile(Long userId, ProfileUpdateDTO profileUpdateDTO) {
		
		Users users = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not found to Update Profile"));
		
		if(profileUpdateDTO.getName() != null) {
			users.setName(profileUpdateDTO.getName());
		}
		
		if(profileUpdateDTO.getPhoneNo() != null) {
			users.setPhone(profileUpdateDTO.getPhoneNo());
		}
		
		if(profileUpdateDTO.getExperience() != null) {
			users.setExperience(profileUpdateDTO.getExperience());
		}
		
		
		userRepository.save(users);
		
	}
	
	
	
	public List<UserBookingHistoryDTO> getUserHistory(Long userId, BookingStatus status) {
		
	    List<Object[]> results = bookingRepository.findUserBookingHistory(userId, status);

	    if (results == null || results.isEmpty()) {
	        throw new ResourceNotFoundException("No bookings found with status: " + status.name());
	    }

	    return results.stream().map(row -> {
	        Booking booking = (Booking) row[0];
	        String mechanicName = (String) row[1];
	        String mechanicPhone = (String) row[2];

	        return UserBookingHistoryDTO.builder()
	                .bookingId(booking.getId())
	                .bookedTime(booking.getCreatedAt())
	                .problem(booking.getProblem())
	                .status(booking.getStatus())
	                .mechanicName(mechanicName != null ? mechanicName : "Waiting for Mechanic")
	                .mechanicPhone(mechanicPhone != null ? mechanicPhone : "N/A")
	                .totalAmount(booking.getTotalAmount())
	                .build();
	    }).collect(Collectors.toList());
	}
    public UserDashboardDTO getUserDashboard(
            Long userId
    ) {

        Long totalBookings =
                bookingRepository.countByUserId(
                        userId
                );

        Long pendingBookings =
                bookingRepository
                        .countByUserIdAndStatus(
                                userId,
                                BookingStatus.PENDING
                        );

        Long acceptedBookings =
                bookingRepository
                        .countByUserIdAndStatus(
                                userId,
                                BookingStatus.ACCEPTED
                        );

        Long completedBookings =
                bookingRepository
                        .countByUserIdAndStatus(
                                userId,
                                BookingStatus.COMPLETED
                        );

        return UserDashboardDTO.builder()
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .acceptedBookings(acceptedBookings)
                .completedBookings(completedBookings)
                .build();
    }


}
