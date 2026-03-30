import api from './request'

export const volunteerAPI = {
  getVolunteerInfo(userId) {
    return api.get(`/volunteers/user/${userId}`)
  },
  becomeVolunteer(userId) {
    return api.post(`/volunteers/become/${userId}`)
  },
  quitVolunteer(userId) {
    return api.post(`/volunteers/quit/${userId}`)
  },
  getActivities(params) {
    return api.get('/volunteer-activities', { params })
  },
  getAvailableActivities() {
    return api.get('/volunteer-activities/active')
  },
  getMemberOnlyActivities() {
    return api.get('/volunteer-activities', { params: { isMemberOnly: true } })
  },
  registerActivity(activityId, userId) {
    return api.post(`/activities/${activityId}/register/${userId}`)
  },
  cancelRegistration(userId, activityId) {
    return api.post(`/activities/${activityId}/cancel/${userId}`)
  },
  signInActivity(userId, activityId) {
    return api.post(`/activities/${activityId}/signin/${userId}`)
  },
  getRanking() {
    return api.get('/volunteers/rankings')
  },
  checkUserStatus(userId) {
    return api.get(`/volunteers/check/${userId}`)
  },
  getMyRegistrations(userId) {
    return api.get('/volunteers/my-registrations', { params: { userId } })
  }
}
