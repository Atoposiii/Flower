package com.flower.repository;

import com.flower.entity.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Integer> {
    Optional<ReplyLike> findByReplyIdAndUserId(Integer replyId, Integer userId);

    boolean existsByReplyIdAndUserId(Integer replyId, Integer userId);

    void deleteByReplyIdAndUserId(Integer replyId, Integer userId);

    long countByReplyId(Integer replyId);
}