import request from '@/utils/request'

export const userApi = {
  login(username, password) {
    return request({
      url: '/user/login/',
      method: 'post',
      data: { username, password }
    })
  },
  
  register(data) {
    return request({
      url: '/user/register/',
      method: 'post',
      data
    })
  },
  
  getUserInfo() {
    return request({
      url: '/user/info/',
      method: 'get'
    })
  },
  
  updateInfo(data) {
    return request({
      url: '/user/info/',
      method: 'put',
      data
    })
  },
  
  getUserList(params) {
    return request({
      url: '/user/list/',
      method: 'get',
      params
    })
  },
  
  toggleActive(id) {
    return request({
      url: `/user/list/${id}/toggle_active/`,
      method: 'post'
    })
  },
  
  setRole(id, role) {
    return request({
      url: `/user/list/${id}/set_role/`,
      method: 'post',
      data: { role }
    })
  }
}
