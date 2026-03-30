package com.flower.controller;

import com.flower.entity.Volunteer;
import com.flower.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户端志愿者申请接口
 */
@RestController
@RequestMapping("/volunteer/user")
public class UserVolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    /**
     * 用户提交志愿者申请
     * POST /api/volunteer/apply
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

        Optional<Volunteer> existing = volunteerService.getVolunteerByUserId(userId);
        if (existing.isPresent()) {
            result.put("success", false);
            result.put("message", "您已提交过志愿者申请，请勿重复申请");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        Volunteer volunteer = new Volunteer();
        volunteer.setRealName(body.getOrDefault("realName", "").toString());
        volunteer.setIdCard(body.containsKey("idCard") ? body.get("idCard").toString() : null);
        volunteer.setAddress(body.containsKey("address") ? body.get("address").toString() : null);
        volunteer.setSkills(body.containsKey("skills") ? body.get("skills").toString() : null);

        try {
            Volunteer saved = volunteerService.applyVolunteer(userId, volunteer);
            result.put("success", true);
            result.put("message", "志愿者申请已提交，请等待审核");
            result.put("data", saved);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 用户查询自己的志愿者状态
     * GET /api/volunteer/status?userId={userId}
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        Optional<Volunteer> volunteerOpt = volunteerService.getVolunteerByUserId(userId);
        if (!volunteerOpt.isPresent()) {
            result.put("hasApplied", false);
            result.put("message", "您尚未申请志愿者");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        Volunteer volunteer = volunteerOpt.get();
        result.put("hasApplied", true);
        result.put("status", volunteer.getStatus());
        result.put("applicationTime", volunteer.getApplicationTime());
        result.put("approvalTime", volunteer.getApprovalTime());
        result.put("volunteerId", volunteer.getId());
        result.put("realName", volunteer.getRealName());
        result.put("skills", volunteer.getSkills());
        result.put("serviceHours", volunteer.getServiceHours());
        result.put("totalServiceHours", volunteer.getTotalServiceHours());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
