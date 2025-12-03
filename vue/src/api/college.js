import request from '@/utils/request'

// 获取学院列表
export function getCollegeList() {
  return request({
    url: '/college/list',
    method: 'get'
  })
}

// 根据ID获取学院
export function getCollegeById(id) {
  return request({
    url: `/college/${id}`,
    method: 'get'
  })
}

// 新增学院
export function addCollege(data) {
  return request({
    url: '/college',
    method: 'post',
    data
  })
}

// 更新学院
export function updateCollege(id, data) {
  return request({
    url: `/college/${id}`,
    method: 'put',
    data
  })
}

// 删除学院
export function deleteCollege(id) {
  return request({
    url: `/college/${id}`,
    method: 'delete'
  })
}
