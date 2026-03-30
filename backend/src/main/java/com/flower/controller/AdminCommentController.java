package com.flower.controller;

import com.flower.entity.Comment;
import com.flower.entity.Post;
import com.flower.repository.PostRepository;
import com.flower.service.CommentService;
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
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Comment> commentPage;

        if (postId != null) {
            commentPage = commentService.getPostComments(postId, pageable);
        } else if (userId != null) {
            commentPage = commentService.getUserComments(userId, pageable);
        } else {
            commentPage = commentService.getPostComments(1, pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", commentPage.getContent());
        result.put("totalElements", commentPage.getTotalElements());
        result.put("totalPages", commentPage.getTotalPages());
        result.put("currentPage", commentPage.getNumber());
        result.put("pageSize", commentPage.getSize());
        result.put("hasNext", commentPage.hasNext());
        result.put("hasPrevious", commentPage.hasPrevious());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable Integer id) {
        Comment comment = commentService.getComment(id);
        return comment != null ?
                new ResponseEntity<>(comment, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(
            @RequestParam Integer postId,
            @RequestParam Integer userId,
            @RequestParam String content) {
        try {
            Comment comment = commentService.createComment(postId, userId, content);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评论创建成功");
            result.put("data", comment);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "评论创建失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable Integer id,
            @RequestParam Integer userId,
            @RequestParam String content) {
        try {
            Comment existingComment = commentService.getComment(id);
            if (existingComment == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "评论不存在");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
            existingComment.setContent(content);
            Post post = existingComment.getPost();
            Comment updated = commentService.createComment(post.getId(), userId, content);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评论更新成功");
            result.put("data", updated);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "评论更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Integer id) {
        try {
            commentService.deleteComment(id, null);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "评论删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "评论删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteCommentsBatch(@RequestBody List<Integer> ids) {
        try {
            int deletedCount = 0;
            for (Integer id : ids) {
                commentService.deleteComment(id, null);
                deletedCount++;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量删除成功，共删除 " + deletedCount + " 条评论");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCommentStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            Comment comment = commentService.getComment(id);
            if (comment == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "评论不存在");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
            comment.setStatus(status);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "状态更新成功");
            result.put("data", comment);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getPostComments(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getPostComments(postId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", comments.getContent());
        result.put("totalElements", comments.getTotalElements());
        result.put("totalPages", comments.getTotalPages());
        result.put("currentPage", comments.getNumber());
        result.put("pageSize", comments.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserComments(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getUserComments(userId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", comments.getContent());
        result.put("totalElements", comments.getTotalElements());
        result.put("totalPages", comments.getTotalPages());
        result.put("currentPage", comments.getNumber());
        result.put("pageSize", comments.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 1);

        Page<Comment> comments = commentService.getPostComments(1, pageable);
        result.put("totalComments", comments.getTotalElements());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}