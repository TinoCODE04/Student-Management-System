import request from '@/utils/request'

// 获取学生自己的选课记录
export function getMySelections() {
  return request({
    url: '/selection/my',
    method: 'get'
  })
}

// 获取某课程的选课学生
export function getCourseSelections(courseId) {
  return request({
    url: `/selection/course/${courseId}`,
    method: 'get'
  })
}

// 学生选课
export function selectCourse(courseId) {
  return request({
    url: `/selection/select/${courseId}`,
    method: 'post'
  })
}

// 学生退课
export function dropCourse(courseId) {
  return request({
    url: `/selection/drop/${courseId}`,
    method: 'post'
  })
}

// 学生重新选课（退选后重选）
export function reselectCourse(courseId) {
  return request({
    url: `/selection/reselect/${courseId}`,
    method: 'post'
  })
}

// 教师录入成绩
export function updateScore(data) {
  return request({
    url: '/selection/score',
    method: 'post',
    data
  })
}
