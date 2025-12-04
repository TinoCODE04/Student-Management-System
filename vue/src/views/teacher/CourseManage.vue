<template>
  <div class="course-manage">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" style="--accent: #409eff;">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">课程总数</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #67c23a;">
        <div class="stat-icon">👨‍🎓</div>
        <div class="stat-info">
          <div class="stat-value">{{ totalSelectedCount }}</div>
          <div class="stat-label">选课总人次</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #e6a23c;">
        <div class="stat-icon">⭐</div>
        <div class="stat-info">
          <div class="stat-value">{{ totalCredits }}</div>
          <div class="stat-label">总学分</div>
        </div>
      </div>
      <div class="stat-card" style="--accent: #f56c6c;">
        <div class="stat-icon">🏫</div>
        <div class="stat-info">
          <div class="stat-value">{{ collegeList.length }}</div>
          <div class="stat-label">开课学院</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Reading /></el-icon>
            课程管理
          </span>
          <div class="header-actions">
            <el-input 
              v-model="queryForm.courseName" 
              placeholder="搜索课程名称" 
              clearable 
              style="width: 200px;"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="queryForm.collegeId" placeholder="全部学院" clearable style="width: 160px;" @change="handleSearch">
              <el-option label="全部学院" :value="null" />
              <el-option v-for="c in collegeList" :key="c.id" :label="c.collegeName" :value="c.id" />
            </el-select>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="success" @click="handleAdd">新增课程</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" stripe border
                :header-cell-style="{ background: '#CCCCFF', color: '#606266', fontWeight: 'bold' }">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="credit" label="学分" width="80" align="center">
          <template #default="{ row }">
            {{ row.credit }} 分
          </template>
        </el-table-column>
        <el-table-column prop="schedule" label="上课时段" width="140" />
        <el-table-column prop="location" label="上课地点" width="120" />
        <el-table-column label="选课情况" width="120" align="center">
          <template #default="{ row }">
            {{ row.selectedCount || 0 }} / {{ row.maxStudents }}
          </template>
        </el-table-column>
        <el-table-column label="授课教师" width="100">
          <template #default="{ row }">
            {{ row.teacher?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="课程描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学分" prop="credit">
              <el-input-number v-model="form.credit" :min="1" :max="10" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大人数" prop="maxStudents">
              <el-input-number v-model="form.maxStudents" :min="1" :max="500" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上课时段" prop="schedule">
              <el-input v-model="form.schedule" placeholder="如：周一 1-2节" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课地点" prop="location">
              <el-input v-model="form.location" placeholder="请输入上课地点" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学院" prop="collegeId">
              <el-select v-model="form.collegeId" placeholder="请选择学院" style="width: 100%;">
                <el-option v-for="c in collegeList" :key="c.id" :label="c.collegeName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授课教师" prop="teacherId">
              <el-select v-model="form.teacherId" placeholder="请选择教师" style="width: 100%;">
                <el-option v-for="t in teacherList" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Reading } from '@element-plus/icons-vue'
import { getCoursePage, addCourse, updateCourse, deleteCourse } from '@/api/course'
import { getCollegeList } from '@/api/college'
import { getTeacherList } from '@/api/teacher'

const queryForm = reactive({
  courseName: '',
  collegeId: null,
  pageNum: 1,
  pageSize: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const collegeList = ref([])
const teacherList = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const submitLoading = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑课程' : '新增课程')

// 统计数据
const totalSelectedCount = computed(() => tableData.value.reduce((sum, c) => sum + (c.selectedCount || 0), 0))
const totalCredits = computed(() => tableData.value.reduce((sum, c) => sum + (c.credit || 0), 0))

const form = reactive({
  id: null,
  courseName: '',
  credit: 3,
  schedule: '',
  location: '',
  maxStudents: 100,
  collegeId: null,
  teacherId: null,
  description: ''
})

const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCoursePage(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadColleges = async () => {
  try {
    const res = await getCollegeList()
    collegeList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadTeachers = async () => {
  try {
    const res = await getTeacherList()
    teacherList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => {
  queryForm.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryForm.courseName = ''
  queryForm.collegeId = null
  queryForm.pageNum = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除课程 "${row.courseName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCourse(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateCourse(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await addCourse(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      console.error(e)
    } finally {
      submitLoading.value = false
    }
  })
}

const resetForm = () => {
  form.id = null
  form.courseName = ''
  form.credit = 3
  form.schedule = ''
  form.location = ''
  form.maxStudents = 100
  form.collegeId = null
  form.teacherId = null
  form.description = ''
}

onMounted(() => {
  loadData()
  loadColleges()
  loadTeachers()
})
</script>

<style scoped>
.course-manage {
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
  flex-wrap: wrap;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
