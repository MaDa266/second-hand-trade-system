<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-title">
        <el-icon :size="32" class="brand-icon"><Goods /></el-icon>
        <h2>校园闲置物品交易平台</h2>
      </div>
      <p class="login-sub">让校园里的闲置物品流动起来</p>
      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>没有账号？</span>
        <el-link type="primary" :underline="false" @click="router.push('/register')">去注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { usePendingStore } from '@/stores/pending'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const pendingStore = usePendingStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await request.post('/auth/login', {
        username: form.username,
        password: form.password
      })
      userStore.setLogin(res)
      ElMessage.success('登录成功')
      if (userStore.isAdmin) {
        router.push('/admin')
        // 管理员登录后检查待办：有待审核商品 / 待处理举报时依次弹窗提醒
        pendingStore.alertOnLogin()
      } else {
        pendingStore.clear()
        router.push(route.query.redirect || '/')
      }
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--app-auth-bg);
}

.login-card {
  width: 400px;
  background: var(--app-bg-elevated);
  border-radius: 12px;
  padding: 40px 36px 28px;
  box-shadow: var(--app-shadow);
}

.login-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* Logo 跟随主题色变化 */
.brand-icon {
  color: var(--el-color-primary);
}

.login-title h2 {
  font-size: 20px;
  color: var(--app-text-primary);
}

.login-sub {
  text-align: center;
  color: var(--app-text-secondary);
  font-size: 13px;
  margin: 8px 0 24px;
}

.login-btn {
  width: 100%;
}

.login-footer {
  text-align: center;
  color: var(--app-text-secondary);
  font-size: 13px;
}
</style>
