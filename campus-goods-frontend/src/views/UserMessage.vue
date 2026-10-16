<template>
  <div class="message-page">
    <h3 class="page-title">我的留言</h3>
    <div v-loading="loading" class="msg-list">
      <div v-for="msg in list" :key="msg.id" class="msg-card">
        <div class="msg-card-head">
          <el-tag size="small" :type="isReceived(msg) ? 'success' : 'info'" effect="plain">
            {{ isReceived(msg) ? '收到的留言' : '我发出的' }}
          </el-tag>
          <span v-if="isReceived(msg) && msg.isRead === 0" class="unread-badge">未读</span>
          <span class="msg-time">{{ formatDateTime(msg.createTime) }}</span>
        </div>
        <div class="msg-goods" @click="router.push(`/goods/${msg.goodsId}`)">
          <el-icon :size="14"><Goods /></el-icon>
          <span>{{ msg.goodsTitle || `商品 #${msg.goodsId}` }}</span>
        </div>
        <div class="msg-content">
          <span class="msg-who">{{ isReceived(msg) ? (msg.fromName || '对方') : (msg.toName || '对方') }}：</span>{{ msg.content }}
        </div>
      </div>
      <el-empty v-if="!loading && list.length === 0" description="暂无留言" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const list = ref([])
const loading = ref(false)

const isReceived = (msg) => Number(msg.toUserId) === Number(userStore.userInfo?.id)

const loadList = async () => {
  loading.value = true
  try {
    const res = await request.get('/message/list')
    list.value = res || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.page-title {
  font-size: 18px;
  color: var(--app-text-primary);
  margin-bottom: 16px;
  padding-left: 8px;
  border-left: 3px solid var(--el-color-primary);
}

.msg-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 200px;
}

.msg-card {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 14px 16px;
  border: 1px solid var(--app-border);
}

.msg-card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.unread-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--app-price);
}

.unread-badge::before {
  content: '';
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--app-price);
}

.msg-time {
  margin-left: auto;
  font-size: 12px;
  color: var(--app-text-placeholder);
}

.msg-goods {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--el-color-primary);
  font-size: 13px;
  cursor: pointer;
  margin-bottom: 8px;
}

.msg-goods:hover {
  text-decoration: underline;
}

.msg-content {
  color: var(--app-text-regular);
  line-height: 1.6;
  word-break: break-word;
}

.msg-who {
  color: var(--app-text-primary);
  font-weight: 600;
}
</style>
