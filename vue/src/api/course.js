import request from '@/utils/request'

// 分页查询课程
export function getCoursePage(params) {
  return request({
    url: '/course/page',
    method: 'get',
    params
  })
}

// 获取课程列表
export function getCourseList() {
  return request({
    url: '/course/list',
    method: 'get'
  })
}

// 获取当前教师的课程列表
export function getTeacherCourses() {
  return request({
    url: '/course/teacher',
    method: 'get'
  })
}

// 根据ID获取课程
export function getCourseById(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

// 新增课程
export function addCourse(data) {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

// 更新课程
export function updateCourse(id, data) {
  return request({
    url: `/course/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export function deleteCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}

// 教师发送课程通知
export function sendCourseNotification(data) {
  return request({
    url: '/course/notify',
    method: 'post',
    data
  })
}
