package com.Project.Mechanic.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.Project.Mechanic.DTO.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.Repo.BookingRepository;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class MechanicService {
	
	private UserRepository userRepository;
	private final BookingRepository bookingRepository;

	public List<NearbyMechanicResDTO> searchNearbyMechanics(double userLat, double userLon) {
	    List<MechanicDistanceProjection> mechanics = userRepository.findNearestMechanicsWithDistance(userLon, userLat);
	    
	    return mechanics.stream().map(mechanic -> {
	        NearbyMechanicResDTO dto = new NearbyMechanicResDTO();
	        dto.setId(mechanic.getId());
	        dto.setName(mechanic.getName());
	        dto.setEmail(mechanic.getEmail());
	        dto.setPhoneNo(mechanic.getPhone());
	        dto.setLatitude(mechanic.getLatitude());
	        dto.setLongitude(mechanic.getLongitude());
	        dto.setExp(mechanic.getExperience());
	        
	        double distanceInKm = mechanic.getDistance() / 1000.0;
	        dto.setDistance(Math.round(distanceInKm * 100.0) / 100.0); 
	        
	        return dto;
	    }).collect(Collectors.toList());
	}
	

	public List<MechanicBookingHistoryDTO> getMechanicHistory(Long mechanicId, BookingStatus status, Pageable pageable) {
		
        List<Object[]> results = bookingRepository.findMechanicBookingHistory(mechanicId, status, pageable);

        if (results == null || results.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found with status: " + status.name());
        }

        return results.stream().map(row -> {
            Booking booking = (Booking) row[0];
            String customerName = (String) row[1];
            String customerPhone = (String) row[2];

            return MechanicBookingHistoryDTO.builder()
                    .bookingId(booking.getId())
                    .bookedTime(booking.getCreatedAt())
                    .problem(booking.getProblem())
                    .status(booking.getStatus())
                    .customerName(customerName != null ? customerName : "Unknown User")
                    .customerPhone(customerPhone != null ? customerPhone : "N/A")
                    .latitude(booking.getBreakdownLocation() != null ? booking.getBreakdownLocation().getY() : 0.0)
                    .longitude(booking.getBreakdownLocation() != null ? booking.getBreakdownLocation().getX() : 0.0)
                    .totalAmount(booking.getTotalAmount())
                    .build();
        }).collect(Collectors.toList());
    }
    public MechanicDashboardDTO getMechanicDashboard(
            Long mechanicId
    ) {

        Long totalJobs =
                bookingRepository.countByMechanicId(
                        mechanicId
                );

        Long completedJobs =
                bookingRepository
                        .countByMechanicIdAndStatus(
                                mechanicId,
                                BookingStatus.COMPLETED
                        );

        Long acceptedJobs =
                bookingRepository
                        .countByMechanicIdAndStatus(
                                mechanicId,
                                BookingStatus.ACCEPTED
                        );

        Long pendingJobs =
                bookingRepository
                        .countByMechanicIdAndStatus(
                                mechanicId,
                                BookingStatus.PENDING
                        );
        Long rejectedJobs =
                bookingRepository
                        .countByMechanicIdAndStatus(
                                mechanicId,
                                BookingStatus.REJECTED
                        );

        return MechanicDashboardDTO.builder()
                .totalJobs(totalJobs)
                .completedJobs(completedJobs)
                .acceptedJobs(acceptedJobs)
                .pendingJobs(pendingJobs)
                .rejectedJobs(rejectedJobs)
                .build();
    }
    public List<WeeklyJobsDTO> getWeeklyJobs(
            Long mechanicId
    ) {

        List<Object[]> results =
                bookingRepository.getWeeklyJobs(
                        mechanicId
                );

        Map<String, Long> jobsMap =
                new HashMap<>();

        // Store database values
        for (Object[] obj : results) {

            String day =
                    obj[0].toString().trim();

            Long jobs =
                    ((Number) obj[1]).longValue();

            jobsMap.put(day, jobs);
        }

        // All days
        List<String> days = Arrays.asList(
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        );

        List<WeeklyJobsDTO> response =
                new ArrayList<>();

        for (String day : days) {

            response.add(
                    new WeeklyJobsDTO(
                            day,
                            jobsMap.getOrDefault(day, 0L)
                    )
            );
        }

        return response;
    }
}
