<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-title">
        <el-icon :size="32" class="brand-icon"><Goods /></el-icon>
        <h2>注册账号</h2>
      </div>
      <p class="register-sub">加入校园闲置物品交易平台</p>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" :prefix-icon="Postcard" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" :prefix-icon="Iphone" maxlength="11" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            @keyup.enter="handleRegister"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister">注 册</el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>已有账号？</span>
        <el-link type="primary" :underline="false" @click="router.push('/login')">去登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Postcard } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await request.post('/auth/register', {
        username: form.username,
        nickname: form.nickname,
        phone: form.phone,
        password: form.password
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--app-auth-bg);
}

.register-card {
  width: 420px;
  background: var(--app-bg-elevated);
  border-radius: 12px;
  padding: 40px 36px 28px;
  box-shadow: var(--app-shadow);
}

.register-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* Logo 跟随主题色变化 */
.brand-icon {
  color: var(--el-color-primary);
}

.register-title h2 {
  font-size: 20px;
  color: var(--app-text-primary);
}

.register-sub {
  text-align: center;
  color: var(--app-text-secondary);
  font-size: 13px;
  margin: 8px 0 24px;
}

.register-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  color: var(--app-text-secondary);
  font-size: 13px;
}
</style>
