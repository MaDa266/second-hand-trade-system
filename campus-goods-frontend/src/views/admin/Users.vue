<template>
  <div class="page-card">
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名 / 昵称"
        clearable
        style="width: 260px"
        @keyup.enter="loadData(1)"
        @clear="loadData(1)"
      />
      <el-button type="primary" @click="loadData(1)">查询</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" min-width="110" />
      <el-table-column prop="nickname" label="昵称" min-width="110" />
      <el-table-column prop="phone" label="手机号" min-width="120">
        <template #default="{ row }">{{ row.phone || '-' }}</template>
      </el-table-column>
      <el-table-column label="角色" width="90">
        <template #default="{ row }">
          <el-tag :type="row.role === 0 ? 'danger' : 'primary'" size="small">
            {{ row.role === 0 ? '管理员' : '学生' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status"
            :active-value="1"
            :inactive-value="0"
            :disabled="row.role === 0"
            @change="(val) => changeStatus(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="170" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button type="warning" size="small" link @click="resetPassword(row)">
            重置密码
          </el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const keyword = ref('')
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

const loadData = async (p = 1) => {
  page.value = p
  loading.value = true
  try {
    const res = await request.get('/admin/users', {
      params: { keyword: keyword.value || undefined, page: page.value, size: size.value }
    })
    list.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const changeStatus = async (row, val) => {
  try {
    await request.put(`/admin/users/${row.id}/status`, null, { params: { status: val } })
    row.status = val
    ElMessage.success(val === 1 ? '已启用' : '已禁用')
  } catch (e) {
    // 失败不修改本地状态
  }
}

const resetPassword = async (row) => {
  await ElMessageBox.confirm(`确定将用户「${row.username}」的密码重置为 123456 吗？`, '提示', {
    type: 'warning'
  })
  await request.put(`/admin/users/${row.id}/password`)
  ElMessage.success('密码已重置为 123456')
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
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
