package com.flower.controller;

import com.flower.entity.ConsultationMessage;
import com.flower.entity.ConsultationSession;
import com.flower.entity.MemberBenefitUsage;
import com.flower.entity.MemberRecord;
import com.flower.repository.MemberBenefitUsageRepository;
import com.flower.repository.MemberRecordRepository;
import com.flower.service.ConsultationService;
import com.flower.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/admin/consultation")
public class AdminConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberBenefitUsageRepository memberBenefitUsageRepository;

    @Autowired
    private MemberRecordRepository memberRecordRepository;

    // In-memory refund request store (keyed by request id)
    private final Map<Integer, Map<String, Object>> refundRequests = new ConcurrentHashMap<>();
    private int refundRequestIdSeq = 1;

    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions;

        if (channel != null && !channel.isEmpty()) {
            sessions = consultationService.getSessionsByChannel(channel, pageable);
        } else if (status != null && !status.isEmpty()) {
            sessions = consultationService.getSessionsByStatus(status, pageable);
        } else {
            sessions = consultationService.getAllSessions(pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", sessions.getContent());
        result.put("totalElements", sessions.getTotalElements());
        result.put("totalPages", sessions.getTotalPages());
        result.put("currentPage", sessions.getNumber());
        result.put("pageSize", sessions.getSize());
        result.put("hasNext", sessions.hasNext());
        result.put("hasPrevious", sessions.hasPrevious());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/sessions/channel/{channel}")
    public ResponseEntity<Map<String, Object>> getSessionsByChannel(
            @PathVariable String channel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions = consultationService.getSessionsByChannel(channel, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", sessions.getContent());
        result.put("totalElements", sessions.getTotalElements());
        result.put("totalPages", sessions.getTotalPages());
        result.put("currentPage", sessions.getNumber());
        result.put("pageSize", sessions.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/sessions/status/{status}")
    public ResponseEntity<Map<String, Object>> getSessionsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions = consultationService.getSessionsByStatus(status, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", sessions.getContent());
        result.put("totalElements", sessions.getTotalElements());
        result.put("totalPages", sessions.getTotalPages());
        result.put("currentPage", sessions.getNumber());
        result.put("pageSize", sessions.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<ConsultationSession> getSession(@PathVariable Integer id) {
        ConsultationSession session = consultationService.getSession(id);
        return session != null ?
                new ResponseEntity<>(session, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/session/{id}/messages")
    public ResponseEntity<List<ConsultationMessage>> getSessionMessages(@PathVariable Integer id) {
        List<ConsultationMessage> messages = consultationService.getSessionMessages(id);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PostMapping("/session/{id}/reply")
    public ResponseEntity<Map<String, Object>> replyToSession(
            @PathVariable Integer id,
            @RequestParam Integer adminId,
            @RequestParam(required = false) String adminName,
            @RequestParam String content) {
        Map<String, Object> result = consultationService.sendMessage(id, adminId, "admin", content);
        if (result != null) {
            Map<String, Object> successResult = new HashMap<>();
            successResult.put("success", true);
            successResult.put("message", "回复成功");
            successResult.put("data", result.get("message"));
            return new ResponseEntity<>(successResult, HttpStatus.OK);
        }
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("success", false);
        errorResult.put("message", "回复失败，会话不存在或已关闭");
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/session/{id}/close")
    public ResponseEntity<Map<String, Object>> closeSession(@PathVariable Integer id) {
        ConsultationSession session = consultationService.closeSession(id);
        if (session != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "会话已关闭");
            result.put("data", session);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("success", false);
        errorResult.put("message", "会话不存在");
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 1);

        Page<ConsultationSession> memberSessions = consultationService.getSessionsByChannel("member", pageable);
        Page<ConsultationSession> nonMemberSessions = consultationService.getSessionsByChannel("non-member", pageable);
        Page<ConsultationSession> openSessions = consultationService.getSessionsByStatus("open", pageable);
        Page<ConsultationSession> closedSessions = consultationService.getSessionsByStatus("closed", pageable);

        result.put("memberSessionCount", memberSessions.getTotalElements());
        result.put("nonMemberSessionCount", nonMemberSessions.getTotalElements());
        result.put("openSessionCount", openSessions.getTotalElements());
        result.put("closedSessionCount", closedSessions.getTotalElements());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/sessions")
    public ResponseEntity<Map<String, Object>> getUserSessions(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationSession> sessions = consultationService.getUserSessions(userId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", sessions.getContent());
        result.put("totalElements", sessions.getTotalElements());
        result.put("totalPages", sessions.getTotalPages());
        result.put("currentPage", sessions.getNumber());
        result.put("pageSize", sessions.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/member-refund-request")
    public ResponseEntity<Map<String, Object>> createMemberRefundRequest(
            @RequestParam Integer userId,
            @RequestParam String reason,
            @RequestParam(required = false) String contactInfo) {
        Map<String, Object> result = new HashMap<>();

        // Check if member exists
        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(userId);
        if (!memberOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        // Check benefit usage
        long usageCount = memberBenefitUsageRepository.countByUserId(userId);

        Map<String, Object> request = new HashMap<>();
        int requestId;
        synchronized (this) {
            requestId = refundRequestIdSeq++;
        }
        request.put("id", requestId);
        request.put("userId", userId);
        request.put("reason", reason);
        request.put("contactInfo", contactInfo);
        request.put("status", "pending");
        request.put("hasUsedBenefits", usageCount > 0);
        request.put("usageCount", usageCount);
        request.put("memberRecord", memberOpt.get());
        request.put("createTime", new Date());
        refundRequests.put(requestId, request);

        result.put("success", true);
        result.put("message", "退款申请已提交，客服将在24小时内处理");
        result.put("requestId", requestId);
        result.put("hasUsedBenefits", usageCount > 0);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/member-refund-requests")
    public ResponseEntity<Map<String, Object>> getMemberRefundRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<Map<String, Object>> all = new ArrayList<>(refundRequests.values());
        if (status != null && !status.isEmpty()) {
            all = all.stream()
                    .filter(r -> status.equals(r.get("status")))
                    .collect(java.util.stream.Collectors.toList());
        }
        // Sort by createTime desc
        all.sort((a, b) -> {
            Date da = (Date) a.get("createTime");
            Date db = (Date) b.get("createTime");
            if (da == null || db == null) return 0;
            return db.compareTo(da);
        });

        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<Map<String, Object>> pageContent = start < all.size() ? all.subList(start, end) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("content", pageContent);
        result.put("totalElements", all.size());
        result.put("totalPages", (int) Math.ceil((double) all.size() / size));
        result.put("currentPage", page);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/member-refund-request/{requestId}/approve")
    public ResponseEntity<Map<String, Object>> approveMemberRefundRequest(
            @PathVariable Integer requestId,
            @RequestParam(required = false) String adminName) {
        Map<String, Object> request = refundRequests.get(requestId);
        if (request == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "退款申请不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        if (!"pending".equals(request.get("status"))) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "申请已处理");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        Integer userId = (Integer) request.get("userId");
        Map<String, Object> refundResult = memberService.processRefund(userId, "客服审批退款");
        if ((boolean) refundResult.get("success")) {
            request.put("status", "approved");
            request.put("processedBy", adminName != null ? adminName : "admin");
            request.put("processedTime", new Date());
            // Notify user via consultation message
            consultationService.createRefundNotificationSession(userId, "您的退款申请已通过，¥9.90 将退回至原支付方式。");
        }
        return new ResponseEntity<>(refundResult, HttpStatus.OK);
    }

    @PutMapping("/member-refund-request/{requestId}/reject")
    public ResponseEntity<Map<String, Object>> rejectMemberRefundRequest(
            @PathVariable Integer requestId,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String adminName) {
        Map<String, Object> request = refundRequests.get(requestId);
        if (request == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "退款申请不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        if (!"pending".equals(request.get("status"))) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "申请已处理");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        request.put("status", "rejected");
        request.put("rejectReason", reason != null ? reason : "不符合退款条件");
        request.put("processedBy", adminName != null ? adminName : "admin");
        request.put("processedTime", new Date());

        Integer userId = (Integer) request.get("userId");
        String rejectMsg = "您的退款申请已被拒绝。原因：" + (reason != null ? reason : "不符合退款条件");
        consultationService.createRefundNotificationSession(userId, rejectMsg);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "退款申请已拒绝");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/member-refund/{userId}/process")
    public ResponseEntity<Map<String, Object>> processMemberRefund(
            @PathVariable Integer userId,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Integer adminId,
            @RequestParam(required = false) String adminName) {
        Map<String, Object> result = memberService.processRefund(userId, reason != null ? reason : "客服处理退款");
        if ((boolean) result.get("success")) {
            consultationService.createRefundNotificationSession(userId, "您的退款已处理成功，¥9.90 将退回至原支付方式。");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/member/{userId}/benefit-usage")
    public ResponseEntity<Map<String, Object>> getMemberBenefitUsage(@PathVariable Integer userId) {
        long usageCount = memberBenefitUsageRepository.countByUserId(userId);
        List<MemberBenefitUsage> usageList = memberBenefitUsageRepository.findByUserIdOrderByUsageTimeDesc(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("hasUsedBenefits", usageCount > 0);
        result.put("usageCount", usageCount);
        result.put("usageList", usageList);
        result.put("message", usageCount > 0 ? "已使用会员权益，不可自助退款" : "可安全退款");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}