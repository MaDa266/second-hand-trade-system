<template>
  <div class="page-card">
    <div class="toolbar">
      <el-select
        v-model="query.module"
        placeholder="全部模块"
        clearable
        style="width: 150px"
        @change="loadData(1)"
      >
        <el-option v-for="m in modules" :key="m" :label="m" :value="m" />
      </el-select>
      <el-input
        v-model="query.keyword"
        placeholder="搜索操作人 / 动作 / 详情"
        clearable
        style="width: 240px"
        @keyup.enter="loadData(1)"
        @clear="loadData(1)"
      />
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px"
        @change="loadData(1)"
      />
      <el-button type="primary" @click="loadData(1)">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
      <el-button type="danger" plain class="right-btn" @click="openClear">清理日志</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="操作时间" width="160">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作人" width="120">
        <template #default="{ row }">
          <span>{{ row.username || '匿名' }}</span>
          <span v-if="row.userId" class="uid">#{{ row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="模块" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="moduleType(row.module)" effect="plain">{{ row.module }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="action" label="操作" width="130" />
      <el-table-column label="对象" width="110">
        <template #default="{ row }">{{ targetText(row) }}</template>
      </el-table-column>
      <el-table-column prop="detail" label="详情" min-width="220" show-overflow-tooltip />
      <el-table-column label="备注" width="150" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.remark" class="remark">{{ row.remark }}</span>
          <span v-else class="empty">-</span>
        </template>
      </el-table-column>
      <el-table-column label="来源IP" width="130">
        <template #default="{ row }">{{ row.ip || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="openDetail(row)">详情</el-button>
          <el-button type="warning" size="small" link @click="openRemark(row)">备注</el-button>
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

    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions v-if="current" :column="1" border>
        <el-descriptions-item label="操作时间">{{ formatDateTime(current.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="操作人">
          {{ current.username || '匿名' }}<span v-if="current.userId">（用户ID {{ current.userId }}）</span>
        </el-descriptions-item>
        <el-descriptions-item label="模块 / 操作">{{ current.module }} / {{ current.action }}</el-descriptions-item>
        <el-descriptions-item label="操作对象">{{ targetText(current) }}</el-descriptions-item>
        <el-descriptions-item label="详情">{{ current.detail || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ current.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="来源IP">{{ current.ip || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="detail-tip">
        操作人、动作、时间与详情属于审计证据，系统不允许修改；如需补充说明请编辑备注。
      </div>
    </el-dialog>

    <el-dialog v-model="remarkVisible" title="修改日志备注" width="460px">
      <el-input
        v-model="remarkForm.remark"
        type="textarea"
        :rows="3"
        maxlength="255"
        show-word-limit
        placeholder="填写处理说明，例如：已核实并警告该用户"
      />
      <template #footer>
        <el-button @click="remarkVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveRemark">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="clearVisible" title="清理日志" width="460px">
      <div class="clear-tip">
        留空表示<strong>清空全部日志</strong>，该操作不可恢复，请谨慎使用。
      </div>
      <el-date-picker
        v-model="clearBefore"
        type="date"
        placeholder="只清理该日期之前的日志"
        value-format="YYYY-MM-DD"
        style="width: 100%"
      />
      <template #footer>
        <el-button @click="clearVisible = false">取消</el-button>
        <el-button type="danger" :loading="clearing" @click="doClear">确认清理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/format'

const modules = ref([])
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const loading = ref(false)

const query = reactive({ module: '', keyword: '' })
const dateRange = ref([])

const detailVisible = ref(false)
const remarkVisible = ref(false)
const clearVisible = ref(false)
const current = ref(null)
const remarkForm = reactive({ id: null, remark: '' })
const clearBefore = ref('')
const saving = ref(false)
const clearing = ref(false)

const MODULE_TAG = {
  认证: 'info',
  商品: 'primary',
  留言: 'success',
  收藏: 'info',
  举报: 'warning',
  用户管理: 'danger',
  分类管理: 'primary',
  公告管理: 'warning',
  日志管理: 'danger'
}

const moduleType = (module) => MODULE_TAG[module] || 'info'

const targetText = (row) => {
  if (!row || !row.targetType) return '-'
  return row.targetId ? `${row.targetType}#${row.targetId}` : row.targetType
}

const loadModules = async () => {
  modules.value = (await request.get('/admin/log/modules')) || []
}

const loadData = async (p = 1) => {
  page.value = p
  loading.value = true
  try {
    const res = await request.get('/admin/log', {
      params: {
        module: query.module || undefined,
        keyword: query.keyword || undefined,
        startTime: dateRange.value?.[0] || undefined,
        endTime: dateRange.value?.[1] || undefined,
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

const resetQuery = () => {
  query.module = ''
  query.keyword = ''
  dateRange.value = []
  loadData(1)
}

const openDetail = (row) => {
  current.value = row
  detailVisible.value = true
}

const openRemark = (row) => {
  remarkForm.id = row.id
  remarkForm.remark = row.remark || ''
  remarkVisible.value = true
}

const saveRemark = async () => {
  saving.value = true
  try {
    await request.put(`/admin/log/${remarkForm.id}/remark`, { remark: remarkForm.remark })
    ElMessage.success('备注已保存')
    remarkVisible.value = false
    loadData(page.value)
  } finally {
    saving.value = false
  }
}

const remove = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定删除「${row.username} 在 ${formatDateTime(row.createTime)} 的${row.action}」这条日志吗？`,
      '提示',
      { type: 'warning' }
    )
  } catch (e) {
    return
  }
  await request.delete(`/admin/log/${row.id}`)
  ElMessage.success('删除成功')
  loadData(page.value)
}

const openClear = () => {
  clearBefore.value = ''
  clearVisible.value = true
}

const doClear = async () => {
  const tip = clearBefore.value ? `清理 ${clearBefore.value} 之前的日志` : '清空全部日志'
  try {
    await ElMessageBox.confirm(`${tip}，该操作不可恢复，确定继续吗？`, '二次确认', { type: 'warning' })
  } catch (e) {
    return
  }
  clearing.value = true
  try {
    const res = await request.delete('/admin/log', {
      params: { beforeTime: clearBefore.value || undefined }
    })
    ElMessage.success(`已清理 ${res?.deleted ?? 0} 条日志`)
    clearVisible.value = false
    loadData(1)
  } finally {
    clearing.value = false
  }
}

onMounted(() => {
  loadModules()
  loadData(1)
})
</script>

<style scoped>
.page-card {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: var(--app-card-padding);
}

.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.right-btn {
  margin-left: auto;
}

.uid {
  color: var(--app-text-secondary);
  font-size: 12px;
  margin-left: 4px;
}

.remark {
  color: var(--el-color-warning);
}

.empty {
  color: var(--app-text-placeholder);
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.detail-tip {
  margin-top: 14px;
  font-size: 12px;
  color: var(--app-text-secondary);
  line-height: 1.7;
}

.clear-tip {
  font-size: 13px;
  color: var(--app-text-regular);
  margin-bottom: 12px;
  line-height: 1.7;
}
</style>
