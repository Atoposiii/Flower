import api from './request'

export const memberAPI = {
  getMemberInfo(userId) {
    return api.get(`/members/info/${userId}`)
  },
  isMember(userId) {
    return api.get(`/members/status/${userId}`)
  },
  getMemberRecord(userId) {
    return api.get(`/members/record/${userId}`)
  },
  createMember(userId) {
    return api.post(`/members/create/${userId}`)
  },
  renewMember(userId, paymentMethod) {
    return api.post(`/members/renew/${userId}`, null, { params: { paymentMethod } })
  },
  cancelMember(userId, reason) {
    return api.post(`/members/cancel/${userId}`, null, { params: { reason } })
  },
  refundMember(userId, reason) {
    return api.post(`/members/refund/${userId}`, null, { params: { reason } })
  },
  // 创建支付订单
  createOrder(userId, paymentMethod) {
    return api.post(`/members/order/${userId}`, null, { params: { paymentMethod } })
  },
  // 查询订单状态
  getOrder(orderId) {
    return api.get(`/members/orders/${orderId}`)
  },
  // 订单退款（2分钟内）
  refundOrder(orderId) {
    return api.post(`/members/orders/${orderId}/refund`)
  },
  // 获取退款资格
  checkRefundEligibility(userId) {
    return api.get(`/members/refund-eligibility/${userId}`)
  },
  // 获取会员权益列表
  getBenefits() {
    return api.get('/members/benefits')
  },
  // 支付回调（模拟）
  paymentCallback(orderNumber, transactionId) {
    return api.post('/members/callback', null, { params: { orderNumber, transactionId } })
  }
}

export const consultationAPI = {
  createSession(userId, channel, title) {
    return api.post('/consultation/session', null, { params: { userId, channel, title } })
  },
  createFlowerSession(userId, channel, flowerId, title) {
    return api.post('/consultation/flower-session', null, { params: { userId, channel, flowerId, title } })
  },
  getSession(id) {
    return api.get(`/consultation/session/${id}`)
  },
  getUserSessions(userId, params) {
    return api.get(`/consultation/user/${userId}`, { params })
  },
  getUserFlowerSessions(flowerId, userId, channel) {
    return api.get(`/consultation/flower/${flowerId}/user/${userId}`, { params: { channel } })
  },
  sendMessage(sessionId, senderId, senderType, content) {
    return api.post('/consultation/message', null, { params: { sessionId, senderId, senderType, content } })
  },
  getMessages(sessionId) {
    return api.get(`/consultation/session/${sessionId}/messages`)
  }
}

export const feedbackAPI = {
  createFeedback(userId, feedbackType, targetType, targetId, targetName, content) {
    return api.post('/feedback', null, { params: { userId, feedbackType, targetType, targetId, targetName, content } })
  },
  getFeedback(id) {
    return api.get(`/feedback/${id}`)
  },
  getUserFeedbacks(userId, params) {
    return api.get(`/feedback/user/${userId}`, { params })
  },
  getUserFlowerFeedbacks(userId, flowerId) {
    return api.get(`/feedback/user/${userId}`, { params: { targetId: flowerId, targetType: 'flower' } })
  }
}

export const featureAPI = {
  getFeatures() {
    return api.get('/features')
  }
}
