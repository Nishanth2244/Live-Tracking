package com.Project.Mechanic.Service;

import java.time.LocalDateTime;
import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.ChangePasswordDTO;
import com.Project.Mechanic.DTO.MechanicRegistrationDto;
import com.Project.Mechanic.DTO.ResetPasswordDTO;
import com.Project.Mechanic.DTO.UserRegistrationDto;
import com.Project.Mechanic.DTO.VerifyOtpDTO;
import com.Project.Mechanic.Entity.Otps;
import com.Project.Mechanic.Entity.Roles;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.BadRequestException;
import com.Project.Mechanic.ExceptionHandler.ConflictException;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.Repo.OtpRepository;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; 
    private final OtpRepository otpRepository;

    public String registerUser(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists.");
        }

        Users user = new Users();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Roles.USER); 
        user.setApprovalStatus(true);

        userRepository.save(user);
        return "User registered successfully";
    }
    
    
    public String registerMechanic(MechanicRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists.");
        }

        Users mechanic = new Users();
        mechanic.setName(dto.getName());
        mechanic.setEmail(dto.getEmail());
        mechanic.setPassword(passwordEncoder.encode(dto.getPassword()));
        mechanic.setRoles(Roles.MECHANIC); 
        mechanic.setApprovalStatus(false);
        mechanic.setPhone(dto.getPhone());
        mechanic.setIsAvailable(false);

        // Convert Lat/Lon to PostGIS Point
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        
        Point pt = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        mechanic.setLocation(pt);

        userRepository.save(mechanic);
        return "Mechanic registered successfully. Pending Admin Approval.";
    }
    
    
    public String forgotPassword(String email) {
        
        if (!userRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("User not found with this email");
        }

        otpRepository.findByEmailAndIsActiveTrue(email).ifPresent(existingOtp -> {
            existingOtp.setActive(false);
            otpRepository.save(existingOtp);
        });

        String generatedOtp = String.format("%06d", new Random().nextInt(999999));
        
        Otps otpEntity = new Otps();
        otpEntity.setEmail(email);
        otpEntity.setOtp(generatedOtp);
        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(10)); // 10 mins validity
        otpEntity.setActive(true);
        
        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(email, generatedOtp);
        
        log.info("Otp sent to email: {}", email);
        return "OTP sent to your email successfully.";
    }
    
    
    public String verifyOtp(VerifyOtpDTO dto) {
    	
        Otps otp = otpRepository.findByEmailAndIsActiveTrue(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("No active OTP found. Please request a new one."));

        if (!otp.getOtp().equals(dto.getOtp())) {
            throw new BadRequestException("Invalid OTP.");
        }

        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            otp.setActive(false);
            otpRepository.save(otp);
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        otp.setActive(false);
        otpRepository.save(otp);
        
        log.info("Successfully otp verified");
        return "OTP Verified Successfully. You can now reset your password.";
    }
    


	public String resetPassword(ResetPasswordDTO dto) {
		
		Users user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		
        userRepository.save(user);
        
        return "Password reset successful! You can now login.";

	}


    public String changePassword(Long userId, ChangePasswordDTO dto) {
    	
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password that you entered is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        log.info("password has been reset successfully");
        return "Password updated successfully for User: "+ user.getEmail();
    }

}