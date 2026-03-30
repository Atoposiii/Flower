package com.flower.service.impl;

import com.flower.entity.MemberBenefit;
import com.flower.entity.MemberBenefitUsage;
import com.flower.entity.MemberRecord;
import com.flower.entity.MemberRefund;
import com.flower.entity.PaymentRecord;
import com.flower.entity.User;
import com.flower.repository.MemberBenefitRepository;
import com.flower.repository.MemberBenefitUsageRepository;
import com.flower.repository.MemberRecordRepository;
import com.flower.repository.MemberRefundRepository;
import com.flower.repository.PaymentRecordRepository;
import com.flower.repository.UserRepository;
import com.flower.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("all")
public class MemberServiceImpl implements MemberService {

    @Resource
    private MemberRecordRepository memberRecordRepository;

    @Resource
    private PaymentRecordRepository paymentRecordRepository;

    @Resource
    private MemberBenefitRepository memberBenefitRepository;

    @Resource
    private MemberRefundRepository memberRefundRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private MemberBenefitUsageRepository memberBenefitUsageRepository;

    private static final BigDecimal YEARLY_FEE = new BigDecimal("9.90");
    private static final long REFUND_TIME_LIMIT_MINUTES = 2;

    @Override
    public Optional<MemberRecord> getMemberRecordByUserId(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return memberRecordRepository.findByUserId(userId);
    }

    @Override
    public boolean isUserMember(Integer userId) {
        if (userId == null) return false;
        Optional<MemberRecord> member = memberRecordRepository.findByUserId(userId);
        if (!member.isPresent()) return false;
        MemberRecord m = member.get();
        if (!"active".equals(m.getStatus())) return false;
        // Check expiry
        if (m.getExpiryDate() != null && m.getExpiryDate().before(new Date())) return false;
        return true;
    }

    private String computeEffectiveStatus(MemberRecord member) {
        if (member == null) return null;
        if ("active".equals(member.getStatus()) && member.getExpiryDate() != null
                && member.getExpiryDate().before(new Date())) {
            return "expired";
        }
        return member.getStatus();
    }

    @Override
    @Transactional
    public Map<String, Object> applyMembership(Integer userId, String realName, String phone, String reason) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        Optional<MemberRecord> existing = memberRecordRepository.findByUserId(userId);
        if (existing.isPresent()) {
            result.put("success", false);
            result.put("message", "您已提交过会员申请，请勿重复申请");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        MemberRecord record = new MemberRecord();
        record.setUser(userOpt.get());
        record.setMemberLevel("yearly");
        record.setMemberType("vip");
        record.setStatus("pending");
        record.setMonthlyFee(YEARLY_FEE);
        record.setTotalPaid(BigDecimal.ZERO);
        record.setAutoRenew(false);
        record.setCancelReason(reason);

        memberRecordRepository.save(record);

        result.put("success", true);
        result.put("message", "会员申请已提交，请等待审核");
        result.put("memberRecord", record);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> createMemberRecord(Integer userId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        if (isUserMember(userId)) {
            result.put("success", false);
            result.put("message", "用户已经是会员");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        MemberRecord record = new MemberRecord();
        record.setUser(userOpt.get());
        record.setMemberLevel("yearly");
        record.setMemberType("vip");
        record.setStatus("active");
        record.setMonthlyFee(YEARLY_FEE);
        record.setTotalPaid(BigDecimal.ZERO);
        record.setAutoRenew(false);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        record.setExpiryDate(cal.getTime());

        memberRecordRepository.save(record);

        result.put("success", true);
        result.put("message", "会员开通成功");
        result.put("memberRecord", record);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> renewMembership(Integer userId, String paymentMethod) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null || paymentMethod == null) {
            result.put("success", false);
            result.put("message", "参数不能为空");
            return result;
        }

        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(userId);
        if (!memberOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return result;
        }

        MemberRecord member = memberOpt.get();
        if (!"active".equals(member.getStatus())) {
            result.put("success", false);
            result.put("message", "会员已过期或已取消");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        PaymentRecord payment = new PaymentRecord();
        payment.setUser(userOpt.get());
        payment.setOrderNumber(generateOrderNumber());
        payment.setAmount(YEARLY_FEE);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("completed");
        payment.setPaymentTime(new Date());
        paymentRecordRepository.save(payment);

        member.setTotalPaid(member.getTotalPaid().add(YEARLY_FEE));
        member.setLastPaymentTime(new Date());

        Calendar cal = Calendar.getInstance();
        cal.setTime(member.getExpiryDate());
        cal.add(Calendar.YEAR, 1);
        member.setExpiryDate(cal.getTime());
        member.setStatus("active");

        memberRecordRepository.save(member);

        result.put("success", true);
        result.put("message", "续费成功");
        result.put("newExpiryDate", member.getExpiryDate());
        result.put("orderNumber", payment.getOrderNumber());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> cancelMembership(Integer userId, String reason) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(userId);
        if (!memberOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return result;
        }

        MemberRecord member = memberOpt.get();
        if ("cancelled".equals(member.getStatus())) {
            result.put("success", false);
            result.put("message", "会员已经取消");
            return result;
        }

        Map<String, Object> refundCheck = checkRefundEligibility(userId);
        boolean eligibleForRefund = (boolean) refundCheck.get("eligible");

        member.setStatus("cancelled");
        member.setCancelTime(new Date());
        member.setCancelReason(reason != null ? reason : "用户主动取消");

        if (eligibleForRefund) {
            member.setRefundAmount(YEARLY_FEE);
        }

        memberRecordRepository.save(member);

        result.put("success", true);
        result.put("message", eligibleForRefund ? "取消成功，费用已退款" : "取消成功（非退款期限内，费用不予退还）");
        result.put("refunded", eligibleForRefund);
        if (eligibleForRefund) {
            result.put("refundAmount", YEARLY_FEE);
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> processRefund(Integer userId, String reason) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        Map<String, Object> eligibility = checkRefundEligibility(userId);
        if (!(boolean) eligibility.get("eligible")) {
            result.put("success", false);
            result.put("message", "不在退款期限内，无法退款");
            return result;
        }

        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(userId);
        if (!memberOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return result;
        }

        MemberRecord member = memberOpt.get();
        member.setStatus("refunded");
        member.setRefundAmount(YEARLY_FEE);
        member.setCancelTime(new Date());
        member.setCancelReason(reason != null ? reason : "退款取消");
        memberRecordRepository.save(member);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            // 找到最近一次已支付的订单，用于写退款记录
            List<PaymentRecord> payments = paymentRecordRepository.findByUserIdOrderByCreateTimeDesc(userId);
            PaymentRecord latestPaid = payments.stream()
                    .filter(p -> "completed".equals(p.getPaymentStatus()))
                    .findFirst().orElse(null);

            PaymentRecord refundPayment = new PaymentRecord();
            refundPayment.setUser(userOpt.get());
            refundPayment.setOrderNumber(generateOrderNumber() + "_REFUND");
            refundPayment.setAmount(YEARLY_FEE.negate());
            refundPayment.setPaymentMethod("refund");
            refundPayment.setPaymentStatus("refunded");
            refundPayment.setPaymentTime(new Date());
            refundPayment.setRefundAmount(YEARLY_FEE);
            refundPayment.setRefundReason(reason != null ? reason : "会员退款");
            paymentRecordRepository.save(refundPayment);

            // 写 member_refunds 退款明细记录
            if (latestPaid != null) {
                MemberRefund refundRecord = new MemberRefund();
                refundRecord.setOrderId(latestPaid.getId());
                refundRecord.setRefundAmount(YEARLY_FEE);
                refundRecord.setRefundChannel(latestPaid.getPaymentMethod() != null ? latestPaid.getPaymentMethod() : "unknown");
                memberRefundRepository.save(refundRecord);
            }
        }

        result.put("success", true);
        result.put("message", "退款成功");
        result.put("refundAmount", YEARLY_FEE);

        return result;
    }

    @Override
    public Map<String, Object> checkRefundEligibility(Integer userId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("eligible", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(userId);
        if (!memberOpt.isPresent()) {
            result.put("eligible", false);
            result.put("message", "会员记录不存在");
            return result;
        }

        MemberRecord member = memberOpt.get();

        if (!"active".equals(member.getStatus())) {
            result.put("eligible", false);
            result.put("message", "会员未激活");
            return result;
        }

        Date joinDate = member.getJoinDate();
        if (joinDate == null) {
            result.put("eligible", false);
            result.put("message", "加入时间未知");
            return result;
        }

        Date now = new Date();
        long diffMillis = now.getTime() - joinDate.getTime();
        long diffMinutes = TimeUnit.MINUTES.convert(diffMillis, TimeUnit.MILLISECONDS);

        if (diffMinutes <= REFUND_TIME_LIMIT_MINUTES) {
            result.put("eligible", true);
            result.put("message", "在退款期限内（加入后" + REFUND_TIME_LIMIT_MINUTES + "分钟内）");
            result.put("remainingMinutes", REFUND_TIME_LIMIT_MINUTES - diffMinutes);
            result.put("minutesUsed", diffMinutes);
        } else {
            result.put("eligible", false);
            result.put("message", "超过退款期限（" + REFUND_TIME_LIMIT_MINUTES + "分钟后不可退款）");
            result.put("minutesUsed", diffMinutes);
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> createPaymentOrder(Integer userId, String paymentMethod) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null || paymentMethod == null) {
            result.put("success", false);
            result.put("message", "参数不能为空");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        String orderNumber = generateOrderNumber();

        PaymentRecord payment = new PaymentRecord();
        payment.setUser(userOpt.get());
        payment.setOrderNumber(orderNumber);
        payment.setAmount(YEARLY_FEE);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("pending");
        PaymentRecord saved = paymentRecordRepository.save(payment);

        result.put("success", true);
        result.put("message", "订单创建成功");
        result.put("orderId", saved.getId());
        result.put("orderNumber", orderNumber);
        result.put("amount", YEARLY_FEE);
        result.put("paymentMethod", paymentMethod);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> processPaymentCallback(String orderNumber, String transactionId) {
        Map<String, Object> result = new HashMap<>();

        if (orderNumber == null || transactionId == null) {
            result.put("success", false);
            result.put("message", "参数不能为空");
            return result;
        }

        Optional<PaymentRecord> paymentOpt = paymentRecordRepository.findByOrderNumber(orderNumber);
        if (!paymentOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        PaymentRecord payment = paymentOpt.get();

        if (!"pending".equals(payment.getPaymentStatus())) {
            result.put("success", false);
            result.put("message", "订单状态不是待支付");
            return result;
        }

        payment.setPaymentStatus("completed");
        payment.setTransactionId(transactionId);
        payment.setPaymentTime(new Date());
        paymentRecordRepository.save(payment);

        MemberRecord member = memberRecordRepository.findByUserId(payment.getUser().getId())
                .orElse(null);

        if (member == null) {
            User user = payment.getUser();
            member = new MemberRecord();
            member.setUser(user);
            member.setMemberLevel("yearly");
            member.setMemberType("vip");
            member.setStatus("active");
            member.setMonthlyFee(YEARLY_FEE);
            member.setTotalPaid(YEARLY_FEE);
            member.setLastPaymentTime(new Date());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);
            member.setExpiryDate(cal.getTime());

            memberRecordRepository.save(member);
        } else {
            member.setTotalPaid(member.getTotalPaid().add(YEARLY_FEE));
            member.setLastPaymentTime(new Date());

            Calendar cal = Calendar.getInstance();
            cal.setTime(member.getExpiryDate());
            cal.add(Calendar.YEAR, 1);
            member.setExpiryDate(cal.getTime());
            member.setStatus("active");

            memberRecordRepository.save(member);
        }

        result.put("success", true);
        result.put("message", "支付成功");
        result.put("memberStatus", member.getStatus());
        result.put("expiryDate", member.getExpiryDate());

        return result;
    }

    @Override
    public List<PaymentRecord> getUserPaymentHistory(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return paymentRecordRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    @Transactional
    public Map<String, Object> processRefundByOrderId(Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        if (orderId == null) {
            result.put("success", false);
            result.put("message", "订单ID不能为空");
            return result;
        }
        Optional<PaymentRecord> paymentOpt = paymentRecordRepository.findById(orderId);
        if (!paymentOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        PaymentRecord payment = paymentOpt.get();
        if (!"completed".equals(payment.getPaymentStatus())) {
            result.put("success", false);
            result.put("message", payment.getPaymentStatus().equals("refunded") ? "订单已退款" : "订单尚未支付");
            return result;
        }
        // 校验2分钟退款窗口（基于 paymentTime）
        Date paidAt = payment.getPaymentTime();
        if (paidAt == null) {
            result.put("success", false);
            result.put("message", "支付时间未知，无法退款");
            return result;
        }
        long diffSeconds = (new Date().getTime() - paidAt.getTime()) / 1000;
        if (diffSeconds > 120) {
            result.put("success", false);
            result.put("message", "退款窗口已关闭（支付后120秒内可退款）");
            return result;
        }
        // 乐观锁：通过更新状态确保幂等
        payment.setPaymentStatus("refunded");
        payment.setRefundTime(new Date());
        payment.setRefundAmount(payment.getAmount());
        paymentRecordRepository.save(payment);

        // 注销会员有效期
        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(payment.getUser().getId());
        memberOpt.ifPresent(m -> {
            m.setStatus("refunded");
            m.setCancelTime(new Date());
            m.setRefundAmount(payment.getAmount());
            memberRecordRepository.save(m);
        });

        // 写退款记录
        MemberRefund refundRecord = new MemberRefund();
        refundRecord.setOrderId(orderId);
        refundRecord.setRefundAmount(payment.getAmount());
        refundRecord.setRefundChannel(payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "unknown");
        memberRefundRepository.save(refundRecord);

        result.put("success", true);
        result.put("message", "退款成功");
        result.put("refundAmount", payment.getAmount());
        return result;
    }

    @Override
    public Map<String, Object> getOrderById(Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        if (orderId == null) {
            result.put("success", false);
            result.put("message", "订单ID不能为空");
            return result;
        }
        Optional<PaymentRecord> paymentOpt = paymentRecordRepository.findById(orderId);
        if (!paymentOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        PaymentRecord payment = paymentOpt.get();
        result.put("id", payment.getId());
        result.put("orderNumber", payment.getOrderNumber());
        result.put("amount", payment.getAmount());
        result.put("paymentMethod", payment.getPaymentMethod());
        result.put("paymentStatus", payment.getPaymentStatus());
        result.put("paymentTime", payment.getPaymentTime());
        result.put("createTime", payment.getCreateTime());

        // 如果已支付，附带会员有效期
        if ("completed".equals(payment.getPaymentStatus())) {
            Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(payment.getUser().getId());
            memberOpt.ifPresent(m -> result.put("expiryDate", m.getExpiryDate()));
        }
        return result;
    }

    @Override
    public List<MemberBenefit> getAllBenefits() {
        return memberBenefitRepository.findAllByOrderBySortOrderAsc();
    }

    @Override
    public List<MemberBenefit> getActiveBenefits() {
        return memberBenefitRepository.findByIsActiveOrderBySortOrderAsc(true);
    }

    @Override
    @Transactional
    public MemberBenefit createBenefit(MemberBenefit benefit) {
        if (benefit == null) {
            throw new IllegalArgumentException("权益信息不能为空");
        }
        return memberBenefitRepository.save(benefit);
    }

    @Override
    @Transactional
    public MemberBenefit updateBenefit(Integer id, MemberBenefit benefit) {
        if (id == null || benefit == null) {
            return null;
        }

        Optional<MemberBenefit> benefitOpt = memberBenefitRepository.findById(id);
        if (!benefitOpt.isPresent()) {
            return null;
        }

        MemberBenefit existing = benefitOpt.get();
        existing.setTitle(benefit.getTitle());
        existing.setDescription(benefit.getDescription());
        existing.setIcon(benefit.getIcon());
        existing.setLinkUrl(benefit.getLinkUrl());
        existing.setSortOrder(benefit.getSortOrder());
        existing.setIsActive(benefit.getIsActive());

        return memberBenefitRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteBenefit(Integer id) {
        if (id != null) {
            memberBenefitRepository.deleteById(id);
        }
    }

    @Override
    public BigDecimal getMonthlyFee() {
        return YEARLY_FEE;
    }

    @Override
    public Map<String, Object> getMemberInfo(Integer userId) {
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("isMember", false);
            return result;
        }

        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(userId);
        result.put("isMember", memberOpt.isPresent() && "active".equals(memberOpt.get().getStatus()));
        result.put("yearlyFee", YEARLY_FEE);
        result.put("refundTimeLimitMinutes", REFUND_TIME_LIMIT_MINUTES);

        if (memberOpt.isPresent()) {
            MemberRecord member = memberOpt.get();
            result.put("memberRecord", member);
            result.put("status", computeEffectiveStatus(member));
            result.put("expiryDate", member.getExpiryDate());
            result.put("joinDate", member.getJoinDate());
            result.put("totalPaid", member.getTotalPaid());

            Map<String, Object> eligibility = checkRefundEligibility(userId);
            result.put("refundEligibility", eligibility);
        }

        List<MemberBenefit> benefits = getActiveBenefits();
        result.put("benefits", benefits);

        return result;
    }

    @Override
    public List<MemberRecord> getAllActiveMembers() {
        return memberRecordRepository.findByStatus("active");
    }

    @Override
    public List<MemberRecord> getAllMembers() {
        return memberRecordRepository.findAll();
    }

    @Override
    @Transactional
    public Map<String, Object> approveMembership(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Optional<MemberRecord> opt = memberRecordRepository.findById(id);
        if (!opt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return result;
        }
        MemberRecord record = opt.get();
        if (!"pending".equals(record.getStatus())) {
            result.put("success", false);
            result.put("message", "只有待审核状态的申请才能审核通过");
            return result;
        }
        record.setStatus("active");
        record.setJoinDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        record.setExpiryDate(cal.getTime());
        memberRecordRepository.save(record);
        result.put("success", true);
        result.put("message", "会员审核通过");
        result.put("data", record);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> rejectMembership(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Optional<MemberRecord> opt = memberRecordRepository.findById(id);
        if (!opt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return result;
        }
        MemberRecord record = opt.get();
        if (!"pending".equals(record.getStatus())) {
            result.put("success", false);
            result.put("message", "只有待审核状态的申请才能拒绝");
            return result;
        }
        record.setStatus("cancelled");
        record.setCancelTime(new Date());
        memberRecordRepository.save(record);
        result.put("success", true);
        result.put("message", "会员申请已拒绝");
        result.put("data", record);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> suspendMembership(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Optional<MemberRecord> opt = memberRecordRepository.findById(id);
        if (!opt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return result;
        }
        MemberRecord record = opt.get();
        if (!"active".equals(record.getStatus())) {
            result.put("success", false);
            result.put("message", "只有有效会员才能被停用");
            return result;
        }
        record.setStatus("cancelled");
        record.setCancelTime(new Date());
        memberRecordRepository.save(record);
        result.put("success", true);
        result.put("message", "会员已停用");
        result.put("data", record);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> selfCancelMembership(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        Optional<MemberRecord> memberOpt = memberRecordRepository.findByUserId(userId);
        if (!memberOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "会员记录不存在");
            return result;
        }

        MemberRecord member = memberOpt.get();
        if (!"active".equals(member.getStatus())) {
            result.put("success", false);
            result.put("message", "会员未激活，无法退会");
            return result;
        }

        // Check 2-minute window
        Date joinDate = member.getJoinDate();
        if (joinDate == null) {
            result.put("success", false);
            result.put("message", "加入时间未知，无法处理退会");
            return result;
        }
        long diffMinutes = TimeUnit.MINUTES.convert(new Date().getTime() - joinDate.getTime(), TimeUnit.MILLISECONDS);
        if (diffMinutes > REFUND_TIME_LIMIT_MINUTES) {
            result.put("success", false);
            result.put("message", "已超过自助退会时间（" + REFUND_TIME_LIMIT_MINUTES + "分钟），请联系客服处理");
            result.put("canContactService", true);
            return result;
        }

        // Check benefit usage
        long usageCount = memberBenefitUsageRepository.countByUserId(userId);
        if (usageCount > 0) {
            result.put("success", false);
            result.put("message", "您已使用会员权益，无法自助退会，请联系客服");
            result.put("canContactService", true);
            return result;
        }

        // Process refund
        return processRefund(userId, "用户自助退会（2分钟内）");
    }

    private String generateOrderNumber() {
        return "PAY" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }
}