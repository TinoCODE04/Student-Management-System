<template>
  <div class="college-manage">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" style="--accent: #409eff;">
        <div class="stat-icon">🏫</div>
        <div class="stat-info">
          <div class="stat-value">{{ tableData.length }}</div>
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
            学院管理
          </span>
          <div class="header-actions">
            <el-select v-model="selectedCollegeId" placeholder="按学院筛选" clearable style="width: 200px; margin-right: 12px;">
              <el-option label="全部学院" :value="null" />
              <el-option v-for="c in tableData" :key="c.id" :label="c.collegeName" :value="c.id" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增学院
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredData" v-loading="loading" stripe border 
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
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学院' : '新增学院'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="学院名称" prop="collegeName">
          <el-input v-model="form.collegeName" placeholder="请输入学院名称" />
        </el-form-item>
        <el-form-item label="最低学分" prop="minCredit">
          <el-input-number v-model="form.minCredit" :min="0" :max="300" style="width: 100%;" />
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
import { Plus, Edit, Delete, School, OfficeBuilding } from '@element-plus/icons-vue'
import { getCollegeList, addCollege, updateCollege, deleteCollege } from '@/api/college'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const submitLoading = ref(false)
const selectedCollegeId = ref(null)

// 统计数据
const totalStudents = computed(() => tableData.value.reduce((sum, c) => sum + (c.studentCount || 0), 0))
const totalMajors = computed(() => tableData.value.reduce((sum, c) => sum + (c.majorCount || 0), 0))
const totalCourses = computed(() => tableData.value.reduce((sum, c) => sum + (c.courseCount || 0), 0))

// 筛选数据
const filteredData = computed(() => {
  if (!selectedCollegeId.value) return tableData.value
  return tableData.value.filter(c => c.id === selectedCollegeId.value)
})

const form = reactive({
  id: null,
  collegeName: '',
  minCredit: 150
})

const rules = {
  collegeName: [{ required: true, message: '请输入学院名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCollegeList()
    tableData.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.collegeName = ''
  form.minCredit = 150
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学院 "${row.collegeName}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteCollege(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateCollege(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await addCollege(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.college-manage { 
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

.college-name {
  display: flex;
  align-items: center;
  gap: 8px;
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
</style>
