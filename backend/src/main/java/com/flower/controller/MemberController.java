package com.flower.controller;

import com.flower.entity.MemberBenefit;
import com.flower.entity.MemberRecord;
import com.flower.entity.PaymentRecord;
import com.flower.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/members")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/info/{userId}")
    public ResponseEntity<Map<String, Object>> getMemberInfo(@PathVariable Integer userId) {
        Map<String, Object> info = memberService.getMemberInfo(userId);
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Boolean> isMember(@PathVariable Integer userId) {
        boolean isMember = memberService.isUserMember(userId);
        return new ResponseEntity<>(isMember, HttpStatus.OK);
    }

    @GetMapping("/record/{userId}")
    public ResponseEntity<MemberRecord> getMemberRecord(@PathVariable Integer userId) {
        Optional<MemberRecord> record = memberService.getMemberRecordByUserId(userId);
        return record.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<Map<String, Object>> createMemberRecord(@PathVariable Integer userId) {
        Map<String, Object> result = memberService.createMemberRecord(userId);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/renew/{userId}")
    public ResponseEntity<Map<String, Object>> renewMembership(
            @PathVariable Integer userId,
            @RequestParam String paymentMethod) {
        Map<String, Object> result = memberService.renewMembership(userId, paymentMethod);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/cancel/{userId}")
    public ResponseEntity<Map<String, Object>> cancelMembership(
            @PathVariable Integer userId,
            @RequestParam(required = false) String reason) {
        Map<String, Object> result = memberService.cancelMembership(userId, reason);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/self-cancel/{userId}")
    public ResponseEntity<Map<String, Object>> selfCancelMembership(@PathVariable Integer userId) {
        Map<String, Object> result = memberService.selfCancelMembership(userId);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/refund/{userId}")
    public ResponseEntity<Map<String, Object>> processRefund(
            @PathVariable Integer userId,
            @RequestParam(required = false) String reason) {
        Map<String, Object> result = memberService.processRefund(userId, reason);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/refund-eligibility/{userId}")
    public ResponseEntity<Map<String, Object>> checkRefundEligibility(@PathVariable Integer userId) {
        Map<String, Object> result = memberService.checkRefundEligibility(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/order/{userId}")
    public ResponseEntity<Map<String, Object>> createPaymentOrder(
            @PathVariable Integer userId,
            @RequestParam String paymentMethod) {
        Map<String, Object> result = memberService.createPaymentOrder(userId, paymentMethod);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> paymentCallback(
            @RequestParam String orderNumber,
            @RequestParam String transactionId) {
        Map<String, Object> result = memberService.processPaymentCallback(orderNumber, transactionId);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/payments/{userId}")
    public ResponseEntity<List<PaymentRecord>> getUserPaymentHistory(@PathVariable Integer userId) {
        List<PaymentRecord> payments = memberService.getUserPaymentHistory(userId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Integer orderId) {
        Map<String, Object> result = memberService.getOrderById(orderId);
        if (result.containsKey("success") && !(boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/orders/{orderId}/refund")
    public ResponseEntity<Map<String, Object>> refundByOrderId(@PathVariable Integer orderId) {
        Map<String, Object> result = memberService.processRefundByOrderId(orderId);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/fee")
    public ResponseEntity<BigDecimal> getMonthlyFee() {
        return new ResponseEntity<>(memberService.getMonthlyFee(), HttpStatus.OK);
    }

    @GetMapping("/benefits")
    public ResponseEntity<List<MemberBenefit>> getActiveBenefits() {
        List<MemberBenefit> benefits = memberService.getActiveBenefits();
        return new ResponseEntity<>(benefits, HttpStatus.OK);
    }

    @GetMapping("/benefits/all")
    public ResponseEntity<List<MemberBenefit>> getAllBenefits() {
        List<MemberBenefit> benefits = memberService.getAllBenefits();
        return new ResponseEntity<>(benefits, HttpStatus.OK);
    }

    @PostMapping("/benefits")
    public ResponseEntity<MemberBenefit> createBenefit(@RequestBody MemberBenefit benefit) {
        try {
            MemberBenefit created = memberService.createBenefit(benefit);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/benefits/{id}")
    public ResponseEntity<MemberBenefit> updateBenefit(
            @PathVariable Integer id,
            @RequestBody MemberBenefit benefit) {
        MemberBenefit updated = memberService.updateBenefit(id, benefit);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/benefits/{id}")
    public ResponseEntity<Void> deleteBenefit(@PathVariable Integer id) {
        memberService.deleteBenefit(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MemberRecord>> getAllActiveMembers() {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
}