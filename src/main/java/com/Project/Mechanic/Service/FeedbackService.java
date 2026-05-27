package com.Project.Mechanic.Service;

import com.Project.Mechanic.DTO.FeedbackDTO;
import com.Project.Mechanic.DTO.FeedbackResponseDTO;
import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.Feedback;
import com.Project.Mechanic.ExceptionHandler.AlreadyExistsException;
import com.Project.Mechanic.ExceptionHandler.BadRequestException;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.Repo.BookingRepository;
import com.Project.Mechanic.Repo.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookingRepository bookingRepository;

    // CREATE
    public String addFeedback(Long BookingId,Long userId,FeedbackDTO dto) {

        Booking book=bookingRepository.findById(BookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking not found"));

        if (feedbackRepository.existsByBooking_Id(BookingId)) {

            throw new AlreadyExistsException(
                    "Feedback already submitted"
            );
        }

        if (dto.getRating() < 1 ||
                dto.getRating() > 5) {

            throw new BadRequestException(
                    "Rating must be between 1 and 5"
            );
        }

        Feedback feedback = new Feedback();

        feedback.setBooking(book);

        feedback.setUserId(userId);

        feedback.setMechanicId(book.getMechanicId());

        feedback.setRating(dto.getRating());

        feedback.setReview(dto.getReview());

        feedbackRepository.save(feedback);

        return "Feedback Submitted Successfully";
    }

    // GET ALL FEEDBACKS
    public List<FeedbackResponseDTO> getAllFeedbacks(Long userId) {

        return feedbackRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public List<FeedbackResponseDTO> getAllMechanics(Long mechanicId) {

        return feedbackRepository
                .findByMechanicId(mechanicId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public List<FeedbackResponseDTO> getAllFeedback() {

        return feedbackRepository
                .findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // GET FEEDBACK BY ID
    public FeedbackResponseDTO getFeedbackById(Long id) {

        Feedback feedback = feedbackRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        return mapToDTO(feedback);
    }

    // GET FEEDBACKS OF MECHANIC
    public List<FeedbackResponseDTO> getMechanicFeedbacks(Long mechanicId) {

        return feedbackRepository
                .findByMechanicId(mechanicId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    // UPDATE FEEDBACK
    public String updateFeedback(Long feedbackId,FeedbackDTO dto) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                        .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        feedback.setRating(dto.getRating());

        feedback.setReview(dto.getReview());

        feedbackRepository.save(feedback);

        return "Feedback Updated Successfully";
    }

    // DELETE FEEDBACK
    public String deleteFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                        .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        feedbackRepository.delete(feedback);

        return "Feedback Deleted Successfully";
    }

    // MAPPER
    private FeedbackResponseDTO mapToDTO(
            Feedback feedback
    ) {

        return FeedbackResponseDTO.builder()
                .id(feedback.getId())
                .bookingId(feedback.getBooking().getId())
                .userId(feedback.getUserId())
                .mechanicId(feedback.getMechanicId())
                .rating(feedback.getRating())
                .review(feedback.getReview())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}