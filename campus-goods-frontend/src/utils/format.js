/**
 * 后端 LocalDateTime 序列化后是 "2026-10-15T21:00:22" 这样的字符串，
 * 这里统一格式化成 "2026-10-15 21:00" 便于页面展示。
 * 兼容 Jackson 按数组返回时间的情况（[2026,10,15,21,0,22]）。
 */
export function formatDateTime(value) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  const pad = (n) => String(n).padStart(2, '0')

  if (Array.isArray(value)) {
    const [y, mo, d, h = 0, mi = 0] = value
    if (!y || !mo || !d) return String(value)
    return `${y}-${pad(mo)}-${pad(d)} ${pad(h)}:${pad(mi)}`
  }

  const date = value instanceof Date ? value : new Date(String(value).replace(' ', 'T'))
  if (Number.isNaN(date.getTime())) {
    return String(value)
  }
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}
