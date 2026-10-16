<template>
  <div class="settings-page">
    <div class="settings-card">
      <h3 class="page-title">设置</h3>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="外观设置" name="theme">
          <ThemeSettingsPanel />
        </el-tab-pane>

        <el-tab-pane label="账号信息" name="account">
          <template v-if="userStore.isLogin">
            <el-descriptions :column="1" border class="account-desc">
              <el-descriptions-item label="用户名">
                {{ userStore.userInfo?.username }}
              </el-descriptions-item>
              <el-descriptions-item label="昵称">
                {{ userStore.userInfo?.nickname || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="手机号">
                {{ userStore.userInfo?.phone || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="角色">
                <el-tag size="small" :type="userStore.isAdmin ? 'danger' : 'primary'">
                  {{ userStore.isAdmin ? '管理员' : '学生' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <div class="account-actions">
              <el-button type="primary" @click="router.push('/user')">去个人中心修改资料</el-button>
              <el-button @click="router.push('/user/favorite')">我的收藏</el-button>
              <el-button @click="router.push('/user/message')">我的留言</el-button>
            </div>
          </template>
          <el-empty v-else description="登录后可以查看账号信息">
            <el-button type="primary" @click="router.push('/login')">去登录</el-button>
          </el-empty>
        </el-tab-pane>

        <el-tab-pane label="关于系统" name="about">
          <el-descriptions :column="1" border class="account-desc">
            <el-descriptions-item label="系统名称">校园闲置物品交易平台</el-descriptions-item>
            <el-descriptions-item label="技术栈">
              SpringBoot + MyBatis-Plus + MySQL + Vue 3 + Element Plus
            </el-descriptions-item>
            <el-descriptions-item label="说明">
              本系统仅用于课程实验与模拟交易，不接入真实支付功能。
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import ThemeSettingsPanel from '@/components/ThemeSettingsPanel.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('theme')
</script>

<style scoped>
.settings-page {
  display: flex;
  justify-content: center;
}

.settings-card {
  width: 760px;
  background: var(--app-bg-elevated);
  border-radius: var(--app-radius);
  padding: 10px 24px 24px;
}

.page-title {
  font-size: 18px;
  color: var(--app-text-primary);
  margin: 14px 0 10px;
  padding-left: 8px;
  border-left: 3px solid var(--el-color-primary);
}

.account-desc {
  max-width: 560px;
}

.account-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
