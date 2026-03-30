package com.flower.service;

import com.flower.entity.ErrorFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ErrorFeedbackService {
    ErrorFeedback createFeedback(Integer userId, String feedbackType, String targetType, Integer targetId, String targetName, String content);

    Page<ErrorFeedback> getUserFeedbacksForTarget(Integer userId, Integer targetId, String targetType, Pageable pageable);

    boolean exceedsDailyLimit(Integer userId, Integer targetId, String targetType, int limit);

    ErrorFeedback getFeedback(Integer id);

    Page<ErrorFeedback> getUserFeedbacks(Integer userId, Pageable pageable);

    Page<ErrorFeedback> getAllFeedbacks(Pageable pageable);

    Page<ErrorFeedback> getFeedbacksByStatus(String status, Pageable pageable);

    Page<ErrorFeedback> getFeedbacksByType(String feedbackType, Pageable pageable);

    ErrorFeedback replyToFeedback(Integer feedbackId, String adminReply);

    ErrorFeedback updateFeedbackStatus(Integer feedbackId, String status);
}