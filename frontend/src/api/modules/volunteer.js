import request from '../index'

export default {
  getAllVolunteers() {
    return request({
      url: '/volunteers',
      method: 'get'
    })
  },

  getVolunteerById(id) {
    return request({
      url: `/volunteers/${id}`,
      method: 'get'
    })
  },

  getVolunteerByUserId(userId) {
    return request({
      url: `/volunteers/user/${userId}`,
      method: 'get'
    })
  },

  applyVolunteer(data) {
    return request({
      url: `/volunteers/apply/${data.userId}`,
      method: 'post',
      data
    })
  },

  approveVolunteer(id, approve) {
    return request({
      url: `/volunteers/approve/${id}`,
      method: 'put',
      params: { approve }
    })
  },

  updateServiceHours(id, hours) {
    return request({
      url: `/volunteers/${id}/hours`,
      method: 'put',
      params: { hours }
    })
  },

  getPendingVolunteers() {
    return request({
      url: '/volunteers/pending',
      method: 'get'
    })
  },

  getApprovedVolunteers() {
    return request({
      url: '/volunteers/approved',
      method: 'get'
    })
  },

  getVolunteersByServiceHours() {
    return request({
      url: '/volunteers/ranking',
      method: 'get'
    })
  },

  quitVolunteer(id) {
    return request({
      url: `/volunteers/${id}/quit`,
      method: 'put'
    })
  },

  deleteVolunteer(id) {
    return request({
      url: `/volunteers/${id}`,
      method: 'delete'
    })
  }
}
