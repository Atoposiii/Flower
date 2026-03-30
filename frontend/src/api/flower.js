import api from './request'

export const flowerAPI = {
  getFlowers(params) {
    return api.get('/flower', { params })
  },
  getFlowerDetail(id, userId) {
    return api.get(`/flower/detail/${id}`, { params: { userId } })
  },
  getFlowerDetailForMember(id, userId) {
    return api.get(`/flower/detail/${id}/member/${userId}`)
  }
}
