package com.flower.repository;

import com.flower.entity.ConsultationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationMessageRepository extends JpaRepository<ConsultationMessage, Integer> {
    List<ConsultationMessage> findBySessionIdOrderByCreateTimeAsc(Integer sessionId);
}