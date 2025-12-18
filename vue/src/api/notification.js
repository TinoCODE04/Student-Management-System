import request from '@/utils/request'

/**
 * 获取通知列表(用户端)
 */
export function getNotificationList(params) {
  return request({
    url: '/notification/list',
    method: 'get',
    params
  })
}

/**
 * 获取所有公告(管理员端)
 */
export function getNotifications(params) {
  return request({
    url: '/notification/page',
    method: 'get',
    params
  })
}

/**
 * 新增公告(管理员)
 */
export function addNotification(data) {
  return request({
    url: '/notification',
    method: 'post',
    data
  })
}

/**
 * 更新公告(管理员)
 */
export function updateNotification(data) {
  return request({
    url: `/notification/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告(管理员)
 */
export function deleteNotificationApi(id) {
  return request({
    url: `/notification/${id}`,
    method: 'delete'
  })
}

/**
 * 获取未读数量
 */
export function getUnreadCount() {
  return request({
    url: '/notification/unread-count',
    method: 'get'
  })
}

/**
 * 标记为已读
 */
export function markAsRead(id) {
  return request({
    url: `/notification/read/${id}`,
    method: 'post'
  })
}

/**
 * 全部标记为已读
 */
export function markAllAsRead() {
  return request({
    url: '/notification/read-all',
    method: 'post'
  })
}

