import request from '@/utils/request'

/**
 * 获取所有公告(管理员端 - 分页)
 */
export function getAnnouncements(params) {
  return request({
    url: '/announcement/page',
    method: 'get',
    params
  })
}

/**
 * 发布公告(管理员)
 */
export function publishAnnouncement(data) {
  return request({
    url: '/announcement',
    method: 'post',
    data
  })
}

/**
 * 更新公告(管理员)
 */
export function updateAnnouncement(data) {
  return request({
    url: `/announcement/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告(管理员)
 */
export function deleteAnnouncement(id) {
  return request({
    url: `/announcement/${id}`,
    method: 'delete'
  })
}
