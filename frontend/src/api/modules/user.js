import request from '../index'

// 用户相关API
export default {
  // 登录
  login(data) {
    return request({
      url: '/users/login',
      method: 'post',
      data
    })
  },
  // 注册
  register(data) {
    return request({
      url: '/users/register',
      method: 'post',
      data
    })
  },
  // 获取所有用户
  getAllUsers() {
    return request({
      url: '/users',
      method: 'get'
    })
  },
  // 根据ID获取用户
  getUserById(id) {
    return request({
      url: `/users/${id}`,
      method: 'get'
    })
  },
  // 创建用户
  createUser(data) {
    return request({
      url: '/users',
      method: 'post',
      data
    })
  },
  // 更新用户信息
  updateUser(id, data) {
    return request({
      url: `/users/${id}`,
      method: 'put',
      data
    })
  },
  // 更新用户密码
  updatePassword(id, newPassword) {
    return request({
      url: `/users/${id}/password`,
      method: 'put',
      data: newPassword
    })
  },
  // 删除用户
  deleteUser(id) {
    return request({
      url: `/users/${id}`,
      method: 'delete'
    })
  },
  // 登出
  logout() {
    return request({
      url: '/auth/logout',
      method: 'post'
    })
  },
  // 升级会员
  upgradeToMember(userId, months) {
    return request({
      url: `/users/${userId}/member`,
      method: 'put',
      params: { months }
    })
  }
}