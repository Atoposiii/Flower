package com.flower.service.impl;

import com.flower.entity.Comment;
import com.flower.entity.CommentLike;
import com.flower.entity.CommentReply;
import com.flower.entity.Post;
import com.flower.entity.User;
import com.flower.repository.CommentLikeRepository;
import com.flower.repository.CommentRepository;
import com.flower.repository.CommentReplyRepository;
import com.flower.repository.PostRepository;
import com.flower.repository.ReplyLikeRepository;
import com.flower.repository.UserRepository;
import com.flower.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentReplyRepository replyRepository;

    @Autowired
    private ReplyLikeRepository replyLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Comment createComment(Integer postId, Integer userId, String content) {
        log.info("[createComment] 开始创建评论，postId={}, userId={}", postId, userId);
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            log.warn("[createComment] 帖子不存在，postId={}", postId);
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("[createComment] 用户不存在，userId={}", userId);
            return null;
        }

        Comment comment = new Comment();
        comment.setPost(postOpt.get());
        comment.setUser(user);
        comment.setContent(content);
        try {
            Comment savedComment = commentRepository.save(comment);
            postRepository.incrementCommentCount(postId);
            log.info("[createComment] 评论保存成功，commentId={}", savedComment.getId());
            return savedComment;
        } catch (Exception e) {
            log.error("[createComment] 保存评论时发生异常，postId={}, userId={}", postId, userId, e);
            throw e;
        }
    }

    @Override
    public Comment getComment(Integer commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    @Override
    public Page<Comment> getPostComments(Integer postId, Pageable pageable) {
        return commentRepository.findByPostIdAndStatusOrderByCreateTimeAsc(postId, "published", pageable);
    }

    @Override
    public Page<Comment> getUserComments(Integer userId, Pageable pageable) {
        return commentRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, "published", pageable);
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer commentId, Integer userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return false;
        }

        Comment comment = commentOpt.get();
        if (!comment.getUser().getId().equals(userId)) {
            return false;
        }

        comment.setStatus("deleted");
        commentRepository.save(comment);
        postRepository.decrementCommentCount(comment.getPost().getId());

        return true;
    }

    @Override
    @Transactional
    public Map<String, Object> likeComment(Integer commentId, Integer userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return null;
        }

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        CommentLike like = new CommentLike();
        like.setComment(commentOpt.get());
        like.setUser(user);
        commentLikeRepository.save(like);

        commentRepository.incrementLikeCount(commentId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        result.put("likeCount", commentOpt.get().getLikeCount() + 1);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> unlikeComment(Integer commentId, Integer userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return null;
        }

        Optional<CommentLike> likeOpt = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (!likeOpt.isPresent()) {
            return null;
        }

        commentLikeRepository.delete(likeOpt.get());
        commentRepository.decrementLikeCount(commentId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        result.put("likeCount", Math.max(0, commentOpt.get().getLikeCount() - 1));
        return result;
    }

    @Override
    public boolean hasUserLikedComment(Integer commentId, Integer userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    @Override
    public List<Map<String, Object>> getPostCommentsWithReplies(Integer postId, Integer currentUserId) {
        Pageable all = PageRequest.of(0, 1000);
        Page<Comment> commentPage = commentRepository.findByPostIdAndStatusOrderByCreateTimeAsc(postId, "published", all);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment comment : commentPage.getContent()) {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("content", comment.getContent());
            commentMap.put("likeCount", comment.getLikeCount());
            commentMap.put("replyCount", comment.getReplyCount());
            commentMap.put("createTime", comment.getCreateTime());
            commentMap.put("status", comment.getStatus());
            commentMap.put("userId", comment.getUser().getId());
            commentMap.put("nickname", comment.getUser().getNickname());
            commentMap.put("liked", currentUserId != null && commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), currentUserId));

            List<CommentReply> rawReplies = replyRepository.findByCommentIdAndStatusOrderByCreateTimeAsc(comment.getId(), "published");
            List<Map<String, Object>> replies = new ArrayList<>();
            for (CommentReply reply : rawReplies) {
                Map<String, Object> replyMap = new HashMap<>();
                replyMap.put("id", reply.getId());
                replyMap.put("content", reply.getContent());
                replyMap.put("likeCount", reply.getLikeCount());
                replyMap.put("createTime", reply.getCreateTime());
                replyMap.put("status", reply.getStatus());
                replyMap.put("userId", reply.getUser().getId());
                replyMap.put("nickname", reply.getUser().getNickname());
                replyMap.put("liked", currentUserId != null && replyLikeRepository.existsByReplyIdAndUserId(reply.getId(), currentUserId));
                replies.add(replyMap);
            }
            commentMap.put("replies", replies);
            result.add(commentMap);
        }
        return result;
    }
}