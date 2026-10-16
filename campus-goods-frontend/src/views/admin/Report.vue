<template>
  <div class="page-card">
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="goodsTitle" label="被举报商品" min-width="140" show-overflow-tooltip />
      <el-table-column prop="userName" label="举报人" width="110" />
      <el-table-column prop="reason" label="举报原因" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" width="95">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'warning' : 'success'" size="small">
            {{ row.status === 0 ? '待处理' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="举报时间" width="170" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 0"
            type="primary"
            size="small"
            link
            @click="openHandle(row)"
          >
            处理
          </el-button>
          <span v-else class="done">-</span>
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

    <el-dialog v-model="dialogVisible" title="处理举报" width="440px">
      <template v-if="current">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="商品">{{ current.goodsTitle }}</el-descriptions-item>
          <el-descriptions-item label="举报人">{{ current.userName }}</el-descriptions-item>
          <el-descriptions-item label="举报原因">{{ current.reason }}</el-descriptions-item>
        </el-descriptions>
        <el-checkbox v-model="offline" style="margin-top: 16px">同时下架该商品</el-checkbox>
      </template>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="handling" @click="submitHandle">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const dialogVisible = ref(false)
const current = ref(null)
const offline = ref(true)
const handling = ref(false)

const loadData = async (p = 1) => {
  page.value = p
  loading.value = true
  try {
    const res = await request.get('/admin/report', {
      params: { page: page.value, size: size.value }
    })
    list.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const openHandle = (row) => {
  current.value = row
  offline.value = true
  dialogVisible.value = true
}

const submitHandle = async () => {
  handling.value = true
  try {
    await request.put(`/admin/report/${current.value.id}`, { offline: offline.value })
    ElMessage.success('已处理')
    dialogVisible.value = false
    loadData(page.value)
  } finally {
    handling.value = false
  }
}

onMounted(() => loadData(1))
</script>

<style scoped>
.page-card {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 20px;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.done {
  color: var(--app-text-placeholder);
}
</style>
