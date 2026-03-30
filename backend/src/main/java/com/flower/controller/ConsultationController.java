package com.flower.controller;

import com.flower.entity.ConsultationMessage;
import com.flower.entity.ConsultationSession;
import com.flower.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @PostMapping("/session")
    public ResponseEntity<?> createSession(
            @RequestParam Integer userId,
            @RequestParam String channel,
            @RequestParam(required = false) String title) {
        if (!"member".equals(channel) && !"non-member".equals(channel)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if ("member".equals(channel) && !consultationService.isUserMember(userId)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Only members can access member channel");
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }
        ConsultationSession session = consultationService.createSession(userId, channel, title);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

    @PostMapping("/flower-session")
    public ResponseEntity<?> createFlowerSession(
            @RequestParam Integer userId,
            @RequestParam String channel,
            @RequestParam Integer flowerId,
            @RequestParam(required = false) String title) {
        if (!"member".equals(channel) && !"non-member".equals(channel)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if ("member".equals(channel) && !consultationService.isUserMember(userId)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "请先开通会员");
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }
        ConsultationSession session = consultationService.createSessionForFlower(userId, channel, flowerId, title);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

    @GetMapping("/flower/{flowerId}/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFlowerSessions(
            @PathVariable Integer flowerId,
            @PathVariable Integer userId,
            @RequestParam(required = false) String channel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (channel != null) {
            Page<ConsultationSession> sessions = consultationService.getUserFlowerSessions(userId, flowerId, channel, pageable);
            return buildPageResponse(sessions);
        }
        Page<ConsultationSession> sessions = consultationService.getUserSessions(userId, pageable);
        return buildPageResponse(sessions);
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<ConsultationSession> getSession(@PathVariable Integer id) {
        ConsultationSession session = consultationService.getSession(id);
        return session != null ?
                new ResponseEntity<>(session, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserSessions(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions = consultationService.getUserSessions(userId, pageable);
        return buildPageResponse(sessions);
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<Map<String, Object>> getSessionsByChannel(
            @PathVariable String channel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions = consultationService.getSessionsByChannel(channel, pageable);
        return buildPageResponse(sessions);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions = consultationService.getAllSessions(pageable);
        return buildPageResponse(sessions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getSessionsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions = consultationService.getSessionsByStatus(status, pageable);
        return buildPageResponse(sessions);
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam Integer sessionId,
            @RequestParam Integer senderId,
            @RequestParam String senderType,
            @RequestParam String content) {
        if (!"user".equals(senderType) && !"admin".equals(senderType)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> result = consultationService.sendMessage(sessionId, senderId, senderType, content);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", ((com.flower.entity.ConsultationMessage) result.get("message")).getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<List<ConsultationMessage>> getSessionMessages(@PathVariable Integer sessionId) {
        List<ConsultationMessage> messages = consultationService.getSessionMessages(sessionId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PutMapping("/session/{id}/close")
    public ResponseEntity<ConsultationSession> closeSession(@PathVariable Integer id) {
        ConsultationSession session = consultationService.closeSession(id);
        return session != null ?
                new ResponseEntity<>(session, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Map<String, Object>> buildPageResponse(Page<ConsultationSession> sessions) {
        Map<String, Object> result = new HashMap<>();
        result.put("content", sessions.getContent());
        result.put("totalElements", sessions.getTotalElements());
        result.put("totalPages", sessions.getTotalPages());
        result.put("currentPage", sessions.getNumber());
        result.put("pageSize", sessions.getSize());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}