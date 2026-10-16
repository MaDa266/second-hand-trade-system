<template>
  <div v-if="goods" class="detail-page">
    <div class="detail-main">
      <div class="gallery">
        <el-carousel
          v-if="imgList.length > 0"
          height="420px"
          :autoplay="false"
          arrow="always"
          indicator-position="outside"
        >
          <el-carousel-item v-for="(img, idx) in imgList" :key="idx">
            <img :src="img" class="carousel-img" />
          </el-carousel-item>
        </el-carousel>
        <div v-else class="cover-placeholder">
          <el-icon :size="60" color="#c0c4cc"><Picture /></el-icon>
        </div>
      </div>

      <div class="info-panel">
        <h2 class="detail-title">{{ goods.title }}</h2>
        <div class="price-panel">
          <span class="detail-price">￥{{ goods.price }}</span>
          <span v-if="goods.oldPrice" class="old-price">原价 ￥{{ goods.oldPrice }}</span>
        </div>

        <el-descriptions :column="2" border size="small" class="meta-desc">
          <el-descriptions-item label="成色">{{ goods.conditionLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ categoryName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交易地点">{{ goods.tradePlace || '-' }}</el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ goods.viewCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="发布时间" :span="2">{{ goods.createTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="seller-panel">
          <el-avatar :size="42" :src="sellerAvatar">{{ sellerName[0] }}</el-avatar>
          <div class="seller-info">
            <div class="seller-name">{{ sellerName }}</div>
            <div class="seller-tip">联系卖家请通过下方留言区</div>
          </div>
        </div>

        <div class="action-row">
          <el-button
            :type="favorited ? 'warning' : 'primary'"
            :plain="!favorited"
            @click="toggleFavorite"
          >
            <el-icon style="margin-right: 4px">
              <StarFilled v-if="favorited" />
              <Star v-else />
            </el-icon>
            {{ favorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button type="warning" plain @click="openReport">
            <el-icon style="margin-right: 4px"><WarningFilled /></el-icon>举报
          </el-button>
          <template v-if="isOwner">
            <el-button type="primary" plain @click="router.push(`/publish?id=${goods.id}`)">编辑</el-button>
            <el-button v-if="goods.status === 1" type="success" plain @click="markSold">标记已售</el-button>
            <el-button type="danger" plain @click="deleteGoods">删除</el-button>
          </template>
        </div>
      </div>
    </div>

    <div class="desc-panel">
      <h3 class="panel-title">商品描述</h3>
      <div class="desc-content">{{ goods.description || '暂无描述' }}</div>
    </div>

    <div class="message-panel">
      <h3 class="panel-title">留言板（{{ messages.length }}）</h3>
      <template v-if="userStore.isLogin">
        <div class="msg-input-row">
          <div v-if="replyTarget" class="reply-tip">
            正在回复 <b>{{ replyTarget.name }}</b>
            <el-button link type="primary" size="small" @click="cancelReply">取消回复</el-button>
          </div>
          <el-input
            v-model="msgContent"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            :placeholder="replyTarget ? `回复 ${replyTarget.name}：` : '想问点什么？给卖家留言吧~'"
          />
          <el-button type="primary" :loading="msgSending" @click="sendMessage">
            {{ replyTarget ? '发送回复' : '发送留言' }}
          </el-button>
        </div>
      </template>
      <div v-else class="msg-login-tip">
        <span>登录后可以参与留言</span>
        <el-button size="small" type="primary" plain @click="router.push('/login')">去登录</el-button>
      </div>

      <div class="msg-list">
        <div v-for="msg in messages" :key="msg.id" class="msg-item">
          <el-avatar :size="36">{{ (msg.fromName || '匿')[0] }}</el-avatar>
          <div class="msg-body">
            <div class="msg-head">
              <span class="msg-name">{{ msg.fromName || '匿名同学' }}</span>
              <el-tag
                v-if="msg.fromUserId === goods.userId"
                size="small"
                type="warning"
                effect="plain"
              >卖家</el-tag>
              <span v-else-if="msg.toUserId && msg.toUserId !== goods.userId" class="msg-to">
                回复 {{ msg.toName }}
              </span>
              <span class="msg-time">{{ formatDateTime(msg.createTime) }}</span>
            </div>
            <div class="msg-content">{{ msg.content }}</div>
            <div v-if="userStore.isLogin && msg.fromUserId !== myId" class="msg-actions">
              <el-button link type="primary" size="small" @click="startReply(msg)">回复</el-button>
            </div>
          </div>
        </div>
        <el-empty v-if="messages.length === 0" description="还没有留言，快来抢占第一楼~" :image-size="80" />
      </div>
    </div>

    <el-dialog v-model="reportVisible" title="举报商品" width="420px">
      <el-input
        v-model="reportReason"
        type="textarea"
        :rows="3"
        maxlength="200"
        show-word-limit
        placeholder="请填写举报原因"
      />
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="primary" :loading="reportSubmitting" @click="submitReport">提交举报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, StarFilled, WarningFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const goodsId = route.params.id
const goods = ref(null)
const categories = ref([])
const favorited = ref(false)
const messages = ref([])
const msgContent = ref('')
const msgSending = ref(false)
const reportVisible = ref(false)
const reportReason = ref('')
const reportSubmitting = ref(false)
/** 当前回复目标：{ userId, name }，为空表示发普通留言 */
const replyTarget = ref(null)

const myId = computed(() => Number(userStore.userInfo?.id) || null)

const imgList = computed(() => {
  const arr = []
  if (goods.value?.cover) arr.push(goods.value.cover)
  if (goods.value?.images) {
    goods.value.images.split(',').forEach((u) => {
      const t = u.trim()
      if (t && !arr.includes(t)) arr.push(t)
    })
  }
  return arr
})

const categoryName = computed(() => {
  if (!goods.value) return ''
  return goods.value.categoryName || categories.value.find((c) => c.id === goods.value.categoryId)?.name || ''
})

const sellerName = computed(() => {
  const g = goods.value || {}
  return g.sellerName || g.seller?.nickname || g.nickname || '卖家'
})

const sellerAvatar = computed(() => {
  const g = goods.value || {}
  return g.sellerAvatar || g.seller?.avatar || g.avatar || ''
})

const isOwner = computed(() => {
  return !!goods.value && Number(goods.value.userId) === Number(userStore.userInfo?.id)
})

const loadDetail = async () => {
  const res = await request.get(`/goods/${goodsId}`)
  goods.value = res
  favorited.value = res?.favorite === true
}

const loadMessages = async () => {
  const res = await request.get(`/message/goods/${goodsId}`)
  messages.value = res || []
}

/** 点击某条留言的「回复」，把收件人设为该留言作者 */
const startReply = (msg) => {
  replyTarget.value = { userId: msg.fromUserId, name: msg.fromName || '对方' }
}

const cancelReply = () => {
  replyTarget.value = null
}

const toggleFavorite = async () => {
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  if (favorited.value) {
    await request.delete(`/favorite/${goodsId}`)
    favorited.value = false
    ElMessage.success('已取消收藏')
  } else {
    await request.post(`/favorite/${goodsId}`)
    favorited.value = true
    ElMessage.success('收藏成功')
  }
}

const openReport = () => {
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  reportReason.value = ''
  reportVisible.value = true
}

const submitReport = async () => {
  if (!reportReason.value.trim()) {
    ElMessage.warning('请填写举报原因')
    return
  }
  reportSubmitting.value = true
  try {
    await request.post('/report', {
      goodsId: Number(goodsId),
      reason: reportReason.value.trim()
    })
    ElMessage.success('举报已提交，感谢您的反馈')
    reportVisible.value = false
  } finally {
    reportSubmitting.value = false
  }
}

const markSold = async () => {
  await request.put(`/goods/${goodsId}/sold`)
  ElMessage.success('已标记为已售')
  loadDetail()
}

const deleteGoods = async () => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？删除后不可恢复', '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await request.delete(`/goods/${goodsId}`)
  ElMessage.success('删除成功')
  router.push('/')
}

const sendMessage = async () => {
  if (!msgContent.value.trim()) {
    ElMessage.warning(replyTarget.value ? '请输入回复内容' : '请输入留言内容')
    return
  }
  msgSending.value = true
  try {
    const payload = { goodsId: Number(goodsId), content: msgContent.value.trim() }
    if (replyTarget.value) {
      payload.toUserId = replyTarget.value.userId
    }
    await request.post('/message', payload)
    ElMessage.success(replyTarget.value ? '回复成功' : '留言发送成功')
    msgContent.value = ''
    cancelReply()
    loadMessages()
  } finally {
    msgSending.value = false
  }
}

onMounted(async () => {
  loadDetail()
  request.get('/goods/categories').then((res) => {
    categories.value = res || []
  })
  loadMessages()
})
</script>

<style scoped>
.detail-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-main {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 20px;
  display: flex;
  gap: 24px;
}

.gallery {
  width: 480px;
  flex-shrink: 0;
}

.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--app-radius);
  display: block;
}

.cover-placeholder {
  height: 420px;
  border-radius: var(--app-radius);
  background: linear-gradient(135deg, var(--app-gradient-from) 0%, var(--app-gradient-to) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.detail-title {
  font-size: 22px;
  color: var(--app-text-primary);
  line-height: 1.4;
}

.price-panel {
  background: var(--el-color-danger-light-9);
  border-radius: var(--app-radius);
  padding: 14px 16px;
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.detail-price {
  color: var(--app-price);
  font-size: 28px;
  font-weight: 700;
}

.old-price {
  color: var(--app-text-secondary);
  font-size: 14px;
  text-decoration: line-through;
}

.seller-panel {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: var(--app-bg-soft);
  border-radius: var(--app-radius);
}

.seller-name {
  font-weight: 600;
  color: var(--app-text-primary);
}

.seller-tip {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.desc-panel,
.message-panel {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 20px;
}

.panel-title {
  font-size: 16px;
  color: var(--app-text-primary);
  margin-bottom: 14px;
  padding-left: 8px;
  border-left: 3px solid var(--el-color-primary);
}

.desc-content {
  color: var(--app-text-regular);
  line-height: 1.8;
  white-space: pre-wrap;
}

.msg-input-row {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  margin-bottom: 20px;
}

.reply-tip {
  align-self: flex-start;
  font-size: 13px;
  color: var(--app-text-regular);
  background: var(--el-color-primary-light-9);
  border-radius: 4px;
  padding: 4px 10px;
}

.msg-login-tip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--app-bg-soft);
  border-radius: var(--app-radius);
  padding: 14px 16px;
  color: var(--app-text-secondary);
  margin-bottom: 16px;
}

.msg-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.msg-item {
  display: flex;
  gap: 10px;
  padding: 12px;
  background: var(--app-bg-soft);
  border-radius: var(--app-radius);
}

.msg-body {
  flex: 1;
  min-width: 0;
}

.msg-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.msg-to {
  font-size: 12px;
  color: var(--app-text-secondary);
}

.msg-actions {
  margin-top: 4px;
}

.msg-name {
  font-weight: 600;
  color: var(--app-text-primary);
  font-size: 13px;
}

.msg-time {
  margin-left: auto;
  font-size: 12px;
  color: var(--app-text-placeholder);
}

.msg-content {
  color: var(--app-text-regular);
  line-height: 1.6;
  word-break: break-word;
}
</style>
