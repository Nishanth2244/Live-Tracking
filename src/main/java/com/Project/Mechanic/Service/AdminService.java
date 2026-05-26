package com.Project.Mechanic.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.AdminBookingHistoryDTO;
import com.Project.Mechanic.DTO.AdminBookingHistoryDTO.AdminBookingHistoryDTOBuilder;
import com.Project.Mechanic.DTO.AdminDashboardDTO;
import com.Project.Mechanic.DTO.NearbyMechanicResDTO;
import com.Project.Mechanic.DTO.UserProfileResDTO;
import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.BookingStatus;
import com.Project.Mechanic.Entity.Roles;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.Repo.BookingRepository;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
@Service
public class AdminService {
	
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	
	public List<NearbyMechanicResDTO> mapToDto(List<Users> users){
		
		return users.stream()
				.map(user -> {
					NearbyMechanicResDTO dto = new NearbyMechanicResDTO();
					dto.setId(user.getId());
					dto.setName(user.getName());
					dto.setEmail(user.getEmail());
					dto.setPhoneNo(user.getPhone());
					dto.setExp(user.getExperience());
					if(user.getLocation() != null) {
						dto.setLatitude(user.getLocation().getY());
						dto.setLongitude(user.getLocation().getX());
					}
					return dto;
				}).collect(Collectors.toList());
				
	}
	


	public List<NearbyMechanicResDTO> pendingMech(boolean status) {
		
		List<Users> users = userRepository.findByApprovalStatus(status);
		
		if(users.isEmpty()) {
			throw new ResourceNotFoundException("No pending mechanics");
		}
		
		return mapToDto(users);
	}

	public String approveMech(Long id, boolean status) {
		
		Users users = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Mech Not found to approve"));
		
		users.setApprovalStatus(status);
		users.setIsAvailable(status);
		
		userRepository.save(users);
		
		log.info("User is approved by ADMIN: {}", users.getName());
		return "Mechanic approved Succesfully";
	}

	
	public List<AdminBookingHistoryDTO> getHistory(BookingStatus status) {
		
		List<Object[]> results = bookingRepository.findBookingHistoryWithNames(status);
		
		if(results == null || results.isEmpty()) {
			throw new ResourceNotFoundException("No bookings found for status: " + status);
		}
		
		log.info("Fetching bookings history for ADMIN");
		return results.stream()
				.map(row -> {
					Booking booking = (Booking) row[0];
					String userName = (String) row[1];
					String mechanicName = (String) row[2];

					return AdminBookingHistoryDTO.builder()
							.bookingId(booking.getId())
							.bookedTime(booking.getCreatedAt())
							.lat(booking.getBreakdownLocation().getY())
							.lon(booking.getBreakdownLocation().getX())
							.problem(booking.getProblem())
							.status(booking.getStatus())
							.userName(userName != null ? userName : "Unknown User")
							.mechanicName(mechanicName != null ? mechanicName : "Unknown Mechanic")
							.totaAmount(booking.getTotalAmount())
							.billDetails(booking.getBillingDetails())
							.serviceCharge(booking.getServiceCharge())
							.extraCharges(booking.getExtraCharges())
							.partsCost(booking.getPartsCost())
							.build();
				}).collect(Collectors.toList());
	}
	
	
	
	public AdminDashboardDTO getDashboardStats() {
	    log.info("Fetching Admin Dashboard Statistics");
	    
	    long usersCount = userRepository.countByRoles(Roles.USER);
	    long mechanicsCount = userRepository.countByRoles(Roles.MECHANIC);
	    long pendingMechanics = userRepository.countByRolesAndApprovalStatus(Roles.MECHANIC, false);
	    
	    long totalBookings = bookingRepository.count();
	    Double revenue = bookingRepository.getTotalRevenue(BookingStatus.COMPLETED);

	    return AdminDashboardDTO.builder()
	            .totalUsers(usersCount)
	            .totalMechanics(mechanicsCount)
	            .pendingMechanicApprovals(pendingMechanics)
	            .totalBookings(totalBookings)
	            .totalRevenue(revenue)
	            .build();
	}
	
	
	public String toggleUserAccess(Long userId, boolean hasAccess) {
	    Users user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found to update access"));
	            
	    user.setApprovalStatus(hasAccess);
	    
	    if(user.getRoles() == Roles.MECHANIC && !hasAccess) {
	        user.setIsAvailable(false);
	    }
	    
	    userRepository.save(user);
	    
	    String action = hasAccess ? "UNBLOCKED" : "BLOCKED";
	    log.info("User {} has been {} by Admin", user.getEmail(), action);
	    
	    return "User account successfully " + action.toLowerCase() + ".";
	}
	
	
	public List<UserProfileResDTO> getUsersByRole(Roles role) {
	    List<Users> users = userRepository.findByRoles(role);
	    
	    if(users.isEmpty()) {
	        throw new ResourceNotFoundException("No users found for role: " + role.name());
	    }
	    
	    return users.stream().map(user -> {
	        UserProfileResDTO dto = new UserProfileResDTO();
	        dto.setId(user.getId());
	        dto.setName(user.getName());
	        dto.setEmail(user.getEmail());
	        dto.setRole(user.getRoles().name());
	        dto.setPhone(user.getPhone());
	        dto.setExperience(user.getExperience());
	        dto.setIsAvailable(user.getIsAvailable());
	        dto.setApprovalStatus(user.getApprovalStatus());
	        return dto;
	    }).collect(Collectors.toList());
	}
    public List<UserProfileResDTO> getAllMechanics(){
        List<Users> mechUsers=userRepository.findByRoles(Roles.MECHANIC);
        if(mechUsers.isEmpty()) {
            throw new ResourceNotFoundException("No Mechanic user Found");
        }
        return mechUsers.stream().map(user -> {
            UserProfileResDTO dto = new UserProfileResDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRoles().name());
            dto.setPhone(user.getPhone());
            dto.setExperience(user.getExperience());
            dto.setIsAvailable(user.getIsAvailable());
            dto.setApprovalStatus(user.getApprovalStatus());
            return dto;
        }).collect(Collectors.toList());

    }

}
