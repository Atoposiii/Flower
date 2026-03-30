import request from '../index'

export default {
  getAllFlowers() {
    return request({
      url: '/flowers',
      method: 'get'
    })
  },
  
  getFlowerById(id) {
    return request({
      url: `/flowers/${id}`,
      method: 'get'
    })
  },
  
  addFlower(data) {
    return request({
      url: '/flowers',
      method: 'post',
      data
    })
  },
  
  updateFlower(id, data) {
    return request({
      url: `/flowers/${id}`,
      method: 'put',
      data
    })
  },
  
  deleteFlower(id) {
    return request({
      url: `/flowers/${id}`,
      method: 'delete'
    })
  }
}
