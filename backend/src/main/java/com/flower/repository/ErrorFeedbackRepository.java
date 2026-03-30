package com.flower.repository;

import com.flower.entity.ErrorFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ErrorFeedbackRepository extends JpaRepository<ErrorFeedback, Integer> {
    Page<ErrorFeedback> findByUserIdOrderByCreateTimeDesc(Integer userId, Pageable pageable);

    Page<ErrorFeedback> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    Page<ErrorFeedback> findByFeedbackTypeOrderByCreateTimeDesc(String feedbackType, Pageable pageable);

    Page<ErrorFeedback> findByTargetTypeOrderByCreateTimeDesc(String targetType, Pageable pageable);

    Page<ErrorFeedback> findByUserIdAndTargetIdAndTargetTypeOrderByCreateTimeDesc(
            Integer userId, Integer targetId, String targetType, Pageable pageable);

    @Query("SELECT COUNT(e) FROM ErrorFeedback e WHERE e.user.id = :userId AND e.targetId = :targetId AND e.targetType = :targetType AND e.createTime >= :since")
    long countByUserIdAndTargetIdAndTargetTypeSince(
            @Param("userId") Integer userId,
            @Param("targetId") Integer targetId,
            @Param("targetType") String targetType,
            @Param("since") Date since);
}