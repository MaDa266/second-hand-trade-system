<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <el-icon :size="22"><Goods /></el-icon>
        <span>校园闲置 · 管理后台</span>
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/goods">
          <el-icon><Goods /></el-icon>
          <span>商品审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/report">
          <el-icon><Warning /></el-icon>
          <span>举报管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notice">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/log">
          <el-icon><Document /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="breadcrumb">{{ currentTitle }}</div>
        <div class="right">
          <el-badge
            :value="pending.totalCount"
            :hidden="pending.totalCount === 0"
            type="danger"
            class="pending-badge"
          >
            <el-button text type="primary" @click="pending.openManually()">
              <el-icon style="margin-right: 4px"><BellFilled /></el-icon>待办
            </el-button>
          </el-badge>
          <el-button text type="primary" @click="settings.openDrawer()">
            <el-icon style="margin-right: 4px"><Brush /></el-icon>外观设置
          </el-button>
          <el-button text type="primary" @click="goHome">返回前台</el-button>
          <el-dropdown @command="handleCommand">
            <span class="admin-name">
              <el-icon><Avatar /></el-icon>
              {{ userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSettingsStore } from '@/stores/settings'
import { usePendingStore } from '@/stores/pending'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const settings = useSettingsStore()
const pending = usePendingStore()

const titleMap = {
  '/admin/dashboard': '数据统计',
  '/admin/users': '用户管理',
  '/admin/category': '分类管理',
  '/admin/goods': '商品审核',
  '/admin/report': '举报管理',
  '/admin/notice': '公告管理',
  '/admin/log': '操作日志'
}
const currentTitle = computed(() => titleMap[route.path] || '管理后台')

const goHome = () => router.push('/')

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    } catch (e) {
      return
    }
    userStore.logout()
    pending.clear()
    router.push('/login')
  }
}

// 进入后台时刷新待办数量，显示在顶部「待办」角标上
onMounted(() => {
  pending.refresh().catch(() => {})
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.aside {
  background-color: #001529;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #ffffff;
  font-size: 15px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.aside :deep(.el-menu) {
  border-right: none;
}

.header {
  background-color: var(--app-bg-elevated);
  border-bottom: 1px solid var(--app-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.breadcrumb {
  font-size: 16px;
  font-weight: bold;
}

.right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 待办角标：数字往右上角挪一点，避免和按钮文字重叠 */
.pending-badge :deep(.el-badge__content) {
  top: 8px;
  right: 12px;
}

.admin-name {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: var(--app-text-primary);
}

.main {
  background-color: var(--app-bg);
  padding: 20px;
}
</style>
