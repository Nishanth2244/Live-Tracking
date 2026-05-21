package com.Project.Mechanic.Controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.Project.Mechanic.DTO.LocationUpdateDTO;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/mechanic-location")
    public void updateMechanicLocation(@RequestBody LocationUpdateDTO location) {
        
        String destination = "/topic/user/" + location.getUserId();
        
        System.out.println("📍 Routing location from Mechanic " + location.getMechanicId() + " to User " + location.getUserId());
        
        messagingTemplate.convertAndSend(destination, location);
    }
}