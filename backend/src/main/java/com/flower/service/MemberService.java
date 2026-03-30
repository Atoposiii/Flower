package com.flower.service;

import com.flower.entity.MemberBenefit;
import com.flower.entity.MemberRecord;
import com.flower.entity.PaymentRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemberService {
    Optional<MemberRecord> getMemberRecordByUserId(Integer userId);

    boolean isUserMember(Integer userId);

    Map<String, Object> applyMembership(Integer userId, String realName, String phone, String reason);

    Map<String, Object> createMemberRecord(Integer userId);

    Map<String, Object> renewMembership(Integer userId, String paymentMethod);

    Map<String, Object> cancelMembership(Integer userId, String reason);

    Map<String, Object> processRefund(Integer userId, String reason);

    Map<String, Object> processRefundByOrderId(Integer orderId);

    Map<String, Object> checkRefundEligibility(Integer userId);

    Map<String, Object> createPaymentOrder(Integer userId, String paymentMethod);

    Map<String, Object> processPaymentCallback(String orderNumber, String transactionId);

    List<PaymentRecord> getUserPaymentHistory(Integer userId);

    Map<String, Object> getOrderById(Integer orderId);

    List<MemberBenefit> getAllBenefits();

    List<MemberBenefit> getActiveBenefits();

    MemberBenefit createBenefit(MemberBenefit benefit);

    MemberBenefit updateBenefit(Integer id, MemberBenefit benefit);

    void deleteBenefit(Integer id);

    BigDecimal getMonthlyFee();

    Map<String, Object> getMemberInfo(Integer userId);

    List<MemberRecord> getAllActiveMembers();

    List<MemberRecord> getAllMembers();

    Map<String, Object> approveMembership(Integer id);

    Map<String, Object> rejectMembership(Integer id);

    Map<String, Object> suspendMembership(Integer id);

    Map<String, Object> selfCancelMembership(Integer userId);
}