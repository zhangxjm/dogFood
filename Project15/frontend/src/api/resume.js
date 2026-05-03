import request from '@/utils/request'

export const resumeApi = {
  getMyResume() {
    return request.get('/resume/my')
  },

  getById(id) {
    return request.get(`/resume/detail/${id}`)
  },

  save(data) {
    return request.post('/resume/save', data)
  },

  match(jobId) {
    return request.get(`/resume/match/${jobId}`)
  }
}
