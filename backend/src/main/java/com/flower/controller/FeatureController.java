package com.flower.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/features")
public class FeatureController {

    @Value("${feature.chat.enabled:true}")
    private boolean chatEnabled;

    @Value("${feature.feedback.enabled:true}")
    private boolean feedbackEnabled;

    @Value("${feature.payment.enabled:true}")
    private boolean paymentEnabled;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("chatEnabled", chatEnabled);
        features.put("feedbackEnabled", feedbackEnabled);
        features.put("paymentEnabled", paymentEnabled);
        return ResponseEntity.ok(features);
    }
}
