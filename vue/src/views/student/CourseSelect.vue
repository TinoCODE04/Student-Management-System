<template>
  <div class="course-select">
    <el-card class="select-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon :size="22" color="#fff"><Reading /></el-icon>
            <span class="title">选课中心</span>
          </div>
          <div class="header-right">
            <el-tag type="info" effect="plain" size="large">
              共 {{ courses.length }} 门课程可选
            </el-tag>
          </div>
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="search-area">
        <el-input
          v-model="searchText"
          placeholder="搜索课程名称"
          :prefix-icon="Search"
          clearable
          style="width: 300px"
        />
        <el-select v-model="collegeFilter" placeholder="按学院筛选" clearable style="width: 200px; margin-left: 15px">
          <el-option label="全部学院" value="" />
          <el-option v-for="c in colleges" :key="c.id" :label="c.collegeName" :value="c.id" />
        </el-select>
        <el-radio-group v-model="selectStatusFilter" style="margin-left: 15px">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="selected">已选</el-radio-button>
          <el-radio-button label="unselected">未选</el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 课程列表 -->
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="8" v-for="course in filteredCourses" :key="course.id">
          <el-card class="course-card" shadow="hover" :class="{ 'selected-card': isSelected(course.id) }">
            <div class="course-header">
              <h3>{{ course.courseName }}</h3>
              <el-tag :type="getStatusType(course)" size="small" effect="dark">
                {{ getStatusText(course) }}
              </el-tag>
            </div>
            <div class="course-info">
              <p><el-icon><Star /></el-icon> <span class="label">学分：</span><el-tag type="warning" size="small" round>{{ course.credit }}</el-tag></p>
              <p><el-icon><Avatar /></el-icon> <span class="label">教师：</span><span class="value">{{ course.teacher?.name || '待定' }}</span></p>
              <p><el-icon><Clock /></el-icon> <span class="label">时段：</span><span class="value">{{ course.schedule || '待定' }}</span></p>
              <p><el-icon><Location /></el-icon> <span class="label">地点：</span><span class="value">{{ course.location || '待定' }}</span></p>
              <p><el-icon><User /></el-icon> <span class="label">人数：</span>
                <span :class="{ 'full': course.selectedCount >= course.maxStudents }">
                  {{ course.selectedCount }} / {{ course.maxStudents }}
                </span>
              </p>
            </div>
            <div class="course-desc">
              {{ course.description || '暂无课程描述' }}
            </div>
            <div class="course-action">
              <el-button
                v-if="!isSelected(course.id)"
                type="primary"
                :disabled="course.selectedCount >= course.maxStudents"
                @click="handleSelect(course)"
                round
              >
                <el-icon><Plus /></el-icon>
                {{ course.selectedCount >= course.maxStudents ? '已满员' : '选课' }}
              </el-button>
              <el-tag v-else type="success" effect="dark" size="large" round>
                <el-icon><Check /></el-icon>
                已选择
              </el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-empty v-if="filteredCourses.length === 0 && !loading" description="暂无可选课程" :image-size="120" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Reading, Search, Star, Clock, Location, User, Avatar, Plus, Check } from '@element-plus/icons-vue'
import { getCourseList } from '@/api/course'
import { getMySelections, selectCourse } from '@/api/selection'
import { getCollegeList } from '@/api/college'

const courses = ref([])
const colleges = ref([])
const mySelections = ref([])
const searchText = ref('')
const collegeFilter = ref('')
const selectStatusFilter = ref('')  // 已选/未选筛选
const loading = ref(false)

const filteredCourses = computed(() => {
  let result = courses.value
  
  // 按课程名称搜索
  if (searchText.value) {
    result = result.filter(c => 
      c.courseName.toLowerCase().includes(searchText.value.toLowerCase())
    )
  }
  
  // 按学院筛选
  if (collegeFilter.value) {
    result = result.filter(c => c.collegeId === collegeFilter.value)
  }
  
  // 按已选/未选筛选
  if (selectStatusFilter.value === 'selected') {
    result = result.filter(c => isSelected(c.id))
  } else if (selectStatusFilter.value === 'unselected') {
    result = result.filter(c => !isSelected(c.id))
  }
  
  return result
})

// 判断是否已选（包括pending、studying、completed状态，不包括dropped）
const isSelected = (courseId) => {
  return mySelections.value.some(s => 
    s.courseId === courseId && ['pending', 'studying', 'completed'].includes(s.status)
  )
}

// 获取课程状态类型
const getStatusType = (course) => {
  if (isSelected(course.id)) return 'success'
  if (course.selectedCount >= course.maxStudents) return 'danger'
  return 'primary'
}

// 获取课程状态文字
const getStatusText = (course) => {
  if (isSelected(course.id)) return '已选'
  if (course.selectedCount >= course.maxStudents) return '已满'
  return '可选'
}

const loadCourses = async () => {
  loading.value = true
  try {
    const res = await getCourseList()
    courses.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadColleges = async () => {
  try {
    const res = await getCollegeList()
    colleges.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadMySelections = async () => {
  try {
    const res = await getMySelections()
    mySelections.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleSelect = async (course) => {
  ElMessageBox.confirm(
    `确定要选择课程 "${course.courseName}" 吗？\n授课教师：${course.teacher?.name || '待定'}\n上课时间：${course.schedule || '待定'}`,
    '确认选课',
    { type: 'info' }
  ).then(async () => {
    try {
      await selectCourse(course.id)
      ElMessage.success('选课成功')
      loadCourses()
      loadMySelections()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadCourses()
  loadColleges()
  loadMySelections()
})
</script>

<style scoped>
.course-select {
  padding: 20px;
}

.select-card {
  border-radius: 12px;
}

.select-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #ab7ee2 0%, #88a9e7 100%);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.header-right .el-tag {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.search-area {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.course-card {
  margin-bottom: 20px;
  border-radius: 10px;
  transition: all 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-card.selected-card {
  border: 2px solid #67C23A;
  background: linear-gradient(135deg, #f0f9eb 0%, #fff 100%);
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.course-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.course-info {
  font-size: 14px;
  color: #606266;
}

.course-info p {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.course-info .label {
  color: #909399;
  min-width: 45px;
}

.course-info .value {
  color: #606266;
}

.course-info .full {
  color: #F56C6C;
  font-weight: bold;
}

.course-desc {
  margin: 15px 0;
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
  height: 45px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-action {
  text-align: center;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.course-action .el-tag {
  padding: 8px 20px;
}
</style>
