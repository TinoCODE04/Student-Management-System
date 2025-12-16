<template>
  <div class="notification-bell">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="item">
      <el-icon :size="20" @click="showDrawer = true" style="cursor: pointer;">
        <Bell />
      </el-icon>
    </el-badge>

    <el-drawer v-model="showDrawer" title="通知中心" size="400px">
      <div class="notification-header">
        <el-button text @click="markAllRead" :disabled="unreadCount === 0">
          全部标记为已读
        </el-button>
      </div>

      <el-scrollbar height="calc(100vh - 120px)">
        <div v-if="notifications.length === 0" class="empty-state">
          <el-empty description="暂无通知" />
        </div>

        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-item"
          :class="{ unread: item.status === 0 }"
          @click="handleNotificationClick(item)"
        >
          <div class="notification-title">
            <span>{{ item.title }}</span>
            <el-tag v-if="item.status === 0" type="danger" size="small">未读</el-tag>
          </div>
          <div class="notification-content">{{ item.content }}</div>
          <div class="notification-time">{{ formatTime(item.createTime) }}</div>
        </div>
      </el-scrollbar>

      <div class="notification-footer">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          small
          @current-change="loadNotifications"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getNotificationList, getUnreadCount, markAsRead, markAllAsRead } from '@/api/notification'
import websocket from '@/utils/websocket'

const showDrawer = ref(false)
const unreadCount = ref(0)
const notifications = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载通知列表
const loadNotifications = async () => {
  try {
    const res = await getNotificationList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      notifications.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('加载通知失败:', error)
  }
}

// 加载未读数量
const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    if (res.code === 200) {
      unreadCount.value = res.data
    }
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

// 点击通知
const handleNotificationClick = async (item) => {
  if (item.status === 0) {
    try {
      await markAsRead(item.id)
      item.status = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }

  // 如果有跳转链接，则跳转
  if (item.url) {
    // router.push(item.url)
    showDrawer.value = false
  }
}

// 全部标记为已读
const markAllRead = async () => {
  try {
    await markAllAsRead()
    notifications.value.forEach(item => {
      item.status = 1
    })
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('操作失败')
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  
  return date.toLocaleDateString()
}

// WebSocket 消息处理
const handleWebSocketMessage = (data) => {
  // 收到新通知
  if (data.id) {
    unreadCount.value++
    ElMessage.info({
      message: data.title,
      duration: 3000
    })
    loadNotifications()
  }
}

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
  
  // 监听 WebSocket 消息
  websocket.onMessage(handleWebSocketMessage)
})

onUnmounted(() => {
  websocket.offMessage(handleWebSocketMessage)
})
</script>

<style scoped>
.notification-bell {
  display: inline-block;
  padding: 0 10px;
}

.notification-header {
  padding: 0 0 10px 0;
  border-bottom: 1px solid #eee;
  text-align: right;
}

.notification-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f5f5f5;
}

.notification-item.unread {
  background-color: #f0f9ff;
}

.notification-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  margin-bottom: 8px;
}

.notification-content {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
  line-height: 1.5;
}

.notification-time {
  color: #999;
  font-size: 12px;
}

.empty-state {
  padding: 50px 0;
  text-align: center;
}

.notification-footer {
  padding: 15px 0;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: center;
}
</style>
