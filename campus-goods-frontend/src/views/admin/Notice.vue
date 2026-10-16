<template>
  <div class="page-card">
    <div class="toolbar">
      <el-button type="primary" @click="openDialog(null)">
        <el-icon><Plus /></el-icon>发布公告
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="240" show-overflow-tooltip />
      <el-table-column label="发布时间" width="160">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" size="small" link @click="remove(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '发布公告'" width="560px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            maxlength="5000"
            show-word-limit
            placeholder="请输入公告内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/format'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const form = ref({ id: null, title: '', content: '' })

const loadData = async (p = 1) => {
  page.value = p
  loading.value = true
  try {
    const res = await request.get('/admin/notice', {
      params: { page: page.value, size: size.value }
    })
    list.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  form.value = row
    ? { id: row.id, title: row.title, content: row.content || '' }
    : { id: null, title: '', content: '' }
  dialogVisible.value = true
}

const save = async () => {
  if (!form.value.title || !form.value.title.trim()) {
    ElMessage.warning('请输入公告标题')
    return
  }
  saving.value = true
  try {
    const body = { title: form.value.title.trim(), content: form.value.content }
    if (form.value.id) {
      await request.put(`/admin/notice/${form.value.id}`, body)
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/notice', body)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadData(page.value)
  } finally {
    saving.value = false
  }
}

const remove = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除公告「${row.title}」吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await request.delete(`/admin/notice/${row.id}`)
  ElMessage.success('删除成功')
  loadData(page.value)
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
  justify-content: flex-end;
  margin-bottom: 16px;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
