package com.flower.controller;

import com.flower.entity.Volunteer;
import com.flower.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {
    @Autowired
    private VolunteerService volunteerService;

    @GetMapping
    public ResponseEntity<Iterable<Volunteer>> getAllVolunteers() {
        Iterable<Volunteer> volunteers = volunteerService.getAllVolunteers();
        return new ResponseEntity<>(volunteers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getVolunteerById(@PathVariable Integer id) {
        Optional<Volunteer> volunteer = volunteerService.getVolunteerById(id);
        return volunteer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Volunteer> getVolunteerByUserId(@PathVariable Integer userId) {
        Optional<Volunteer> volunteer = volunteerService.getVolunteerByUserId(userId);
        return volunteer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/apply/{userId}")
    public ResponseEntity<Volunteer> applyVolunteer(@PathVariable Integer userId, @RequestBody Volunteer volunteer) {
        Volunteer appliedVolunteer = volunteerService.applyVolunteer(userId, volunteer);
        return new ResponseEntity<>(appliedVolunteer, HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Volunteer> approveVolunteer(@PathVariable Integer id, @RequestParam boolean approve) {
        Volunteer approvedVolunteer = volunteerService.approveVolunteer(id, approve);
        return approvedVolunteer != null ? new ResponseEntity<>(approvedVolunteer, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/hours")
    public ResponseEntity<Volunteer> updateServiceHours(@PathVariable Integer id, @RequestParam int hours) {
        Volunteer updatedVolunteer = volunteerService.updateServiceHours(id, hours);
        return updatedVolunteer != null ? new ResponseEntity<>(updatedVolunteer, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Volunteer>> getPendingVolunteers() {
        List<Volunteer> pendingVolunteers = volunteerService.getPendingVolunteers();
        return new ResponseEntity<>(pendingVolunteers, HttpStatus.OK);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Volunteer>> getApprovedVolunteers() {
        List<Volunteer> approvedVolunteers = volunteerService.getApprovedVolunteers();
        return new ResponseEntity<>(approvedVolunteers, HttpStatus.OK);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Volunteer>> getVolunteersByServiceHours() {
        List<Volunteer> rankedVolunteers = volunteerService.getVolunteersByServiceHours();
        return new ResponseEntity<>(rankedVolunteers, HttpStatus.OK);
    }

    @PutMapping("/{id}/quit")
    public ResponseEntity<Volunteer> quitVolunteer(@PathVariable Integer id) {
        Volunteer quittedVolunteer = volunteerService.quitVolunteer(id);
        return quittedVolunteer != null ? new ResponseEntity<>(quittedVolunteer, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteer(@PathVariable Integer id) {
        volunteerService.deleteVolunteer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Object>> checkVolunteerStatus(@PathVariable Integer userId) {
        boolean isVolunteer = volunteerService.isUserVolunteer(userId);
        boolean isMember = volunteerService.isUserMember(userId);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("isVolunteer", isVolunteer);
        result.put("isMember", isMember);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/become/{userId}")
    public ResponseEntity<Volunteer> becomeVolunteer(@PathVariable Integer userId) {
        try {
            Volunteer volunteer = volunteerService.becomeVolunteer(userId);
            return new ResponseEntity<>(volunteer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/quit/{userId}")
    public ResponseEntity<Map<String, Object>> quitVolunteerWithWarning(@PathVariable Integer userId) {
        Map<String, Object> result = volunteerService.quitVolunteerWithWarning(userId);
        if ((boolean) result.get("success")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/rankings")
    public ResponseEntity<List<Volunteer>> getVolunteerRanking() {
        List<Volunteer> ranking = volunteerService.getVolunteerRanking();
        return new ResponseEntity<>(ranking, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Volunteer>> getActiveVolunteers() {
        List<Volunteer> volunteers = volunteerService.getActiveVolunteersWithHours();
        return new ResponseEntity<>(volunteers, HttpStatus.OK);
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<java.util.List<Map<String, Object>>> getMyRegistrations(@RequestParam Integer userId) {
        java.util.List<Map<String, Object>> registrations = volunteerService.getMyRegistrations(userId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }
}