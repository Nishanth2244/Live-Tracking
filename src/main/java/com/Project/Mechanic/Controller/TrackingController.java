package com.Project.Mechanic.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import com.Project.Mechanic.DTO.LocationUpdateDTO;
import com.Project.Mechanic.Service.JwtService;


@Slf4j
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtService jwtService;

    @MessageMapping("/mechanic/location")
    public void updateMechanicLocation(@Payload LocationUpdateDTO location, 
    									Authentication authentication) {

        Long authenticatedMechanicId = (Long) authentication.getDetails();
        
        String destination = "/topic/user/tracking/" + location.getUserId();
        
        log.info("📍 [Via WS] Routing location from Authentic Mechanic {} to User {}", authenticatedMechanicId, location.getUserId());
        
        messagingTemplate.convertAndSend(destination, location);
    }
}