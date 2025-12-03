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
      
      <div class="info-content" v-if="studentInfo">
        <!-- 头像和基本信息 -->
        <div class="profile-section">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="studentAvatar">
              <el-icon :size="50"><User /></el-icon>
            </el-avatar>
            <div class="name-tag">{{ studentInfo.name }}</div>
            <el-tag type="success" effect="dark" round>{{ studentInfo.role === 'student' ? '在校学生' : '用户' }}</el-tag>
          </div>
          
          <div class="info-grid">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="学号" label-class-name="desc-label">
                <span class="info-value highlight">{{ studentInfo.studentNo }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="姓名" label-class-name="desc-label">
                <span class="info-value">{{ studentInfo.name }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="性别" label-class-name="desc-label">
                <el-tag :type="studentInfo.gender === '男' ? 'primary' : 'danger'" effect="plain" size="small">
                  {{ studentInfo.gender || '-' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="年龄" label-class-name="desc-label">
                <span class="info-value">{{ studentInfo.age || '-' }} 岁</span>
              </el-descriptions-item>
              <el-descriptions-item label="出生日期" label-class-name="desc-label">
                <span class="info-value">{{ formatDate(studentInfo.birthDate) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="入学日期" label-class-name="desc-label">
                <span class="info-value">{{ formatDate(studentInfo.enrollDate) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="所属学院" label-class-name="desc-label">
                <el-tag type="warning" effect="light">{{ studentInfo.college?.collegeName || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="所属专业" label-class-name="desc-label">
                <el-tag type="success" effect="light">{{ studentInfo.major?.majorName || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="年级" label-class-name="desc-label">
                <span class="info-value">{{ studentInfo.grade || '-' }} 级</span>
              </el-descriptions-item>
              <el-descriptions-item label="班级" label-class-name="desc-label">
                <span class="info-value">{{ studentInfo.className || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="联系电话" label-class-name="desc-label">
                <span class="info-value">{{ studentInfo.phone || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="电子邮箱" label-class-name="desc-label">
                <span class="info-value">{{ studentInfo.email || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="已获学分" label-class-name="desc-label">
                <span class="info-value credit">{{ studentInfo.totalCredit || 0 }} 学分</span>
              </el-descriptions-item>
              <el-descriptions-item label="账号状态" label-class-name="desc-label">
                <el-tag :type="studentInfo.status === 'active' ? 'success' : 'danger'" effect="dark">
                  {{ studentInfo.status === 'active' ? '正常' : (studentInfo.status || '正常') }}
                </el-tag>
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
import { getMyInfo, updateStudent } from '@/api/student'
import { changePassword } from '@/api/auth'

// 导入头像图片
import avatar1 from '@/assets/p1.jpeg'
import avatar2 from '@/assets/p2.png'
import avatar3 from '@/assets/p3.jpg'
import avatar4 from '@/assets/p4.jpg'
import avatar5 from '@/assets/p5.jpg'
import avatar6 from '@/assets/p6.jpg'
import avatar7 from '@/assets/p7.jpg'
import avatar8 from '@/assets/p8.jpg'

const studentInfo = ref(null)
const formRef = ref()
const pwdFormRef = ref()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 头像映射：根据学生ID分配不同头像
const avatarMap = {
  1: avatar1,  // 赵子龙
  2: avatar2,  // 钱雨萱
  3: avatar3,  // 孙浩然
  4: avatar4,  // 李思琪
  5: avatar5,  // 周杰伦
  6: avatar6,  // 吴美玲
  7: avatar7,  // 郑凯文
  8: avatar8   // 王小燕
}

// 计算当前学生的头像
const studentAvatar = computed(() => {
  if (studentInfo.value && studentInfo.value.id) {
    return avatarMap[studentInfo.value.id] || defaultAvatar
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

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 10)
}

const rules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
  ]
}

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
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
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const loadData = async () => {
  try {
    const res = await getMyInfo()
    studentInfo.value = res.data
    if (res.data) {
      editForm.phone = res.data.phone || ''
      editForm.email = res.data.email || ''
    }
  } catch (e) {
    console.error(e)
  }
}

const handleUpdate = async () => {
  await formRef.value.validate()
  try {
    await updateStudent({
      id: studentInfo.value.id,
      phone: editForm.phone,
      email: editForm.email
    })
    ElMessage.success('信息更新成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleChangePwd = async () => {
  await pwdFormRef.value.validate()
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功')
    pwdFormRef.value.resetFields()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.my-info {
  padding: 20px;
}

/* 信息卡片 */
.info-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.info-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
}

.card-header {
  display: flex;
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

/* 个人资料部分 */
.profile-section {
  display: flex;
  gap: 40px;
  padding: 20px;
}

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  min-width: 150px;
}

.name-tag {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.info-grid {
  flex: 1;
}

/* 描述列表样式 */
:deep(.el-descriptions) {
  background: #fff;
}

:deep(.desc-label) {
  width: 100px;
  font-weight: 500;
  background-color: #f5f7fa !important;
}

:deep(.el-descriptions__cell) {
  padding: 14px 16px !important;
}

.info-value {
  color: #606266;
}

.info-value.highlight {
  color: #409EFF;
  font-weight: 600;
}

.info-value.credit {
  color: #67C23A;
  font-weight: 600;
}

/* 编辑区域 */
.edit-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.edit-card {
  border-radius: 12px;
}

.edit-card :deep(.el-card__header) {
  padding: 16px 20px;
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

/* 响应式布局 */
@media (max-width: 992px) {
  .profile-section {
    flex-direction: column;
    align-items: center;
  }
  
  .edit-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  :deep(.el-descriptions) {
    --el-descriptions-item-bordered-label-background: #f5f7fa;
  }
  
  .profile-section {
    padding: 10px;
  }
}
</style>
