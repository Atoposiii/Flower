package com.flower.repository;

import com.flower.entity.MemberRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRecordRepository extends JpaRepository<MemberRecord, Integer> {
    List<MemberRecord> findByUserIdOrderByJoinDateDesc(Integer userId);

    Optional<MemberRecord> findByUserId(Integer userId);

    List<MemberRecord> findByStatus(String status);

    Optional<MemberRecord> findByUserIdAndStatus(Integer userId, String status);
}