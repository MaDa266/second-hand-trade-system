<template>
  <!-- 弹窗一：待审核商品 -->
  <el-dialog
    :model-value="pending.goodsVisible"
    width="580px"
    align-center
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    class="pending-dialog"
    @update:model-value="onGoodsVisible"
  >
    <template #header>
      <div class="pending-header is-goodsaudit">
        <div class="pending-icon"><el-icon :size="22"><Files /></el-icon></div>
        <div class="pending-head-text">
          <div class="pending-title">有 {{ pending.pendingGoodsCount }} 件商品等待审核</div>
          <div class="pending-sub">同学提交的闲置物品需要审核通过后才会出现在首页</div>
        </div>
      </div>
    </template>

    <div class="pending-list">
      <div v-for="item in pending.pendingGoods" :key="item.id" class="pending-item">
        <img v-if="item.cover" :src="item.cover" class="pending-cover" />
        <div v-else class="pending-cover is-empty"><el-icon :size="18"><Picture /></el-icon></div>
        <div class="pending-info">
          <div class="pending-item-title">{{ item.title }}</div>
          <div class="pending-item-meta">
            ￥{{ item.price }} · 卖家 {{ item.sellerName || '未知' }} · {{ formatDateTime(item.createTime) }}
          </div>
        </div>
      </div>
      <div v-if="pending.pendingGoodsCount > pending.pendingGoods.length" class="pending-more">
        还有 {{ pending.pendingGoodsCount - pending.pendingGoods.length }} 件未显示，点击「立即审核」查看全部
      </div>
    </div>

    <template #footer>
      <el-button @click="pending.closeGoods()">稍后处理</el-button>
      <el-button type="primary" @click="goGoodsAudit">立即审核</el-button>
    </template>
  </el-dialog>

  <!-- 弹窗二：待处理举报 -->
  <el-dialog
    :model-value="pending.reportVisible"
    width="580px"
    align-center
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    class="pending-dialog"
    @update:model-value="onReportVisible"
  >
    <template #header>
      <div class="pending-header is-report">
        <div class="pending-icon"><el-icon :size="22"><WarningFilled /></el-icon></div>
        <div class="pending-head-text">
          <div class="pending-title">有 {{ pending.pendingReportsCount }} 条举报待处理</div>
          <div class="pending-sub">同学举报了违规商品，请核实后决定是否下架</div>
        </div>
      </div>
    </template>

    <div class="pending-list">
      <div v-for="item in pending.pendingReports" :key="item.id" class="pending-item">
        <div class="pending-cover is-report"><el-icon :size="18"><Warning /></el-icon></div>
        <div class="pending-info">
          <div class="pending-item-title">{{ item.goodsTitle }}</div>
          <div class="pending-item-meta">
            举报人 {{ item.userName || '未知' }} · {{ formatDateTime(item.createTime) }}
          </div>
          <div class="pending-item-reason">原因：{{ item.reason }}</div>
        </div>
      </div>
      <div v-if="pending.pendingReportsCount > pending.pendingReports.length" class="pending-more">
        还有 {{ pending.pendingReportsCount - pending.pendingReports.length }} 条未显示，点击「立即处理」查看全部
      </div>
    </div>

    <template #footer>
      <el-button @click="pending.closeReport()">稍后处理</el-button>
      <el-button type="danger" @click="goReport">立即处理</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { usePendingStore } from '@/stores/pending'
import { formatDateTime } from '@/utils/format'

const router = useRouter()
const pending = usePendingStore()

const onGoodsVisible = (visible) => {
  if (!visible) {
    pending.closeGoods()
  }
}

const onReportVisible = (visible) => {
  if (!visible) {
    pending.closeReport()
  }
}

const goGoodsAudit = () => {
  pending.closeGoods()
  router.push('/admin/goods')
}

const goReport = () => {
  pending.closeReport()
  router.push('/admin/report')
}
</script>

<style scoped>
.pending-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pending-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.is-goodsaudit .pending-icon {
  background: var(--el-color-warning-light-9);
  color: var(--el-color-warning);
}

.is-report .pending-icon {
  background: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}

.pending-head-text {
  min-width: 0;
}

.pending-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--app-text-primary);
  line-height: 1.4;
}

.pending-sub {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 4px;
}

.pending-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 320px;
  overflow-y: auto;
}

.pending-item {
  display: flex;
  gap: 12px;
  padding: 10px 12px;
  background: var(--app-bg-soft);
  border-radius: var(--app-radius);
}

.pending-cover {
  width: 52px;
  height: 52px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.pending-cover.is-empty,
.pending-cover.is-report {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--app-gradient-from) 0%, var(--app-gradient-to) 100%);
  color: var(--app-text-placeholder);
}

.pending-cover.is-report {
  background: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}

.pending-info {
  min-width: 0;
  flex: 1;
}

.pending-item-title {
  font-size: 14px;
  color: var(--app-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pending-item-meta {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 4px;
}

.pending-item-reason {
  font-size: 12px;
  color: var(--el-color-danger);
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pending-more {
  font-size: 12px;
  color: var(--app-text-secondary);
  text-align: center;
  padding-top: 4px;
}
</style>
