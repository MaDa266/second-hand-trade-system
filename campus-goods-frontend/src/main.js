import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// Element Plus 深色主题变量（配合 html.dark 使用）
import 'element-plus/theme-chalk/dark/css-vars.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { useSettingsStore } from './stores/settings'
import { watchSystemTheme } from './utils/theme'
// 放在 Element Plus 样式之后，保证应用级主题变量优先级正确
import './styles/theme.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 启动时应用本地保存的外观设置，避免刷新后主题丢失
const settings = useSettingsStore()
settings.apply()

// 选择「跟随系统」时，系统切换深浅色要同步刷新
watchSystemTheme(() => {
  if (settings.mode === 'auto') {
    settings.apply()
  }
})

app.mount('#app')
