<template>
  <div class="goods-card" @click="$router.push(`/goods/${goods.id}`)">
    <div class="cover-area">
      <img v-if="goods.cover" :src="goods.cover" class="cover-img" />
      <div v-else class="cover-placeholder">
        <el-icon :size="42" color="#c0c4cc"><Picture /></el-icon>
      </div>
    </div>
    <div class="card-info">
      <div class="goods-title">{{ goods.title }}</div>
      <div class="price-row">
        <span class="goods-price">￥{{ goods.price }}</span>
        <el-tag v-if="goods.conditionLevel" size="small" type="info" effect="plain">
          {{ goods.conditionLevel }}
        </el-tag>
      </div>
      <div class="trade-place">
        <el-icon :size="13"><Location /></el-icon>
        <span>{{ goods.tradePlace || '校内交易' }}</span>
      </div>
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  goods: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.goods-card {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  overflow: hidden;
  cursor: pointer;
  border: 1px solid var(--app-border);
  transition: all 0.25s;
  display: flex;
  flex-direction: column;
}

.goods-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.cover-area {
  height: 180px;
  flex-shrink: 0;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--app-gradient-from) 0%, var(--app-gradient-to) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-info {
  padding: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.goods-title {
  font-size: 14px;
  color: var(--app-text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 39px;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.goods-price {
  color: var(--app-price);
  font-size: 18px;
  font-weight: 700;
}

.trade-place {
  display: flex;
  align-items: center;
  gap: 3px;
  color: var(--app-text-secondary);
  font-size: 12px;
}

.trade-place span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
