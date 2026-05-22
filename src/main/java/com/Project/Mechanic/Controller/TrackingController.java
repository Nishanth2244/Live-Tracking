package com.Project.Mechanic.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.Project.Mechanic.DTO.LocationUpdateDTO;
import com.Project.Mechanic.Service.JwtService;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtService jwtService;

    @MessageMapping("/mechanic/location")
    public void updateMechanicLocation(@RequestBody LocationUpdateDTO location,
    									@RequestHeader("Authorization") String token) {
        
    	Long mechanicId = jwtService.extractUserId(token.substring(7));
        String destination = "/topic/user/tracking/" + location.getUserId();
        
        System.out.println("📍 Routing location from Mechanic " + mechanicId + " to User " + location.getUserId());
        
        messagingTemplate.convertAndSend(destination, location);
    }
}