/**
 * 外观设置的应用逻辑：
 * 1. 通过覆盖 Element Plus 的 CSS 变量实现主题色 / 圆角；
 * 2. 通过 html 上的 dark / compact 类切换深色与紧凑模式；
 * 3. 所有取值都写在 documentElement 的行内样式上，优先级高于样式表。
 */

/** Element Plus 需要成套计算的浅色梯度 */
const LIGHT_SHADES = [3, 5, 7, 8, 9]

/** 深色主题下用于混合的底色，与 Element Plus 深色背景保持一致 */
const DARK_MIX_BASE = '#141414'

const clamp = (value, min, max) => Math.min(Math.max(value, min), max)

function hexToRgb(hex) {
  let value = String(hex || '').trim().replace('#', '')
  if (value.length === 3) {
    value = value.split('').map((c) => c + c).join('')
  }
  if (!/^[0-9a-fA-F]{6}$/.test(value)) {
    return null
  }
  return {
    r: parseInt(value.slice(0, 2), 16),
    g: parseInt(value.slice(2, 4), 16),
    b: parseInt(value.slice(4, 6), 16)
  }
}

function toHex({ r, g, b }) {
  const h = (n) => clamp(Math.round(n), 0, 255).toString(16).padStart(2, '0')
  return `#${h(r)}${h(g)}${h(b)}`
}

/**
 * 颜色混合：weight 表示 to 的占比（0~1）
 * 与 Element Plus 的 light-i / dark-2 计算方式一致
 */
export function mixColor(from, to, weight) {
  const a = hexToRgb(from)
  const b = hexToRgb(to)
  if (!a || !b) {
    return from
  }
  const w = clamp(weight, 0, 1)
  return toHex({
    r: a.r + (b.r - a.r) * w,
    g: a.g + (b.g - a.g) * w,
    b: a.b + (b.b - a.b) * w
  })
}

/** 是否为深色（mode = auto 时跟随系统） */
export function resolveDark(mode) {
  if (mode === 'dark') {
    return true
  }
  if (mode === 'auto') {
    return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
  }
  return false
}

/**
 * 把设置写入 DOM
 * @param {{primaryColor:string, mode:string, radius:number, compact:boolean}} settings
 */
export function applyTheme(settings) {
  const root = document.documentElement
  const dark = resolveDark(settings.mode)
  const radius = clamp(Number(settings.radius) || 0, 0, 20)
  const primary = hexToRgb(settings.primaryColor) ? settings.primaryColor : '#409eff'

  root.classList.toggle('dark', dark)
  root.classList.toggle('compact', !!settings.compact)

  // 主题色及其梯度：浅色模式向白色混合，深色模式向深色底混合
  const lightBase = dark ? DARK_MIX_BASE : '#ffffff'
  const darkBase = dark ? '#ffffff' : '#000000'
  root.style.setProperty('--el-color-primary', primary)
  LIGHT_SHADES.forEach((i) => {
    root.style.setProperty(`--el-color-primary-light-${i}`, mixColor(primary, lightBase, i / 10))
  })
  root.style.setProperty('--el-color-primary-dark-2', mixColor(primary, darkBase, 0.2))

  // 圆角
  root.style.setProperty('--el-border-radius-base', `${radius}px`)
  root.style.setProperty('--el-border-radius-small', `${Math.max(radius - 2, 0)}px`)
  root.style.setProperty('--app-radius', `${radius}px`)

  // 让浏览器原生控件（滚动条等）也跟着切换
  root.style.colorScheme = dark ? 'dark' : 'light'
}

/**
 * 监听系统主题变化，仅在 mode = auto 时回调
 */
export function watchSystemTheme(callback) {
  if (!window.matchMedia) {
    return () => {}
  }
  const query = window.matchMedia('(prefers-color-scheme: dark)')
  const handler = () => callback()
  if (query.addEventListener) {
    query.addEventListener('change', handler)
    return () => query.removeEventListener('change', handler)
  }
  if (query.addListener) {
    query.addListener(handler)
    return () => query.removeListener(handler)
  }
  return () => {}
}
