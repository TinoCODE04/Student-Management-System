import request from '@/utils/request'

/**
 * 上传课程附件
 */
export function uploadAttachment(data) {
  return request({
    url: '/course/attachment/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取课程附件列表
 */
export function getAttachmentList(courseId) {
  return request({
    url: `/course/attachment/list/${courseId}`,
    method: 'get'
  })
}

/**
 * 删除附件
 */
export function deleteAttachment(id) {
  return request({
    url: `/course/attachment/${id}`,
    method: 'delete'
  })
}

/**
 * 记录下载
 */
export function downloadAttachment(id) {
  return request({
    url: `/course/attachment/download/${id}`,
    method: 'post'
  })
}
