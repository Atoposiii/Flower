package com.flower.controller;

import com.flower.entity.ActivityRegistration;
import com.flower.service.ActivityRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/registrations")
public class ActivityRegistrationController {

    @Autowired
    private ActivityRegistrationService registrationService;

    @GetMapping
    public ResponseEntity<Iterable<ActivityRegistration>> getAllRegistrations() {
        return new ResponseEntity<>(registrationService.getAllRegistrations(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityRegistration> getRegistrationById(@PathVariable Integer id) {
        Optional<ActivityRegistration> registration = registrationService.getRegistrationById(id);
        return registration.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ActivityRegistration>> getRegistrationsByActivityId(@PathVariable Integer activityId) {
        List<ActivityRegistration> registrations = registrationService.getRegistrationsByActivityId(activityId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<ActivityRegistration>> getRegistrationsByVolunteerId(@PathVariable Integer volunteerId) {
        List<ActivityRegistration> registrations = registrationService.getRegistrationsByVolunteerId(volunteerId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @PostMapping("/activity/{activityId}/volunteer/{volunteerId}")
    public ResponseEntity<ActivityRegistration> registerActivity(
            @PathVariable Integer activityId,
            @PathVariable Integer volunteerId) {
        ActivityRegistration registration = registrationService.registerActivity(activityId, volunteerId);
        return new ResponseEntity<>(registration, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ActivityRegistration> confirmRegistration(@PathVariable Integer id) {
        ActivityRegistration registration = registrationService.confirmRegistration(id);
        return registration != null ?
                new ResponseEntity<>(registration, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ActivityRegistration> cancelRegistration(@PathVariable Integer id) {
        ActivityRegistration registration = registrationService.cancelRegistration(id);
        return registration != null ?
                new ResponseEntity<>(registration, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ActivityRegistration> completeRegistration(
            @PathVariable Integer id,
            @RequestParam int actualHours,
            @RequestParam(required = false) String feedback,
            @RequestParam(required = false) Integer rating) {
        ActivityRegistration registration = registrationService.completeRegistration(id, actualHours, feedback, rating);
        return registration != null ?
                new ResponseEntity<>(registration, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Integer id) {
        registrationService.deleteRegistration(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
