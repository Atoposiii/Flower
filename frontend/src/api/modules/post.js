import request from '../index'

export default {
  getAllPosts() {
    return request({
      url: '/posts',
      method: 'get'
    })
  },
  
  getPostById(id) {
    return request({
      url: `/posts/${id}`,
      method: 'get'
    })
  },
  
  createPost(data) {
    return request({
      url: '/posts',
      method: 'post',
      data
    })
  },
  
  updatePost(id, data) {
    return request({
      url: `/posts/${id}`,
      method: 'put',
      data
    })
  },
  
  deletePost(id) {
    return request({
      url: `/posts/${id}`,
      method: 'delete'
    })
  }
}
