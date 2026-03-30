import request from '../index'

export default {
  getAllActivities() {
    return request({
      url: '/volunteer-activities',
      method: 'get'
    })
  },

  getActivityById(id) {
    return request({
      url: `/volunteer-activities/${id}`,
      method: 'get'
    })
  },

  createActivity(data) {
    return request({
      url: '/volunteer-activities',
      method: 'post',
      data
    })
  },

  updateActivity(id, data) {
    return request({
      url: `/volunteer-activities/${id}`,
      method: 'put',
      data
    })
  },

  deleteActivity(id) {
    return request({
      url: `/volunteer-activities/${id}`,
      method: 'delete'
    })
  }
}