<template>
  <div class="help-page">
    <el-card>
      <template #header>
        <span>帮助中心</span>
      </template>
      <el-collapse>
        <el-collapse-item title="如何开始使用系统？" name="1">
          <div>1. 登录系统后，查看数据概览页面了解整体情况</div>
          <div>2. 选择左侧菜单中的功能模块开始使用</div>
          <div>3. 使用用户管理功能添加和管理用户数据</div>
        </el-collapse-item>
        <el-collapse-item title="如何添加新用户？" name="2">
          <div>1. 点击左侧菜单"用户管理"</div>
          <div>2. 点击页面右上角的"添加用户"按钮</div>
          <div>3. 填写用户信息并保存</div>
        </el-collapse-item>
        <el-collapse-item title="如何使用AI智能助手？" name="3">
          <div>1. 点击左侧菜单"智能助手"</div>
          <div>2. 在输入框中输入您的问题</div>
          <div>3. AI助手将为您提供智能回答</div>
        </el-collapse-item>
        <el-collapse-item title="如何管理课程资源？" name="4">
          <div>1. 点击左侧菜单"课程管理"查看所有课程</div>
          <div>2. 点击"资源管理"上传和管理课程资源</div>
          <div>3. 支持视频、文档、图片等多种格式</div>
        </el-collapse-item>
        <el-collapse-item title="如何查看学习进度？" name="5">
          <div>1. 点击左侧菜单"学习中心"</div>
          <div>2. 查看学习统计和日历</div>
          <div>3. 管理学习记录和进度</div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.help-page {
  max-width: 800px;
}
</style>
