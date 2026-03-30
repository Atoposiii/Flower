package com.flower.repository;

import com.flower.entity.MemberConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberConsultationRepository extends JpaRepository<MemberConsultation, Integer> {
    List<MemberConsultation> findByUserId(Integer userId);
    List<MemberConsultation> findByStatus(String status);
    List<MemberConsultation> findByStatusOrderByCreateTimeDesc(String status);
    List<MemberConsultation> findAllByOrderByCreateTimeDesc();
}
