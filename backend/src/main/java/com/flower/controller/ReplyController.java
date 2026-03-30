package com.flower.controller;

import com.flower.entity.CommentReply;
import com.flower.service.ReplyService;
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
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @PostMapping
    public ResponseEntity<?> createReply(@RequestBody Map<String, Object> body) {
        Integer commentId = body.get("commentId") != null ? Integer.valueOf(body.get("commentId").toString()) : null;
        Integer userId = body.get("userId") != null ? Integer.valueOf(body.get("userId").toString()) : null;
        String content = (String) body.get("content");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (commentId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (content == null || content.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CommentReply reply = replyService.createReply(commentId, userId, content);
        if (reply == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentReply> getReply(@PathVariable Integer id) {
        CommentReply reply = replyService.getReply(id);
        return reply != null ?
                new ResponseEntity<>(reply, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, Object>> getCommentReplies(
            @PathVariable Integer commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentReply> replies = replyService.getCommentReplies(commentId, pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", replies.getContent());
        result.put("totalElements", replies.getTotalElements());
        result.put("totalPages", replies.getTotalPages());
        result.put("currentPage", replies.getNumber());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReply(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        CommentReply reply = replyService.getReply(id);
        if (reply == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        if (!reply.getUser().getId().equals(userId)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "无权删除他人回复");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        boolean success = replyService.deleteReply(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeReply(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = replyService.likeReply(id, userId);
        if (result == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "您已点赞过该回复");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikeReply(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "请先登录");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = replyService.unlikeReply(id, userId);
        if (result == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "您尚未点赞该回复");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        boolean hasLiked = replyService.hasUserLikedReply(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("hasLiked", hasLiked);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}