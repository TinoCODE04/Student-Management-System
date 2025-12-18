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
            学院信息
          </span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增学院
          </el-button>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm 
              title="确定删除该学院吗？删除后相关专业、学生、课程数据将受影响！" 
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
        label-width="120px"
      >
        <el-form-item label="学院名称" prop="collegeName">
          <el-input v-model="form.collegeName" placeholder="请输入学院名称" />
        </el-form-item>
        <el-form-item label="毕业最低学分" prop="minCredit">
          <el-input-number 
            v-model="form.minCredit" 
            :min="0" 
            :max="300" 
            :step="5"
            placeholder="请输入最低学分"
          />
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
import { Plus, School } from '@element-plus/icons-vue'
import { getCollegeList, addCollege, updateCollege, deleteCollege } from '@/api/college'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

// 统计数据
const totalStudents = computed(() => tableData.value.reduce((sum, c) => sum + (c.studentCount || 0), 0))
const totalMajors = computed(() => tableData.value.reduce((sum, c) => sum + (c.majorCount || 0), 0))
const totalCourses = computed(() => tableData.value.reduce((sum, c) => sum + (c.courseCount || 0), 0))

const form = reactive({
  id: null,
  collegeName: '',
  minCredit: 140
})

const formRules = {
  collegeName: [
    { required: true, message: '请输入学院名称', trigger: 'blur' },
    { min: 2, max: 100, message: '学院名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  minCredit: [
    { required: true, message: '请输入最低学分', trigger: 'blur' },
    { type: 'number', min: 0, max: 300, message: '学分应在 0 到 300 之间', trigger: 'blur' }
  ]
}

// 获取数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getCollegeList()
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
  dialogTitle.value = '新增学院'
  Object.assign(form, {
    id: null,
    collegeName: '',
    minCredit: 140
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑学院'
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
          await updateCollege(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await addCollege(form)
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
    await deleteCollege(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  loadData()
})
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
