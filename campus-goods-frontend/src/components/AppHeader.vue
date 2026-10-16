<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="logo" @click="router.push('/')">
        <el-icon :size="26" class="logo-icon"><Goods /></el-icon>
        <span class="logo-text">校园闲置</span>
      </div>
      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="搜索闲置物品，回车搜索"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      <div class="header-right">
        <el-tooltip
          :content="settings.isDark ? '切换到浅色模式' : '切换到深色模式'"
          placement="bottom"
        >
          <el-button circle text class="theme-toggle" @click="toggleTheme">
            <el-icon :size="18">
              <Sunny v-if="settings.isDark" />
              <Moon v-else />
            </el-icon>
          </el-button>
        </el-tooltip>
        <template v-if="userStore.isLogin">
          <el-dropdown @command="handleCommand">
            <span class="user-trigger">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar || ''">
                {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || '用')[0] }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="/publish">发布商品</el-dropdown-item>
                <el-dropdown-item command="/user">个人中心</el-dropdown-item>
                <el-dropdown-item command="/user/favorite">我的收藏</el-dropdown-item>
                <el-dropdown-item command="/user/message">我的留言</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" command="/admin">后台管理</el-dropdown-item>
                <el-dropdown-item divided command="/settings">设置</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" plain @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
          <el-button text @click="router.push('/settings')">设置</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useSettingsStore } from '@/stores/settings'
import { usePendingStore } from '@/stores/pending'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const settings = useSettingsStore()
const pendingStore = usePendingStore()

/** 顶栏快捷切换浅色 / 深色 */
const toggleTheme = () => {
  settings.setMode(settings.isDark ? 'light' : 'dark')
}

const keyword = ref(route.query.keyword || '')

watch(
  () => route.query.keyword,
  (val) => {
    if (route.path === '/') {
      keyword.value = val || ''
    }
  }
)

const handleSearch = () => {
  router.push({ path: '/', query: keyword.value ? { keyword: keyword.value } : {} })
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    pendingStore.clear()
    router.push('/login')
    return
  }
  router.push(command)
}
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: var(--app-bg-elevated);
  border-bottom: 1px solid var(--app-border-strong);
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 16px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  flex-shrink: 0;
}

/* Logo 跟随主题色变化 */
.logo-icon {
  color: var(--el-color-primary);
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--app-text-primary);
}

.search-box {
  flex: 1;
  max-width: 420px;
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
  padding: 4px 6px;
  border-radius: 6px;
}

.user-trigger:hover {
  background: var(--app-bg-soft);
}

.user-name {
  font-size: 14px;
  color: var(--app-text-primary);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
