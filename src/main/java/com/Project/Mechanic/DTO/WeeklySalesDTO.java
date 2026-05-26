package com.Project.Mechanic.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklySalesDTO {

    private String day;
    private Double sales;
}