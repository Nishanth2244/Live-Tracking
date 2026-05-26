package com.Project.Mechanic.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String experience;
    private Boolean isAvailable;
    private Double latitude;
    private Double longitude;
    private Boolean approvalStatus;
}