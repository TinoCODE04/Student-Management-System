<template>
  <el-container class="admin-layout">
    <el-aside width="240px">
      <div class="logo">
        <el-icon><Monitor /></el-icon>
        <span>管理员系统</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-sub-menu index="users">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/admins">管理员管理</el-menu-item>
          <el-menu-item index="/admin/teachers">教师管理</el-menu-item>
          <el-menu-item index="/admin/students">学生管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="academic">
          <template #title>
            <el-icon><School /></el-icon>
            <span>学术管理</span>
          </template>
          <el-menu-item index="/admin/colleges">学院管理</el-menu-item>
          <el-menu-item index="/admin/majors">专业管理</el-menu-item>
          <el-menu-item index="/admin/courses">课程管理</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/admin/notifications">
          <el-icon><Bell /></el-icon>
          <span>系统公告</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/profile">
          <el-icon><UserFilled /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle && currentTitle !== '首页'">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="username">{{ userInfo.name || userInfo.username || '超级管理员' }}</span>
          <el-dropdown @command="handleCommand">
            <el-avatar :src="adminAvatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { userInfoStorage, tokenStorage } from '@/utils/storage'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Monitor, HomeFilled, User, School, Bell, UserFilled } from '@element-plus/icons-vue'
import { getCurrentAdmin } from '@/api/admin'
// 导入管理员头像
import admin_ava1 from '@/assets/admin1.png'
import admin_ava2 from '@/assets/admin2.jpg'
import admin_ava3 from '@/assets/admin3.png'

const router = useRouter()
const route = useRoute()

const userInfo = ref(userInfoStorage.get() || {})
const activeMenu = computed(() => route.path)

// 加载管理员完整信息
onMounted(async () => {
  try {
    const res = await getCurrentAdmin()
    if (res.code === 200 && res.data) {
      userInfo.value = {
        userId: res.data.id,
        username: res.data.username,
        name: res.data.name || res.data.username,
        role: res.data.role,
        avatar: res.data.avatar,
        gender: res.data.gender,
        phone: res.data.phone,
        email: res.data.email
      }
      // 同步更新到localStorage
      userInfoStorage.set(userInfo.value, true)
    }
  } catch (error) {
    console.error('获取管理员信息失败:', error)
  }
})

// 监听路由变化，确保userInfo保持最新
watch(() => route.path, () => {
  const stored = userInfoStorage.get()
  if (stored) {
    userInfo.value = stored
  }
})

// 管理员头像映射
const adminAvatarMap = {
  1: admin_ava1,
  2: admin_ava2,
  3: admin_ava3
}

// 计算当前管理员头像
const adminAvatar = computed(() => {
  const adminId = userInfo.value.userId
  if (adminId && adminAvatarMap[adminId]) {
    return adminAvatarMap[adminId]
  }
  // 如果管理员ID超过3，循环使用头像
  if (adminId) {
    const index = ((adminId - 1) % 3) + 1
    return adminAvatarMap[index]
  }
  // 默认使用第一个管理员头像
  return admin_ava1
})

const currentTitle = computed(() => {
  const titles = {
    '/admin/dashboard': '首页',
    '/admin/admins': '管理员管理',
    '/admin/teachers': '教师管理',
    '/admin/students': '学生管理',
    '/admin/colleges': '学院管理',
    '/admin/majors': '专业管理',
    '/admin/courses': '课程管理',
    '/admin/notifications': '系统公告',
    '/admin/profile': '个人信息',
    '/admin/password': '修改密码'
  }
  return titles[route.path] || '管理系统'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      tokenStorage.remove()
      userInfoStorage.remove()
      ElMessage.success('退出成功')
      router.push('/admin/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/admin/profile')
  } else if (command === 'password') {
    router.push('/admin/password')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  background: #2b3a4a;
}

.logo .el-icon {
  font-size: 28px;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 0 40px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 16px;
  color: #666;
}

.el-avatar {
  cursor: pointer;
  width: 40px;
  height: 40px;
}

.el-main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
