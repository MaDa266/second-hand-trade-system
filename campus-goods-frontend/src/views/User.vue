<template>
  <div class="user-page">
    <div class="user-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的发布" name="goods">
          <el-table :data="goodsList" v-loading="goodsLoading" stripe>
            <el-table-column label="封面" width="90">
              <template #default="{ row }">
                <img v-if="row.cover" :src="row.cover" class="table-cover" />
                <div v-else class="table-cover cover-placeholder">
                  <el-icon :size="20" color="#c0c4cc"><Picture /></el-icon>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column label="价格" width="100">
              <template #default="{ row }">
                <span class="price-text">￥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="110">
              <template #default="{ row }">
                <el-tag size="small" :type="statusMap[row.status]?.type || 'info'">
                  {{ statusMap[row.status]?.text || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="170" />
            <el-table-column label="操作" width="230" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" plain @click="router.push(`/publish?id=${row.id}`)">编辑</el-button>
                <el-button v-if="row.status === 1" size="small" type="success" plain @click="markSold(row)">标记已售</el-button>
                <el-button size="small" type="danger" plain @click="deleteGoods(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrap">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="goodsTotal"
              :page-size="size"
              :current-page="page"
              @current-change="handlePage"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="资料修改" name="profile">
          <el-form :model="profileForm" label-width="80px" class="profile-form">
            <el-form-item label="头像">
              <div class="avatar-row">
                <el-avatar :size="64" :src="profileForm.avatar || ''">
                  {{ (profileForm.nickname || userStore.userInfo?.username || '用')[0] }}
                </el-avatar>
                <el-upload
                  action="/api/upload"
                  name="file"
                  accept="image/*"
                  :show-file-list="false"
                  :headers="uploadHeaders"
                  :on-success="handleAvatarSuccess"
                  :on-error="handleAvatarError"
                >
                  <el-button size="small" type="primary" plain>上传头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item label="用户名">
              <el-input :model-value="userStore.userInfo?.username" disabled style="width: 260px" />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="20" style="width: 260px" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" maxlength="11" style="width: 260px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('goods')
const goodsList = ref([])
const goodsTotal = ref(0)
const page = ref(1)
const size = 10
const goodsLoading = ref(false)
const saving = ref(false)

const statusMap = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '在售', type: 'success' },
  2: { text: '已售', type: 'info' },
  3: { text: '已下架', type: 'info' },
  4: { text: '审核不通过', type: 'danger' }
}

const profileForm = reactive({
  nickname: userStore.userInfo?.nickname || '',
  phone: userStore.userInfo?.phone || '',
  avatar: userStore.userInfo?.avatar || ''
})

const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}))

const loadMyGoods = async () => {
  goodsLoading.value = true
  try {
    const res = await request.get('/goods/my', {
      params: { page: page.value, size }
    })
    if (Array.isArray(res)) {
      goodsList.value = res
      goodsTotal.value = res.length
    } else {
      goodsList.value = res?.records || []
      goodsTotal.value = res?.total || 0
    }
  } finally {
    goodsLoading.value = false
  }
}

const handlePage = (p) => {
  page.value = p
  loadMyGoods()
}

const markSold = async (row) => {
  await request.put(`/goods/${row.id}/sold`)
  ElMessage.success('已标记为已售')
  loadMyGoods()
}

const deleteGoods = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除商品「${row.title}」吗？删除后不可恢复`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await request.delete(`/goods/${row.id}`)
  ElMessage.success('删除成功')
  loadMyGoods()
}

const handleAvatarSuccess = (res) => {
  if (res.code === 200 && res.data) {
    profileForm.avatar = res.data
    ElMessage.success('头像上传成功，记得点击保存修改')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const handleAvatarError = () => {
  ElMessage.error('上传失败，请重试')
}

const saveProfile = async () => {
  saving.value = true
  try {
    const res = await request.put('/auth/me', {
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      avatar: profileForm.avatar
    })
    userStore.setLogin({ token: userStore.token, user: res })
    ElMessage.success('资料修改成功')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadMyGoods()
})
</script>

<style scoped>
.user-card {
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 10px 20px 20px;
}

.table-cover {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}

.cover-placeholder {
  background: linear-gradient(135deg, var(--app-gradient-from) 0%, var(--app-gradient-to) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.price-text {
  color: var(--app-price);
  font-weight: 600;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding-top: 16px;
}

.profile-form {
  max-width: 460px;
  padding-top: 10px;
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>
