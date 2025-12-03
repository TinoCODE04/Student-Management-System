<template>
  <div class="major-view">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" style="--accent: #409eff;">
        <div class="stat-icon">📖</div>
        <div class="stat-info">
          <div class="stat-value">{{ majors.length }}</div>
          <div class="stat-label">专业总数</div>
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
        <div class="stat-icon">👨‍🏫</div>
        <div class="stat-info">
          <div class="stat-value">{{ totalTeachers }}</div>
          <div class="stat-label">教师总数</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #f56c6c;">
        <div class="stat-icon">🏫</div>
        <div class="stat-info">
          <div class="stat-value">{{ colleges.length }}</div>
          <div class="stat-label">包含学院</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Reading /></el-icon>
            专业信息
          </span>
          <el-select v-model="collegeId" placeholder="按学院筛选" @change="filterData" style="width: 200px">
            <el-option label="全部学院" :value="null" />
            <el-option
              v-for="c in colleges"
              :key="c.id"
              :label="c.collegeName"
              :value="c.id"
            />
          </el-select>
        </div>
      </template>
      
      <el-table :data="filteredMajors" v-loading="loading" stripe border
                :header-cell-style="{ background: '#CCCCFF', color: '#606266', fontWeight: 'bold' }">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="majorName" label="专业名称" min-width="160" />
        <el-table-column prop="departmentName" label="所属系" min-width="140">
          <template #default="{ row }">
            {{ row.departmentName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="所属学院" min-width="160">
          <template #default="{ row }">
            {{ row.college?.collegeName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="学生人数" width="120" align="center">
          <template #default="{ row }">
            {{ row.studentCount || 0 }} 人
          </template>
        </el-table-column>
        <el-table-column label="教师人数" width="120" align="center">
          <template #default="{ row }">
            {{ row.teacherCount || 0 }} 人
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="filteredMajors.length === 0 && !loading" description="暂无专业信息" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Reading } from '@element-plus/icons-vue'
import { getMajorList } from '@/api/major'
import { getCollegeList } from '@/api/college'

const majors = ref([])
const colleges = ref([])
const collegeId = ref('')
const loading = ref(false)

// 统计数据
const totalStudents = computed(() => majors.value.reduce((sum, m) => sum + (m.studentCount || 0), 0))
const totalTeachers = computed(() => majors.value.reduce((sum, m) => sum + (m.teacherCount || 0), 0))

// 根据学院筛选
const filteredMajors = computed(() => {
  if (!collegeId.value) return majors.value
  return majors.value.filter(m => m.collegeId === collegeId.value)
})

const filterData = () => {
  // 筛选已在 computed 中完成
}

const loadColleges = async () => {
  try {
    const res = await getCollegeList()
    colleges.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMajorList()
    majors.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadColleges()
  loadData()
})
</script>

<style scoped>
.major-view {
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
  flex-wrap: wrap;
  gap: 12px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.major-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.major-icon {
  color: #409eff;
  font-size: 18px;
}

.college-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.college-icon {
  color: #e6a23c;
  font-size: 16px;
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
