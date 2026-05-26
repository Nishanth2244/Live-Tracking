package com.Project.Mechanic.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDTO {
    private Double expenses;
    private Double profit;
}
