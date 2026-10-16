<template>
  <div class="home-page">
    <div v-if="notices.length" class="notice-bar" @click="noticeVisible = true">
      <el-icon :size="16" color="#e6a23c"><Bell /></el-icon>
      <span class="notice-label">公告</span>
      <span class="notice-text">{{ notices[0].title }}</span>
      <span class="notice-more">查看全部 ></span>
    </div>

    <div class="category-bar">
      <span
        class="cat-item"
        :class="{ active: categoryId === null }"
        @click="selectCategory(null)"
      >全部</span>
      <span
        v-for="cat in categories"
        :key="cat.id"
        class="cat-item"
        :class="{ active: categoryId === cat.id }"
        @click="selectCategory(cat.id)"
      >{{ cat.name }}</span>
    </div>

    <div v-loading="loading" class="goods-grid">
      <GoodsCard v-for="item in goodsList" :key="item.id" :goods="item" />
    </div>
    <el-empty v-if="!loading && goodsList.length === 0" description="暂无商品，快去发布一件吧~" />

    <div v-if="total > 0" class="pagination-wrap">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="size"
        :current-page="page"
        @current-change="handlePage"
      />
    </div>

    <el-dialog v-model="noticeVisible" title="平台公告" width="600px">
      <div v-for="notice in notices" :key="notice.id" class="notice-block">
        <div class="notice-block-title">{{ notice.title }}</div>
        <div class="notice-block-time">{{ formatDateTime(notice.createTime) }}</div>
        <div class="notice-block-content">{{ notice.content || '暂无内容' }}</div>
      </div>
      <el-empty v-if="notices.length === 0" description="暂无公告" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/format'
import GoodsCard from '@/components/GoodsCard.vue'

const route = useRoute()

const categories = ref([])
const goodsList = ref([])
const total = ref(0)
const page = ref(1)
const size = 12
const categoryId = ref(null)
const keyword = ref('')
const loading = ref(false)
const notices = ref([])
const noticeVisible = ref(false)

const loadNotices = async () => {
  const res = await request.get('/notice/list')
  notices.value = res || []
}

const loadCategories = async () => {
  const res = await request.get('/goods/categories')
  categories.value = res || []
}

const loadGoods = async () => {
  loading.value = true
  try {
    const res = await request.get('/goods/list', {
      params: {
        keyword: keyword.value || undefined,
        categoryId: categoryId.value ?? undefined,
        page: page.value,
        size
      }
    })
    goodsList.value = res?.records || []
    total.value = res?.total || 0
  } finally {
    loading.value = false
  }
}

const selectCategory = (id) => {
  categoryId.value = id
  page.value = 1
  loadGoods()
}

const handlePage = (p) => {
  page.value = p
  loadGoods()
}

watch(
  () => route.query.keyword,
  (val) => {
    keyword.value = val || ''
    page.value = 1
    loadGoods()
  }
)

onMounted(() => {
  keyword.value = route.query.keyword || ''
  loadNotices()
  loadCategories()
  loadGoods()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-bar {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 14px 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.notice-bar {
  background: var(--el-color-warning-light-9);
  border: 1px solid var(--el-color-warning-light-8);
  border-radius: var(--app-radius);
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 13px;
}

.notice-label {
  color: #e6a23c;
  font-weight: 600;
  flex-shrink: 0;
}

.notice-text {
  color: var(--app-text-regular);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-more {
  margin-left: auto;
  color: #e6a23c;
  flex-shrink: 0;
}

.notice-block + .notice-block {
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid var(--app-border);
}

.notice-block-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--app-text-primary);
}

.notice-block-time {
  font-size: 12px;
  color: var(--app-text-placeholder);
  margin: 4px 0 8px;
}

.notice-block-content {
  color: var(--app-text-regular);
  line-height: 1.8;
  white-space: pre-wrap;
}

.cat-item {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 14px;
  color: var(--app-text-regular);
  cursor: pointer;
  background: var(--app-bg-soft);
  transition: all 0.2s;
}

.cat-item:hover {
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.cat-item.active {
  color: #fff;
  background: var(--el-color-primary);
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 10px 0 20px;
}
</style>
