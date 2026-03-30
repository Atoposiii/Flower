package com.flower.controller;

import com.flower.entity.Post;
import com.flower.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/post")
public class AdminPostController {

    @Autowired
    private PostService postService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> postPage = postService.getAllPosts(pageable);

        if (keyword != null && !keyword.isEmpty()) {
            postPage = postService.getAllPosts(pageable);
        }

        if (userId != null) {
            postPage = postService.getUserPosts(userId, pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", postPage.getContent());
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        result.put("currentPage", postPage.getNumber());
        result.put("pageSize", postPage.getSize());
        result.put("hasNext", postPage.hasNext());
        result.put("hasPrevious", postPage.hasPrevious());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Integer id) {
        Map<String, Object> postDetail = postService.getPostDetail(id, null);
        if (postDetail != null) {
            return new ResponseEntity<>(postDetail, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestParam Integer userId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String coverImage) {
        try {
            Post post = postService.createPost(userId, title, content, coverImage);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "帖子创建成功");
            result.put("data", post);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "帖子创建失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String coverImage) {
        try {
            Post post = postService.updatePost(id, userId, title, content, coverImage);
            if (post != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "帖子更新成功");
                result.put("data", post);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "帖子不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "帖子更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Integer id) {
        try {
            postService.deletePost(id, null);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "帖子删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "帖子删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deletePostsBatch(@RequestBody List<Integer> ids) {
        try {
            int deletedCount = 0;
            for (Integer id : ids) {
                postService.deletePost(id, null);
                deletedCount++;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量删除成功，共删除 " + deletedCount + " 条帖子");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updatePostStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            Post post = postService.getPost(id);
            if (post == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "帖子不存在");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
            post.setStatus(status);
            Post updated = postService.updatePost(id, post.getUser().getId(), post.getTitle(), post.getContent(), post.getCoverImage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "状态更新成功");
            result.put("data", updated);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPosts(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.getUserPosts(userId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", posts.getContent());
        result.put("totalElements", posts.getTotalElements());
        result.put("totalPages", posts.getTotalPages());
        result.put("currentPage", posts.getNumber());
        result.put("pageSize", posts.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 1);

        Page<Post> allPosts = postService.getAllPosts(pageable);

        result.put("totalPosts", allPosts.getTotalElements());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.getAllPosts(pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", posts.getContent());
        result.put("totalElements", posts.getTotalElements());
        result.put("totalPages", posts.getTotalPages());
        result.put("currentPage", posts.getNumber());
        result.put("pageSize", posts.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}