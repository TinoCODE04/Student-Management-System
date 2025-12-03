<template>
  <div class="my-courses">
    <el-card class="course-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon :size="22" color="#409EFF"><Collection /></el-icon>
            <span class="title">我的课程</span>
          </div>
          <div class="header-right">
            <el-tag type="info" effect="plain" size="large">
              共 {{ myCourses.length }} 门课程
            </el-tag>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="myCourses" 
        v-loading="loading" 
        stripe 
        border
        :header-cell-style="{ background: '#f4f5f6', color: '#606266', fontWeight: 'bold', textAlign: 'center', height: '50px' }"
        :cell-style="{ textAlign: 'center', height: '55px' }"
        :row-class-name="tableRowClassName"
      >
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="课程名称" min-width="180" align="center">
          <template #default="{ row }">
            <span class="course-name">{{ row.course?.courseName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="授课教师" min-width="120" align="center">
          <template #default="{ row }">
            <span>{{ row.course?.teacher?.name || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="学分" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="warning" effect="light" round>{{ row.course?.credit }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上课时段" min-width="130" align="center">
          <template #default="{ row }">
            <span class="schedule-text">{{ row.course?.schedule || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="上课地点" min-width="130" align="center">
          <template #default="{ row }">
            {{ row.course?.location || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="dark">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="选课时间" min-width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ formatTime(row.selectTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <!-- 只有待开课(pending)状态才能退课 -->
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              round
              @click="handleDrop(row)"
            >
              退课
            </el-button>
            <!-- 已退选(dropped)状态可以重新选课 -->
            <el-button
              v-else-if="row.status === 'dropped'"
              type="primary"
              size="small"
              round
              @click="handleReselect(row)"
            >
              重选
            </el-button>
            <span v-else class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="myCourses.length === 0 && !loading" description="暂无选课记录" :image-size="120" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Collection } from '@element-plus/icons-vue'
import { getMySelections, dropCourse, reselectCourse } from '@/api/selection'

const myCourses = ref([])
const loading = ref(false)

const getStatusType = (status) => {
  const map = { 
    'pending': 'warning',    // 待开课 - 橙色
    'studying': 'primary',   // 学习中 - 蓝色
    'completed': 'success',  // 已完成 - 绿色
    'dropped': 'info'        // 已退选 - 灰色
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 
    'pending': '待开课',     // 可以退课
    'studying': '学习中',    // 不能退课
    'completed': '已完成', 
    'dropped': '已退选' 
  }
  return map[status] || status
}

// 格式化时间显示
const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 16)
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  // if (row.status === 'dropped') return 'dropped-row'
  // if (row.status === 'completed') return 'completed-row'
  return ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMySelections()
    myCourses.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleDrop = (row) => {
  ElMessageBox.confirm(
    `确定要退选课程 "${row.course?.courseName}" 吗？`,
    '确认退课',
    { type: 'warning' }
  ).then(async () => {
    try {
      await dropCourse(row.courseId)
      ElMessage.success('退课成功')
      loadData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

// 重新选课
const handleReselect = (row) => {
  ElMessageBox.confirm(
    `确定要重新选修课程 "${row.course?.courseName}" 吗？`,
    '确认重选',
    { type: 'info' }
  ).then(async () => {
    try {
      await reselectCourse(row.courseId)
      ElMessage.success('重新选课成功')
      loadData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.my-courses {
  padding: 20px;
}

.course-card {
  border-radius: 12px;
}

.course-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
  
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-left .el-icon {
  color: #fff;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.header-right .el-tag {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.course-card :deep(.el-card__body) {
  padding: 20px;
}

.course-name {
  font-weight: 500;
  color: #303133;
}

.schedule-text {
  color: #606266;
}

.time-text {
  color: #909399;
  font-size: 13px;
}

.no-action {
  color: #c0c4cc;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  font-size: 14px;
}

:deep(.el-table td) {
  font-size: 14px;
}

/* 加粗表格边框线 */
:deep(.el-table--border) {
  border: 1px solid #ebeef5;
}

:deep(.el-table--border .el-table__cell) {
  border-right: 2px solid #ebeef5;
  border-bottom: 2px solid #ebeef5;
}

:deep(.el-table th.el-table__cell) {
  border-bottom: 2px solid #ebeef5;
}

:deep(.el-table .dropped-row) {
  background-color: #fafafa;
  color: #999;
}

:deep(.el-table .completed-row) {
  background-color: #f0f9eb;
}

/* 按钮样式 */
:deep(.el-button.is-round) {
  padding: 8px 16px;
}
</style>
