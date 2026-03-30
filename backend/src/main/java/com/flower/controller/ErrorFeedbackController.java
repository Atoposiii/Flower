package com.flower.controller;

import com.flower.entity.ErrorFeedback;
import com.flower.service.ErrorFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/feedback")
public class ErrorFeedbackController {

    @Autowired
    private ErrorFeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> createFeedback(
            @RequestParam Integer userId,
            @RequestParam String feedbackType,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Integer targetId,
            @RequestParam(required = false) String targetName,
            @RequestParam String content) {
        if (targetId != null && targetType != null) {
            if (feedbackService.exceedsDailyLimit(userId, targetId, targetType, 3)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "今日对该花卉的反馈次数已达上限");
                return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
            }
        }
        ErrorFeedback feedback = feedbackService.createFeedback(
                userId, feedbackType, targetType, targetId, targetName, content);
        if (feedback == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(feedback, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ErrorFeedback> getFeedback(@PathVariable Integer id) {
        ErrorFeedback feedback = feedbackService.getFeedback(id);
        return feedback != null ?
                new ResponseEntity<>(feedback, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFeedbacks(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer targetId,
            @RequestParam(required = false) String targetType) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorFeedback> feedbacks;
        if (targetId != null && targetType != null) {
            feedbacks = feedbackService.getUserFeedbacksForTarget(userId, targetId, targetType, pageable);
        } else {
            feedbacks = feedbackService.getUserFeedbacks(userId, pageable);
        }
        return buildPageResponse(feedbacks);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorFeedback> feedbacks = feedbackService.getAllFeedbacks(pageable);
        return buildPageResponse(feedbacks);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getFeedbacksByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorFeedback> feedbacks = feedbackService.getFeedbacksByStatus(status, pageable);
        return buildPageResponse(feedbacks);
    }

    @GetMapping("/type/{feedbackType}")
    public ResponseEntity<Map<String, Object>> getFeedbacksByType(
            @PathVariable String feedbackType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorFeedback> feedbacks = feedbackService.getFeedbacksByType(feedbackType, pageable);
        return buildPageResponse(feedbacks);
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<ErrorFeedback> replyToFeedback(
            @PathVariable Integer id,
            @RequestParam String adminReply) {
        ErrorFeedback feedback = feedbackService.replyToFeedback(id, adminReply);
        return feedback != null ?
                new ResponseEntity<>(feedback, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ErrorFeedback> updateFeedbackStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        ErrorFeedback feedback = feedbackService.updateFeedbackStatus(id, status);
        return feedback != null ?
                new ResponseEntity<>(feedback, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Map<String, Object>> buildPageResponse(Page<ErrorFeedback> feedbacks) {
        Map<String, Object> result = new HashMap<>();
        result.put("content", feedbacks.getContent());
        result.put("totalElements", feedbacks.getTotalElements());
        result.put("totalPages", feedbacks.getTotalPages());
        result.put("currentPage", feedbacks.getNumber());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}