import { createRouter, createWebHistory } from 'vue-router'
import { tokenStorage, userInfoStorage } from '@/utils/storage'

// 公共路由
const publicRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/AdminLogin.vue'),
    meta: { title: '管理员登录', public: true }
  }
]

// 教师路由
const teacherRoutes = [
  {
    path: '/teacher/info',
    name: 'TeacherInfo',
    component: () => import('@/views/teacher/MyInfo.vue'),
    meta: { title: '个人信息', role: 'teacher' }
  },
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
    meta: { title: '学院信息', role: 'teacher' }
  },
  {
    path: '/teacher/majors',
    name: 'TeacherMajors',
    component: () => import('@/views/teacher/MajorManage.vue'),
    meta: { title: '专业信息', role: 'teacher' }
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

// 管理员路由
const adminRoutes = [
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '系统概览', role: 'admin' }
      },
      {
        path: 'admins',
        name: 'AdminManage',
        component: () => import('@/views/admin/AdminManage.vue'),
        meta: { title: '管理员管理', role: 'admin' }
      },
      {
        path: 'teachers',
        name: 'AdminTeachers',
        component: () => import('@/views/admin/TeacherManage.vue'),
        meta: { title: '教师管理', role: 'admin' }
      },
      {
        path: 'students',
        name: 'AdminStudents',
        component: () => import('@/views/teacher/StudentManage.vue'),
        meta: { title: '学生管理', role: 'admin' }
      },
      {
        path: 'colleges',
        name: 'AdminColleges',
        component: () => import('@/views/admin/CollegeManage.vue'),
        meta: { title: '学院管理', role: 'admin' }
      },
      {
        path: 'majors',
        name: 'AdminMajors',
        component: () => import('@/views/admin/MajorManage.vue'),
        meta: { title: '专业管理', role: 'admin' }
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import('@/views/teacher/CourseManage.vue'),
        meta: { title: '课程管理', role: 'admin' }
      },
      {
        path: 'notifications',
        name: 'AdminNotifications',
        component: () => import('@/views/admin/AnnouncementManage.vue'),
        meta: { title: '系统公告', role: 'admin' }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息', role: 'admin' }
      },
      {
        path: 'password',
        name: 'AdminPassword',
        component: () => import('@/views/Password.vue'),
        meta: { title: '修改密码', role: 'admin' }
      }
    ]
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
  ...adminRoutes,
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
  
  // 获取token和用户角色（使用 sessionStorage）
  const token = tokenStorage.get()
  const userInfo = userInfoStorage.get()
  const role = userInfo.role
  
  // 已登录用户访问登录页，跳转到首页
  if (to.path === '/login' && token) {
    if (role === 'admin') {
      next('/admin/dashboard')
    } else {
      next('/dashboard')
    }
    return
  }
  
  // 管理员访问普通登录页，跳转到管理员登录
  if (to.path === '/login' && !token) {
    next()
    return
  }
  
  // 公共页面直接放行
  if (to.meta.public) {
    next()
    return
  }
  
  // 未登录跳转到登录页
  if (!token) {
    if (to.path.startsWith('/admin')) {
      next('/admin/login')
    } else {
      next('/login')
    }
    return
  }
  
  // 检查角色权限
  if (to.meta.role) {
    if (to.meta.role !== role) {
      // 无权限访问，跳转到相应首页
      if (role === 'admin') {
        next('/admin/dashboard')
      } else {
        next('/dashboard')
      }
      return
    }
  }
  
  next()
})

export default router
