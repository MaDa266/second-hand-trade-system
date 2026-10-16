<template>
  <div class="favorite-page">
    <h3 class="page-title">我的收藏</h3>
    <div v-loading="loading" class="goods-grid">
      <GoodsCard v-for="(item, idx) in list" :key="goodsOf(item).id || idx" :goods="goodsOf(item)">
        <el-button size="small" type="danger" plain class="fav-btn" @click.stop="cancelFavorite(item)">
          取消收藏
        </el-button>
      </GoodsCard>
    </div>
    <el-empty v-if="!loading && list.length === 0" description="暂无收藏，去逛逛吧~" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import GoodsCard from '@/components/GoodsCard.vue'

const list = ref([])
const loading = ref(false)

const goodsOf = (item) => item.goods || item

const loadList = async () => {
  loading.value = true
  try {
    const res = await request.get('/favorite/list')
    list.value = res || []
  } finally {
    loading.value = false
  }
}

const cancelFavorite = async (item) => {
  const g = goodsOf(item)
  const gid = item.goodsId ?? g.id
  await request.delete(`/favorite/${gid}`)
  ElMessage.success('已取消收藏')
  loadList()
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

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.fav-btn {
  margin-top: 4px;
  width: 100%;
  margin-left: 0;
}
</style>
