package com.flower.service;

import com.flower.entity.ActivityRegistration;
import com.flower.entity.Volunteer;
import com.flower.entity.VolunteerActivity;
import com.flower.entity.VolunteerActivityRecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VolunteerService {
    Iterable<Volunteer> getAllVolunteers();
    Optional<Volunteer> getVolunteerById(Integer id);
    Optional<Volunteer> getVolunteerByUserId(Integer userId);
    Volunteer applyVolunteer(Integer userId, Volunteer volunteer);
    Volunteer approveVolunteer(Integer id, boolean approve);
    Map<String, Object> approveVolunteerWithCheck(Integer id);
    Map<String, Object> rejectVolunteerWithCheck(Integer id);
    VolunteerActivityRecord addActivityRecord(Integer volunteerId, VolunteerActivityRecord record);
    List<VolunteerActivityRecord> getActivityRecordsByVolunteerId(Integer volunteerId);
    Volunteer updateServiceHours(Integer id, int hours);
    List<Volunteer> getPendingVolunteers();
    List<Volunteer> getApprovedVolunteers();
    List<Volunteer> getVolunteersByStatus(String status);
    List<Volunteer> getVolunteersByServiceHours();
    Volunteer quitVolunteer(Integer id);
    void deleteVolunteer(Integer id);

    boolean isUserVolunteer(Integer userId);
    boolean isUserMember(Integer userId);
    Volunteer becomeVolunteer(Integer userId);
    Map<String, Object> quitVolunteerWithWarning(Integer userId);

    List<Volunteer> getVolunteerRanking();
    List<Volunteer> getActiveVolunteersWithHours();

    List<VolunteerActivity> getAllActivities();
    Optional<VolunteerActivity> getActivityById(Integer id);
    VolunteerActivity createActivity(VolunteerActivity activity);
    VolunteerActivity updateActivity(Integer id, VolunteerActivity activity);
    void deleteActivity(Integer id);
    List<VolunteerActivity> getAvailableActivities();
    List<VolunteerActivity> getMemberOnlyActivities();
    List<VolunteerActivity> getActivitiesByStatus(String status);

    Map<String, Object> registerForActivity(Integer userId, Integer activityId);
    Map<String, Object> cancelRegistration(Integer userId, Integer activityId);
    List<ActivityRegistration> getUserRegistrations(Integer userId);
    List<ActivityRegistration> getActivityRegistrations(Integer activityId);
    boolean hasUserRegistered(Integer userId, Integer activityId);

    Map<String, Object> signIn(Integer userId, Integer activityId);
    Map<String, Object> getSignInStatus(Integer userId, Integer activityId);
    List<ActivityRegistration> getUnsignInUsers(Integer activityId);
    Map<String, Object> processSignInResult(Integer registrationId);

    Map<String, Object> calculateActivityHours(Integer activityId);

    Map<String, Object> grantActivityHours(Integer activityId);

    List<Map<String, Object>> getMyRegistrations(Integer userId);
}