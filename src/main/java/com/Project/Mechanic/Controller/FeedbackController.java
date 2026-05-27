package com.Project.Mechanic.Controller;

import com.Project.Mechanic.DTO.FeedbackDTO;
import com.Project.Mechanic.DTO.FeedbackResponseDTO;
import com.Project.Mechanic.Service.FeedbackService;
import com.Project.Mechanic.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final JwtService jwtService;

    // CREATE
    @PostMapping("/add/{bookingId}")
    public ResponseEntity<String> addFeedback(@PathVariable Long bookingId,
                                              @RequestHeader("Authorization") String token,
                                              @RequestBody FeedbackDTO dto) {

        Long userId = jwtService.extractUserId(token.substring(7));

        return ResponseEntity.ok(feedbackService.addFeedback(bookingId,userId,dto));
    }

    // GET ALL
    @GetMapping("/user/all")
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedbacks(@RequestHeader("Authorization") String token) {

        Long userId = jwtService.extractUserId(token.substring(7));

        return ResponseEntity.ok(feedbackService.getAllFeedbacks(userId));
    }
    @GetMapping("/mechanic/all")
    public ResponseEntity<List<FeedbackResponseDTO>> getAll(@RequestHeader("Authorization") String token) {

        Long mechanicId = jwtService.extractUserId(token.substring(7));

        return ResponseEntity.ok(feedbackService.getAllMechanics(mechanicId));
    }
    @GetMapping("/admin/all")
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedbackByAdmin() {

        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDTO> getFeedbackById(@PathVariable Long id) {

        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    // GET MECHANIC FEEDBACKS
    @GetMapping("/admin/{mechanicId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getMechanicFeedbacks(@PathVariable Long mechanicId) {

        return ResponseEntity.ok(feedbackService.getMechanicFeedbacks(mechanicId));
    }

    // UPDATE
    @PutMapping("/update/{feedbackId}")
    public ResponseEntity<String> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackDTO dto
    ) {
        return ResponseEntity.ok(feedbackService.updateFeedback(feedbackId, dto));
    }

    // DELETE
    @DeleteMapping("/delete/{feedbackId}")
    public ResponseEntity<String> deleteFeedback(
            @PathVariable Long feedbackId
    ) {
        return ResponseEntity.ok(feedbackService.deleteFeedback(feedbackId));
    }
}