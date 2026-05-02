import request from '@/utils/request'

export function getTeacherCourses(params) {
  return request.get('/teacher/courses', { params })
}

export function getTeacherCourseDetail(courseId) {
  return request.get(`/teacher/courses/${courseId}`)
}

export function createCourse(data) {
  return request.post('/teacher/courses', data)
}

export function updateCourse(courseId, data) {
  return request.put(`/teacher/courses/${courseId}`, data)
}

export function deleteCourse(courseId) {
  return request.delete(`/teacher/courses/${courseId}`)
}

export function togglePublish(courseId) {
  return request.post(`/teacher/courses/${courseId}/publish`)
}

export function getCourseChapters(courseId) {
  return request.get(`/teacher/courses/${courseId}/chapters`)
}

export function createChapter(courseId, data) {
  return request.post(`/teacher/courses/${courseId}/chapters`, data)
}

export function updateChapter(chapterId, data) {
  return request.put(`/teacher/chapters/${chapterId}`, data)
}

export function deleteChapter(chapterId) {
  return request.delete(`/teacher/chapters/${chapterId}`)
}

export function getCourseStudents(courseId, params) {
  return request.get(`/teacher/courses/${courseId}/students`, { params })
}
