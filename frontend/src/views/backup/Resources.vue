<template>
  <div class="resources-page">
    <!-- 页面标题� -->
    <div class="page-header">
      <h2>资源管理</h2>
      <p class="subtitle">管理课程▼瑙嗛�銆佹枃框ｃ€佸浘片囩瓑数欏�资源</p>
    </div>

    <!-- 鎿嶄綔栏?-->
    <el-card class="operation-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜滅储资源后嶇О"
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
            <el-icon><Upload /></el-icon> 三婁紶资源
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 资源删楄〃 -->
    <el-card class="list-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="resourceList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="资源后嶇О" min-width="200">
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
        <el-table-column label="三婁紶日堕棿" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="鎿嶄綔" width="180" fixed="right">
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

    <!-- 三婁紶对话框?-->
    <el-dialog
      v-model="uploadDialogVisible"
      title="三婁紶资源"
      width="600px"
      destroy-on-close
    >
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="选夋嫨新囦欢" required>
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
              择栨嫿新囦欢删版�像勬垨 <em>鐐瑰嚮三婁紶</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                鏀�寔瑙嗛�銆佹枃框ｃ€佸浘片囥€侀煶棰戠瓑栏煎紡锛屽崟三�枃以朵笉瓒呰繃500MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="资源后嶇О">
          <el-input v-model="uploadForm.resourceName" placeholder="榛樿�三烘枃以跺悕" />
        </el-form-item>
        <el-form-item label="资源描述">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请疯緭六ヨ祫婧愭弿进?
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">
          三婁紶
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
import { getResourcePage, getResourceTypes, deleteResource, uploadResource } from '@/resource'
import { formatDate } from '@/utils/format'

// 数据
const loading = ref(false)
const resourceList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterType = ref('')
const resourceTypes = ref([])
const selectedResources = ref([])

// 三婁紶目稿叧
const uploadDialogVisible = ref(false)
const uploadForm = ref({
  resourceName: '',
  description: ''
})
const uploadRef = ref(null)
const currentFile = ref(null)
const uploading = ref(false)

// 鑾峰彇资源删楄〃
const fetchResourceList = async () => {
  loading.value = true
  try {
    const res = await getResourcePage({
      page: currentPage.value,
      size: pageSize.value,
      resourceType: filterType.value,
      keyword: searchKeyword.value
    })
    resourceList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('鑾峰彇资源删楄〃失败:', error)
    ElMessage.error('鑾峰彇资源删楄〃失败')
  } finally {
    loading.value = false
  }
}

// 鑾峰彇资源类型
const fetchResourceTypes = async () => {
  try {
    const res = await getResourceTypes()
    resourceTypes.value = res.data
  } catch (error) {
    console.error('鑾峰彇资源类型失败:', error)
  }
}

// 搜滅储
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

// 选夋嫨
const handleSelectionChange = (val) => {
  selectedResources.value = val
}

// 三婁紶对话框?
const showUploadDialog = () => {
  uploadDialogVisible.value = true
  uploadForm.value = { resourceName: '', description: '' }
  currentFile.value = null
}

// 新囦欢选夋嫨
const handleFileChange = (file) => {
  currentFile.value = file.raw
  if (!uploadForm.value.resourceName) {
    uploadForm.value.resourceName = file.name.replace(/\.[^/.]+$/, '')
  }
}

const handleFileRemove = () => {
  currentFile.value = null
}

// 三婁紶
const handleUpload = async () => {
  if (!currentFile.value) {
    ElMessage.warning('请烽€夋嫨瑕佷笂传犵殑新囦欢')
    return
  }

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', currentFile.value)
    formData.append('resourceName', uploadForm.value.resourceName)
    formData.append('description', uploadForm.value.description)
    formData.append('uploaderId', 1) // 三存椂使用
    formData.append('uploaderName', '管理周?) // 三存椂使用

    await uploadResource(formData)
    ElMessage.success('三婁紶成功')
    uploadDialogVisible.value = false
    fetchResourceList()
  } catch (error) {
    console.error('三婁紶失败:', error)
    ElMessage.error('三婁紶失败')
  } finally {
    uploading.value = false
  }
}

// 三嬭浇
const handleDownload = (row) => {
  window.open(`http://localhost:8081/resources/${row.resourceId}/download`, '_blank')
}

// 棰勮�
const handlePreview = (row) => {
  ElMessage.info(`棰勮�功熻兘开€取戜腑: ${row.resourceName}`)
}

// 删犻櫎
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确�畾瑕佸垹除よ祫婧?"${row.resourceName}" 后楋紵`,
      '确��删犻櫎',
      { type: 'warning' }
    )
    await deleteResource(row.resourceId)
    ElMessage.success('删犻櫎成功')
    fetchResourceList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删犻櫎失败:', error)
      ElMessage.error('删犻櫎失败')
    }
  }
}

// 格式化文件大小?
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

// 鑾峰彇类型栏囩�栏峰紡
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

// 鑾峰彇类型栏囩�新囨湰
const getTypeLabel = (type) => {
  const labelMap = {
    video: '瑙嗛�',
    document: '新囨。',
    image: '四剧墖',
    audio: '闊抽�',
    other: '六朵粬'
  }
  return labelMap[type] || type
}

onMounted(() => {
  fetchResourceList()
  fetchResourceTypes()
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
