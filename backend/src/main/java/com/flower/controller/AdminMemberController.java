package com.flower.controller;

import com.flower.entity.MemberRecord;
import com.flower.entity.MemberBenefit;
import com.flower.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/admin/member")
public class AdminMemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "joinDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        List<MemberRecord> members;
        if (status != null && !status.isEmpty()) {
            members = memberService.getAllMembers().stream()
                    .filter(m -> status.equals(m.getStatus()))
                    .collect(Collectors.toList());
        } else {
            members = memberService.getAllMembers();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", members);
        result.put("totalElements", members.size());
        result.put("totalPages", 1);
        result.put("currentPage", 0);
        result.put("pageSize", size);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberRecord> getMemberById(@PathVariable Integer id) {
        List<MemberRecord> members = memberService.getAllMembers();
        MemberRecord member = members.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
        return member != null ?
                new ResponseEntity<>(member, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getMemberByUserId(@PathVariable Integer userId) {
        Map<String, Object> memberInfo = memberService.getMemberInfo(userId);
        if (memberInfo != null && memberInfo.get("memberRecord") != null) {
            return new ResponseEntity<>(memberInfo, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveMember(@PathVariable Integer id) {
        Map<String, Object> result = memberService.approveMembership(id);
        return new ResponseEntity<>(result, (boolean) result.get("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectMember(@PathVariable Integer id) {
        Map<String, Object> result = memberService.rejectMembership(id);
        return new ResponseEntity<>(result, (boolean) result.get("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<Map<String, Object>> suspendMember(@PathVariable Integer id) {
        Map<String, Object> result = memberService.suspendMembership(id);
        return new ResponseEntity<>(result, (boolean) result.get("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelMember(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        MemberRecord targetMember = members.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (targetMember == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "会员不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> cancelResult = memberService.cancelMembership(targetMember.getUser().getId(), reason);
        return new ResponseEntity<>(cancelResult, HttpStatus.OK);
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<Map<String, Object>> renewMember(
            @PathVariable Integer id,
            @RequestParam String paymentMethod) {
        List<MemberRecord> members = memberService.getAllActiveMembers();
        MemberRecord targetMember = members.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (targetMember == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "会员不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> renewResult = memberService.renewMembership(targetMember.getUser().getId(), paymentMethod);
        return new ResponseEntity<>(renewResult, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<MemberRecord> allMembers = memberService.getAllMembers();
        List<MemberRecord> activeMembers = allMembers.stream()
                .filter(m -> "active".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MemberRecord> expiredMembers = allMembers.stream()
                .filter(m -> "expired".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MemberRecord> cancelledMembers = allMembers.stream()
                .filter(m -> "cancelled".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MemberRecord> pendingMembers = allMembers.stream()
                .filter(m -> "pending".equals(m.getStatus()))
                .collect(Collectors.toList());

        BigDecimal totalRevenue = allMembers.stream()
                .map(MemberRecord::getTotalPaid)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("totalMembers", allMembers.size());
        result.put("activeMembers", activeMembers.size());
        result.put("expiredMembers", expiredMembers.size());
        result.put("cancelledMembers", cancelledMembers.size());
        result.put("pendingMembers", pendingMembers.size());
        result.put("totalRevenue", totalRevenue);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/benefits")
    public ResponseEntity<Map<String, Object>> getAllBenefits() {
        List<MemberBenefit> benefits = memberService.getAllBenefits();
        Map<String, Object> result = new HashMap<>();
        result.put("content", benefits);
        result.put("totalElements", benefits.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/benefits")
    public ResponseEntity<Map<String, Object>> createBenefit(@RequestBody MemberBenefit benefit) {
        try {
            MemberBenefit created = memberService.createBenefit(benefit);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "权益创建成功");
            result.put("data", created);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "权益创建失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/benefits/{id}")
    public ResponseEntity<Map<String, Object>> updateBenefit(
            @PathVariable Integer id,
            @RequestBody MemberBenefit benefit) {
        try {
            MemberBenefit updated = memberService.updateBenefit(id, benefit);
            if (updated != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "权益更新成功");
                result.put("data", updated);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "权益不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "权益更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/benefits/{id}")
    public ResponseEntity<Map<String, Object>> deleteBenefit(@PathVariable Integer id) {
        try {
            memberService.deleteBenefit(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "权益删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "权益删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<Map<String, Object>> getExpiredMembers() {
        List<MemberRecord> allMembers = memberService.getAllActiveMembers();
        List<MemberRecord> expiredMembers = allMembers.stream()
                .filter(m -> "expired".equals(m.getStatus()))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", expiredMembers);
        result.put("totalElements", expiredMembers.size());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}