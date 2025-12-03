import request from '@/utils/request'

// 获取专业列表
export function getMajorList() {
  return request({
    url: '/major/list',
    method: 'get'
  })
}

// 根据学院ID获取专业列表
export function getMajorsByCollege(collegeId) {
  return request({
    url: `/major/college/${collegeId}`,
    method: 'get'
  })
}

// 根据ID获取专业
export function getMajorById(id) {
  return request({
    url: `/major/${id}`,
    method: 'get'
  })
}

// 新增专业
export function addMajor(data) {
  return request({
    url: '/major',
    method: 'post',
    data
  })
}

// 更新专业
export function updateMajor(id, data) {
  return request({
    url: `/major/${id}`,
    method: 'put',
    data
  })
}

// 删除专业
export function deleteMajor(id) {
  return request({
    url: `/major/${id}`,
    method: 'delete'
  })
}
