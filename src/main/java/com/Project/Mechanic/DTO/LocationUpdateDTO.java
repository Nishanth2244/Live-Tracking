package com.Project.Mechanic.DTO;

import lombok.Data;

@Data
public class LocationUpdateDTO {
    private Long mechanicId;
    private Long userId;
    private double lat;
    private double lon;
}