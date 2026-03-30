package com.flower.service;

import com.flower.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PostService {
    Post createPost(Integer userId, String title, String content, String coverImage);

    Post getPost(Integer postId);

    Page<Post> getAllPosts(Pageable pageable);

    Page<Post> getUserPosts(Integer userId, Pageable pageable);

    Post updatePost(Integer postId, Integer userId, String title, String content, String coverImage);

    boolean deletePost(Integer postId, Integer userId);

    Map<String, Object> likePost(Integer postId, Integer userId);

    Map<String, Object> unlikePost(Integer postId, Integer userId);

    boolean hasUserLikedPost(Integer postId, Integer userId);

    void incrementViewCount(Integer postId);

    Map<String, Object> getPostDetail(Integer postId, Integer userId);

    Map<String, Object> getPostsWithLikedStatus(int page, int size, Integer currentUserId);
}