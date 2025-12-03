<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <span v-if="!isCollapse">学生管理系统</span>
        <span v-else>管理</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <!-- 教师菜单 -->
        <template v-if="userStore.isTeacher">
          <el-menu-item index="/teacher/students">
            <el-icon><User /></el-icon>
            <span>学生管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/courses">
            <el-icon><Reading /></el-icon>
            <span>课程管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/selections">
            <el-icon><List /></el-icon>
            <span>选课管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/colleges">
            <el-icon><School /></el-icon>
            <span>学院管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/majors">
            <el-icon><Collection /></el-icon>
            <span>专业管理</span>
          </el-menu-item>
        </template>
        
        <!-- 学生菜单 -->
        <template v-if="userStore.isStudent">
          <el-menu-item index="/student/info">
            <el-icon><UserFilled /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/student/courses">
            <el-icon><Reading /></el-icon>
            <span>选课中心</span>
          </el-menu-item>
          <el-menu-item index="/student/my-courses">
            <el-icon><Notebook /></el-icon>
            <span>我的课程</span>
          </el-menu-item>
          <el-menu-item index="/student/grades">
            <el-icon><TrophyBase /></el-icon>
            <span>我的成绩</span>
          </el-menu-item>
          <el-menu-item index="/student/colleges">
            <el-icon><School /></el-icon>
            <span>学院信息</span>
          </el-menu-item>
          <el-menu-item index="/student/majors">
            <el-icon><Collection /></el-icon>
            <span>专业信息</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 主体区域 -->
    <el-container class="main-container">
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar" :src="userAvatar">
                {{ userStore.userInfo.name?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo.name }}</span>
              <span class="user-role">({{ userStore.isTeacher ? '教师' : '学生' }})</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区域 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { 
  HomeFilled, User, Reading, List, School, Collection,
  Notebook, TrophyBase, Fold, Expand, ArrowDown, UserFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/counter'
import { logout } from '@/api/auth'

// 导入头像图片
import avatar1 from '@/assets/p1.jpeg'
import avatar2 from '@/assets/p2.png'
import avatar3 from '@/assets/p3.jpg'
import avatar4 from '@/assets/p4.jpg'
import avatar5 from '@/assets/p5.jpg'
import avatar6 from '@/assets/p6.jpg'
import avatar7 from '@/assets/p7.jpg'
import avatar8 from '@/assets/p8.jpg'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

// 头像映射
const avatarMap = {
  'p1.jpeg': avatar1,
  'p2.png': avatar2,
  'p3.jpg': avatar3,
  'p4.jpg': avatar4,
  'p5.jpg': avatar5,
  'p6.jpg': avatar6,
  'p7.jpg': avatar7,
  'p8.jpg': avatar8
}

// 计算用户头像
const userAvatar = computed(() => {
  const avatar = userStore.userInfo.avatar
  if (avatar && avatarMap[avatar]) {
    return avatarMap[avatar]
  }
  return null
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/password')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await logout()
        } catch (e) {
          // 忽略错误
        }
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      }).catch(() => {})
      break
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}

.el-menu {
  border-right: none;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.collapse-btn:hover {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}

.user-avatar {
  background-color: #409EFF;
  color: white;
}

.user-name {
  font-size: 14px;
  color: #333;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.main {
  flex: 1;
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
