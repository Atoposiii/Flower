package com.flower.controller;

import com.flower.entity.ActivityRegistration;
import com.flower.entity.VolunteerActivity;
import com.flower.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/activity")
public class AdminActivityController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) Boolean isMemberOnly,
            @RequestParam(defaultValue = "createdTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        List<VolunteerActivity> activities;
        if (status != null && !status.isEmpty()) {
            activities = volunteerService.getActivitiesByStatus(status);
        } else {
            activities = volunteerService.getAllActivities();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", activities);
        result.put("totalElements", activities.size());
        result.put("totalPages", 1);
        result.put("currentPage", 0);
        result.put("pageSize", size);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerActivity> getActivity(@PathVariable Integer id) {
        return volunteerService.getActivityById(id)
                .map(activity -> new ResponseEntity<>(activity, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createActivity(@RequestBody VolunteerActivity activity) {
        try {
            VolunteerActivity created = volunteerService.createActivity(activity);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "活动创建成功");
            result.put("data", created);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "活动创建失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateActivity(
            @PathVariable Integer id,
            @RequestBody VolunteerActivity activity) {
        try {
            VolunteerActivity updated = volunteerService.updateActivity(id, activity);
            if (updated != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "活动更新成功");
                result.put("data", updated);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "活动不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "活动更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteActivity(@PathVariable Integer id) {
        try {
            volunteerService.deleteActivity(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "活动删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "活动删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteActivitiesBatch(@RequestBody List<Integer> ids) {
        try {
            int deletedCount = 0;
            for (Integer id : ids) {
                volunteerService.deleteActivity(id);
                deletedCount++;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量删除成功，共删除 " + deletedCount + " 个活动");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateActivityStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            VolunteerActivity activity = volunteerService.getActivityById(id).orElse(null);
            if (activity == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "活动不存在");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
            activity.setStatus(status);
            VolunteerActivity updated = volunteerService.updateActivity(id, activity);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "状态更新成功");
            result.put("data", updated);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<Map<String, Object>> getActivityParticipants(@PathVariable Integer id) {
        List<ActivityRegistration> registrations = volunteerService.getActivityRegistrations(id);
        Map<String, Object> result = new HashMap<>();
        result.put("content", registrations);
        result.put("totalElements", registrations.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableActivities() {
        List<VolunteerActivity> activities = volunteerService.getAvailableActivities();
        Map<String, Object> result = new HashMap<>();
        result.put("content", activities);
        result.put("totalElements", activities.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/member-only")
    public ResponseEntity<Map<String, Object>> getMemberOnlyActivities() {
        List<VolunteerActivity> activities = volunteerService.getMemberOnlyActivities();
        Map<String, Object> result = new HashMap<>();
        result.put("content", activities);
        result.put("totalElements", activities.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id}/calculate-hours")
    public ResponseEntity<Map<String, Object>> calculateActivityHours(@PathVariable Integer id) {
        Map<String, Object> result = volunteerService.calculateActivityHours(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id}/grant-hours")
    public ResponseEntity<Map<String, Object>> grantActivityHours(@PathVariable Integer id) {
        Map<String, Object> result = volunteerService.grantActivityHours(id);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<VolunteerActivity> allActivities = volunteerService.getAllActivities();
        List<VolunteerActivity> availableActivities = volunteerService.getAvailableActivities();
        List<VolunteerActivity> memberOnlyActivities = volunteerService.getMemberOnlyActivities();

        Map<String, Object> result = new HashMap<>();
        result.put("totalActivities", allActivities.size());
        result.put("availableCount", availableActivities.size());
        result.put("memberOnlyCount", memberOnlyActivities.size());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}