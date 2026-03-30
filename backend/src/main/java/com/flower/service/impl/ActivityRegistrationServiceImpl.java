package com.flower.service.impl;

import com.flower.entity.ActivityRegistration;
import com.flower.entity.Volunteer;
import com.flower.entity.VolunteerActivity;
import com.flower.repository.ActivityRegistrationRepository;
import com.flower.repository.VolunteerActivityRepository;
import com.flower.repository.VolunteerRepository;
import com.flower.service.ActivityRegistrationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("all")
public class ActivityRegistrationServiceImpl implements ActivityRegistrationService {

    @Resource
    private ActivityRegistrationRepository registrationRepository;

    @Resource
    private VolunteerActivityRepository activityRepository;

    @Resource
    private VolunteerRepository volunteerRepository;

    @Override
    public Iterable<ActivityRegistration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public Optional<ActivityRegistration> getRegistrationById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return registrationRepository.findById(id);
    }

    @Override
    public List<ActivityRegistration> getRegistrationsByActivityId(Integer activityId) {
        if (activityId == null) {
            return List.of();
        }
        return registrationRepository.findByActivityId(activityId);
    }

    @Override
    public List<ActivityRegistration> getRegistrationsByVolunteerId(Integer volunteerId) {
        if (volunteerId == null) {
            return List.of();
        }
        return registrationRepository.findByVolunteerIdOrderByRegistrationTimeDesc(volunteerId);
    }

    @Override
    public ActivityRegistration registerActivity(Integer activityId, Integer volunteerId) {
        if (activityId == null || volunteerId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        Optional<VolunteerActivity> activityOptional = activityRepository.findById(activityId);
        if (!activityOptional.isPresent()) {
            throw new IllegalArgumentException("活动不存在");
        }

        Optional<Volunteer> volunteerOptional = volunteerRepository.findById(volunteerId);
        if (!volunteerOptional.isPresent()) {
            throw new IllegalArgumentException("志愿者不存在");
        }

        Optional<ActivityRegistration> existing = registrationRepository.findByActivityIdAndVolunteerId(activityId, volunteerId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("已经报名过该活动");
        }

        VolunteerActivity activity = activityOptional.get();
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new IllegalArgumentException("活动报名人数已满");
        }

        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivity(activity);
        registration.setVolunteer(volunteerOptional.get());
        registration.setStatus("registered");

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityRepository.save(activity);

        return registrationRepository.save(registration);
    }

    @Override
    public ActivityRegistration confirmRegistration(Integer id) {
        if (id == null) {
            return null;
        }

        Optional<ActivityRegistration> registrationOptional = registrationRepository.findById(id);
        if (!registrationOptional.isPresent()) {
            return null;
        }

        ActivityRegistration registration = registrationOptional.get();
        registration.setStatus("confirmed");
        registration.setConfirmTime(new Date());

        return registrationRepository.save(registration);
    }

    @Override
    public ActivityRegistration cancelRegistration(Integer id) {
        if (id == null) {
            return null;
        }

        Optional<ActivityRegistration> registrationOptional = registrationRepository.findById(id);
        if (!registrationOptional.isPresent()) {
            return null;
        }

        ActivityRegistration registration = registrationOptional.get();
        registration.setStatus("cancelled");

        VolunteerActivity activity = registration.getActivity();
        activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
        activityRepository.save(activity);

        return registrationRepository.save(registration);
    }

    @Override
    public ActivityRegistration completeRegistration(Integer id, int actualHours, String feedback, Integer rating) {
        if (id == null) {
            return null;
        }

        Optional<ActivityRegistration> registrationOptional = registrationRepository.findById(id);
        if (!registrationOptional.isPresent()) {
            return null;
        }

        ActivityRegistration registration = registrationOptional.get();
        registration.setStatus("participated");
        registration.setActualHours(actualHours);
        registration.setFeedback(feedback);
        registration.setRating(rating);

        Volunteer volunteer = registration.getVolunteer();
        volunteer.setServiceHours(volunteer.getServiceHours() + actualHours);
        volunteer.setLastServiceTime(new Date());
        volunteerRepository.save(volunteer);

        return registrationRepository.save(registration);
    }

    @Override
    public void deleteRegistration(Integer id) {
        if (id != null) {
            registrationRepository.deleteById(id);
        }
    }
}
