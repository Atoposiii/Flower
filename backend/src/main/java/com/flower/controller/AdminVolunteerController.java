package com.flower.controller;

import com.flower.entity.Volunteer;
import com.flower.entity.VolunteerActivityRecord;
import com.flower.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/volunteer")
public class AdminVolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllVolunteers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "serviceHours") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        List<Volunteer> volunteers;
        if (status != null && !status.isEmpty()) {
            volunteers = volunteerService.getVolunteersByStatus(status);
        } else {
            volunteers = (List<Volunteer>) volunteerService.getAllVolunteers();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", volunteers);
        result.put("totalElements", volunteers.size());
        result.put("totalPages", 1);
        result.put("currentPage", 0);
        result.put("pageSize", size);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getVolunteer(@PathVariable Integer id) {
        return volunteerService.getVolunteerById(id)
                .map(volunteer -> new ResponseEntity<>(volunteer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Volunteer> getVolunteerByUserId(@PathVariable Integer userId) {
        return volunteerService.getVolunteerByUserId(userId)
                .map(volunteer -> new ResponseEntity<>(volunteer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createVolunteer(
            @RequestParam Integer userId,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String skills) {
        try {
            Volunteer volunteer = new Volunteer();
            volunteer.setRealName(realName);
            volunteer.setIdCard(idCard);
            volunteer.setAddress(address);
            volunteer.setSkills(skills);
            Volunteer created = volunteerService.applyVolunteer(userId, volunteer);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "志愿者创建成功");
            result.put("data", created);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "志愿者创建失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveVolunteer(
            @PathVariable Integer id,
            @RequestParam boolean approve) {
        try {
            Volunteer volunteer = volunteerService.approveVolunteer(id, approve);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", approve ? "志愿者审批通过" : "志愿者审批拒绝");
            result.put("data", volunteer);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "审批失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/approve-check")
    public ResponseEntity<Map<String, Object>> approveVolunteerWithCheck(@PathVariable Integer id) {
        Map<String, Object> result = volunteerService.approveVolunteerWithCheck(id);
        return new ResponseEntity<>(result, (boolean) result.get("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectVolunteer(@PathVariable Integer id) {
        Map<String, Object> result = volunteerService.rejectVolunteerWithCheck(id);
        return new ResponseEntity<>(result, (boolean) result.get("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<Map<String, Object>> getVolunteerHistory(@PathVariable Integer id) {
        return volunteerService.getVolunteerById(id)
                .map(volunteer -> {
                    List<com.flower.entity.VolunteerActivityRecord> activityRecords = volunteerService.getActivityRecordsByVolunteerId(id);
                    Map<String, Object> result = new HashMap<>();
                    result.put("volunteerId", volunteer.getId());
                    result.put("userId", volunteer.getUser() != null ? volunteer.getUser().getId() : null);
                    result.put("realName", volunteer.getRealName());
                    result.put("status", volunteer.getStatus());
                    result.put("applicationTime", volunteer.getApplicationTime());
                    result.put("approvalTime", volunteer.getApprovalTime());
                    result.put("totalServiceHours", volunteer.getTotalServiceHours());
                    result.put("activityRecords", activityRecords);
                    result.put("activityCount", activityRecords.size());
                    return new ResponseEntity<>(result, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<Map<String, Object>> getVolunteerActivityRecords(@PathVariable Integer id) {
        List<com.flower.entity.VolunteerActivityRecord> records = volunteerService.getActivityRecordsByVolunteerId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("content", records);
        result.put("totalElements", records.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<Map<String, Object>> addVolunteerActivityRecord(
            @PathVariable Integer id,
            @RequestBody VolunteerActivityRecord record) {
        try {
            VolunteerActivityRecord saved = volunteerService.addActivityRecord(id, record);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "活动参与记录添加成功");
            result.put("data", saved);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/hours")
    public ResponseEntity<Map<String, Object>> updateServiceHours(
            @PathVariable Integer id,
            @RequestParam int hours) {
        try {
            Volunteer volunteer = volunteerService.updateServiceHours(id, hours);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "服务时长更新成功");
            result.put("data", volunteer);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "服务时长更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/quit")
    public ResponseEntity<Map<String, Object>> quitVolunteer(@PathVariable Integer id) {
        try {
            Volunteer volunteer = volunteerService.quitVolunteer(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "志愿者已退出");
            result.put("data", volunteer);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "退出失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVolunteer(@PathVariable Integer id) {
        try {
            volunteerService.deleteVolunteer(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "志愿者删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking() {
        List<Volunteer> ranking = volunteerService.getVolunteerRanking();
        Map<String, Object> result = new HashMap<>();
        result.put("content", ranking);
        result.put("totalElements", ranking.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveVolunteers() {
        List<Volunteer> volunteers = volunteerService.getActiveVolunteersWithHours();
        Map<String, Object> result = new HashMap<>();
        result.put("content", volunteers);
        result.put("totalElements", volunteers.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<Volunteer> allVolunteers = (List<Volunteer>) volunteerService.getAllVolunteers();
        List<Volunteer> pendingVolunteers = volunteerService.getPendingVolunteers();
        List<Volunteer> approvedVolunteers = volunteerService.getApprovedVolunteers();
        List<Volunteer> ranking = volunteerService.getVolunteerRanking();

        Map<String, Object> result = new HashMap<>();
        result.put("totalVolunteers", allVolunteers.size());
        result.put("pendingCount", pendingVolunteers.size());
        result.put("approvedCount", approvedVolunteers.size());
        result.put("ranking", ranking);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}