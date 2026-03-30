import api from './request'

export const postAPI = {
  getPosts(params) {
    return api.get('/post/all', { params })
  },
  getPostDetail(id, userId) {
    return api.get(`/post/${id}`, { params: { userId } })
  },
  createPost(userId, title, content, coverImage) {
    return api.post('/post', { userId, title, content, coverImage })
  },
  updatePost(id, userId, title, content, coverImage) {
    return api.put(`/post/${id}`, { userId, title, content, coverImage })
  },
  deletePost(id, userId) {
    return api.delete(`/post/${id}`, { params: { userId } })
  },
  likePost(id, userId) {
    return api.post(`/post/${id}/like`, null, { params: { userId } })
  },
  unlikePost(id, userId) {
    return api.delete(`/post/${id}/like`, { params: { userId } })
  }
}

export const commentAPI = {
  getCommentsTree(postId, userId) {
    return api.get(`/comment/post/${postId}/tree`, { params: { userId } })
  },
  getComments(postId, params) {
    return api.get(`/comment/post/${postId}`, { params })
  },
  createComment(postId, userId, content) {
    return api.post('/comment', { postId, userId, content })
  },
  deleteComment(id, userId) {
    return api.delete(`/comment/${id}`, { params: { userId } })
  },
  likeComment(id, userId) {
    return api.post(`/comment/${id}/like`, null, { params: { userId } })
  },
  unlikeComment(id, userId) {
    return api.delete(`/comment/${id}/like`, { params: { userId } })
  }
}

export const replyAPI = {
  getReplies(commentId, params) {
    return api.get(`/reply/comment/${commentId}`, { params })
  },
  createReply(commentId, userId, content) {
    return api.post('/reply', { commentId, userId, content })
  },
  deleteReply(id, userId) {
    return api.delete(`/reply/${id}`, { params: { userId } })
  },
  likeReply(id, userId) {
    return api.post(`/reply/${id}/like`, null, { params: { userId } })
  },
  unlikeReply(id, userId) {
    return api.delete(`/reply/${id}/like`, { params: { userId } })
  }
}
