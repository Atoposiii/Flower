package com.flower.service;

import com.flower.entity.ActivityRegistration;

import java.util.List;
import java.util.Optional;

public interface ActivityRegistrationService {
    Iterable<ActivityRegistration> getAllRegistrations();
    Optional<ActivityRegistration> getRegistrationById(Integer id);
    List<ActivityRegistration> getRegistrationsByActivityId(Integer activityId);
    List<ActivityRegistration> getRegistrationsByVolunteerId(Integer volunteerId);
    ActivityRegistration registerActivity(Integer activityId, Integer volunteerId);
    ActivityRegistration confirmRegistration(Integer id);
    ActivityRegistration cancelRegistration(Integer id);
    ActivityRegistration completeRegistration(Integer id, int actualHours, String feedback, Integer rating);
    void deleteRegistration(Integer id);
}
