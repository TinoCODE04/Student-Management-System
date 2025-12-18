import request from '@/utils/request'

/**
 * 分页查询管理员
 */
export function getAdminPage(params) {
  return request({
    url: '/admin/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询管理员
 */
export function getAdminById(id) {
  return request({
    url: `/admin/${id}`,
    method: 'get'
  })
}

/**
 * 新增管理员
 */
export function addAdmin(data) {
  return request({
    url: '/admin',
    method: 'post',
    data
  })
}

/**
 * 更新管理员
 */
export function updateAdmin(id, data) {
  return request({
    url: `/admin/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除管理员
 */
export function deleteAdmin(id) {
  return request({
    url: `/admin/${id}`,
    method: 'delete'
  })
}

/**
 * 重置密码
 */
export function resetAdminPassword(id) {
  return request({
    url: `/admin/${id}/reset-password`,
    method: 'post'
  })
}

/**
 * 启用/禁用管理员
 */
export function toggleAdminStatus(id, status) {
  return request({
    url: `/admin/${id}/status`,
    method: 'post',
    params: { status }
  })
}

/**
 * 获取当前管理员信息
 */
export function getCurrentAdmin() {
  return request({
    url: '/admin/me',
    method: 'get'
  })
}
