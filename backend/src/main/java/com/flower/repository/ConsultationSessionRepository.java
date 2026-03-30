package com.flower.repository;

import com.flower.entity.ConsultationSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsultationSessionRepository extends JpaRepository<ConsultationSession, Integer> {
    Page<ConsultationSession> findByUserIdOrderByLastMessageTimeDesc(Integer userId, Pageable pageable);

    Page<ConsultationSession> findByChannelOrderByLastMessageTimeDesc(String channel, Pageable pageable);

    Page<ConsultationSession> findByStatusOrderByLastMessageTimeDesc(String status, Pageable pageable);

    @Query("SELECT cs FROM ConsultationSession cs WHERE cs.channel = :channel AND cs.status = :status ORDER BY cs.lastMessageTime DESC")
    Page<ConsultationSession> findByChannelAndStatus(@Param("channel") String channel, @Param("status") String status, Pageable pageable);

    @Query("SELECT cs FROM ConsultationSession cs ORDER BY cs.lastMessageTime DESC")
    Page<ConsultationSession> findAllOrderByLastMessageTimeDesc(Pageable pageable);

    Page<ConsultationSession> findByUserIdAndFlowerIdAndChannelOrderByLastMessageTimeDesc(
            Integer userId, Integer flowerId, String channel, Pageable pageable);
}