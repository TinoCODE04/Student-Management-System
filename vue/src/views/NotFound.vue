<template>
  <div class="not-found">
    <el-empty description="页面不存在">
      <template #image>
        <h1 class="error-code">404</h1>
      </template>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.error-code {
  font-size: 120px;
  color: #909399;
  margin: 0;
}
</style>
