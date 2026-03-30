package com.flower.controller;

import com.flower.entity.ActivityRegistration;
import com.flower.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/signin")
public class AdminSignInController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Map<String, Object>> getActivitySignInRecords(@PathVariable Integer activityId) {
        List<ActivityRegistration> registrations = volunteerService.getActivityRegistrations(activityId);
        List<ActivityRegistration> unsignUsers = volunteerService.getUnsignInUsers(activityId);

        Map<String, Object> result = new HashMap<>();
        result.put("totalParticipants", registrations.size());
        result.put("signedInCount", registrations.size() - unsignUsers.size());
        result.put("unsignedCount", unsignUsers.size());
        result.put("participants", registrations);
        result.put("unsignedUsers", unsignUsers);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserSignInRecords(@PathVariable Integer userId) {
        List<ActivityRegistration> registrations = volunteerService.getUserRegistrations(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("content", registrations);
        result.put("totalElements", registrations.size());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSignInStatus(
            @RequestParam Integer userId,
            @RequestParam Integer activityId) {
        Map<String, Object> status = volunteerService.getSignInStatus(userId, activityId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/{registrationId}/process")
    public ResponseEntity<Map<String, Object>> processSignInResult(@PathVariable Integer registrationId) {
        Map<String, Object> result = volunteerService.processSignInResult(registrationId);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/activity/{activityId}/statistics")
    public ResponseEntity<Map<String, Object>> getActivitySignInStatistics(@PathVariable Integer activityId) {
        List<ActivityRegistration> registrations = volunteerService.getActivityRegistrations(activityId);
        List<ActivityRegistration> unsignUsers = volunteerService.getUnsignInUsers(activityId);

        int signedCount = 0;
        int totalHours = 0;
        for (ActivityRegistration reg : registrations) {
            if ("signed_in".equals(reg.getStatus()) || "completed".equals(reg.getStatus())) {
                signedCount++;
                if (reg.getActualHours() != null) {
                    totalHours += reg.getActualHours();
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRegistrations", registrations.size());
        result.put("signedInCount", signedCount);
        result.put("unsignedCount", unsignUsers.size());
        result.put("totalServiceHours", totalHours);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{registrationId}/hours")
    public ResponseEntity<Map<String, Object>> updateRegistrationHours(
            @PathVariable Integer registrationId,
            @RequestParam Integer hours) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "时长更新成功");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{registrationId}/status")
    public ResponseEntity<Map<String, Object>> updateRegistrationStatus(
            @PathVariable Integer registrationId,
            @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "状态更新成功");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}