package com.flower.service.impl;

import com.flower.entity.ErrorFeedback;
import com.flower.entity.User;
import com.flower.repository.ErrorFeedbackRepository;
import com.flower.repository.UserRepository;
import com.flower.service.ErrorFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class ErrorFeedbackServiceImpl implements ErrorFeedbackService {

    @Autowired
    private ErrorFeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ErrorFeedback createFeedback(Integer userId, String feedbackType, String targetType, Integer targetId, String targetName, String content) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        ErrorFeedback feedback = new ErrorFeedback();
        feedback.setUser(user);
        feedback.setFeedbackType(feedbackType);
        feedback.setTargetType(targetType);
        feedback.setTargetId(targetId);
        feedback.setTargetName(targetName);
        feedback.setContent(content);
        feedback.setStatus("pending");
        return feedbackRepository.save(feedback);
    }

    @Override
    public ErrorFeedback getFeedback(Integer id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    @Override
    public Page<ErrorFeedback> getUserFeedbacks(Integer userId, Pageable pageable) {
        return feedbackRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }

    @Override
    public Page<ErrorFeedback> getAllFeedbacks(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    @Override
    public Page<ErrorFeedback> getFeedbacksByStatus(String status, Pageable pageable) {
        return feedbackRepository.findByStatusOrderByCreateTimeDesc(status, pageable);
    }

    @Override
    public Page<ErrorFeedback> getFeedbacksByType(String feedbackType, Pageable pageable) {
        return feedbackRepository.findByFeedbackTypeOrderByCreateTimeDesc(feedbackType, pageable);
    }

    @Override
    public Page<ErrorFeedback> getUserFeedbacksForTarget(Integer userId, Integer targetId, String targetType, Pageable pageable) {
        return feedbackRepository.findByUserIdAndTargetIdAndTargetTypeOrderByCreateTimeDesc(userId, targetId, targetType, pageable);
    }

    @Override
    public boolean exceedsDailyLimit(Integer userId, Integer targetId, String targetType, int limit) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();
        long count = feedbackRepository.countByUserIdAndTargetIdAndTargetTypeSince(userId, targetId, targetType, startOfDay);
        return count >= limit;
    }

    @Override
    public ErrorFeedback replyToFeedback(Integer feedbackId, String adminReply) {
        Optional<ErrorFeedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isPresent()) {
            ErrorFeedback feedback = feedbackOpt.get();
            feedback.setAdminReply(adminReply);
            feedback.setAdminReplyTime(new Date());
            feedback.setStatus("resolved");
            return feedbackRepository.save(feedback);
        }
        return null;
    }

    @Override
    public ErrorFeedback updateFeedbackStatus(Integer feedbackId, String status) {
        Optional<ErrorFeedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isPresent()) {
            ErrorFeedback feedback = feedbackOpt.get();
            feedback.setStatus(status);
            return feedbackRepository.save(feedback);
        }
        return null;
    }
}