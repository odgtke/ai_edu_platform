<template>
  <div class="settings-page">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>
      <el-form label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="settings.siteName" />
        </el-form-item>
        <el-form-item label="通知设置">
          <el-switch v-model="settings.enableNotification" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Settings',
  data() {
    return {
      settings: {
        siteName: '智慧教育平台',
        enableNotification: true
      }
    }
  },
  methods: {
    saveSettings() {
      this.$message.success('设置保存成功')
    }
  }
}
</script>
