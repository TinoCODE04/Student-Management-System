import request from '@/utils/request'

/**
 * 获取通知列表
 */
export function getNotificationList(params) {
  return request({
    url: '/notification/list',
    method: 'get',
    params
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
