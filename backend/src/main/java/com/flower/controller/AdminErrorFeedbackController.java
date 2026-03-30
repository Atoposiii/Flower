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

@RestController
@RequestMapping("/admin/feedback")
public class AdminErrorFeedbackController {

    @Autowired
    private ErrorFeedbackService errorFeedbackService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String feedbackType) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorFeedback> feedbacks;

        if (status != null && !status.isEmpty()) {
            feedbacks = errorFeedbackService.getFeedbacksByStatus(status, pageable);
        } else if (feedbackType != null && !feedbackType.isEmpty()) {
            feedbacks = errorFeedbackService.getFeedbacksByType(feedbackType, pageable);
        } else {
            feedbacks = errorFeedbackService.getAllFeedbacks(pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", feedbacks.getContent());
        result.put("totalElements", feedbacks.getTotalElements());
        result.put("totalPages", feedbacks.getTotalPages());
        result.put("currentPage", feedbacks.getNumber());
        result.put("pageSize", feedbacks.getSize());
        result.put("hasNext", feedbacks.hasNext());
        result.put("hasPrevious", feedbacks.hasPrevious());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ErrorFeedback> getFeedback(@PathVariable Integer id) {
        ErrorFeedback feedback = errorFeedbackService.getFeedback(id);
        return feedback != null ?
                new ResponseEntity<>(feedback, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFeedbacks(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorFeedback> feedbacks = errorFeedbackService.getUserFeedbacks(userId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", feedbacks.getContent());
        result.put("totalElements", feedbacks.getTotalElements());
        result.put("totalPages", feedbacks.getTotalPages());
        result.put("currentPage", feedbacks.getNumber());
        result.put("pageSize", feedbacks.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<Map<String, Object>> replyToFeedback(
            @PathVariable Integer id,
            @RequestParam String adminReply) {
        try {
            ErrorFeedback feedback = errorFeedbackService.replyToFeedback(id, adminReply);
            if (feedback != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "回复成功");
                result.put("data", feedback);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "反馈不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "回复失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateFeedbackStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            ErrorFeedback feedback = errorFeedbackService.updateFeedbackStatus(id, status);
            if (feedback != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "状态更新成功");
                result.put("data", feedback);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "反馈不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 1);

        Page<ErrorFeedback> pendingFeedbacks = errorFeedbackService.getFeedbacksByStatus("pending", pageable);
        Page<ErrorFeedback> processingFeedbacks = errorFeedbackService.getFeedbacksByStatus("processing", pageable);
        Page<ErrorFeedback> resolvedFeedbacks = errorFeedbackService.getFeedbacksByStatus("resolved", pageable);
        Page<ErrorFeedback> closedFeedbacks = errorFeedbackService.getFeedbacksByStatus("closed", pageable);

        result.put("pendingCount", pendingFeedbacks.getTotalElements());
        result.put("processingCount", processingFeedbacks.getTotalElements());
        result.put("resolvedCount", resolvedFeedbacks.getTotalElements());
        result.put("closedCount", closedFeedbacks.getTotalElements());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/type/{feedbackType}")
    public ResponseEntity<Map<String, Object>> getFeedbacksByType(
            @PathVariable String feedbackType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorFeedback> feedbacks = errorFeedbackService.getFeedbacksByType(feedbackType, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", feedbacks.getContent());
        result.put("totalElements", feedbacks.getTotalElements());
        result.put("totalPages", feedbacks.getTotalPages());
        result.put("currentPage", feedbacks.getNumber());
        result.put("pageSize", feedbacks.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}