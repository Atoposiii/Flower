package com.flower.service;

import com.flower.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommentService {
    Comment createComment(Integer postId, Integer userId, String content);

    Comment getComment(Integer commentId);

    Page<Comment> getPostComments(Integer postId, Pageable pageable);

    Page<Comment> getUserComments(Integer userId, Pageable pageable);

    boolean deleteComment(Integer commentId, Integer userId);

    Map<String, Object> likeComment(Integer commentId, Integer userId);

    Map<String, Object> unlikeComment(Integer commentId, Integer userId);

    boolean hasUserLikedComment(Integer commentId, Integer userId);

    List<Map<String, Object>> getPostCommentsWithReplies(Integer postId, Integer currentUserId);
}