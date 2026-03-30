package com.flower.repository;

import com.flower.entity.CommentReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Integer> {
    List<CommentReply> findByCommentIdAndStatusOrderByCreateTimeAsc(Integer commentId, String status);

    Page<CommentReply> findByCommentIdAndStatusOrderByCreateTimeAsc(Integer commentId, String status, Pageable pageable);

    Page<CommentReply> findByUserIdAndStatusOrderByCreateTimeDesc(Integer userId, String status, Pageable pageable);

    @Modifying
    @Query("UPDATE CommentReply r SET r.likeCount = r.likeCount + 1 WHERE r.id = :replyId")
    void incrementLikeCount(@Param("replyId") Integer replyId);

    @Modifying
    @Query("UPDATE CommentReply r SET r.likeCount = r.likeCount - 1 WHERE r.id = :replyId AND r.likeCount > 0")
    void decrementLikeCount(@Param("replyId") Integer replyId);
}