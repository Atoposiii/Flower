package com.flower.controller;

import com.flower.entity.VolunteerActivity;
import com.flower.service.VolunteerActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/volunteer-activities")
public class VolunteerActivityController {
    @Autowired
    private VolunteerActivityService volunteerActivityService;

    @GetMapping
    public ResponseEntity<Iterable<VolunteerActivity>> getAllActivities() {
        Iterable<VolunteerActivity> activities = volunteerActivityService.getAllActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerActivity> getActivityById(@PathVariable Integer id) {
        Optional<VolunteerActivity> activity = volunteerActivityService.getActivityById(id);
        return activity.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<VolunteerActivity> createActivity(@RequestBody VolunteerActivity activity) {
        VolunteerActivity createdActivity = volunteerActivityService.createActivity(activity);
        return new ResponseEntity<>(createdActivity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerActivity> updateActivity(@PathVariable Integer id, @RequestBody VolunteerActivity activity) {
        VolunteerActivity updatedActivity = volunteerActivityService.updateActivity(id, activity);
        return updatedActivity != null ? new ResponseEntity<>(updatedActivity, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Integer id) {
        volunteerActivityService.deleteActivity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/active")
    public ResponseEntity<List<VolunteerActivity>> getActiveActivities() {
        List<VolunteerActivity> activeActivities = volunteerActivityService.getActiveActivities();
        return new ResponseEntity<>(activeActivities, HttpStatus.OK);
    }
}