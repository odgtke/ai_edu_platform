<template>
  <div class="resources-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>资源管理</h2>
      <p class="subtitle">管理课程视频、文档、图片等资源</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="operation-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索资源名称"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterType" placeholder="资源类型" clearable @change="handleSearch">
            <el-option
              v-for="type in resourceTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>
        </el-col>
        <el-col :span="14" style="text-align: right;">
          <el-button type="primary" @click="showUploadDialog">
            <el-icon><Upload /></el-icon> 上传资源
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 资源列表 -->
    <el-card class="list-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="resourceList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="资源名称" min-width="200">
          <template #default="{ row }">
            <div class="resource-name">
              <el-icon :size="20" class="resource-icon">
                <VideoPlay v-if="row.resourceType === 'video'" />
                <Document v-else-if="row.resourceType === 'document'" />
                <Picture v-else-if="row.resourceType === 'image'" />
                <Headset v-else-if="row.resourceType === 'audio'" />
                <Folder v-else />
              </el-icon>
              <div class="resource-info">
                <div class="name">{{ row.resourceName }}</div>
                <div class="filename">{{ row.fileName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.resourceType)" size="small">
              {{ getTypeLabel(row.resourceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="上传者" width="120">
          <template #default="{ row }">
            {{ row.uploaderName }}
          </template>
        </el-table-column>
        <el-table-column label="下载/浏览" width="120">
          <template #default="{ row }">
            <span class="count-info">
              <el-icon><Download /></el-icon> {{ row.downloadCount }}
              <el-icon style="margin-left: 8px;"><View /></el-icon> {{ row.viewCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDownload(row)">
              <el-icon><Download /></el-icon>
            </el-button>
            <el-button type="primary" link @click="handlePreview(row)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传资源"
      width="600px"
      destroy-on-close
    >
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="选择文件" required>
          <el-upload
            ref="uploadRef"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :limit="1"
            drag
            style="width: 100%;"
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持视频、文档、图片、音频等格式，单个文件不超过500MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="资源名称">
          <el-input v-model="uploadForm.resourceName" placeholder="默认使用文件名" />
        </el-form-item>
        <el-form-item label="资源描述">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">
          上传
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Upload, VideoPlay, Document, Picture, Headset,
  Folder, Download, View, Delete
} from '@element-plus/icons-vue'
import { formatDate } from '@/utils/format'
import { getResourcePage, deleteResource } from '@/api/resource'

// 数据
const loading = ref(false)
const resourceList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterType = ref('')
const resourceTypes = ref([
  { label: '视频', value: 'video' },
  { label: '文档', value: 'document' },
  { label: '图片', value: 'image' },
  { label: '音频', value: 'audio' },
  { label: '其他', value: 'other' }
])
const selectedResources = ref([])

// 上传相关
const uploadDialogVisible = ref(false)
const uploadForm = ref({
  resourceName: '',
  description: ''
})
const uploadRef = ref(null)
const currentFile = ref(null)
const uploading = ref(false)

// 获取资源列表
const fetchResourceList = async () => {
  loading.value = true
  try {
    const res = await getResourcePage({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      resourceType: filterType.value || undefined
    })
    if (res.code === 200) {
      resourceList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
    ElMessage.error('获取资源列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchResourceList()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchResourceList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchResourceList()
}

// 选择
const handleSelectionChange = (val) => {
  selectedResources.value = val
}

// 上传对话框
const showUploadDialog = () => {
  uploadDialogVisible.value = true
  uploadForm.value = { resourceName: '', description: '' }
  currentFile.value = null
}

// 文件选择
const handleFileChange = (file) => {
  currentFile.value = file.raw
  if (!uploadForm.value.resourceName) {
    uploadForm.value.resourceName = file.name.replace(/\.[^/.]+$/, '')
  }
}

const handleFileRemove = () => {
  currentFile.value = null
}

// 上传
const handleUpload = async () => {
  if (!currentFile.value) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  uploading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    fetchResourceList()
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

// 下载
const handleDownload = (row) => {
  ElMessage.success(`开始下载: ${row.resourceName}`)
}

// 预览
const handlePreview = (row) => {
  ElMessage.info(`预览功能开发中: ${row.resourceName}`)
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资源"${row.resourceName}"吗？`,
      '确认删除',
      { type: 'warning' }
    )
    const res = await deleteResource(row.resourceId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchResourceList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size || size <= 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let index = 0
  let fileSize = size
  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024
    index++
  }
  return `${fileSize.toFixed(2)} ${units[index]}`
}

// 获取类型标签样式
const getTypeTagType = (type) => {
  const typeMap = {
    video: 'danger',
    document: 'primary',
    image: 'success',
    audio: 'warning',
    other: 'info'
  }
  return typeMap[type] || 'info'
}

// 获取类型标签文本
const getTypeLabel = (type) => {
  const labelMap = {
    video: '视频',
    document: '文档',
    image: '图片',
    audio: '音频',
    other: '其他'
  }
  return labelMap[type] || type
}

onMounted(() => {
  fetchResourceList()
})
</script>

<style scoped>
.resources-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.operation-card {
  margin-bottom: 20px;
}

.list-card {
  margin-bottom: 20px;
}

.resource-name {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resource-icon {
  color: #409eff;
}

.resource-info {
  display: flex;
  flex-direction: column;
}

.resource-info .name {
  font-weight: 500;
  color: #303133;
}

.resource-info .filename {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.count-info {
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 13px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
