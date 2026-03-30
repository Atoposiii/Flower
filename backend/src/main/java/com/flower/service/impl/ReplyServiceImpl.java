package com.flower.service.impl;

import com.flower.entity.Comment;
import com.flower.entity.CommentReply;
import com.flower.entity.ReplyLike;
import com.flower.entity.User;
import com.flower.repository.CommentReplyRepository;
import com.flower.repository.CommentRepository;
import com.flower.repository.ReplyLikeRepository;
import com.flower.repository.UserRepository;
import com.flower.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private CommentReplyRepository replyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyLikeRepository replyLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CommentReply createReply(Integer commentId, Integer userId, String content) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        CommentReply reply = new CommentReply();
        reply.setComment(commentOpt.get());
        reply.setUser(user);
        reply.setContent(content);
        CommentReply savedReply = replyRepository.save(reply);

        commentRepository.incrementReplyCount(commentId);

        return savedReply;
    }

    @Override
    public CommentReply getReply(Integer replyId) {
        return replyRepository.findById(replyId).orElse(null);
    }

    @Override
    public Page<CommentReply> getCommentReplies(Integer commentId, Pageable pageable) {
        return replyRepository.findByCommentIdAndStatusOrderByCreateTimeAsc(commentId, "published", pageable);
    }

    @Override
    @Transactional
    public boolean deleteReply(Integer replyId, Integer userId) {
        Optional<CommentReply> replyOpt = replyRepository.findById(replyId);
        if (!replyOpt.isPresent()) {
            return false;
        }

        CommentReply reply = replyOpt.get();
        if (!reply.getUser().getId().equals(userId)) {
            return false;
        }

        reply.setStatus("deleted");
        replyRepository.save(reply);
        commentRepository.decrementReplyCount(reply.getComment().getId());

        return true;
    }

    @Override
    @Transactional
    public Map<String, Object> likeReply(Integer replyId, Integer userId) {
        Optional<CommentReply> replyOpt = replyRepository.findById(replyId);
        if (!replyOpt.isPresent()) {
            return null;
        }

        if (replyLikeRepository.existsByReplyIdAndUserId(replyId, userId)) {
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        ReplyLike like = new ReplyLike();
        like.setReply(replyOpt.get());
        like.setUser(user);
        replyLikeRepository.save(like);

        replyRepository.incrementLikeCount(replyId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        result.put("likeCount", replyOpt.get().getLikeCount() + 1);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> unlikeReply(Integer replyId, Integer userId) {
        Optional<CommentReply> replyOpt = replyRepository.findById(replyId);
        if (!replyOpt.isPresent()) {
            return null;
        }

        Optional<ReplyLike> likeOpt = replyLikeRepository.findByReplyIdAndUserId(replyId, userId);
        if (!likeOpt.isPresent()) {
            return null;
        }

        replyLikeRepository.delete(likeOpt.get());
        replyRepository.decrementLikeCount(replyId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        result.put("likeCount", Math.max(0, replyOpt.get().getLikeCount() - 1));
        return result;
    }

    @Override
    public boolean hasUserLikedReply(Integer replyId, Integer userId) {
        return replyLikeRepository.existsByReplyIdAndUserId(replyId, userId);
    }
}