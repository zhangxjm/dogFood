import request from '@/utils/request'

export function getAttendancePage(params) {
  return request({
    url: '/attendance/page',
    method: 'get',
    params
  })
}

export function getTodayRecord(empId) {
  return request({
    url: `/attendance/today/${empId}`,
    method: 'get'
  })
}

export function clockIn(data) {
  return request({
    url: '/attendance/clockIn',
    method: 'post',
    data
  })
}

export function clockOut(data) {
  return request({
    url: '/attendance/clockOut',
    method: 'post',
    data
  })
}

export function getAttendanceById(id) {
  return request({
    url: `/attendance/${id}`,
    method: 'get'
  })
}

export function deleteAttendance(id) {
  return request({
    url: `/attendance/${id}`,
    method: 'delete'
  })
}

export function exportAttendance(params) {
  window.location.href = `/api/attendance/export?${new URLSearchParams(params).toString()}`
}
