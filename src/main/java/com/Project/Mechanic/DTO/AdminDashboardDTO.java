package com.Project.Mechanic.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardDTO {
    private long totalUsers;
    private long totalMechanics;
    private long pendingMechanicApprovals;
    private long totalBookings;
    private double totalRevenue;
}