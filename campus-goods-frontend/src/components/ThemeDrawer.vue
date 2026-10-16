<template>
  <el-drawer
    :model-value="settings.drawerVisible"
    title="外观设置"
    direction="rtl"
    size="440px"
    :append-to-body="true"
    @update:model-value="handleVisible"
  >
    <ThemeSettingsPanel />
  </el-drawer>
</template>

<script setup>
import ThemeSettingsPanel from '@/components/ThemeSettingsPanel.vue'
import { useSettingsStore } from '@/stores/settings'

const settings = useSettingsStore()

const handleVisible = (visible) => {
  if (visible) {
    settings.openDrawer()
  } else {
    settings.closeDrawer()
  }
}
</script>
