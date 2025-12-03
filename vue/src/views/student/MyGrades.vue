<template>
  <div class="my-grades">
    <el-card class="grade-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon :size="22" color="#fff"><TrophyBase /></el-icon>
            <span class="title">我的成绩</span>
          </div>
          <div class="header-right">
            <div class="stat-item">
              <el-icon color="#67C23A"><CircleCheck /></el-icon>
              <span class="stat-label">已获学分</span>
              <span class="stat-value credit">{{ totalCredit }}</span>
            </div>
            <div class="stat-item">
              <el-icon color="#409EFF"><DataLine /></el-icon>
              <span class="stat-label">平均成绩</span>
              <span class="stat-value score">{{ avgScore }}</span>
            </div>
            <div class="stat-item">
              <el-icon color="#E6A23C"><Document /></el-icon>
              <span class="stat-label">已修课程</span>
              <span class="stat-value count">{{ completedCourses.length }}</span>
            </div>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="completedCourses" 
        v-loading="loading" 
        stripe 
        border
        :header-cell-style="{ background: '#F4F3F2', color: '#606266', fontWeight: 'bold', textAlign: 'center', height: '50px' }"
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
        <el-table-column label="学分" width="90" align="center">
          <template #default="{ row }">
            <el-tag type="warning" effect="light" round>{{ row.course?.credit }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="{ row }">
            <span class="score-display" :class="getScoreClass(row.score)">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getGradeType(row.score)" effect="dark" round>{{ getGrade(row.score) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'" effect="dark">
              {{ row.score >= 60 ? '通过' : '不通过' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绩点" width="90" align="center">
          <template #default="{ row }">
            <span class="gpa-text">{{ getGPA(row.score) }}</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="completedCourses.length === 0 && !loading" description="暂无成绩记录" :image-size="120" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { TrophyBase, CircleCheck, DataLine, Document } from '@element-plus/icons-vue'
import { getMySelections } from '@/api/selection'

const selections = ref([])
const loading = ref(false)

const completedCourses = computed(() => {
  return selections.value.filter(s => s.status === 'completed' && s.score !== null)
})

const totalCredit = computed(() => {
  return completedCourses.value
    .filter(s => s.score >= 60)
    .reduce((sum, s) => sum + (s.course?.credit || 0), 0)
})

const avgScore = computed(() => {
  if (completedCourses.value.length === 0) return '--'
  const total = completedCourses.value.reduce((sum, s) => sum + parseFloat(s.score), 0)
  return (total / completedCourses.value.length).toFixed(1)
})

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const getGrade = (score) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 70) return '中等'
  if (score >= 60) return '及格'
  return '不及格'
}

const getGradeType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 计算绩点
const getGPA = (score) => {
  if (score >= 90) return '4.0'
  if (score >= 85) return '3.7'
  if (score >= 82) return '3.3'
  if (score >= 78) return '3.0'
  if (score >= 75) return '2.7'
  if (score >= 72) return '2.3'
  if (score >= 68) return '2.0'
  if (score >= 64) return '1.5'
  if (score >= 60) return '1.0'
  return '0.0'
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  // if (row.score < 60) return 'fail-row'
  // if (row.score >= 90) return 'excellent-row'
  return ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMySelections()
    selections.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.my-grades {
  padding: 20px;
}

.grade-card {
  border-radius: 12px;
}

.grade-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
  border-bottom: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.5);
  padding: 8px 16px;
  border-radius: 20px;
  
}

.stat-item .el-icon {
  font-size: 16px;
}

.stat-label {
  color: #151516;
  font-size: 13px;
  font-weight: 500;
}

.stat-value {
  font-weight: 700;
  font-size: 16px;
  color: #303133;
  margin-left: 4px;

}

.grade-card :deep(.el-card__body) {
  padding: 20px;
}

.course-name {
  font-weight: 500;
  color: #303133;
}

.score-display {
  font-size: 18px;
  font-weight: 700;
}

.score-excellent { color: #67C23A; }
.score-good { color: #409EFF; }
.score-pass { color: #E6A23C; }
.score-fail { color: #F56C6C; }

.gpa-text {
  font-weight: 600;
  color: #606266;
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

:deep(.el-table .fail-row) {
  background-color: #fff;
}

:deep(.el-table .excellent-row) {
  background-color: #fff;
}

/* 响应式 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-right {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .stat-item {
    padding: 6px 12px;
  }
}
</style>
