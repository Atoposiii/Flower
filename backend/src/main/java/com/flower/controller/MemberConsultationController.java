package com.flower.controller;

import com.flower.entity.MemberConsultation;
import com.flower.service.MemberConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consultations")
public class MemberConsultationController {

    @Autowired
    private MemberConsultationService consultationService;

    @GetMapping
    public ResponseEntity<Iterable<MemberConsultation>> getAllConsultations() {
        return new ResponseEntity<>(consultationService.getAllConsultations(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberConsultation> getConsultationById(@PathVariable Integer id) {
        Optional<MemberConsultation> consultation = consultationService.getConsultationById(id);
        return consultation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MemberConsultation>> getConsultationsByUserId(@PathVariable Integer userId) {
        List<MemberConsultation> consultations = consultationService.getConsultationsByUserId(userId);
        return new ResponseEntity<>(consultations, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MemberConsultation>> getConsultationsByStatus(@PathVariable String status) {
        List<MemberConsultation> consultations = consultationService.getConsultationsByStatus(status);
        return new ResponseEntity<>(consultations, HttpStatus.OK);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<MemberConsultation>> getPendingConsultations() {
        List<MemberConsultation> consultations = consultationService.getPendingConsultations();
        return new ResponseEntity<>(consultations, HttpStatus.OK);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<MemberConsultation> createConsultation(
            @PathVariable Integer userId,
            @RequestBody MemberConsultation consultation) {
        MemberConsultation created = consultationService.createConsultation(userId, consultation);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<MemberConsultation> replyConsultation(
            @PathVariable Integer id,
            @RequestParam String replyContent,
            @RequestParam(required = false) String replierName) {
        MemberConsultation replied = consultationService.replyConsultation(id, replyContent, replierName);
        return replied != null ?
                new ResponseEntity<>(replied, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<MemberConsultation> closeConsultation(@PathVariable Integer id) {
        MemberConsultation closed = consultationService.closeConsultation(id);
        return closed != null ?
                new ResponseEntity<>(closed, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Integer id) {
        consultationService.deleteConsultation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
