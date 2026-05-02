import request from '@/utils/request'

export function getCourses(params) {
  return request.get('/courses', { params })
}

export function getCourseDetail(courseId) {
  return request.get(`/courses/${courseId}`)
}

export function enrollCourse(courseId) {
  return request.post(`/courses/enroll/${courseId}`)
}

export function toggleFavorite(courseId) {
  return request.post(`/courses/favorite/${courseId}`)
}

export function getChapterDetail(chapterId) {
  return request.get(`/courses/chapters/${chapterId}`)
}

export function updateProgress(data) {
  return request.post('/courses/progress', data)
}

export function getCourseComments(courseId, params) {
  return request.get(`/courses/${courseId}/comments`, { params })
}

export function createComment(data) {
  return request.post('/courses/comments', data)
}

export function getMyEnrollments(params) {
  return request.get('/users/enrollments', { params })
}

export function getMyFavorites(params) {
  return request.get('/users/favorites', { params })
}
