package com.flower.controller;

import com.flower.entity.Post;
import com.flower.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> body) {
        Integer userId = body.get("userId") != null ? Integer.valueOf(body.get("userId").toString()) : null;
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        String coverImage = (String) body.get("coverImage");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Post post = postService.createPost(userId, title, content, coverImage);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPost(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        Map<String, Object> result = postService.getPostDetail(id, userId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer userId) {
        Map<String, Object> result = postService.getPostsWithLikedStatus(page, size, userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPosts(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.getUserPosts(userId, pageable);
        return buildPageResponse(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        Integer userId = body.get("userId") != null ? Integer.valueOf(body.get("userId").toString()) : null;
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        String coverImage = (String) body.get("coverImage");
        Post post = postService.updatePost(id, userId, title, content, coverImage);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Post existing = postService.getPost(id);
        if (existing == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        if (!existing.getUser().getId().equals(userId)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "无权删除他人帖子");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        boolean success = postService.deletePost(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = postService.likePost(id, userId);
        if (result == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "您已点赞过该帖子");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikePost(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = postService.unlikePost(id, userId);
        if (result == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "您尚未点赞该帖子");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        boolean hasLiked = postService.hasUserLikedPost(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("hasLiked", hasLiked);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> buildPageResponse(Page<Post> posts) {
        Map<String, Object> result = new HashMap<>();
        result.put("content", posts.getContent());
        result.put("totalElements", posts.getTotalElements());
        result.put("totalPages", posts.getTotalPages());
        result.put("currentPage", posts.getNumber());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}