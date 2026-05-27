package com.Project.Mechanic.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponseDTO {

    private Long id;

    private Long bookingId;

    private Long userId;

    private Long mechanicId;

    private Integer rating;

    private String review;

    private LocalDateTime createdAt;
}