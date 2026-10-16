import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
  { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
  { path: '/goods/:id', name: 'GoodsDetail', component: () => import('@/views/GoodsDetail.vue') },
  { path: '/publish', name: 'Publish', component: () => import('@/views/Publish.vue'), meta: { requireAuth: true } },
  { path: '/user', name: 'User', component: () => import('@/views/User.vue'), meta: { requireAuth: true } },
  { path: '/user/favorite', name: 'UserFavorite', component: () => import('@/views/UserFavorite.vue'), meta: { requireAuth: true } },
  { path: '/user/message', name: 'UserMessage', component: () => import('@/views/UserMessage.vue'), meta: { requireAuth: true } },
  { path: '/settings', name: 'Settings', component: () => import('@/views/Settings.vue') },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { requireAuth: true, requireAdmin: true } },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue'), meta: { requireAuth: true, requireAdmin: true } },
      { path: 'category', name: 'AdminCategory', component: () => import('@/views/admin/Category.vue'), meta: { requireAuth: true, requireAdmin: true } },
      { path: 'goods', name: 'AdminGoods', component: () => import('@/views/admin/GoodsAudit.vue'), meta: { requireAuth: true, requireAdmin: true } },
      { path: 'report', name: 'AdminReport', component: () => import('@/views/admin/Report.vue'), meta: { requireAuth: true, requireAdmin: true } },
      { path: 'notice', name: 'AdminNotice', component: () => import('@/views/admin/Notice.vue'), meta: { requireAuth: true, requireAdmin: true } },
      { path: 'log', name: 'AdminLog', component: () => import('@/views/admin/Log.vue'), meta: { requireAuth: true, requireAdmin: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requireAuth && !userStore.isLogin) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  if (to.meta.requireAdmin && !userStore.isAdmin) {
    next('/')
    return
  }
  next()
})

export default router
