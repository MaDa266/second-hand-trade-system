<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="icon-wrap" :style="{ backgroundColor: card.bg }">
              <el-icon :size="28" :color="card.color"><component :is="card.icon" /></el-icon>
            </div>
            <div class="info">
              <div class="num">{{ card.value }}</div>
              <div class="title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({})

const cards = ref([
  { title: '用户总数', key: 'userTotal', value: 0, icon: 'User', color: 'var(--el-color-primary)', bg: 'var(--el-color-primary-light-9)' },
  { title: '商品总数', key: 'goodsTotal', value: 0, icon: 'Goods', color: 'var(--el-color-success)', bg: 'var(--el-color-success-light-9)' },
  { title: '待审核商品', key: 'pendingTotal', value: 0, icon: 'Clock', color: 'var(--el-color-warning)', bg: 'var(--el-color-warning-light-9)' },
  { title: '已售商品', key: 'soldTotal', value: 0, icon: 'CircleCheck', color: 'var(--el-color-danger)', bg: 'var(--el-color-danger-light-9)' }
])

const loadStats = async () => {
  const res = await request.get('/admin/statistics')
  stats.value = res || {}
  cards.value.forEach((c) => {
    c.value = stats.value[c.key] ?? 0
  })
}

onMounted(loadStats)
</script>

<style scoped>
.stat-card {
  border-radius: var(--app-radius);
}

.stat-body {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-wrap {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.num {
  font-size: 28px;
  font-weight: bold;
  line-height: 1.2;
}

.title {
  color: var(--app-text-secondary);
  font-size: 13px;
  margin-top: 4px;
}
</style>
