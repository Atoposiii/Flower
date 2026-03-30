package com.flower.service;

import com.flower.entity.CommentReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReplyService {
    CommentReply createReply(Integer commentId, Integer userId, String content);

    CommentReply getReply(Integer replyId);

    Page<CommentReply> getCommentReplies(Integer commentId, Pageable pageable);

    boolean deleteReply(Integer replyId, Integer userId);

    Map<String, Object> likeReply(Integer replyId, Integer userId);

    Map<String, Object> unlikeReply(Integer replyId, Integer userId);

    boolean hasUserLikedReply(Integer replyId, Integer userId);
}