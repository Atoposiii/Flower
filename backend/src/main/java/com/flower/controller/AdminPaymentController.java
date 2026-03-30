package com.flower.controller;

import com.flower.entity.PaymentRecord;
import com.flower.entity.MemberRecord;
import com.flower.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/payment")
public class AdminPaymentController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<MemberRecord> members = memberService.getAllActiveMembers();
        List<PaymentRecord> allPayments = members.stream()
                .flatMap(m -> memberService.getUserPaymentHistory(m.getUser().getId()).stream())
                .collect(Collectors.toList());

        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            allPayments = allPayments.stream()
                    .filter(p -> paymentStatus.equals(p.getPaymentStatus()))
                    .collect(Collectors.toList());
        }

        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            allPayments = allPayments.stream()
                    .filter(p -> paymentMethod.equals(p.getPaymentMethod()))
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, allPayments.size());
        List<PaymentRecord> pageContent = start < allPayments.size() ?
                allPayments.subList(start, end) : List.of();

        Map<String, Object> result = new HashMap<>();
        result.put("content", pageContent);
        result.put("totalElements", allPayments.size());
        result.put("totalPages", (int) Math.ceil((double) allPayments.size() / size));
        result.put("currentPage", page);
        result.put("pageSize", size);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentRecord> getPaymentById(@PathVariable Integer id) {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        for (MemberRecord member : members) {
            List<PaymentRecord> payments = memberService.getUserPaymentHistory(member.getUser().getId());
            PaymentRecord payment = payments.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (payment != null) {
                return new ResponseEntity<>(payment, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPayments(@PathVariable Integer userId) {
        List<PaymentRecord> payments = memberService.getUserPaymentHistory(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("content", payments);
        result.put("totalElements", payments.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        List<PaymentRecord> allPayments = members.stream()
                .flatMap(m -> memberService.getUserPaymentHistory(m.getUser().getId()).stream())
                .collect(Collectors.toList());

        BigDecimal totalRevenue = allPayments.stream()
                .filter(p -> "completed".equals(p.getPaymentStatus()))
                .map(PaymentRecord::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefund = allPayments.stream()
                .filter(p -> p.getRefundAmount() != null)
                .map(PaymentRecord::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long wechatPayments = allPayments.stream()
                .filter(p -> "wechat".equals(p.getPaymentMethod()))
                .count();
        long alipayPayments = allPayments.stream()
                .filter(p -> "alipay".equals(p.getPaymentMethod()))
                .count();
        long bankPayments = allPayments.stream()
                .filter(p -> "bank_card".equals(p.getPaymentMethod()))
                .count();
        long completedPayments = allPayments.stream()
                .filter(p -> "completed".equals(p.getPaymentStatus()))
                .count();
        long pendingPayments = allPayments.stream()
                .filter(p -> "pending".equals(p.getPaymentStatus()))
                .count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalPayments", allPayments.size());
        result.put("totalRevenue", totalRevenue);
        result.put("totalRefund", totalRefund);
        result.put("netRevenue", totalRevenue.subtract(totalRefund));
        result.put("wechatPayments", wechatPayments);
        result.put("alipayPayments", alipayPayments);
        result.put("bankCardPayments", bankPayments);
        result.put("completedPayments", completedPayments);
        result.put("pendingPayments", pendingPayments);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Map<String, Object>> processRefund(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        for (MemberRecord member : members) {
            List<PaymentRecord> payments = memberService.getUserPaymentHistory(member.getUser().getId());
            PaymentRecord payment = payments.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (payment != null) {
                Map<String, Object> refundResult = memberService.processRefund(payment.getUser().getId(), reason);
                return new ResponseEntity<>(refundResult, HttpStatus.OK);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "支付记录不存在");
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyRevenue(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        List<PaymentRecord> allPayments = members.stream()
                .flatMap(m -> memberService.getUserPaymentHistory(m.getUser().getId()).stream())
                .filter(p -> "completed".equals(p.getPaymentStatus()))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", allPayments);
        result.put("totalElements", allPayments.size());
        result.put("totalRevenue", allPayments.stream()
                .map(PaymentRecord::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/method/{method}")
    public ResponseEntity<Map<String, Object>> getPaymentsByMethod(
            @PathVariable String method,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        List<PaymentRecord> payments = members.stream()
                .flatMap(m -> memberService.getUserPaymentHistory(m.getUser().getId()).stream())
                .filter(p -> method.equals(p.getPaymentMethod()))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", payments);
        result.put("totalElements", payments.size());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}