package com.Project.Mechanic.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project.Mechanic.DTO.LoginDto;
import com.Project.Mechanic.DTO.MechanicRegistrationDto;
import com.Project.Mechanic.DTO.UserRegistrationDto;
import com.Project.Mechanic.Service.AuthService;
import com.Project.Mechanic.Service.CustomUserDetailsService;
import com.Project.Mechanic.Service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

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
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getAuthorities());
        
        return jwtService.generateToken(extraClaims, userDetails);
    }
}