package com.Project.Mechanic.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.ChangePasswordDTO;
import com.Project.Mechanic.DTO.LoginDto;
import com.Project.Mechanic.DTO.MechanicRegistrationDto;
import com.Project.Mechanic.DTO.ResetPasswordDTO;
import com.Project.Mechanic.DTO.UserRegistrationDto;
import com.Project.Mechanic.DTO.VerifyOtpDTO;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.ForbiddenException;
import com.Project.Mechanic.Repo.UserRepository;
import com.Project.Mechanic.Service.AuthService;
import com.Project.Mechanic.Service.CustomUserDetailsService;
import com.Project.Mechanic.Service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationDto dto) {
        String response = authService.registerUser(dto);
        return response;
    }
    
    @PostMapping("/mechRegister")
    public String mechRegister(@RequestBody MechanicRegistrationDto mechanicRegistrationDto) {
    	return authService.registerMechanic(mechanicRegistrationDto);
    }
    
    
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        
        Users user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));
        
        if (user.getApprovalStatus() == null || !user.getApprovalStatus()) {
            log.warn("Login denied for user: {}. Account pending approval or blocked.", dto.getEmail());
            throw new ForbiddenException("Your account is pending admin approval or has been blocked by the Admin.");
        }
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getAuthorities());
        extraClaims.put("userId", user.getId());
        
        log.info("User logged in Succesfully: {}", dto.getEmail());
        return jwtService.generateToken(extraClaims, userDetails);
    }

	
	@PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }
	
	@PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody VerifyOtpDTO dto) {
        return authService.verifyOtp(dto);
    }
	
	
	@PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordDTO dto) {
        return authService.resetPassword(dto);
    }
	
	
	@PostMapping("/Change-password")
	public String resetPassword(@RequestBody ChangePasswordDTO changePasswordDTO,
								@RequestHeader ("Authorization") String token) {
		
		Long userId = jwtService.extractUserId(token.substring(7));
		return authService.changePassword(userId, changePasswordDTO);
				
	}
}