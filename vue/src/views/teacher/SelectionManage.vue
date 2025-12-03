<template>
  <div class="selection-manage">
    <el-card>
      <template #header>
        <span>选课管理 - 查看课程选课学生</span>
      </template>
      
      <!-- 课程选择 -->
      <div class="course-select">
        <el-select v-model="selectedCourseId" placeholder="请选择课程" @change="loadSelections">
          <el-option
            v-for="course in myCourses"
            :key="course.id"
            :label="`${course.courseName} (${course.selectedCount}/${course.maxStudents}人)`"
            :value="course.id"
          />
        </el-select>
      </div>
      
      <!-- 选课学生列表 -->
      <el-table :data="selections" v-loading="loading" stripe border v-if="selectedCourseId">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="student.studentNo" label="学号" width="120" />
        <el-table-column prop="student.name" label="姓名" width="100" />
        <el-table-column prop="student.className" label="班级" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="{ row }">
            {{ row.score ?? '未录入' }}
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="选课时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleScore(row)">
              录入成绩
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-else description="请选择课程查看选课学生" />
    </el-card>
    
    <!-- 录入成绩对话框 -->
    <el-dialog v-model="scoreDialogVisible" title="录入成绩" width="400px">
      <el-form :model="scoreForm" label-width="80px">
        <el-form-item label="学生">
          {{ currentStudent?.student?.name }}
        </el-form-item>
        <el-form-item label="成绩">
          <el-input-number v-model="scoreForm.score" :min="0" :max="100" :precision="1" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
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

const loadMyCourses = async () => {
  try {
    const res = await getTeacherCourses()
    myCourses.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadSelections = async () => {
  if (!selectedCourseId.value) return
  
  loading.value = true
  try {
    const res = await getCourseSelections(selectedCourseId.value)
    selections.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = {
    'selected': 'info',
    'completed': 'success',
    'dropped': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'selected': '已选',
    'completed': '已完成',
    'dropped': '已退选'
  }
  return map[status] || status
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
      courseId: selectedCourseId.value,
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

onMounted(() => {
  loadMyCourses()
})
</script>

<style scoped>
.selection-manage {
  padding: 10px;
}

.course-select {
  margin-bottom: 20px;
}

.course-select .el-select {
  width: 400px;
}
</style>
