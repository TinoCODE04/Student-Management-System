<template>
  <div class="profile">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon :size="22" color="#fff"><User /></el-icon>
          <span class="title">个人中心</span>
        </div>
      </template>
      
      <div class="profile-content">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <el-avatar :size="120" :src="userAvatar" class="user-avatar">
            <el-icon :size="60"><User /></el-icon>
          </el-avatar>
          <h2 class="user-name">{{ userStore.userInfo.name }}</h2>
          <el-tag :type="userStore.isTeacher ? 'primary' : 'success'" effect="dark" size="large" round>
            {{ userStore.isTeacher ? '教师' : '学生' }}
          </el-tag>
        </div>
        
        <!-- 基本信息 -->
        <div class="info-section">
          <el-descriptions :column="1" border size="large">
            <el-descriptions-item label-class-name="desc-label">
              <template #label>
                <el-icon><UserFilled /></el-icon> 用户名
              </template>
              <span class="info-value">{{ userStore.userInfo.username }}</span>
            </el-descriptions-item>
            <el-descriptions-item label-class-name="desc-label">
              <template #label>
                <el-icon><Avatar /></el-icon> 姓名
              </template>
              <span class="info-value highlight">{{ userStore.userInfo.name }}</span>
            </el-descriptions-item>
            <el-descriptions-item label-class-name="desc-label">
              <template #label>
                <el-icon><Stamp /></el-icon> 角色身份
              </template>
              <el-tag :type="userStore.isTeacher ? 'primary' : 'success'" effect="light">
                {{ userStore.isTeacher ? '教师' : '在校学生' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label-class-name="desc-label">
              <template #label>
                <el-icon><Clock /></el-icon> 登录状态
              </template>
              <el-tag type="success" effect="dark">
                <el-icon><CircleCheck /></el-icon> 在线
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>

    <!-- 快捷操作区域 -->
    <div class="action-cards">
      <el-card class="action-card" shadow="hover" @click="goToDetail">
        <el-icon :size="40" color="#409EFF"><Document /></el-icon>
        <h3>{{ userStore.isTeacher ? '教师信息' : '学生信息' }}</h3>
        <p>查看完整的个人详细信息</p>
      </el-card>
      
      <el-card class="action-card" shadow="hover" @click="router.push('/password')">
        <el-icon :size="40" color="#E6A23C"><Lock /></el-icon>
        <h3>修改密码</h3>
        <p>更新您的账户登录密码</p>
      </el-card>
      
      <el-card class="action-card" shadow="hover" @click="goToDashboard">
        <el-icon :size="40" color="#67C23A"><HomeFilled /></el-icon>
        <h3>返回首页</h3>
        <p>回到系统首页仪表盘</p>
      </el-card>
      
      <el-card class="action-card" shadow="hover" @click="handleLogout">
        <el-icon :size="40" color="#F56C6C"><SwitchButton /></el-icon>
        <h3>退出登录</h3>
        <p>安全退出当前账号</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { 
  User, UserFilled, Avatar, Stamp, Clock, CircleCheck, 
  Document, Lock, HomeFilled, SwitchButton, InfoFilled 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/counter'
import { logout } from '@/api/auth'

// 导入头像图片 - 学生头像
import stu_ava1 from '@/assets/p1.jpeg'
import stu_ava2 from '@/assets/p2.png'
import stu_ava3 from '@/assets/p3.jpg'
import stu_ava4 from '@/assets/p4.jpg'
import stu_ava5 from '@/assets/p5.jpg'
import stu_ava6 from '@/assets/p6.jpg'
import stu_ava7 from '@/assets/p7.jpg'
import stu_ava8 from '@/assets/p8.jpg'
// 导入头像图片 - 教师头像
import t_ava1 from '@/assets/t1.png'
import t_ava2 from '@/assets/t2.png'
import t_ava3 from '@/assets/t3.png'
import t_ava4 from '@/assets/t4.png'
import t_ava5 from '@/assets/t5.png'
import t_ava6 from '@/assets/t6.png'

const router = useRouter()
const userStore = useUserStore()

// 头像映射 - 学生
const studentAvatarMap = {
  'p1.jpeg': stu_ava1,
  'p2.png': stu_ava2,
  'p3.jpg': stu_ava3,
  'p4.jpg': stu_ava4,
  'p5.jpg': stu_ava5,
  'p6.jpg': stu_ava6,
  'p7.jpg': stu_ava7,
  'p8.jpg': stu_ava8
}

// 头像映射 - 教师（根据教师ID分配头像）
const teacherAvatarMap = {
  1: t_ava1,
  2: t_ava2,
  3: t_ava3,
  4: t_ava4,
  5: t_ava5,
  6: t_ava6
}

// 计算用户头像
const userAvatar = computed(() => {
  // 教师使用教师头像
  if (userStore.isTeacher) {
    const teacherId = userStore.userInfo.userId
    if (teacherId && teacherAvatarMap[teacherId]) {
      return teacherAvatarMap[teacherId]
    }
    // 如果教师ID超过6，循环使用头像
    if (teacherId) {
      const index = ((teacherId - 1) % 6) + 1
      return teacherAvatarMap[index]
    }
  }
  // 学生使用学生头像
  const avatar = userStore.userInfo.avatar
  if (avatar && studentAvatarMap[avatar]) {
    return studentAvatarMap[avatar]
  }
  return null
})

const goToDetail = () => {
  if (userStore.isStudent) {
    router.push('/student/info')
  }
}

const goToDashboard = () => {
  router.push('/dashboard')
}

const handleLogout = () => {
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
}
</script>

<style scoped>
.profile {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

/* 个人信息卡片 */
.profile-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.profile-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.profile-content {
  display: flex;
  gap: 40px;
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  min-width: 180px;
}

.user-avatar {
  border: 4px solid #e6e8eb;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-name {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.info-section {
  flex: 1;
}

:deep(.desc-label) {
  width: 120px;
  font-weight: 500;
  background-color: #f5f7fa !important;
}

:deep(.el-descriptions__cell) {
  padding: 16px !important;
}

.info-value {
  color: #606266;
  font-size: 15px;
}

.info-value.highlight {
  color: #409EFF;
  font-weight: 600;
}

/* 快捷操作卡片 */
.action-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.action-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  padding: 10px;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.action-card h3 {
  margin: 15px 0 8px;
  font-size: 16px;
  color: #303133;
}

.action-card p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.system-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

/* 响应式布局 */
@media (max-width: 992px) {
  .action-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    align-items: center;
  }
  
  .action-cards {
    grid-template-columns: 1fr;
  }
}
</style>
