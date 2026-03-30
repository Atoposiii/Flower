import api from './request'

export const userAPI = {
  login(username, password) {
    return api.post('/users/login', { username, password })
  },
  logout() {
    return api.post('/users/logout')
  },
  register(user) {
    return api.post('/users/register', user)
  },
  getUserInfo(id) {
    return api.get(`/users/${id}`)
  },
  updateUser(id, user) {
    return api.put(`/users/${id}`, user)
  },
  // 个人信息：获取
  getUserProfile(id) {
    return api.get(`/users/profile/${id}`)
  },
  // 个人信息：更新 nickname/gender/bio（支持 409 网名重复）
  updateProfile(id, { nickname, gender, bio }) {
    return api.put(`/users/profile/${id}`, null, { params: { nickname, gender, bio } })
  },
  // 账户管理：修改邮箱
  updateEmail(id, email) {
    return api.put(`/users/account/${id}/email`, null, { params: { email } })
  },
  // 账户管理：修改密码（需旧密码验证）
  updatePasswordWithOld(id, oldPassword, newPassword) {
    return api.put(`/users/account/${id}/password`, null, { params: { oldPassword, newPassword } })
  }
}
