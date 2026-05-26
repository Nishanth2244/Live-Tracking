package com.Project.Mechanic.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyFinanceDTO {

    private String day;
    private Double profit;
    private Double expense;
}