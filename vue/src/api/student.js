import request from '@/utils/request'

// 分页查询学生
export function getStudentPage(params) {
  return request({
    url: '/students/page',
    method: 'get',
    params
  })
}

// 获取学生列表
export function getStudentList() {
  return request({
    url: '/students/list',
    method: 'get'
  })
}

// 根据ID获取学生
export function getStudentById(id) {
  return request({
    url: `/students/${id}`,
    method: 'get'
  })
}

// 新增学生
export function addStudent(data) {
  return request({
    url: '/students',
    method: 'post',
    data
  })
}

// 更新学生
export function updateStudent(id, data) {
  return request({
    url: `/students/${id}`,
    method: 'put',
    data
  })
}

// 删除学生
export function deleteStudent(id) {
  return request({
    url: `/students/${id}`,
    method: 'delete'
  })
}

// 修改密码（学生）
export function updateStudentPassword(data) {
  return request({
    url: '/students/password',
    method: 'put',
    data
  })
}

// 获取当前学生信息
export function getCurrentStudentInfo() {
  return request({
    url: '/students/info',
    method: 'get'
  })
}

// 别名：获取当前学生信息
export const getMyInfo = getCurrentStudentInfo
