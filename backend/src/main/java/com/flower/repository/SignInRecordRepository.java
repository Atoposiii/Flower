package com.flower.repository;

import com.flower.entity.SignInRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignInRecordRepository extends JpaRepository<SignInRecord, Integer> {
    List<SignInRecord> findByRegistrationId(Integer registrationId);

    Optional<SignInRecord> findByRegistrationIdAndStatus(Integer registrationId, String status);

    List<SignInRecord> findByStatus(String status);
}