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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class IndexController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.getAllPosts(pageable);
        return buildPageResponse(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable Integer id) {
        Map<String, Object> result = postService.getPostDetail(id, null);
        if (result != null && result.get("post") != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPostsByUserId(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.getUserPosts(userId, pageable);
        return buildPageResponse(posts);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        if (post.getUser() == null || post.getUser().getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Post savedPost = postService.createPost(
                post.getUser().getId(),
                post.getTitle(),
                post.getContent(),
                post.getCoverImage()
        );
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Integer id, @RequestBody Post post) {
        if (post.getUser() == null || post.getUser().getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Post updatedPost = postService.updatePost(
                id,
                post.getUser().getId(),
                post.getTitle(),
                post.getContent(),
                post.getCoverImage()
        );
        return updatedPost != null ? new ResponseEntity<>(updatedPost, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{postId}/user/{userId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId, @PathVariable Integer userId) {
        boolean deleted = postService.deletePost(postId, userId);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer id) {
        postService.incrementViewCount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        Map<String, Object> result = postService.likePost(id, userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikePost(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        Map<String, Object> result = postService.unlikePost(id, userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}/like/status")
    public ResponseEntity<Map<String, Object>> hasUserLikedPost(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("liked", postService.hasUserLikedPost(id, userId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> buildPageResponse(Page<Post> page) {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("pageSize", page.getSize());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}