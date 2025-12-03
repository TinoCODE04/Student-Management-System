import { createRouter, createWebHistory } from 'vue-router'

// 公共路由
const publicRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true }
  }
]

// 教师路由
const teacherRoutes = [
  {
    path: '/teacher/students',
    name: 'TeacherStudents',
    component: () => import('@/views/teacher/StudentManage.vue'),
    meta: { title: '学生管理', role: 'teacher' }
  },
  {
    path: '/teacher/courses',
    name: 'TeacherCourses',
    component: () => import('@/views/teacher/CourseManage.vue'),
    meta: { title: '课程管理', role: 'teacher' }
  },
  {
    path: '/teacher/selections',
    name: 'TeacherSelections',
    component: () => import('@/views/teacher/SelectionManage.vue'),
    meta: { title: '选课管理', role: 'teacher' }
  },
  {
    path: '/teacher/colleges',
    name: 'TeacherColleges',
    component: () => import('@/views/teacher/CollegeManage.vue'),
    meta: { title: '学院管理', role: 'teacher' }
  },
  {
    path: '/teacher/majors',
    name: 'TeacherMajors',
    component: () => import('@/views/teacher/MajorManage.vue'),
    meta: { title: '专业管理', role: 'teacher' }
  }
]

// 学生路由
const studentRoutes = [
  {
    path: '/student/info',
    name: 'StudentInfo',
    component: () => import('@/views/student/MyInfo.vue'),
    meta: { title: '个人信息', role: 'student' }
  },
  {
    path: '/student/courses',
    name: 'StudentCourses',
    component: () => import('@/views/student/CourseSelect.vue'),
    meta: { title: '选课中心', role: 'student' }
  },
  {
    path: '/student/my-courses',
    name: 'StudentMyCourses',
    component: () => import('@/views/student/MyCourses.vue'),
    meta: { title: '我的课程', role: 'student' }
  },
  {
    path: '/student/grades',
    name: 'StudentGrades',
    component: () => import('@/views/student/MyGrades.vue'),
    meta: { title: '我的成绩', role: 'student' }
  },
  {
    path: '/student/colleges',
    name: 'StudentColleges',
    component: () => import('@/views/student/CollegeView.vue'),
    meta: { title: '学院信息', role: 'student' }
  },
  {
    path: '/student/majors',
    name: 'StudentMajors',
    component: () => import('@/views/student/MajorView.vue'),
    meta: { title: '专业信息', role: 'student' }
  }
]

// 共享路由（教师和学生都可以访问）
const sharedRoutes = [
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/password',
    name: 'Password',
    component: () => import('@/views/Password.vue'),
    meta: { title: '修改密码' }
  }
]

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  ...publicRoutes,
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      ...teacherRoutes,
      ...studentRoutes,
      ...sharedRoutes
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404', public: true }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学生信息管理系统` : '学生信息管理系统'
  
  // 获取token和用户角色
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const role = userInfo.role
  
  // 已登录用户访问登录页，跳转到首页
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }
  
  // 公共页面直接放行
  if (to.meta.public) {
    next()
    return
  }
  
  // 未登录跳转到登录页
  if (!token) {
    next('/login')
    return
  }
  
  // 检查角色权限
  if (to.meta.role) {
    if (to.meta.role !== role) {
      // 无权限访问，跳转到首页
      next('/dashboard')
      return
    }
  }
  
  next()
})

export default router
