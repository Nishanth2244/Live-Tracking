package com.Project.Mechanic.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDashboardDTO {

    private Long totalBookings;

    private Long pendingBookings;

    private Long acceptedBookings;

    private Long completedBookings;
}