<template>
  <div v-if="isAuthPage">
    <router-view />
  </div>
  <div v-else-if="isAdminPage">
    <router-view />
  </div>
  <div v-else>
    <AppHeader />
    <main class="main-container">
      <router-view />
    </main>
  </div>

  <!-- 全局外观设置抽屉，前台与后台都能唤起 -->
  <ThemeDrawer />

  <!-- 管理员待办提醒弹窗（待审核商品 / 待处理举报） -->
  <AdminPendingAlert />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import ThemeDrawer from '@/components/ThemeDrawer.vue'
import AdminPendingAlert from '@/components/AdminPendingAlert.vue'

const route = useRoute()
const isAuthPage = computed(() => ['/login', '/register'].includes(route.path))
const isAdminPage = computed(() => route.path.startsWith('/admin'))
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  height: 100%;
}

/* 背景色与文字色由 styles/theme.css 按主题提供 */
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  font-size: 14px;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--app-gap) 16px 40px;
  min-height: calc(100vh - 61px);
}
</style>
