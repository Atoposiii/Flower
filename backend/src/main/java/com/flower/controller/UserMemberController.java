package com.flower.controller;

import com.flower.entity.MemberRecord;
import com.flower.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户端会员申请接口
 */
@RestController
@RequestMapping("/member/user")
public class UserMemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 用户提交会员申请
     * POST /api/member/apply
     */
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> apply(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        Object userIdObj = body.get("userId");
        if (userIdObj == null) {
            result.put("success", false);
            result.put("message", "userId 不能为空");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        Integer userId = Integer.valueOf(userIdObj.toString());
        String realName = body.containsKey("realName") ? body.get("realName").toString() : null;
        String phone = body.containsKey("phone") ? body.get("phone").toString() : null;
        String reason = body.containsKey("reason") ? body.get("reason").toString() : null;

        Map<String, Object> applyResult = memberService.applyMembership(userId, realName, phone, reason);
        if ((boolean) applyResult.get("success")) {
            return new ResponseEntity<>(applyResult, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(applyResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * 用户查询自己的会员状态
     * GET /api/member/status?userId={userId}
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        Optional<MemberRecord> memberOpt = memberService.getMemberRecordByUserId(userId);
        if (!memberOpt.isPresent()) {
            result.put("hasApplied", false);
            result.put("message", "您尚未申请会员");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        MemberRecord record = memberOpt.get();
        result.put("hasApplied", true);
        result.put("status", record.getStatus());
        result.put("joinDate", record.getJoinDate());
        result.put("expiryDate", record.getExpiryDate());
        result.put("memberLevel", record.getMemberLevel());
        result.put("memberType", record.getMemberType());
        result.put("memberId", record.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
