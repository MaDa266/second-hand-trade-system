<template>
  <div class="theme-panel">
    <div class="setting-block">
      <div class="block-title">主题色</div>
      <div class="color-row">
        <span
          v-for="item in PRESET_COLORS"
          :key="item.value"
          class="color-dot"
          :class="{ active: settings.primaryColor.toLowerCase() === item.value.toLowerCase() }"
          :style="{ backgroundColor: item.value }"
          :title="item.name"
          @click="settings.setPrimaryColor(item.value)"
        />
        <el-color-picker
          :model-value="settings.primaryColor"
          :predefine="PRESET_COLORS.map((c) => c.value)"
          @change="onCustomColor"
        />
      </div>
      <div class="block-tip">当前：{{ settings.primaryColor }}</div>
    </div>

    <div class="setting-block">
      <div class="block-title">界面模式</div>
      <el-radio-group :model-value="settings.mode" @change="settings.setMode">
        <el-radio-button value="light">浅色</el-radio-button>
        <el-radio-button value="dark">深色</el-radio-button>
        <el-radio-button value="auto">跟随系统</el-radio-button>
      </el-radio-group>
    </div>

    <div class="setting-block">
      <div class="block-title">圆角大小</div>
      <el-slider
        :model-value="settings.radius"
        :min="0"
        :max="16"
        :step="2"
        show-stops
        :show-tooltip="false"
        class="radius-slider"
        @input="settings.setRadius"
      />
      <div class="block-tip">{{ settings.radius }} px（0 为直角，16 为圆润）</div>
    </div>

    <div class="setting-block">
      <div class="block-title">紧凑模式</div>
      <div class="switch-row">
        <el-switch :model-value="settings.compact" @change="settings.setCompact" />
        <span class="block-tip">开启后收紧卡片内边距与栅格间距，一屏能看到更多内容</span>
      </div>
    </div>

    <div class="setting-block">
      <div class="block-title">实时预览</div>
      <div class="preview">
        <div class="preview-row">
          <el-button type="primary">主要按钮</el-button>
          <el-button type="primary" plain>次要按钮</el-button>
          <el-button>默认按钮</el-button>
        </div>
        <div class="preview-row">
          <el-tag>标签</el-tag>
          <el-tag type="success" effect="plain">在售</el-tag>
          <el-tag type="warning" effect="plain">待审核</el-tag>
          <span class="preview-price">￥25.00</span>
        </div>
        <div class="preview-row">
          <el-input placeholder="输入框效果" class="preview-input" />
          <el-progress :percentage="66" class="preview-progress" />
        </div>
        <div class="preview-card">
          <div class="preview-cover" />
          <div class="preview-card-body">
            <div class="preview-card-title">高等数学教材（第七版）</div>
            <div class="preview-card-price">￥25.00</div>
          </div>
        </div>
      </div>
    </div>

    <div class="panel-footer">
      <el-button @click="settings.reset()">恢复默认外观</el-button>
    </div>
  </div>
</template>

<script setup>
import { useSettingsStore, PRESET_COLORS } from '@/stores/settings'

const settings = useSettingsStore()

const onCustomColor = (color) => {
  if (color) {
    settings.setPrimaryColor(color)
  }
}
</script>

<style scoped>
.setting-block {
  padding: 16px 0;
  border-bottom: 1px solid var(--app-border);
}

.setting-block:first-child {
  padding-top: 0;
}

.block-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--app-text-primary);
  margin-bottom: 12px;
}

.block-tip {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 8px;
}

.color-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.color-dot {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid transparent;
  box-shadow: 0 0 0 1px var(--app-border);
  transition: transform 0.15s;
}

.color-dot:hover {
  transform: scale(1.12);
}

.color-dot.active {
  border-color: var(--app-bg-elevated);
  box-shadow: 0 0 0 2px var(--el-color-primary);
}

.radius-slider {
  max-width: 360px;
  margin-left: 6px;
}

.switch-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.switch-row .block-tip {
  margin-top: 0;
}

.preview {
  background: var(--app-bg-soft);
  border-radius: var(--app-radius);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.preview-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.preview-price {
  color: var(--app-price);
  font-size: 18px;
  font-weight: 700;
}

.preview-input {
  width: 200px;
}

.preview-progress {
  flex: 1;
  min-width: 140px;
}

.preview-card {
  width: 200px;
  background: var(--app-bg-elevated);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  overflow: hidden;
}

.preview-cover {
  height: 90px;
  background: linear-gradient(135deg, var(--app-gradient-from) 0%, var(--app-gradient-to) 100%);
}

.preview-card-body {
  padding: 10px;
}

.preview-card-title {
  font-size: 13px;
  color: var(--app-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-card-price {
  color: var(--app-price);
  font-weight: 700;
  margin-top: 6px;
}

.panel-footer {
  padding-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
