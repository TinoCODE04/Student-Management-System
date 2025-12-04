<template>
  <div class="my-info">
    <!-- 个人信息卡片 -->
    <el-card class="info-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon :size="22" color="#fff"><User /></el-icon>
            <span class="title">个人信息</span>
          </div>
        </div>
      </template>
      
      <div class="info-content" v-if="teacherInfo">
        <!-- 头像和基本信息 -->
        <div class="profile-section">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="teacherAvatar">
              <el-icon :size="50"><User /></el-icon>
            </el-avatar>
            <div class="name-tag">{{ teacherInfo.name }}</div>
            <el-tag type="primary" effect="dark" round>{{ teacherInfo.title || '教师' }}</el-tag>
          </div>
          
          <div class="info-grid">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="工号" label-class-name="desc-label">
                <span class="info-value highlight">{{ teacherInfo.username }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="姓名" label-class-name="desc-label">
                <span class="info-value">{{ teacherInfo.name }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="性别" label-class-name="desc-label">
                <el-tag :type="teacherInfo.gender === '男' ? 'primary' : 'danger'" effect="plain" size="small">
                  {{ teacherInfo.gender || '-' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="职称" label-class-name="desc-label">
                <el-tag type="warning" effect="light">{{ teacherInfo.title || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="所属专业" label-class-name="desc-label">
                <el-tag type="success" effect="light">{{ teacherInfo.major?.majorName || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="所属学院" label-class-name="desc-label">
                <el-tag type="info" effect="light">{{ teacherInfo.major?.college?.collegeName || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="联系电话" label-class-name="desc-label">
                <span class="info-value">{{ teacherInfo.phone || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="电子邮箱" label-class-name="desc-label">
                <span class="info-value">{{ teacherInfo.email || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="角色" label-class-name="desc-label">
                <el-tag type="primary" effect="dark">
                  {{ teacherInfo.role === 'teacher' ? '教师' : teacherInfo.role }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间" label-class-name="desc-label">
                <span class="info-value">{{ formatDateTime(teacherInfo.createTime) }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </div>
      
      <el-empty v-else description="无法获取个人信息" :image-size="100" />
    </el-card>

    <!-- 修改信息和密码区域 -->
    <div class="edit-section">
      <!-- 修改个人信息 -->
      <el-card class="edit-card" shadow="hover">
        <template #header>
          <div class="edit-header">
            <el-icon :size="18" color="#409EFF"><Edit /></el-icon>
            <span>修改个人信息</span>
          </div>
        </template>
        
        <el-form :model="editForm" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="editForm.phone" placeholder="请输入联系电话" :prefix-icon="Phone" />
          </el-form-item>
          <el-form-item label="电子邮箱" prop="email">
            <el-input v-model="editForm.email" placeholder="请输入电子邮箱" :prefix-icon="Message" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdate" round>
              <el-icon><Check /></el-icon>
              保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 修改密码 -->
      <el-card class="edit-card" shadow="hover">
        <template #header>
          <div class="edit-header">
            <el-icon :size="18" color="#E6A23C"><Lock /></el-icon>
            <span>修改密码</span>
          </div>
        </template>
        
        <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px">
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前密码" :prefix-icon="Lock" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" :prefix-icon="Key" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" :prefix-icon="Key" />
          </el-form-item>
          <el-form-item>
            <el-button type="warning" @click="handleChangePwd" round>
              <el-icon><Refresh /></el-icon>
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Edit, Lock, Key, Phone, Message, Check, Refresh } from '@element-plus/icons-vue'
import { getCurrentTeacherInfo, updateTeacherInfo, updateTeacherPassword } from '@/api/teacher'

// 导入头像图片
import avatar1 from '@/assets/t1.png'
import avatar2 from '@/assets/t2.png'
import avatar3 from '@/assets/t3.png'
import avatar4 from '@/assets/t4.png'
import avatar5 from '@/assets/t5.png'
import avatar6 from '@/assets/t6.png'

const teacherInfo = ref(null)
const formRef = ref()
const pwdFormRef = ref()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 头像映射：根据教师ID分配不同头像
const avatarMap = {
  1: avatar1,
  2: avatar2,
  3: avatar3,
  4: avatar4,
  5: avatar5,
  6: avatar6
}

// 计算当前教师的头像
const teacherAvatar = computed(() => {
  if (teacherInfo.value && teacherInfo.value.id) {
    const id = teacherInfo.value.id
    // 如果ID超过6，则循环使用
    const avatarIndex = ((id - 1) % 6) + 1
    return avatarMap[avatarIndex] || defaultAvatar
  }
  return defaultAvatar
})

const editForm = reactive({
  phone: '',
  email: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

const rules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const pwdRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次密码输入不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取教师信息
const fetchTeacherInfo = async () => {
  try {
    const res = await getCurrentTeacherInfo()
    if (res.code === 200) {
      teacherInfo.value = res.data
      // 初始化编辑表单
      editForm.phone = res.data.phone || ''
      editForm.email = res.data.email || ''
    }
  } catch (error) {
    ElMessage.error('获取信息失败')
  }
}

// 更新个人信息
const handleUpdate = async () => {
  try {
    await formRef.value.validate()
    const res = await updateTeacherInfo(editForm)
    if (res.code === 200) {
      ElMessage.success('信息更新成功')
      fetchTeacherInfo()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('更新失败')
    }
  }
}

// 修改密码
const handleChangePwd = async () => {
  try {
    await pwdFormRef.value.validate()
    const res = await updateTeacherPassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      // 清空表单
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
      pwdForm.confirmPassword = ''
    } else {
      ElMessage.error(res.message || '密码修改失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('密码修改失败')
    }
  }
}

onMounted(() => {
  fetchTeacherInfo()
})
</script>

<style scoped>
.my-info {
  padding: 20px;
  min-height: calc(100vh - 140px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.info-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.info-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 16px 20px;
  border-bottom: none;
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

.header-left .title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.info-content {
  padding: 20px;
}

.profile-section {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  min-width: 140px;
}

.avatar-wrapper .el-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.name-tag {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.info-grid {
  flex: 1;
}

.info-grid :deep(.el-descriptions__label) {
  width: 100px;
  font-weight: 500;
  background-color: #f5f7fa;
}

.info-grid :deep(.el-descriptions__content) {
  min-width: 150px;
}

.info-value {
  color: #606266;
}

.info-value.highlight {
  color: #409EFF;
  font-weight: 600;
}

.edit-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.edit-card {
  border-radius: 12px;
}

.edit-card :deep(.el-card__header) {
  padding: 14px 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

.edit-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.edit-card :deep(.el-card__body) {
  padding: 24px;
}

.edit-card :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

@media (max-width: 1024px) {
  .profile-section {
    flex-direction: column;
    align-items: center;
  }

  .avatar-wrapper {
    margin-bottom: 20px;
  }

  .info-grid {
    width: 100%;
  }

  .info-grid :deep(.el-descriptions) {
    --el-descriptions-table-border: 1px solid #ebeef5;
  }
}

@media (max-width: 768px) {
  .edit-section {
    grid-template-columns: 1fr;
  }

  .info-grid :deep(.el-descriptions__label),
  .info-grid :deep(.el-descriptions__content) {
    display: block;
    width: 100%;
  }
}
</style>
