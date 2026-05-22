package com.Project.Mechanic.DTO;
import lombok.Data;

@Data
public class BillRequestDTO {
    private Long bookingId;
    private Double serviceCharge;
    private Double partsCost;
    private Double extraCharges;
    private String billingDetails;
}