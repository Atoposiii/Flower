package com.flower.repository;

import com.flower.entity.VolunteerActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerActivityRecordRepository extends JpaRepository<VolunteerActivityRecord, Integer> {
    List<VolunteerActivityRecord> findByVolunteerIdOrderByParticipationTimeDesc(Integer volunteerId);
}
