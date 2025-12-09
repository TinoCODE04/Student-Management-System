import request from '@/utils/request'

// 获取专业列表
export function getMajorList() {
  return request({
    url: '/majors/list',
    method: 'get'
  })
}

// 根据学院ID获取专业列表
export function getMajorsByCollege(collegeId) {
  return request({
    url: `/majors/college/${collegeId}`,
    method: 'get'
  })
}

// 根据ID获取专业
export function getMajorById(id) {
  return request({
    url: `/majors/${id}`,
    method: 'get'
  })
}

// 新增专业
export function addMajor(data) {
  return request({
    url: '/majors',
    method: 'post',
    data
  })
}

// 更新专业
export function updateMajor(id, data) {
  return request({
    url: `/majors/${id}`,
    method: 'put',
    data
  })
}

// 删除专业
export function deleteMajor(id) {
  return request({
    url: `/majors/${id}`,
    method: 'delete'
  })
}
