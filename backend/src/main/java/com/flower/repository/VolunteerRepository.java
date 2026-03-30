package com.flower.repository;

import com.flower.entity.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {
    Optional<Volunteer> findByUserId(Integer userId);

    List<Volunteer> findByStatus(String status);

    List<Volunteer> findByStatusOrderByServiceHoursDesc(String status);

    List<Volunteer> findByOrderByServiceHoursDesc();

    Page<Volunteer> findByStatusOrderByServiceHoursDesc(String status, Pageable pageable);

    @Query("SELECT v FROM Volunteer v WHERE v.status = 'approved' ORDER BY v.serviceHours DESC")
    List<Volunteer> findApprovedVolunteersOrderByServiceHoursDesc();

    @Query("SELECT v FROM Volunteer v WHERE v.status = 'approved' AND v.serviceHours > 0 ORDER BY v.serviceHours DESC")
    List<Volunteer> findActiveVolunteersWithHours();

    boolean existsByUserId(Integer userId);
}