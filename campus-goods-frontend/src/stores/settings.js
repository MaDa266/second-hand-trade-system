import { defineStore } from 'pinia'
import { applyTheme } from '@/utils/theme'

const STORAGE_KEY = 'app-appearance'

/** 默认外观 */
export const DEFAULT_APPEARANCE = {
  primaryColor: '#409eff',
  mode: 'light',
  radius: 8,
  compact: false
}

/** 可选主题色 */
export const PRESET_COLORS = [
  { name: '拂晓蓝', value: '#409eff' },
  { name: '薄暮红', value: '#f56c6c' },
  { name: '火山橙', value: '#fa8c16' },
  { name: '极光绿', value: '#52c41a' },
  { name: '青碧', value: '#13c2c2' },
  { name: '酱紫', value: '#722ed1' },
  { name: '洋红', value: '#eb2f96' },
  { name: '石墨', value: '#2f3542' }
]

function loadAppearance() {
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY))
    return { ...DEFAULT_APPEARANCE, ...(saved || {}) }
  } catch (e) {
    return { ...DEFAULT_APPEARANCE }
  }
}

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    ...loadAppearance(),
    /** 外观设置抽屉是否展开（全局共用） */
    drawerVisible: false
  }),
  getters: {
    /** 是否处于深色模式（mode = auto 时跟随系统） */
    isDark: (state) => {
      if (state.mode === 'dark') return true
      if (state.mode === 'auto') {
        return !!(window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches)
      }
      return false
    }
  },
  actions: {
    /** 应用当前设置并写入本地存储 */
    apply() {
      applyTheme({
        primaryColor: this.primaryColor,
        mode: this.mode,
        radius: this.radius,
        compact: this.compact
      })
      localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({
          primaryColor: this.primaryColor,
          mode: this.mode,
          radius: this.radius,
          compact: this.compact
        })
      )
    },
    setPrimaryColor(color) {
      this.primaryColor = color
      this.apply()
    },
    setMode(mode) {
      this.mode = mode
      this.apply()
    },
    setRadius(radius) {
      this.radius = radius
      this.apply()
    },
    setCompact(compact) {
      this.compact = compact
      this.apply()
    },
    /** 恢复默认外观 */
    reset() {
      Object.assign(this, DEFAULT_APPEARANCE)
      this.apply()
    },
    openDrawer() {
      this.drawerVisible = true
    },
    closeDrawer() {
      this.drawerVisible = false
    }
  }
})
