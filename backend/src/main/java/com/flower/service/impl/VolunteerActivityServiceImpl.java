package com.flower.service.impl;

import com.flower.entity.VolunteerActivity;
import com.flower.repository.VolunteerActivityRepository;
import com.flower.service.VolunteerActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("all")
public class VolunteerActivityServiceImpl implements VolunteerActivityService {

    @Resource
    private VolunteerActivityRepository volunteerActivityRepository;

    @Override
    public Iterable<VolunteerActivity> getAllActivities() {
        return volunteerActivityRepository.findAll();
    }

    @Override
    public Optional<VolunteerActivity> getActivityById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return volunteerActivityRepository.findById(id);
    }

    @Override
    public VolunteerActivity createActivity(VolunteerActivity activity) {
        if (activity == null) {
            return null;
        }
        activity.setCurrentParticipants(0);
        activity.setStatus("active");
        activity.setCreatedTime(new Date());
        activity.setUpdatedTime(new Date());
        return volunteerActivityRepository.save(activity);
    }

    @Override
    public VolunteerActivity updateActivity(Integer id, VolunteerActivity activity) {
        if (id == null || activity == null) {
            return null;
        }

        Optional<VolunteerActivity> existingActivityOptional = volunteerActivityRepository.findById(id);
        if (!existingActivityOptional.isPresent()) {
            return null;
        }

        VolunteerActivity existingActivity = existingActivityOptional.get();
        existingActivity.setTitle(activity.getTitle());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setActivityTime(activity.getActivityTime());
        existingActivity.setLocation(activity.getLocation());
        existingActivity.setMaxParticipants(activity.getMaxParticipants());
        existingActivity.setStatus(activity.getStatus());
        existingActivity.setUpdatedTime(new Date());

        return volunteerActivityRepository.save(existingActivity);
    }

    @Override
    public void deleteActivity(Integer id) {
        if (id != null) {
            volunteerActivityRepository.deleteById(id);
        }
    }

    @Override
    public List<VolunteerActivity> getActiveActivities() {
        return volunteerActivityRepository.findByStatusIn(Arrays.asList("active", "published"));
    }
}