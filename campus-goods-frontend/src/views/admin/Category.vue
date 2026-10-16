<template>
  <div class="page-card">
    <div class="toolbar">
      <el-button type="primary" @click="openDialog(null)">
        <el-icon><Plus /></el-icon>新增分类
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" min-width="150" />
      <el-table-column prop="sort" label="排序" width="90" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="(val) => changeStatus(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="openDialog(row)">编辑</el-button>
          <el-button type="danger" size="small" link @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" />
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

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const form = ref({ id: null, name: '', sort: 0, status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    list.value = (await request.get('/admin/category')) || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  form.value = row
    ? { id: row.id, name: row.name, sort: row.sort, status: row.status }
    : { id: null, name: '', sort: 0, status: 1 }
  dialogVisible.value = true
}

const save = async () => {
  if (!form.value.name || !form.value.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  saving.value = true
  try {
    const body = { name: form.value.name.trim(), sort: form.value.sort, status: form.value.status }
    if (form.value.id) {
      await request.put(`/admin/category/${form.value.id}`, body)
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/category', body)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const changeStatus = async (row, val) => {
  try {
    await request.put(`/admin/category/${row.id}`, { name: row.name, sort: row.sort, status: val })
    row.status = val
    ElMessage.success(val === 1 ? '已启用' : '已禁用')
  } catch (e) {
    // 失败不修改本地状态
  }
}

const remove = async (row) => {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await request.delete(`/admin/category/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
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
</style>
