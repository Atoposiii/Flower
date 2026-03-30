package com.flower.repository;

import com.flower.entity.ActivityRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Integer> {
    List<ActivityRegistration> findByActivityId(Integer activityId);

    List<ActivityRegistration> findByVolunteerId(Integer volunteerId);

    Optional<ActivityRegistration> findByActivityIdAndVolunteerId(Integer activityId, Integer volunteerId);

    List<ActivityRegistration> findByStatus(String status);

    List<ActivityRegistration> findByVolunteerIdOrderByRegistrationTimeDesc(Integer volunteerId);

    List<ActivityRegistration> findByActivityIdAndSignInStatus(Integer activityId, String signInStatus);

    List<ActivityRegistration> findBySignInStatus(String signInStatus);

    boolean existsByActivityIdAndVolunteerId(Integer activityId, Integer volunteerId);
}