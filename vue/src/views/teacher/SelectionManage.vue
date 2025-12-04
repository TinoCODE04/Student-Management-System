<template>
  <div class="selection-manage">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" style="--accent: #409eff;">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <div class="stat-value">{{ myCourses.length }}</div>
          <div class="stat-label">我的课程</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #67c23a;">
        <div class="stat-icon">👨‍🎓</div>
        <div class="stat-info">
          <div class="stat-value">{{ selections.length }}</div>
          <div class="stat-label">选课学生</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #e6a23c;">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-value">{{ scoredCount }}</div>
          <div class="stat-label">已录成绩</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #f56c6c;">
        <div class="stat-icon">⏳</div>
        <div class="stat-info">
          <div class="stat-value">{{ unscoredCount }}</div>
          <div class="stat-label">待录成绩</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><List /></el-icon>
            选课管理
          </span>
          <div class="header-actions">
            <el-select v-model="selectedCourseId" placeholder="请选择我的课程" style="width: 300px;" @change="loadSelections">
              <el-option label="全部课程" :value="null" />
              <el-option
                v-for="course in myCourses"
                :key="course.id"
                :label="`${course.courseName} (${course.selectedCount || 0}/${course.maxStudents}人)`"
                :value="course.id"
              />
            </el-select>
          </div>
        </div>
      </template>
      
      <!-- 选课学生列表 -->
      <el-table :data="selections" v-loading="loading" stripe border
                :header-cell-style="{ background: '#CCCCFF', color: '#606266', fontWeight: 'bold' }">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="student.studentNo" label="学号" width="120" />
        <el-table-column prop="student.name" label="姓名" width="100" />
        <el-table-column prop="student.className" label="班级" width="120" />
        <el-table-column label="课程" min-width="140">
          <template #default="{ row }">
            {{ row.course?.courseName || getCourseName(row.courseId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            {{ getStatusText(row.status) }}
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="{ row }">
            {{ row.score !== null && row.score !== undefined ? row.score : '未录入' }}
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="选课时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.selectTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleScore(row)">录入成绩</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="selections.length === 0 && !loading" description="暂无选课学生数据" />
    </el-card>
    
    <!-- 录入成绩对话框 -->
    <el-dialog v-model="scoreDialogVisible" title="录入成绩" width="400px" :close-on-click-modal="false">
      <el-form :model="scoreForm" label-width="80px">
        <el-form-item label="学生">
          <el-input :value="currentStudent?.student?.name" disabled />
        </el-form-item>
        <el-form-item label="课程">
          <el-input :value="currentStudent?.course?.courseName || getCourseName(currentStudent?.courseId)" disabled />
        </el-form-item>
        <el-form-item label="成绩">
          <el-input-number v-model="scoreForm.score" :min="0" :max="100" :precision="1" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitScore">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { List } from '@element-plus/icons-vue'
import { getTeacherCourses } from '@/api/course'
import { getCourseSelections, updateScore } from '@/api/selection'

const myCourses = ref([])
const selectedCourseId = ref(null)
const selections = ref([])
const loading = ref(false)

const scoreDialogVisible = ref(false)
const currentStudent = ref(null)
const submitLoading = ref(false)
const scoreForm = reactive({
  score: 0
})

// 统计数据
const scoredCount = computed(() => selections.value.filter(s => s.score !== null && s.score !== undefined).length)
const unscoredCount = computed(() => selections.value.filter(s => s.score === null || s.score === undefined).length)

const loadMyCourses = async () => {
  try {
    const res = await getTeacherCourses()
    myCourses.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadSelections = async () => {
  loading.value = true
  try {
    if (selectedCourseId.value) {
      const res = await getCourseSelections(selectedCourseId.value)
      selections.value = res.data || []
    } else {
      // 加载所有课程的选课学生
      let allSelections = []
      for (const course of myCourses.value) {
        const res = await getCourseSelections(course.id)
        if (res.data) {
          allSelections = allSelections.concat(res.data)
        }
      }
      selections.value = allSelections
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getCourseName = (courseId) => {
  const course = myCourses.value.find(c => c.id === courseId)
  return course?.courseName || '-'
}

const getStatusText = (status) => {
  const map = {
    'pending': '待上课',
    'selected': '已选',
    'studying': '学习中',
    'completed': '已完成',
    'dropped': '已退选'
  }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const handleScore = (row) => {
  currentStudent.value = row
  scoreForm.score = row.score || 0
  scoreDialogVisible.value = true
}

const submitScore = async () => {
  submitLoading.value = true
  try {
    await updateScore({
      studentId: currentStudent.value.studentId,
      courseId: currentStudent.value.courseId || selectedCourseId.value,
      score: scoreForm.score
    })
    ElMessage.success('成绩录入成功')
    scoreDialogVisible.value = false
    loadSelections()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await loadMyCourses()
  loadSelections()
})
</script>

<style scoped>
.selection-manage {
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

.header-actions {
  display: flex;
  align-items: center;
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
