package com.Project.Mechanic.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyJobsDTO {

    private String day;

    private Long totalJobs;
}
