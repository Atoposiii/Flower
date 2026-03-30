import api from './request'

const adminAPI = {
  flower: {
    getFlowers(params) {
      return api.get('/admin/flower/list', { params })
    },
    getFlower(id) {
      return api.get(`/admin/flower/${id}`)
    },
    createFlower(flower) {
      return api.post('/admin/flower', flower)
    },
    updateFlower(id, flower) {
      return api.put(`/admin/flower/${id}`, flower)
    },
    deleteFlower(id) {
      return api.delete(`/admin/flower/${id}`)
    }
  },
  post: {
    getPosts(params) {
      return api.get('/admin/post/list', { params })
    },
    getPost(id) {
      return api.get(`/admin/post/${id}`)
    },
    updatePost(id, post) {
      return api.put(`/admin/post/${id}`, null, { params: post })
    },
    deletePost(id) {
      return api.delete(`/admin/post/${id}`)
    },
    getStatistics() {
      return api.get('/admin/post/statistics')
    }
  },
  comment: {
    getComments(params) {
      return api.get('/admin/comment/list', { params })
    },
    deleteComment(id) {
      return api.delete(`/admin/comment/${id}`)
    }
  },
  reply: {
    getReplies(params) {
      return api.get('/admin/reply/list', { params })
    },
    getCommentReplies(commentId) {
      return api.get(`/admin/reply/comment/${commentId}`)
    },
    deleteReply(id) {
      return api.delete(`/admin/reply/${id}`)
    }
  },
  volunteer: {
    getVolunteers(params) {
      return api.get('/admin/volunteer/list', { params })
    },
    approveVolunteer(id, approve) {
      return api.put(`/admin/volunteer/approve/${id}`, null, { params: { approve } })
    },
    approveVolunteerCheck(id) {
      return api.put(`/admin/volunteer/${id}/approve-check`)
    },
    rejectVolunteer(id) {
      return api.put(`/admin/volunteer/${id}/reject`)
    },
    getVolunteerActivityRecords(id) {
      return api.get(`/admin/volunteer/${id}/activities`)
    },
    addVolunteerActivityRecord(id, record) {
      return api.post(`/admin/volunteer/${id}/activities`, record)
    },
    deleteVolunteer(id) {
      return api.delete(`/admin/volunteer/${id}`)
    },
    getActivities(params) {
      return api.get('/admin/activity/list', { params })
    },
    createActivity(activity) {
      return api.post('/admin/activity', activity)
    },
    updateActivity(id, activity) {
      return api.put(`/admin/activity/${id}`, activity)
    },
    deleteActivity(id) {
      return api.delete(`/admin/activity/${id}`)
    },
    getSignIns(params) {
      return api.get('/admin/signin/list', { params })
    },
    getSignInRecords(activityId) {
      return api.get(`/admin/signin/activity/${activityId}`)
    },
    grantHours(activityId) {
      return api.post(`/admin/activity/${activityId}/grant-hours`)
    },
    getRanking() {
      return api.get('/admin/volunteer/ranking')
    },
    updateVolunteerHours(id, hours) {
      return api.put(`/admin/volunteer/${id}/hours`, null, { params: { hours } })
    }
  },
  member: {
    getMembers(params) {
      return api.get('/admin/member/list', { params })
    },
    getMember(id) {
      return api.get(`/admin/member/${id}`)
    },
    approveMember(id) {
      return api.put(`/admin/member/${id}/approve`)
    },
    rejectMember(id) {
      return api.put(`/admin/member/${id}/reject`)
    },
    suspendMember(id) {
      return api.put(`/admin/member/${id}/suspend`)
    },
    forceCancelMember(id, reason) {
      return api.put(`/admin/member/${id}/force-cancel`, null, { params: { reason } })
    },
    getStatistics() {
      return api.get('/admin/member/statistics')
    },
    getExpiredMembers() {
      return api.get('/admin/member/expired')
    }
  },
  user: {
    getUsers(params) {
      return api.get('/admin/user/list', { params })
    },
    getUser(id) {
      return api.get(`/admin/user/${id}`)
    },
    banUser(id, reason) {
      return api.put(`/admin/user/${id}/ban`, null, { params: { reason } })
    },
    unbanUser(id) {
      return api.put(`/admin/user/${id}/unban`)
    },
    forceQuitVolunteer(id, reason) {
      return api.put(`/admin/user/${id}/force-quit-volunteer`, null, { params: { reason } })
    },
    forceCancelMembership(id, reason) {
      return api.put(`/admin/user/${id}/force-cancel-membership`, null, { params: { reason } })
    },
    getStatistics() {
      return api.get('/admin/user/statistics')
    }
  },
  consultation: {
    getSessions(params) {
      return api.get('/admin/consultation/sessions', { params })
    },
    getSession(id) {
      return api.get(`/admin/consultation/session/${id}`)
    },
    getSessionMessages(id) {
      return api.get(`/admin/consultation/session/${id}/messages`)
    },
    reply(sessionId, adminId, content) {
      return api.post(`/admin/consultation/session/${sessionId}/reply`, null, { params: { adminId, content } })
    },
    closeSession(id) {
      return api.put(`/admin/consultation/session/${id}/close`)
    },
    getRefundRequests(params) {
      return api.get('/admin/consultation/member-refund-requests', { params })
    },
    approveRefundRequest(requestId) {
      return api.put(`/admin/consultation/member-refund-request/${requestId}/approve`)
    },
    rejectRefundRequest(requestId, reason) {
      return api.put(`/admin/consultation/member-refund-request/${requestId}/reject`, null, { params: { reason } })
    }
  },
  feedback: {
    getFeedbacks(params) {
      return api.get('/admin/feedback/list', { params })
    },
    getFeedback(id) {
      return api.get(`/admin/feedback/${id}`)
    },
    updateStatus(id, status) {
      return api.put(`/admin/feedback/${id}/status`, null, { params: { status } })
    },
    reply(id, adminReply) {
      return api.put(`/admin/feedback/${id}/reply`, null, { params: { adminReply } })
    },
    processFeedback(id, adminReply, status) {
      return api.put(`/admin/feedback/${id}/process`, null, { params: { adminReply, status } })
    }
  },
  payment: {
    getPayments(params) {
      return api.get('/admin/payment/list', { params })
    },
    getPayment(id) {
      return api.get(`/admin/payment/${id}`)
    },
    getPaymentsByMethod(method, params) {
      return api.get(`/admin/payment/method/${method}`, { params })
    },
    getStatistics() {
      return api.get('/admin/payment/statistics')
    }
  }
}

export default adminAPI
