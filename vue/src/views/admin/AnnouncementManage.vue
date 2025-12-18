<template>
  <div class="announcement-container">
    <el-card class="header-card">
      <div class="header">
        <div class="title">
          <el-icon><Bell /></el-icon>
          <span>系统公告管理</span>
        </div>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
      </div>
    </el-card>

    <el-card class="table-card">
      <el-table :data="announcements" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="公告标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="content" label="公告内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="publisherName" label="发布人" width="100" align="center" />
        <el-table-column prop="targetRole" label="目标角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.targetRole === 'all'" type="success" size="small">全体</el-tag>
            <el-tag v-else-if="row.targetRole === 'teacher'" type="primary" size="small">教师</el-tag>
            <el-tag v-else-if="row.targetRole === 'student'" type="warning" size="small">学生</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.priority === 'high'" type="danger" size="small">高</el-tag>
            <el-tag v-else-if="row.priority === 'medium'" type="warning" size="small">中</el-tag>
            <el-tag v-else type="info" size="small">低</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewAnnouncement(row)">
              查看
            </el-button>
            <el-button link type="warning" size="small" @click="editAnnouncement(row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadAnnouncements"
        @current-change="loadAnnouncements"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请输入公告内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="目标角色" prop="targetRole">
          <el-radio-group v-model="form.targetRole">
            <el-radio value="all">全体用户</el-radio>
            <el-radio value="teacher">仅教师</el-radio>
            <el-radio value="student">仅学生</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio value="high">高</el-radio>
            <el-radio value="medium">中</el-radio>
            <el-radio value="low">低</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">
          {{ isEdit ? '更新' : '发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看公告详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="公告标题">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="公告内容">
          <div style="white-space: pre-wrap;">{{ viewData.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ viewData.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="目标角色">
          <el-tag v-if="viewData.targetRole === 'all'" type="success">全体用户</el-tag>
          <el-tag v-else-if="viewData.targetRole === 'teacher'" type="primary">仅教师</el-tag>
          <el-tag v-else-if="viewData.targetRole === 'student'" type="warning">仅学生</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag v-if="viewData.priority === 'high'" type="danger">高</el-tag>
          <el-tag v-else-if="viewData.priority === 'medium'" type="warning">中</el-tag>
          <el-tag v-else type="info">低</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Plus } from '@element-plus/icons-vue'
import { getAnnouncements, publishAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'

const loading = ref(false)
const submitting = ref(false)
const announcements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = ref('发布公告')
const formRef = ref(null)

const form = reactive({
  id: null,
  title: '',
  content: '',
  targetRole: 'all',
  priority: 'medium'
})

const viewData = reactive({
  title: '',
  content: '',
  targetRole: '',
  priority: '',
  publisherName: '',
  createTime: ''
})

const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 5, max: 500, message: '内容长度在 5 到 500 个字符', trigger: 'blur' }
  ],
  targetRole: [
    { required: true, message: '请选择目标角色', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ]
}

// 加载公告列表
const loadAnnouncements = async () => {
  loading.value = true
  try {
    const res = await getAnnouncements({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.code === 200) {
      announcements.value = res.data.records || res.data
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '发布公告'
  dialogVisible.value = true
}

// 查看公告
const viewAnnouncement = (row) => {
  Object.assign(viewData, row)
  viewDialogVisible.value = true
}

// 编辑公告
const editAnnouncement = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑公告'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除公告
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除公告"${row.title}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteAnnouncement(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadAnnouncements()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const apiFunc = isEdit.value ? updateAnnouncement : publishAnnouncement
      const res = await apiFunc(form)
      
      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
        dialogVisible.value = false
        loadAnnouncements()
      }
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '发布失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.title = ''
  form.content = ''
  form.targetRole = 'all'
  form.priority = 'medium'
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcement-container {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
}
</style>
