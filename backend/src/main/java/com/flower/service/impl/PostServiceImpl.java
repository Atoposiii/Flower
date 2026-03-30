package com.flower.service.impl;

import com.flower.entity.Post;
import com.flower.entity.PostLike;
import com.flower.entity.User;
import com.flower.repository.PostLikeRepository;
import com.flower.repository.PostRepository;
import com.flower.repository.UserRepository;
import com.flower.service.PostService;
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
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Post createPost(Integer userId, String title, String content, String coverImage) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        Post post = new Post();
        post.setUser(user);
        post.setTitle(title);
        post.setContent(content);
        post.setCoverImage(coverImage);
        return postRepository.save(post);
    }

    @Override
    public Post getPost(Integer postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findByStatusOrderByCreateTimeDesc("published", pageable);
    }

    @Override
    public Page<Post> getUserPosts(Integer userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }

    @Override
    @Transactional
    public Post updatePost(Integer postId, Integer userId, String title, String content, String coverImage) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            return null;
        }

        Post post = postOpt.get();
        if (!post.getUser().getId().equals(userId)) {
            return null;
        }

        if (title != null) post.setTitle(title);
        if (content != null) post.setContent(content);
        if (coverImage != null) post.setCoverImage(coverImage);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public boolean deletePost(Integer postId, Integer userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            return false;
        }

        Post post = postOpt.get();
        if (!post.getUser().getId().equals(userId)) {
            return false;
        }

        post.setStatus("deleted");
        postRepository.save(post);
        return true;
    }

    @Override
    @Transactional
    public Map<String, Object> likePost(Integer postId, Integer userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            return null;
        }

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        PostLike like = new PostLike();
        like.setPost(postOpt.get());
        like.setUser(user);
        postLikeRepository.save(like);

        postRepository.incrementLikeCount(postId);
        int newLikeCount = postRepository.findById(postId).map(Post::getLikeCount).orElse(postOpt.get().getLikeCount() + 1);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        result.put("likeCount", newLikeCount);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> unlikePost(Integer postId, Integer userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            return null;
        }

        Optional<PostLike> likeOpt = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (!likeOpt.isPresent()) {
            return null;
        }

        postLikeRepository.delete(likeOpt.get());
        postRepository.decrementLikeCount(postId);
        int newLikeCount = postRepository.findById(postId).map(Post::getLikeCount).orElse(Math.max(0, postOpt.get().getLikeCount() - 1));

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        result.put("likeCount", newLikeCount);
        return result;
    }

    @Override
    public boolean hasUserLikedPost(Integer postId, Integer userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Override
    @Transactional
    public void incrementViewCount(Integer postId) {
        postRepository.incrementViewCount(postId);
    }

    @Override
    public Map<String, Object> getPostDetail(Integer postId, Integer userId) {
        Post post = getPost(postId);
        if (post == null) {
            log.warn("[getPostDetail] 帖子不存在，postId={}", postId);
            return null;
        }
        log.info("[getPostDetail] postId={}, status='{}'", postId, post.getStatus());
        if (!"published".equals(post.getStatus())) {
            log.warn("[getPostDetail] 帖子状态非published，postId={}, status='{}'", postId, post.getStatus());
            return null;
        }

        incrementViewCount(postId);
        // 重新查询，获取 @Modifying 更新后的最新计数
        post = postRepository.findById(postId).orElse(post);

        Map<String, Object> result = new HashMap<>();
        result.put("id", post.getId());
        result.put("title", post.getTitle());
        result.put("content", post.getContent());
        result.put("coverImage", post.getCoverImage());
        result.put("viewCount", post.getViewCount());
        result.put("likeCount", post.getLikeCount());
        result.put("commentCount", post.getCommentCount());
        result.put("createTime", post.getCreateTime());
        result.put("updateTime", post.getUpdateTime());
        result.put("userId", post.getUser().getId());
        result.put("nickname", post.getUser().getNickname());
        result.put("liked", userId != null && hasUserLikedPost(postId, userId));

        return result;
    }

    @Override
    public Map<String, Object> getPostsWithLikedStatus(int page, int size, Integer currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByStatusOrderByCreateTimeDesc("published", pageable);

        List<Map<String, Object>> content = new ArrayList<>();
        for (Post post : postPage.getContent()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", post.getId());
            item.put("title", post.getTitle());
            item.put("content", post.getContent());
            item.put("coverImage", post.getCoverImage());
            item.put("viewCount", post.getViewCount());
            item.put("likeCount", post.getLikeCount());
            item.put("commentCount", post.getCommentCount());
            item.put("createTime", post.getCreateTime());
            item.put("userId", post.getUser().getId());
            item.put("nickname", post.getUser().getNickname());
            item.put("liked", currentUserId != null && postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId));
            content.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        result.put("currentPage", postPage.getNumber());
        return result;
    }
}