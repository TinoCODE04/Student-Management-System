import request from '@/utils/request'

// 获取教师列表
export function getTeacherList() {
  return request({
    url: '/teacher/list',
    method: 'get'
  })
}

// 根据ID获取教师
export function getTeacherById(id) {
  return request({
    url: `/teacher/${id}`,
    method: 'get'
  })
}

// 修改密码（教师）
export function updateTeacherPassword(data) {
  return request({
    url: '/teacher/password',
    method: 'put',
    data
  })
}

// 获取当前教师信息
export function getCurrentTeacherInfo() {
  return request({
    url: '/teacher/info',
    method: 'get'
  })
}

// 更新教师个人信息
export function updateTeacherInfo(data) {
  return request({
    url: '/teacher/info',
    method: 'put',
    data
  })
}
