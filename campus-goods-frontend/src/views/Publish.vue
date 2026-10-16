<template>
  <div class="publish-page">
    <div class="publish-card">
      <h3 class="page-title">{{ editId ? '编辑商品' : '发布商品' }}</h3>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="publish-form">
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 240px">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格（元）" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0.01"
            :max="999999"
            :precision="2"
            :step="1"
            controls-position="right"
            placeholder="请输入售价"
          />
        </el-form-item>
        <el-form-item label="原价（元）">
          <el-input-number
            v-model="form.oldPrice"
            :min="0"
            :max="999999"
            :precision="2"
            :step="1"
            controls-position="right"
            placeholder="选填"
          />
        </el-form-item>
        <el-form-item label="成色">
          <el-select v-model="form.conditionLevel" placeholder="请选择成色" style="width: 240px" clearable>
            <el-option v-for="item in conditionOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易地点" prop="tradePlace">
          <el-input v-model="form.tradePlace" placeholder="如：东区宿舍楼下 / 图书馆门口" style="width: 320px" maxlength="100" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            maxlength="1000"
            show-word-limit
            placeholder="描述一下商品的来源、使用情况、转手原因等"
          />
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            v-model:file-list="fileList"
            action="/api/upload"
            name="file"
            list-type="picture-card"
            multiple
            accept="image/*"
            :limit="9"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleUploadRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传 9 张图片，第一张将作为封面展示</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ editId ? '保存修改' : '提交发布' }}
          </el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const editId = ref(route.query.id || null)
const categories = ref([])
const images = ref([])
const fileList = ref([])
const submitting = ref(false)

const form = reactive({
  title: '',
  categoryId: null,
  price: null,
  oldPrice: null,
  conditionLevel: '',
  tradePlace: '',
  description: ''
})

const conditionOptions = ['全新', '九成新', '八成新', '轻微使用', '明显使用']

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  tradePlace: [{ required: true, message: '请输入交易地点', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }]
}

const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}))

const handleUploadSuccess = (res) => {
  if (res.code === 200 && res.data) {
    images.value.push(res.data)
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败，请重试')
}

const handleUploadRemove = (file) => {
  const url = file?.response?.data || file?.url
  const idx = images.value.indexOf(url)
  if (idx > -1) {
    images.value.splice(idx, 1)
  }
}

const resetForm = () => {
  form.title = ''
  form.categoryId = null
  form.price = null
  form.oldPrice = null
  form.conditionLevel = ''
  form.tradePlace = ''
  form.description = ''
  images.value = []
  fileList.value = []
}

const fillForm = (g) => {
  form.title = g.title || ''
  form.categoryId = g.categoryId ?? null
  form.price = g.price != null ? Number(g.price) : null
  form.oldPrice = g.oldPrice != null ? Number(g.oldPrice) : null
  form.conditionLevel = g.conditionLevel || ''
  form.tradePlace = g.tradePlace || ''
  form.description = g.description || ''
  const imgs = g.images ? g.images.split(',').map((u) => u.trim()).filter(Boolean) : []
  if (g.cover && !imgs.includes(g.cover)) {
    imgs.unshift(g.cover)
  }
  images.value = imgs
  fileList.value = imgs.map((u) => ({ name: u, url: u, status: 'success' }))
}

const initForm = async () => {
  editId.value = route.query.id || null
  if (editId.value) {
    const res = await request.get(`/goods/${editId.value}`)
    if (res) fillForm(res)
  } else {
    resetForm()
  }
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    const payload = {
      title: form.title,
      categoryId: form.categoryId,
      price: form.price,
      oldPrice: form.oldPrice || null,
      cover: images.value[0] || '',
      images: images.value.join(','),
      conditionLevel: form.conditionLevel,
      tradePlace: form.tradePlace,
      description: form.description
    }
    submitting.value = true
    try {
      if (editId.value) {
        await request.put(`/goods/${editId.value}`, payload)
        ElMessage.success('修改成功')
        router.push('/user')
      } else {
        await request.post('/goods', payload)
        ElMessage.success('已提交，等待管理员审核')
        router.push('/')
      }
    } finally {
      submitting.value = false
    }
  })
}

watch(
  () => route.query.id,
  () => {
    if (route.path === '/publish') {
      initForm()
    }
  }
)

onMounted(async () => {
  const res = await request.get('/goods/categories')
  categories.value = res || []
  if (editId.value) {
    const res = await request.get(`/goods/${editId.value}`)
    if (res) fillForm(res)
  }
})
</script>

<style scoped>
.publish-page {
  display: flex;
  justify-content: center;
}

.publish-card {
  width: 760px;
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 24px 32px 32px;
}

.page-title {
  font-size: 18px;
  color: var(--app-text-primary);
  margin-bottom: 24px;
  padding-left: 8px;
  border-left: 3px solid var(--el-color-primary);
}

.upload-tip {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 4px;
}
</style>
