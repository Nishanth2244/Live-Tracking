package com.Project.Mechanic.Controller;

import com.Project.Mechanic.DTO.LoginDto;
import com.Project.Mechanic.DTO.UserRegistrationDto;
import com.Project.Mechanic.Service.CustomUserDetailsService;
import com.Project.Mechanic.Service.JwtService;
import com.Project.Mechanic.Service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationDto dto) {
        String response = userService.registerUser(dto);
        return response;
    }
    
    
    @PostMapping("/login")
    public String loginUser(LoginDto dto) {
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