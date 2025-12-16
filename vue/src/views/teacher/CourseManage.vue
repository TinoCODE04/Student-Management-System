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
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleAttachment(row)">附件</el-button>
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

    <!-- 附件管理对话框 -->
    <el-dialog
      v-model="attachmentDialogVisible"
      :title="`课程附件管理 - ${currentCourse?.courseName}`"
      width="1100px"
      :close-on-click-modal="false"
      draggable
      destroy-on-close
      class="attachment-dialog"
      v-dialog-resize
    >
      <div class="attachment-content">
        <!-- 上传区域 -->
        <el-upload
          class="upload-demo"
          :action="uploadAction"
          :headers="uploadHeaders"
          :data="uploadData"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :before-upload="beforeUpload"
          :show-file-list="false"
          drag
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 PDF、Word、PPT、音视频文件，单个文件不超过 10MB
            </div>
          </template>
        </el-upload>

        <!-- 附件列表 -->
        <div class="attachment-list">
          <el-table :data="attachmentList" v-loading="attachmentLoading" stripe>
            <el-table-column prop="originalFilename" label="文件名" min-width="180" show-overflow-tooltip />
            <el-table-column prop="fileSize" label="大小" width="100" align="center">
              <template #default="{ row }">
                {{ formatFileSize(row.fileSize) }}
              </template>
            </el-table-column>
            <el-table-column prop="uploaderName" label="上传者" width="100" />
            <el-table-column prop="downloadCount" label="下载次数" width="100" align="center" />
            <el-table-column prop="createTime" label="上传时间" width="160" />
            <el-table-column label="操作" width="240" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handlePreview(row)">
                  <el-icon><View /></el-icon> 预览
                </el-button>
                <el-button type="success" size="small" @click="handleDownload(row)">
                  <el-icon><Download /></el-icon> 下载
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteAttachment(row)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      :title="`预览: ${previewFile?.originalFilename}`"
      width="80%"
      :close-on-click-modal="false"
      draggable
      destroy-on-close
      class="preview-dialog"
      v-dialog-resize
      align-center
    >
      <div class="preview-content">
        <div v-if="previewLoading" class="preview-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <p>加载中...</p>
        </div>
        
        <!-- 图片预览 -->
        <div v-else-if="isImage(previewFile)" class="preview-image">
          <div class="image-controls">
            <el-button-group>
              <el-button :icon="ZoomIn" @click="zoomIn">放大</el-button>
              <el-button :icon="ZoomOut" @click="zoomOut">缩小</el-button>
              <el-button @click="resetZoom">重置</el-button>
              <el-button :icon="FullScreen" @click="fitToScreen">适应窗口</el-button>
            </el-button-group>
            <span style="margin-left: 16px; color: #606266; font-weight: 500;">缩放: {{ imageZoom }}%</span>
            <span style="margin-left: 16px; color: #909399; font-size: 12px;">范围: 5%-500% | 滚轮快速缩放</span>
          </div>
          <div 
            class="image-preview-container" 
            @wheel.prevent="handleWheel"
            ref="imageContainer"
          >
            <img 
              :src="previewFile.fileUrl" 
              :style="{
                transform: `scale(${imageZoom / 100})`,
                transformOrigin: 'center center',
                transition: 'transform 0.3s ease'
              }"
              alt="预览图片"
              @load="onImageLoad"
              draggable="false"
            />
          </div>
        </div>
        
        <!-- PDF预览 -->
        <div v-else-if="isPDF(previewFile)" class="preview-pdf">
          <iframe 
            :src="previewFile.fileUrl" 
            width="100%" 
            height="600px"
            frameborder="0"
          ></iframe>
        </div>
        
        <!-- 视频预览 -->
        <div v-else-if="isVideo(previewFile)" class="preview-video">
          <video 
            :src="previewFile.fileUrl" 
            controls 
            style="width: 100%; max-height: 70vh;"
          >
            您的浏览器不支持视频播放
          </video>
        </div>
        
        <!-- 音频预览 -->
        <div v-else-if="isAudio(previewFile)" class="preview-audio">
          <audio 
            :src="previewFile.fileUrl" 
            controls 
            style="width: 100%;"
          >
            您的浏览器不支持音频播放
          </audio>
        </div>
        
        <!-- 文本文件预览 -->
        <div v-else-if="isText(previewFile)" class="preview-text">
          <pre>{{ previewFileContent }}</pre>
        </div>
        
        <!-- 不支持预览的文件类型 -->
        <div v-else class="preview-unsupported">
          <el-icon :size="80" color="#909399"><Document /></el-icon>
          <p>此文件类型不支持在线预览</p>
          <p class="file-info">文件名: {{ previewFile?.originalFilename }}</p>
          <p class="file-info">文件大小: {{ formatFileSize(previewFile?.fileSize) }}</p>
          <el-button type="primary" @click="handleDownload(previewFile)">
            <el-icon><Download /></el-icon> 下载文件
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <el-button type="success" @click="handleDownload(previewFile)">
          <el-icon><Download /></el-icon> 下载
        </el-button>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Reading, UploadFilled, View, Download, Delete, Document, Loading, ZoomIn, ZoomOut, FullScreen } from '@element-plus/icons-vue'
import { getCoursePage, addCourse, updateCourse, deleteCourse } from '@/api/course'
import { getCollegeList } from '@/api/college'
import { getTeacherList } from '@/api/teacher'
import { getAttachmentList, deleteAttachment, downloadAttachment } from '@/api/attachment'
import vDialogResize from '@/directives/dialogResize'

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

// 附件管理相关
const attachmentDialogVisible = ref(false)
const currentCourse = ref(null)
const attachmentList = ref([])
const attachmentLoading = ref(false)
const uploadAction = computed(() => {
  return `/api/course/attachment/upload`
})
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))
const uploadData = computed(() => ({
  courseId: currentCourse.value?.id
}))

// 预览相关
const previewDialogVisible = ref(false)
const previewFile = ref(null)
const previewLoading = ref(false)
const previewFileContent = ref('')
const imageZoom = ref(100) // 图片缩放比例
const originalImageSize = ref({ width: 0, height: 0 }) // 原始图片尺寸
const imageContainer = ref(null) // 图片容器引用

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

// 附件管理方法
const handleAttachment = async (row) => {
  currentCourse.value = row
  attachmentDialogVisible.value = true
  await loadAttachments()
}

const loadAttachments = async () => {
  if (!currentCourse.value) return
  attachmentLoading.value = true
  try {
    const res = await getAttachmentList(currentCourse.value.id)
    attachmentList.value = res.data || []
  } catch (e) {
    console.error(e)
    ElMessage.error('加载附件列表失败')
  } finally {
    attachmentLoading.value = false
  }
}

const beforeUpload = (file) => {
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 10MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    loadAttachments()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

// 判断文件类型
const isImage = (file) => {
  if (!file) return false
  const imageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp', 'image/webp']
  return imageTypes.includes(file.contentType) || /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(file.originalFilename)
}

const isPDF = (file) => {
  if (!file) return false
  return file.contentType === 'application/pdf' || /\.pdf$/i.test(file.originalFilename)
}

const isVideo = (file) => {
  if (!file) return false
  const videoTypes = ['video/mp4', 'video/webm', 'video/ogg', 'video/avi']
  return videoTypes.includes(file.contentType) || /\.(mp4|webm|ogg|avi|mov)$/i.test(file.originalFilename)
}

const isAudio = (file) => {
  if (!file) return false
  const audioTypes = ['audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/mp3']
  return audioTypes.includes(file.contentType) || /\.(mp3|wav|ogg|m4a)$/i.test(file.originalFilename)
}

const isText = (file) => {
  if (!file) return false
  const textTypes = ['text/plain', 'text/html', 'text/css', 'application/json']
  return textTypes.includes(file.contentType) || /\.(txt|html|css|js|json|xml)$/i.test(file.originalFilename)
}

// 预览文件
const handlePreview = async (row) => {
  previewFile.value = row
  previewDialogVisible.value = true
  previewLoading.value = false
  imageZoom.value = 100 // 重置缩放
  
  // 如果是文本文件，尝试加载内容
  if (isText(row)) {
    previewLoading.value = true
    try {
      const response = await fetch(row.fileUrl)
      previewFileContent.value = await response.text()
    } catch (e) {
      console.error('加载文本内容失败', e)
      previewFileContent.value = '无法加载文件内容'
    } finally {
      previewLoading.value = false
    }
  }
  
  // 记录下载次数
  try {
    await downloadAttachment(row.id)
  } catch (e) {
    console.error(e)
  }
}

// 图片加载完成
const onImageLoad = (e) => {
  originalImageSize.value = {
    width: e.target.naturalWidth,
    height: e.target.naturalHeight
  }
  // 自动适应窗口大小
  fitToScreen()
}

// 放大图片
const zoomIn = () => {
  imageZoom.value = Math.min(imageZoom.value + 25, 500)
}

// 缩小图片
const zoomOut = () => {
  imageZoom.value = Math.max(imageZoom.value - 25, 5)
}

// 重置缩放
const resetZoom = () => {
  imageZoom.value = 100
}

// 适应窗口
const fitToScreen = () => {
  const container = document.querySelector('.image-preview-container')
  if (!container || !originalImageSize.value.width) return
  
  const containerWidth = container.clientWidth - 40 // 减去内边距
  const containerHeight = container.clientHeight - 40
  
  const widthRatio = containerWidth / originalImageSize.value.width
  const heightRatio = containerHeight / originalImageSize.value.height
  
  const ratio = Math.min(widthRatio, heightRatio, 1) // 不超过100%
  imageZoom.value = Math.round(ratio * 100)
}

// 鼠标滚轮缩放
const handleWheel = (e) => {
  const delta = e.deltaY > 0 ? -20 : 20
  imageZoom.value = Math.max(5, Math.min(500, imageZoom.value + delta))
}

// 下载文件
const handleDownload = async (row) => {
  if (!row) return
  
  try {
    ElMessage.info('正在下载...')
    
    // 使用 fetch 下载文件，避免打开新窗口
    const response = await fetch(row.fileUrl)
    const blob = await response.blob()
    
    // 创建 blob URL
    const blobUrl = window.URL.createObjectURL(blob)
    
    // 创建下载链接
    const link = document.createElement('a')
    link.href = blobUrl
    link.download = row.originalFilename
    link.style.display = 'none'
    
    // 触发下载
    document.body.appendChild(link)
    link.click()
    
    // 清理
    setTimeout(() => {
      document.body.removeChild(link)
      window.URL.revokeObjectURL(blobUrl)
    }, 100)
    
    // 记录下载次数
    await downloadAttachment(row.id)
    
    ElMessage.success('下载成功')
    
    // 刷新列表更新下载次数
    loadAttachments()
  } catch (e) {
    console.error('下载失败:', e)
    ElMessage.error('下载失败，请重试')
  }
}

const handleDeleteAttachment = (row) => {
  ElMessageBox.confirm(`确定要删除附件 "${row.originalFilename}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAttachment(row.id)
      ElMessage.success('删除成功')
      loadAttachments()
    } catch (e) {
      console.error(e)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
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

.attachment-content {
  max-height: 600px;
  overflow-y: auto;
}

.upload-demo {
  margin-bottom: 20px;
}

.attachment-list {
  margin-top: 20px;
}

.el-icon--upload {
  font-size: 67px;
  color: #409eff;
}

/* 附件对话框样式 */
:deep(.attachment-dialog) {
  min-width: 700px;
  max-width: 95vw;
}

:deep(.attachment-dialog .el-dialog__body) {
  padding: 10px 20px;
}

/* 预览对话框样式 */
:deep(.preview-dialog) {
  min-width: 600px;
  max-width: 95vw;
}

:deep(.preview-dialog .el-dialog__body) {
  padding: 20px;
  max-height: 75vh;
  overflow-y: auto;
}

.preview-content {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-loading {
  text-align: center;
  color: #909399;
}

.preview-loading .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.preview-loading p {
  font-size: 16px;
  margin: 0;
}

.preview-image,
.preview-pdf,
.preview-video,
.preview-audio {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.image-controls {
  width: 100%;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview-container {
  width: 100%;
  height: calc(70vh - 100px);
  overflow: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.image-preview-container img {
  display: block;
  cursor: move;
}

.preview-text {
  width: 100%;
  background: #f5f7fa;
  border-radius: 4px;
  padding: 16px;
  max-height: 600px;
  overflow-y: auto;
}

.preview-text pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
}

.preview-unsupported {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.preview-unsupported .el-icon {
  margin-bottom: 20px;
}

.preview-unsupported p {
  margin: 10px 0;
  font-size: 16px;
}

.preview-unsupported .file-info {
  font-size: 14px;
  color: #606266;
}

.preview-unsupported .el-button {
  margin-top: 20px;
}

/* PDF iframe样式 */
.preview-pdf iframe {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

/* 对话框调整大小样式 */
:deep(.el-dialog) {
  position: relative;
  min-width: 600px;
  min-height: 400px;
}

:deep(.dialog-resizer) {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 20px;
  height: 20px;
  cursor: nwse-resize;
  z-index: 1000;
  background: linear-gradient(135deg, transparent 50%, #409eff 50%);
  opacity: 0.6;
  transition: opacity 0.3s;
}

:deep(.dialog-resizer:hover) {
  opacity: 1;
}

/* 边缘拖拽区域悬停效果 */
:deep(.el-dialog [class*="dialog-edge-"]) {
  transition: background-color 0.2s;
}

:deep(.el-dialog [class*="dialog-edge-"]:hover) {
  background-color: rgba(64, 158, 255, 0.1);
}

</style>
