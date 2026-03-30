package com.flower.service;

import com.flower.entity.VolunteerActivity;
import java.util.List;
import java.util.Optional;

public interface VolunteerActivityService {
    Iterable<VolunteerActivity> getAllActivities();
    Optional<VolunteerActivity> getActivityById(Integer id);
    VolunteerActivity createActivity(VolunteerActivity activity);
    VolunteerActivity updateActivity(Integer id, VolunteerActivity activity);
    void deleteActivity(Integer id);
    List<VolunteerActivity> getActiveActivities();
}