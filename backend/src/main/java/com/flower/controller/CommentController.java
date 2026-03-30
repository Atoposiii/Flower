package com.flower.controller;

import com.flower.entity.Comment;
import com.flower.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> body) {
        Integer postId = body.get("postId") != null ? Integer.valueOf(body.get("postId").toString()) : null;
        Integer userId = body.get("userId") != null ? Integer.valueOf(body.get("userId").toString()) : null;
        String content = (String) body.get("content");
        log.info("[createComment] postId={}, userId={}, content='{}'", postId, userId, content);
        if (userId == null) {
            log.warn("[createComment] 未登录，拒绝请求");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (postId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (content == null || content.trim().isEmpty()) {
            log.warn("[createComment] 评论内容为空，postId={}, userId={}", postId, userId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Comment comment = commentService.createComment(postId, userId, content);
        if (comment == null) {
            log.warn("[createComment] 创建失败（post或user不存在），postId={}, userId={}", postId, userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("[createComment] 评论创建成功，commentId={}", comment.getId());
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable Integer id) {
        Comment comment = commentService.getComment(id);
        return comment != null ?
                new ResponseEntity<>(comment, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getPostComments(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getPostComments(postId, pageable);
        return buildPageResponse(comments);
    }

    @GetMapping("/post/{postId}/tree")
    public ResponseEntity<?> getPostCommentsTree(
            @PathVariable Integer postId,
            @RequestParam(required = false) Integer userId) {
        java.util.List<Map<String, Object>> comments = commentService.getPostCommentsWithReplies(postId, userId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserComments(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getUserComments(userId, pageable);
        return buildPageResponse(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Comment comment = commentService.getComment(id);
        if (comment == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        if (!comment.getUser().getId().equals(userId)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "无权删除他人评论");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        boolean success = commentService.deleteComment(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeComment(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = commentService.likeComment(id, userId);
        if (result == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "您已点赞过该评论");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikeComment(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = commentService.unlikeComment(id, userId);
        if (result == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "您尚未点赞该评论");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        boolean hasLiked = commentService.hasUserLikedComment(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("hasLiked", hasLiked);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> buildPageResponse(Page<Comment> comments) {
        Map<String, Object> result = new HashMap<>();
        result.put("content", comments.getContent());
        result.put("totalElements", comments.getTotalElements());
        result.put("totalPages", comments.getTotalPages());
        result.put("currentPage", comments.getNumber());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}