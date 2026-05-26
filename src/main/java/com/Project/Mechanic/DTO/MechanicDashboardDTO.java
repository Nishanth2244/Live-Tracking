package com.Project.Mechanic.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MechanicDashboardDTO {

    private Long totalJobs;

    private Long completedJobs;

    private Long acceptedJobs;

    private Long pendingJobs;

    private Long rejectedJobs;
}