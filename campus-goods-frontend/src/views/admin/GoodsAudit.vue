<template>
  <div class="page-card">
    <div class="toolbar">
      <el-radio-group v-model="status" @change="loadData(1)">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button :value="0">待审核</el-radio-button>
        <el-radio-button :value="1">在售</el-radio-button>
        <el-radio-button :value="2">已售</el-radio-button>
        <el-radio-button :value="3">已下架</el-radio-button>
        <el-radio-button :value="4">审核不通过</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="封面" width="90">
        <template #default="{ row }">
          <el-image
            v-if="row.cover"
            :src="row.cover"
            fit="cover"
            style="width: 56px; height: 56px; border-radius: 4px"
            :preview-src-list="imageList(row)"
            preview-teleported
          />
          <div v-else class="no-img"><el-icon><Picture /></el-icon></div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">
          <span class="price">￥{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sellerName" label="卖家" width="110" />
      <el-table-column prop="categoryName" label="分类" width="110" />
      <el-table-column label="状态" width="105">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button type="success" size="small" link @click="audit(row, 1)">通过</el-button>
            <el-button type="danger" size="small" link @click="audit(row, 4)">拒绝</el-button>
          </template>
          <el-button v-if="row.status === 1" type="warning" size="small" link @click="offline(row)">
            下架
          </el-button>
          <el-button type="primary" size="small" link @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="size"
        :current-page="page"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="detailVisible" title="商品详情" width="560px">
      <template v-if="current">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="标题" :span="2">{{ current.title }}</el-descriptions-item>
          <el-descriptions-item label="价格">￥{{ current.price }}</el-descriptions-item>
          <el-descriptions-item label="原价">
            {{ current.oldPrice ? '￥' + current.oldPrice : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="成色">{{ current.conditionLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交易地点">{{ current.tradePlace || '-' }}</el-descriptions-item>
          <el-descriptions-item label="卖家">{{ current.sellerName }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ current.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            <div class="desc">{{ current.description || '无' }}</div>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="imageList(current).length" class="imgs">
          <el-image
            v-for="(img, i) in imageList(current)"
            :key="i"
            :src="img"
            fit="cover"
            style="width: 100px; height: 100px; border-radius: 4px"
            :preview-src-list="imageList(current)"
            :initial-index="i"
            preview-teleported
          />
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const status = ref(0)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const detailVisible = ref(false)
const current = ref(null)

const statusText = (s) =>
  ({ 0: '待审核', 1: '在售', 2: '已售', 3: '已下架', 4: '审核不通过' }[s] ?? '未知')
const statusType = (s) =>
  ({ 0: 'warning', 1: 'success', 2: 'info', 3: 'info', 4: 'danger' }[s] ?? 'info')

const imageList = (row) => {
  const imgs = []
  if (row.cover) imgs.push(row.cover)
  if (row.images) {
    row.images.split(',').forEach((i) => {
      const t = i.trim()
      if (t && !imgs.includes(t)) imgs.push(t)
    })
  }
  return imgs
}

const loadData = async (p = 1) => {
  page.value = p
  loading.value = true
  try {
    const res = await request.get('/admin/goods/audit', {
      params: {
        status: status.value === '' ? undefined : status.value,
        page: page.value,
        size: size.value
      }
    })
    list.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const audit = async (row, target) => {
  const action = target === 1 ? '通过' : '拒绝'
  await ElMessageBox.confirm(`确定${action}商品「${row.title}」的审核吗？`, '提示', {
    type: 'warning'
  })
  await request.put(`/admin/goods/${row.id}/audit`, { status: target })
  ElMessage.success(`已${action}`)
  loadData(page.value)
}

const offline = async (row) => {
  await ElMessageBox.confirm(`确定下架商品「${row.title}」吗？`, '提示', { type: 'warning' })
  await request.put(`/admin/goods/${row.id}/offline`)
  ElMessage.success('已下架')
  loadData(page.value)
}

const showDetail = (row) => {
  current.value = row
  detailVisible.value = true
}

onMounted(() => loadData(1))
</script>

<style scoped>
.page-card {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 20px;
}

.toolbar {
  margin-bottom: 16px;
}

.price {
  color: var(--app-price);
  font-weight: bold;
}

.no-img {
  width: 56px;
  height: 56px;
  border-radius: 4px;
  background: linear-gradient(135deg, var(--app-gradient-from), var(--app-gradient-to));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--app-text-placeholder);
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.desc {
  white-space: pre-wrap;
  word-break: break-all;
}

.imgs {
  margin-top: 14px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
