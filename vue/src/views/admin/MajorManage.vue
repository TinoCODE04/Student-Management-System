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
            <el-select 
              v-model="searchCollegeId" 
              placeholder="按学院筛选" 
              clearable 
              style="width: 200px; margin-right: 10px;"
              @change="loadData"
            >
              <el-option 
                v-for="college in collegeList" 
                :key="college.id" 
                :label="college.collegeName" 
                :value="college.id" 
              />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增专业
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border
                :header-cell-style="{ background: '#CCCCFF', color: '#606266', fontWeight: 'bold' }">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="majorName" label="专业名称" min-width="150" />
        <el-table-column prop="departmentName" label="所属系" width="150" />
        <el-table-column prop="collegeName" label="所属学院" width="180">
          <template #default="{ row }">
            {{ getCollegeName(row.collegeId) }}
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm 
              title="确定删除该专业吗？删除后相关学生、教师数据将受影响！" 
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="form.majorName" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="所属系" prop="departmentName">
          <el-input v-model="form.departmentName" placeholder="请输入所属系名称" />
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeId">
          <el-select v-model="form.collegeId" placeholder="请选择所属学院" style="width: 100%;">
            <el-option 
              v-for="college in collegeList" 
              :key="college.id" 
              :label="college.collegeName" 
              :value="college.id" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Reading } from '@element-plus/icons-vue'
import { getMajorList, addMajor, updateMajor, deleteMajor } from '@/api/major'
import { getCollegeList } from '@/api/college'

const tableData = ref([])
const collegeList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const searchCollegeId = ref(null)

// 统计数据
const totalStudents = computed(() => tableData.value.reduce((sum, m) => sum + (m.studentCount || 0), 0))
const totalTeachers = computed(() => tableData.value.reduce((sum, m) => sum + (m.teacherCount || 0), 0))

const form = reactive({
  id: null,
  majorName: '',
  departmentName: '',
  collegeId: null
})

const formRules = {
  majorName: [
    { required: true, message: '请输入专业名称', trigger: 'blur' },
    { min: 2, max: 100, message: '专业名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  departmentName: [
    { min: 2, max: 100, message: '系名长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  collegeId: [
    { required: true, message: '请选择所属学院', trigger: 'change' }
  ]
}

// 获取学院名称
const getCollegeName = (collegeId) => {
  const college = collegeList.value.find(c => c.id === collegeId)
  return college ? college.collegeName : '-'
}

// 加载学院列表
const loadColleges = async () => {
  try {
    const res = await getCollegeList()
    collegeList.value = res.data || []
  } catch (error) {
    console.error('获取学院列表失败', error)
  }
}

// 获取数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchCollegeId.value) {
      params.collegeId = searchCollegeId.value
    }
    const res = await getMajorList(params)
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增专业'
  Object.assign(form, {
    id: null,
    majorName: '',
    departmentName: '',
    collegeId: null
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑专业'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateMajor(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await addMajor(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 删除
const handleDelete = async (id) => {
  try {
    await deleteMajor(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

onMounted(async () => {
  await loadColleges()
  loadData()
})
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
  gap: 10px;
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
