package com.Project.Mechanic.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingNotificationDTO {

    private Long bookingId;
    private Long userId;
    private Long mechanicId;
    private String problem;
    private String status;
    private double latitude;
    private double longitude;
}