package com.flower.service.impl;

import com.flower.entity.ConsultationMessage;
import com.flower.entity.ConsultationSession;
import com.flower.entity.MemberRecord;
import com.flower.entity.User;
import com.flower.repository.ConsultationMessageRepository;
import com.flower.repository.ConsultationSessionRepository;
import com.flower.repository.MemberRecordRepository;
import com.flower.repository.UserRepository;
import com.flower.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationSessionRepository sessionRepository;

    @Autowired
    private ConsultationMessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRecordRepository memberRecordRepository;

    @Override
    public ConsultationSession createSession(Integer userId, String channel, String title) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        if ("member".equals(channel) && !isUserMember(userId)) {
            return null;
        }

        ConsultationSession session = new ConsultationSession();
        session.setUser(user);
        session.setChannel(channel);
        session.setTitle(title);
        session.setStatus("open");
        return sessionRepository.save(session);
    }

    @Override
    public ConsultationSession createSessionForFlower(Integer userId, String channel, Integer flowerId, String title) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        if ("member".equals(channel) && !isUserMember(userId)) {
            return null;
        }
        ConsultationSession session = new ConsultationSession();
        session.setUser(user);
        session.setChannel(channel);
        session.setFlowerId(flowerId);
        session.setTitle(title);
        session.setStatus("open");
        return sessionRepository.save(session);
    }

    @Override
    public ConsultationSession getSession(Integer sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }

    @Override
    public Page<ConsultationSession> getUserSessions(Integer userId, Pageable pageable) {
        return sessionRepository.findByUserIdOrderByLastMessageTimeDesc(userId, pageable);
    }

    @Override
    public Page<ConsultationSession> getUserFlowerSessions(Integer userId, Integer flowerId, String channel, Pageable pageable) {
        return sessionRepository.findByUserIdAndFlowerIdAndChannelOrderByLastMessageTimeDesc(userId, flowerId, channel, pageable);
    }

    @Override
    public Page<ConsultationSession> getSessionsByChannel(String channel, Pageable pageable) {
        return sessionRepository.findByChannelOrderByLastMessageTimeDesc(channel, pageable);
    }

    @Override
    public Page<ConsultationSession> getAllSessions(Pageable pageable) {
        return sessionRepository.findAllOrderByLastMessageTimeDesc(pageable);
    }

    @Override
    public Page<ConsultationSession> getSessionsByStatus(String status, Pageable pageable) {
        return sessionRepository.findByStatusOrderByLastMessageTimeDesc(status, pageable);
    }

    @Override
    public Map<String, Object> sendMessage(Integer sessionId, Integer senderId, String senderType, String content) {
        ConsultationSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null || !"open".equals(session.getStatus())) {
            return null;
        }

        User sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) {
            return null;
        }

        ConsultationMessage message = new ConsultationMessage();
        message.setSession(session);
        message.setSender(sender);
        message.setSenderType(senderType);
        message.setContent(content);
        messageRepository.save(message);

        session.setLastMessageTime(new Date());
        sessionRepository.save(session);

        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("session", session);
        return result;
    }

    @Override
    public List<ConsultationMessage> getSessionMessages(Integer sessionId) {
        return messageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
    }

    @Override
    public ConsultationSession closeSession(Integer sessionId) {
        ConsultationSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.setStatus("closed");
            return sessionRepository.save(session);
        }
        return null;
    }

    @Override
    public boolean isUserMember(Integer userId) {
        Optional<MemberRecord> record = memberRecordRepository.findByUserId(userId);
        return record.isPresent() && "active".equals(record.get().getStatus());
    }

    @Override
    public void createRefundNotificationSession(Integer userId, String message) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) return;

            // Find or create a system notification session for this user
            ConsultationSession session = new ConsultationSession();
            session.setUser(userOpt.get());
            session.setChannel("system");
            session.setTitle("退款通知");
            session.setStatus("open");
            session = sessionRepository.save(session);

            ConsultationMessage msg = new ConsultationMessage();
            msg.setSession(session);
            msg.setSender(userOpt.get()); // use user as sender for system messages
            msg.setSenderType("admin");
            msg.setContent(message);
            messageRepository.save(msg);
        } catch (Exception e) {
            // Non-critical: notification failure should not block refund
        }
    }
}