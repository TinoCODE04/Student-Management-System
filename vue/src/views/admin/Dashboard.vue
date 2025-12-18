<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon admin">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">管理员总数</div>
              <div class="stat-value">{{ stats.adminCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon teacher">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">教师总数</div>
              <div class="stat-value">{{ stats.teacherCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon student">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">学生总数</div>
              <div class="stat-value">{{ stats.studentCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon course">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">课程总数</div>
              <div class="stat-value">{{ stats.courseCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>系统公告</span>
          </template>
          <el-empty v-if="notifications.length === 0" description="暂无公告" />
          <el-timeline v-else>
            <el-timeline-item
              v-for="item in notifications"
              :key="item.id"
              :timestamp="item.publishTime"
              placement="top"
            >
              <h4>{{ item.title }}</h4>
              <p>{{ item.content }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UserFilled, User, Reading } from '@element-plus/icons-vue'

const stats = ref({
  adminCount: 0,
  teacherCount: 0,
  studentCount: 0,
  courseCount: 0
})

const notifications = ref([])

onMounted(() => {
  // TODO: 调用API获取统计数据
  stats.value = {
    adminCount: 3,
    teacherCount: 6,
    studentCount: 8,
    courseCount: 12
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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

.stat-icon.admin {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.teacher {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.student {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.course {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
