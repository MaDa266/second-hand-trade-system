import { defineStore } from 'pinia'
import request from '@/utils/request'

/**
 * 管理员待办提醒
 * 管理员登录后拉取一次待审核商品与待处理举报，按类型分别弹窗；
 * 弹窗状态放在 store 里，由 App.vue 中的 AdminPendingAlert 统一渲染。
 */
export const usePendingStore = defineStore('pending', {
  state: () => ({
    loading: false,
    /** 待审核商品数量 */
    pendingGoodsCount: 0,
    /** 待处理举报数量 */
    pendingReportsCount: 0,
    /** 待审核商品预览列表 */
    pendingGoods: [],
    /** 待处理举报预览列表 */
    pendingReports: [],
    /** 两个弹窗的显隐 */
    goodsVisible: false,
    reportVisible: false
  }),
  getters: {
    totalCount: (state) => (state.pendingGoodsCount || 0) + (state.pendingReportsCount || 0)
  },
  actions: {
    async refresh() {
      this.loading = true
      try {
        const res = await request.get('/admin/pending')
        this.pendingGoodsCount = res?.pendingGoodsCount || 0
        this.pendingReportsCount = res?.pendingReportsCount || 0
        this.pendingGoods = res?.pendingGoods || []
        this.pendingReports = res?.pendingReports || []
      } finally {
        this.loading = false
      }
    },

    /**
     * 管理员登录后调用：先弹「待审核商品」，关掉后若还有待处理举报再弹第二个
     */
    async alertOnLogin() {
      try {
        await this.refresh()
      } catch (e) {
        // 待办提醒属于附加功能，拉取失败不打扰登录流程
        return
      }
      if (this.pendingGoodsCount > 0) {
        this.goodsVisible = true
      } else if (this.pendingReportsCount > 0) {
        this.reportVisible = true
      }
    },

    /** 关闭商品弹窗：还有举报待办就接着弹第二个 */
    closeGoods() {
      this.goodsVisible = false
      if (this.pendingReportsCount > 0) {
        this.reportVisible = true
      }
    },

    closeReport() {
      this.reportVisible = false
    },

    /** 后台顶部「待办」按钮手动唤起 */
    async openManually() {
      await this.refresh()
      if (this.pendingGoodsCount > 0) {
        this.goodsVisible = true
      } else if (this.pendingReportsCount > 0) {
        this.reportVisible = true
      } else {
        this.goodsVisible = false
        this.reportVisible = false
      }
    },

    /** 退出登录时清空，避免换账号后串数据 */
    clear() {
      this.pendingGoodsCount = 0
      this.pendingReportsCount = 0
      this.pendingGoods = []
      this.pendingReports = []
      this.goodsVisible = false
      this.reportVisible = false
    }
  }
})
