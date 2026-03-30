package com.flower.controller;

import com.flower.entity.ActivityRegistration;
import com.flower.entity.VolunteerActivity;
import com.flower.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    @Autowired
    private VolunteerService volunteerService;

    @GetMapping
    public ResponseEntity<Iterable<VolunteerActivity>> getAllActivities() {
        Iterable<VolunteerActivity> activities = volunteerService.getAllActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerActivity> getActivityById(@PathVariable Integer id) {
        Optional<VolunteerActivity> activity = volunteerService.getActivityById(id);
        return activity.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<VolunteerActivity> createActivity(@RequestBody VolunteerActivity activity) {
        try {
            VolunteerActivity createdActivity = volunteerService.createActivity(activity);
            return new ResponseEntity<>(createdActivity, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerActivity> updateActivity(
            @PathVariable Integer id,
            @RequestBody VolunteerActivity activity) {
        VolunteerActivity updatedActivity = volunteerService.updateActivity(id, activity);
        return updatedActivity != null ? new ResponseEntity<>(updatedActivity, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Integer id) {
        volunteerService.deleteActivity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/available")
    public ResponseEntity<List<VolunteerActivity>> getAvailableActivities() {
        List<VolunteerActivity> activities = volunteerService.getAvailableActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    @GetMapping("/member-only")
    public ResponseEntity<List<VolunteerActivity>> getMemberOnlyActivities() {
        List<VolunteerActivity> activities = volunteerService.getMemberOnlyActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VolunteerActivity>> getActivitiesByStatus(@PathVariable String status) {
        List<VolunteerActivity> activities = volunteerService.getActivitiesByStatus(status);
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    @PostMapping("/{id}/register/{userId}")
    public ResponseEntity<Map<String, Object>> registerForActivity(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        Map<String, Object> result = volunteerService.registerForActivity(userId, id);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{id}/cancel/{userId}")
    public ResponseEntity<Map<String, Object>> cancelRegistration(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        Map<String, Object> result = volunteerService.cancelRegistration(userId, id);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<List<ActivityRegistration>> getActivityRegistrations(@PathVariable Integer id) {
        List<ActivityRegistration> registrations = volunteerService.getActivityRegistrations(id);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/registrations")
    public ResponseEntity<List<ActivityRegistration>> getUserRegistrations(@PathVariable Integer userId) {
        List<ActivityRegistration> registrations = volunteerService.getUserRegistrations(userId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @GetMapping("/{id}/check/{userId}")
    public ResponseEntity<Boolean> hasUserRegistered(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        boolean registered = volunteerService.hasUserRegistered(userId, id);
        return new ResponseEntity<>(registered, HttpStatus.OK);
    }

    @PostMapping("/{id}/signin/{userId}")
    public ResponseEntity<Map<String, Object>> signIn(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        Map<String, Object> result = volunteerService.signIn(userId, id);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}/signin-status/{userId}")
    public ResponseEntity<Map<String, Object>> getSignInStatus(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        Map<String, Object> result = volunteerService.getSignInStatus(userId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}/unsign-users")
    public ResponseEntity<List<ActivityRegistration>> getUnsignInUsers(@PathVariable Integer id) {
        List<ActivityRegistration> users = volunteerService.getUnsignInUsers(id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/registration/{registrationId}/process")
    public ResponseEntity<Map<String, Object>> processSignInResult(@PathVariable Integer registrationId) {
        Map<String, Object> result = volunteerService.processSignInResult(registrationId);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}/calculate-hours")
    public ResponseEntity<Map<String, Object>> calculateActivityHours(@PathVariable Integer id) {
        Map<String, Object> result = volunteerService.calculateActivityHours(id);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}