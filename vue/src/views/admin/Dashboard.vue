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
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>系统公告</span>
              <el-button type="primary" size="small" @click="$router.push('/admin/notifications')">
                管理公告
              </el-button>
            </div>
          </template>
          <div v-loading="loading">
            <el-empty v-if="announcements.length === 0" description="暂无公告" :image-size="100" />
            <div v-else class="announcement-list">
              <div
                v-for="item in announcements"
                :key="item.id"
                class="announcement-item"
              >
                <div class="announcement-header">
                  <h4 class="announcement-title">{{ item.title }}</h4>
                  <div class="announcement-meta">
                    <el-tag 
                      :type="item.priority === 'high' ? 'danger' : (item.priority === 'medium' ? 'warning' : 'info')" 
                      size="small"
                    >
                      {{ item.priority === 'high' ? '高' : (item.priority === 'medium' ? '中' : '低') }}
                    </el-tag>
                    <el-tag 
                      :type="item.targetRole === 'all' ? 'success' : (item.targetRole === 'teacher' ? 'primary' : 'warning')" 
                      size="small"
                      style="margin-left: 8px"
                    >
                      {{ item.targetRole === 'all' ? '全体' : (item.targetRole === 'teacher' ? '教师' : '学生') }}
                    </el-tag>
                    <span class="announcement-time">{{ item.createTime }}</span>
                  </div>
                </div>
                <p class="announcement-content">{{ item.content }}</p>
                <div class="announcement-footer">
                  <span class="publisher">发布人: {{ item.publisherName || '管理员' }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UserFilled, User, Reading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAnnouncements } from '@/api/announcement'

const stats = ref({
  adminCount: 0,
  teacherCount: 0,
  studentCount: 0,
  courseCount: 0
})

const announcements = ref([])
const loading = ref(false)

// 加载公告列表
const loadAnnouncements = async () => {
  loading.value = true
  try {
    const res = await getAnnouncements({
      page: 1,
      size: 5  // 只显示最新的5条公告
    })
    if (res.code === 200) {
      announcements.value = res.data.records || res.data || []
    }
  } catch (error) {
    console.error('加载公告失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // TODO: 调用API获取统计数据
  stats.value = {
    adminCount: 3,
    teacherCount: 6,
    studentCount: 8,
    courseCount: 12
  }
  
  // 加载公告数据
  loadAnnouncements()
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

/* 公告列表样式 */
.announcement-list {
  max-height: 500px;
  overflow-y: auto;
}

.announcement-item {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-item:hover {
  background-color: #f5f7fa;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.announcement-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.announcement-time {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.announcement-content {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.publisher {
  font-size: 12px;
  color: #909399;
}
</style>
