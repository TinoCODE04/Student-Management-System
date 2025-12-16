<template>
  <div class="student-manage">
    <el-card>
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="姓名">
            <el-input v-model="queryForm.name" placeholder="请输入姓名" clearable />
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="queryForm.studentNo" placeholder="请输入学号" clearable />
          </el-form-item>
          <el-form-item label="班级">
            <el-input v-model="queryForm.className" placeholder="请输入班级" clearable />
          </el-form-item>
          <el-form-item label="学院">
            <el-select v-model="queryForm.collegeId" placeholder="请选择学院" clearable>
              <el-option
                v-for="college in collegeList"
                :key="college.id"
                :label="college.collegeName"
                :value="college.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="学号排序">
            <el-select v-model="sortOrder" placeholder="请选择排序" clearable @change="handleSortOrderChange" style="width: 150px">
              <el-option label="从小到大" value="asc" />
              <el-option label="从大到小" value="desc" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增学生
        </el-button>
      </div>
      
      <!-- 表格区域 -->
      <el-table :data="tableData" v-loading="loading" stripe border @sort-change="handleSortChange">
        <el-table-column prop="studentNo" label="学号" width="120" sortable="custom" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="totalCredit" label="学分" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
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
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="1" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院" prop="collegeId">
              <el-select v-model="form.collegeId" placeholder="请选择学院">
                <el-option
                  v-for="college in collegeList"
                  :key="college.id"
                  :label="college.collegeName"
                  :value="college.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总学分" prop="totalCredit">
              <el-input-number v-model="form.totalCredit" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="!isEdit">
          <el-col :span="24">
            <el-form-item label="初始密码">
              <el-input value="123456" disabled />
              <span class="form-tip">新增学生默认密码为 123456</span>
            </el-form-item>
          </el-col>
        </el-row>
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getStudentPage, addStudent, updateStudent, deleteStudent } from '@/api/student'
import { getCollegeList } from '@/api/college'

// 查询表单
const queryForm = reactive({
  name: '',
  studentNo: '',
  className: '',
  collegeId: null,
  pageNum: 1,
  pageSize: 10,
  orderBy: '',
  orderType: ''
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 学院列表
const collegeList = ref([])

// 排序选择
const sortOrder = ref('')

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const submitLoading = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑学生' : '新增学生')

// 表单数据
const form = reactive({
  id: null,
  username: '',
  studentNo: '',
  name: '',
  gender: '男',
  age: 20,
  className: '',
  phone: '',
  email: '',
  collegeId: null,
  totalCredit: 0
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  collegeId: [
    { required: true, message: '请选择学院', trigger: 'change' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 加载学院列表
const loadColleges = async () => {
  try {
    const res = await getCollegeList()
    collegeList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

// 查询
const handleSearch = () => {
  queryForm.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryForm.name = ''
  queryForm.studentNo = ''
  queryForm.className = ''
  queryForm.collegeId = null
  queryForm.pageNum = 1
  queryForm.orderBy = ''
  queryForm.orderType = ''
  sortOrder.value = ''
  loadData()
}

// 排序选择改变
const handleSortOrderChange = (value) => {
  if (value) {
    queryForm.orderBy = 'studentNo'
    queryForm.orderType = value
  } else {
    queryForm.orderBy = ''
    queryForm.orderType = ''
  }
  queryForm.pageNum = 1
  loadData()
}

// 排序处理（表头点击）
const handleSortChange = ({ prop, order }) => {
  if (order) {
    queryForm.orderBy = prop
    queryForm.orderType = order === 'ascending' ? 'asc' : 'desc'
    sortOrder.value = queryForm.orderType
  } else {
    queryForm.orderBy = ''
    queryForm.orderType = ''
    sortOrder.value = ''
  }
  queryForm.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    username: row.username,
    studentNo: row.studentNo,
    name: row.name,
    gender: row.gender,
    age: row.age,
    className: row.className,
    phone: row.phone,
    email: row.email,
    collegeId: row.collegeId,
    totalCredit: row.totalCredit
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学生 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStudent(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateStudent(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await addStudent(form)
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

// 重置表单
const resetForm = () => {
  form.id = null
  form.username = ''
  form.studentNo = ''
  form.name = ''
  form.gender = '男'
  form.age = 20
  form.className = ''
  form.phone = ''
  form.email = ''
  form.collegeId = null
  form.totalCredit = 0
}

onMounted(() => {
  loadData()
  loadColleges()
})
</script>

<style scoped>
.student-manage {
  padding: 10px;
}

.search-area {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.search-form {
  flex: 1;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}
</style>
