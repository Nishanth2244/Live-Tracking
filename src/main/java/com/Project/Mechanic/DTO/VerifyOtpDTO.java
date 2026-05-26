package com.Project.Mechanic.DTO;

import lombok.Data;

@Data
public class VerifyOtpDTO {
    private String email;
    private String otp;
}