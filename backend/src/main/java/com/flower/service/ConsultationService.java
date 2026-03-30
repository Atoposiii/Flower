package com.flower.service;

import com.flower.entity.ConsultationMessage;
import com.flower.entity.ConsultationSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ConsultationService {
    ConsultationSession createSession(Integer userId, String channel, String title);

    ConsultationSession createSessionForFlower(Integer userId, String channel, Integer flowerId, String title);

    ConsultationSession getSession(Integer sessionId);

    Page<ConsultationSession> getUserSessions(Integer userId, Pageable pageable);

    Page<ConsultationSession> getUserFlowerSessions(Integer userId, Integer flowerId, String channel, Pageable pageable);

    Page<ConsultationSession> getSessionsByChannel(String channel, Pageable pageable);

    Page<ConsultationSession> getAllSessions(Pageable pageable);

    Page<ConsultationSession> getSessionsByStatus(String status, Pageable pageable);

    Map<String, Object> sendMessage(Integer sessionId, Integer senderId, String senderType, String content);

    List<ConsultationMessage> getSessionMessages(Integer sessionId);

    ConsultationSession closeSession(Integer sessionId);

    boolean isUserMember(Integer userId);

    void createRefundNotificationSession(Integer userId, String message);
}