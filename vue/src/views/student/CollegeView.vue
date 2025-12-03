<template>
  <div class="college-view">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" style="--accent: #409eff;">
        <div class="stat-icon">🏫</div>
        <div class="stat-info">
          <div class="stat-value">{{ colleges.length }}</div>
          <div class="stat-label">学院总数</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #67c23a;">
        <div class="stat-icon">👨‍🎓</div>
        <div class="stat-info">
          <div class="stat-value">{{ totalStudents }}</div>
          <div class="stat-label">学生总数</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #e6a23c;">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <div class="stat-value">{{ totalMajors }}</div>
          <div class="stat-label">专业总数</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #f56c6c;">
        <div class="stat-icon">📖</div>
        <div class="stat-info">
          <div class="stat-value">{{ totalCourses }}</div>
          <div class="stat-label">课程总数</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><School /></el-icon>
            学院信息
          </span>
        </div>
      </template>
      
      <el-table :data="colleges" v-loading="loading" stripe border
                :header-cell-style="{ background: '#CCCCFF', color: '#606266', fontWeight: 'bold' }">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="collegeName" label="学院名称" min-width="180" />
        <el-table-column prop="minCredit" label="毕业最低学分" width="140" align="center">
          <template #default="{ row }">
            {{ row.minCredit }} 学分
          </template>
        </el-table-column>
        <el-table-column label="专业数量" width="120" align="center">
          <template #default="{ row }">
            {{ row.majorCount || 0 }} 个
          </template>
        </el-table-column>
        <el-table-column label="学生人数" width="120" align="center">
          <template #default="{ row }">
            {{ row.studentCount || 0 }} 人
          </template>
        </el-table-column>
        <el-table-column label="课程数量" width="120" align="center">
          <template #default="{ row }">
            {{ row.courseCount || 0 }} 门
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="colleges.length === 0 && !loading" description="暂无学院信息" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { School } from '@element-plus/icons-vue'
import { getCollegeList } from '@/api/college'

const colleges = ref([])
const loading = ref(false)

// 统计数据
const totalStudents = computed(() => colleges.value.reduce((sum, c) => sum + (c.studentCount || 0), 0))
const totalMajors = computed(() => colleges.value.reduce((sum, c) => sum + (c.majorCount || 0), 0))
const totalCourses = computed(() => colleges.value.reduce((sum, c) => sum + (c.courseCount || 0), 0))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCollegeList()
    colleges.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.college-view {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  min-height: calc(100vh - 120px);
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  border-left: 4px solid var(--accent);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 36px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--accent) 0%, color-mix(in srgb, var(--accent) 80%, white) 100%);
  border-radius: 12px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.table-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.college-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.college-icon {
  color: #409eff;
  font-size: 18px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  padding: 14px 0;
}

:deep(.el-table td) {
  padding: 12px 0;
}

@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
