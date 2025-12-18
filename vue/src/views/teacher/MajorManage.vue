<template>
  <div class="major-manage">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" style="--accent: #409eff;">
        <div class="stat-icon">📖</div>
        <div class="stat-info">
          <div class="stat-value">{{ tableData.length }}</div>
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
          <div class="stat-value">{{ collegeList.length }}</div>
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
          <div class="header-actions">
            <el-select v-model="selectedCollegeId" placeholder="按学院筛选" clearable style="width: 200px;">
              <el-option label="全部学院" :value="null" />
              <el-option v-for="c in collegeList" :key="c.id" :label="c.collegeName" :value="c.id" />
            </el-select>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredData" v-loading="loading" stripe border
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
            {{ row.college?.collegeName || getCollegeName(row.collegeId) || '-' }}
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Reading } from '@element-plus/icons-vue'
import { getMajorList } from '@/api/major'
import { getCollegeList } from '@/api/college'

const tableData = ref([])
const collegeList = ref([])
const selectedCollegeId = ref(null)
const loading = ref(false)

// 统计数据
const totalStudents = computed(() => tableData.value.reduce((sum, m) => sum + (m.studentCount || 0), 0))
const totalTeachers = computed(() => tableData.value.reduce((sum, m) => sum + (m.teacherCount || 0), 0))

const filteredData = computed(() => {
  if (!selectedCollegeId.value) return tableData.value
  return tableData.value.filter(m => m.collegeId === selectedCollegeId.value)
})

const getCollegeName = (id) => collegeList.value.find(c => c.id === id)?.collegeName || '-'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMajorList()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const loadColleges = async () => {
  const res = await getCollegeList()
  collegeList.value = res.data || []
}

onMounted(() => { loadData(); loadColleges() })
</script>

<style scoped>
.major-manage { 
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
  color: white;
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

.header-actions {
  display: flex;
  align-items: center;
}

.major-name {
  display: flex;
  align-items: center;
  gap: 8px;
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
</style>
