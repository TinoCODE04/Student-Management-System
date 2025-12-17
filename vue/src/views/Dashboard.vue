<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>欢迎回来，{{ userStore.userInfo.name }}！</h2>
              <p>{{ greeting }}，祝您工作愉快！</p>
              <!-- 登录状态提示 -->
              <p v-if="loginStatusMessage" class="login-status">
                <el-icon><Clock /></el-icon>
                <span>{{ loginStatusMessage }}</span>
              </p>
            </div>
            <div class="welcome-date">
              <p class="date">{{ currentDate }}</p>
              <p class="time">{{ currentTime }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 教师统计 -->
    <template v-if="userStore.isTeacher">
      <el-row :gutter="20" class="stat-row">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" style="background: #409EFF"><User /></el-icon>
              <div class="stat-info">
                <p class="stat-value">{{ stats.studentCount }}</p>
                <p class="stat-label">学生总数</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" style="background: #67C23A"><Reading /></el-icon>
              <div class="stat-info">
                <p class="stat-value">{{ stats.courseCount }}</p>
                <p class="stat-label">课程总数</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" style="background: #E6A23C"><School /></el-icon>
              <div class="stat-info">
                <p class="stat-value">{{ stats.collegeCount }}</p>
                <p class="stat-label">学院总数</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" style="background: #F56C6C"><Collection /></el-icon>
              <div class="stat-info">
                <p class="stat-value">{{ stats.majorCount }}</p>
                <p class="stat-label">专业总数</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 快捷操作 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card class="quick-card">
            <template #header>
              <span>快捷操作</span>
            </template>
            <div class="quick-actions">
              <el-button type="primary" @click="$router.push('/teacher/students')">
                <el-icon><User /></el-icon>学生管理
              </el-button>
              <el-button type="success" @click="$router.push('/teacher/courses')">
                <el-icon><Reading /></el-icon>课程管理
              </el-button>
              <el-button type="warning" @click="$router.push('/teacher/selections')">
                <el-icon><List /></el-icon>选课管理
              </el-button>
              <el-button type="info" @click="$router.push('/teacher/colleges')">
                <el-icon><School /></el-icon>学院管理
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
    
    <!-- 学生统计 -->
    <template v-if="userStore.isStudent">
      <el-row :gutter="20" class="stat-row">
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" style="background: #409EFF"><Reading /></el-icon>
              <div class="stat-info">
                <p class="stat-value">{{ studentStats.selectedCourses }}</p>
                <p class="stat-label">已选课程</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" style="background: #67C23A"><TrophyBase /></el-icon>
              <div class="stat-info">
                <p class="stat-value">{{ studentStats.totalCredit }}</p>
                <p class="stat-label">已获学分</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon" style="background: #E6A23C"><Star /></el-icon>
              <div class="stat-info">
                <p class="stat-value">{{ studentStats.avgScore }}</p>
                <p class="stat-label">平均成绩</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 快捷操作 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card class="quick-card">
            <template #header>
              <span>快捷操作</span>
            </template>
            <div class="quick-actions">
              <el-button type="primary" @click="$router.push('/student/courses')">
                <el-icon><Reading /></el-icon>选课中心
              </el-button>
              <el-button type="success" @click="$router.push('/student/my-courses')">
                <el-icon><Notebook /></el-icon>我的课程
              </el-button>
              <el-button type="warning" @click="$router.push('/student/grades')">
                <el-icon><TrophyBase /></el-icon>我的成绩
              </el-button>
              <el-button type="info" @click="$router.push('/profile')">
                <el-icon><User /></el-icon>个人中心
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { User, Reading, School, Collection, List, Notebook, TrophyBase, Star, Clock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/counter'
import { getStudentList } from '@/api/student'
import { getCourseList } from '@/api/course'
import { getCollegeList } from '@/api/college'
import { getMajorList } from '@/api/major'
import { getMySelections } from '@/api/selection'
import { getCurrentStudentInfo } from '@/api/student'
import { tokenStorage } from '@/utils/storage'

const userStore = useUserStore()

// 时间显示
const currentDate = ref('')
const currentTime = ref('')
let timer = null

// 教师统计
const stats = reactive({
  studentCount: 0,
  courseCount: 0,
  collegeCount: 0,
  majorCount: 0
})

// 学生统计
const studentStats = reactive({
  selectedCourses: 0,
  totalCredit: 0,
  avgScore: '--'
})

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
})

// 登录状态提示
const loginStatusMessage = computed(() => {
  const remainingDays = tokenStorage.getRemainingDays()
  if (remainingDays === null) {
    // 未勾选"记住我"，使用sessionStorage
    return '会话登录中（关闭标签页后需重新登录）'
  } else if (remainingDays > 0) {
    // 勾选了"记住我"，显示剩余天数
    return `已记住登录状态，还剩 ${remainingDays} 天有效期`
  } else {
    // 已过期
    return '登录已过期，请重新登录'
  }
})

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
  currentTime.value = now.toLocaleTimeString('zh-CN')
}

// 加载教师统计数据
const loadTeacherStats = async () => {
  try {
    const [students, courses, colleges, majors] = await Promise.all([
      getStudentList(),
      getCourseList(),
      getCollegeList(),
      getMajorList()
    ])
    stats.studentCount = students.data?.length || 0
    stats.courseCount = courses.data?.length || 0
    stats.collegeCount = colleges.data?.length || 0
    stats.majorCount = majors.data?.length || 0
  } catch (e) {
    console.error(e)
  }
}

// 加载学生统计数据
const loadStudentStats = async () => {
  try {
    const [selections, info] = await Promise.all([
      getMySelections(),
      getCurrentStudentInfo()
    ])
    
    const selectionList = selections.data || []
    studentStats.selectedCourses = selectionList.filter(s => s.status === 'selected').length
    studentStats.totalCredit = info.data?.totalCredit || 0
    
    // 计算平均成绩
    const completedSelections = selectionList.filter(s => s.status === 'completed' && s.score)
    if (completedSelections.length > 0) {
      const totalScore = completedSelections.reduce((sum, s) => sum + parseFloat(s.score), 0)
      studentStats.avgScore = (totalScore / completedSelections.length).toFixed(1)
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  
  if (userStore.isTeacher) {
    loadTeacherStats()
  } else if (userStore.isStudent) {
    loadStudentStats()
  }
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
  height: 100%;
  overflow-y: auto;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
}

.welcome-text .login-status {
  margin-top: 12px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: #fff;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.welcome-text .login-status .el-icon {
  font-size: 14px;
}

.welcome-date {
  text-align: right;
}

.welcome-date .date {
  margin: 0 0 5px 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-date .time {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  height: auto;
  min-height: 100px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  margin: 5px 0 0 0;
  font-size: 14px;
  color: #909399;
}

.quick-card {
  margin-bottom: 20px;
}

.quick-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}
</style>
