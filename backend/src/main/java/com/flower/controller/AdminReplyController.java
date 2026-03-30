package com.flower.controller;

import com.flower.entity.CommentReply;
import com.flower.repository.CommentReplyRepository;
import com.flower.service.ReplyService;
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
@RequestMapping("/admin/reply")
public class AdminReplyController {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private CommentReplyRepository commentReplyRepository;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllReplies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer commentId,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CommentReply> replyPage;
        if (commentId != null) {
            replyPage = replyService.getCommentReplies(commentId, pageable);
        } else {
            replyPage = commentReplyRepository.findAll(pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", replyPage.getContent());
        result.put("totalElements", replyPage.getTotalElements());
        result.put("totalPages", replyPage.getTotalPages());
        result.put("currentPage", replyPage.getNumber());
        result.put("pageSize", replyPage.getSize());
        result.put("hasNext", replyPage.hasNext());
        result.put("hasPrevious", replyPage.hasPrevious());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentReply> getReply(@PathVariable Integer id) {
        CommentReply reply = replyService.getReply(id);
        return reply != null ?
                new ResponseEntity<>(reply, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReply(
            @RequestParam Integer commentId,
            @RequestParam Integer userId,
            @RequestParam String content) {
        try {
            CommentReply reply = replyService.createReply(commentId, userId, content);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "回复创建成功");
            result.put("data", reply);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "回复创建失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReply(@PathVariable Integer id) {
        try {
            replyService.deleteReply(id, null);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "回复删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "回复删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteRepliesBatch(@RequestBody List<Integer> ids) {
        try {
            int deletedCount = 0;
            for (Integer id : ids) {
                replyService.deleteReply(id, null);
                deletedCount++;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量删除成功，共删除 " + deletedCount + " 条回复");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
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
        result.put("pageSize", replies.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}