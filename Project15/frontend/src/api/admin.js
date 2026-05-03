import request from '@/utils/request'

export const adminApi = {
  statistics() {
    return request.get('/admin/statistics')
  },

  userList(params) {
    return request.get('/admin/user/list', { params })
  },

  companyList(params) {
    return request.get('/admin/company/list', { params })
  },

  jobList(params) {
    return request.get('/admin/job/list', { params })
  }
}
