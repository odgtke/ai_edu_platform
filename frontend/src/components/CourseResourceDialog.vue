<template>
  <el-dialog
    v-model="visible"
    title="璇剧▼璧勬簮绠＄悊"
    width="900px"
    destroy-on-close
  >
    <el-row :gutter="20">
      <!-- 宸插叧鑱旇祫婧?-->
      <el-col :span="12">
        <div class="section-title">
          <span>宸插叧鑱旇祫婧?/span>
          <el-tag size="small">{{ linkedResources.length }}</el-tag>
        </div>
        <el-scrollbar height="400px">
          <div v-if="linkedResources.length === 0" class="empty-text">
            鏆傛棤鍏宠仈璧勬簮
          </div>
          <div
            v-for="item in linkedResources"
            :key="item.resourceId"
            class="resource-item linked"
          >
            <div class="resource-info">
              <el-icon :size="18">
                <VideoPlay v-if="item.resourceType === 'video'" />
                <Document v-else-if="item.resourceType === 'document'" />
                <Picture v-else-if="item.resourceType === 'image'" />
                <Headset v-else-if="item.resourceType === 'audio'" />
                <Folder v-else />
              </el-icon>
              <div class="info">
                <div class="name">{{ item.resourceName }}</div>
                <div class="meta">{{ formatFileSize(item.fileSize) }} 路 {{ getTypeLabel(item.resourceType) }}</div>
              </div>
            </div>
            <div class="actions">
              <el-tag v-if="item.isRequired === 1" type="danger" size="small">蹇呭</el-tag>
              <el-tag v-else type="info" size="small">閫夊</el-tag>
              <el-button type="danger" link size="small" @click="handleRemove(item)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </el-scrollbar>
      </el-col>

      <!-- 璧勬簮搴?-->
      <el-col :span="12">
        <div class="section-title">
          <span>璧勬簮搴?/span>
          <el-input
            v-model="searchKeyword"
            placeholder="鎼滅储璧勬簮"
            size="small"
            clearable
            style="width: 150px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <el-scrollbar height="360px">
          <div v-if="filteredResources.length === 0" class="empty-text">
            鏆傛棤鍙鐢ㄨ祫婧
          </div>
          <div
            v-for="item in filteredResources"
            :key="item.resourceId"
            class="resource-item"
            :class="{ disabled: isLinked(item.resourceId) }"
            @click="!isLinked(item.resourceId) && handleAdd(item)"
          >
            <div class="resource-info">
              <el-icon :size="18">
                <VideoPlay v-if="item.resourceType === 'video'" />
                <Document v-else-if="item.resourceType === 'document'" />
                <Picture v-else-if="item.resourceType === 'image'" />
                <Headset v-else-if="item.resourceType === 'audio'" />
                <Folder v-else />
              </el-icon>
              <div class="info">
                <div class="name">{{ item.resourceName }}</div>
                <div class="meta">{{ formatFileSize(item.fileSize) }} 路 {{ getTypeLabel(item.resourceType) }}</div>
              </div>
            </div>
            <div class="actions">
              <el-tag v-if="isLinked(item.resourceId)" type="success" size="small">宸叉坊鍔?/el-tag>
              <el-button v-else type="primary" link size="small">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </div>
        </el-scrollbar>
        <div class="pagination-mini">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            small
            layout="prev, pager, next"
            @current-change="fetchResourceList"
          />
        </div>
      </el-col>
    </el-row>

    <template #footer">
      <el-button @click="visible = false">鍏抽棴</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  VideoPlay, Document, Picture, Headset, Folder,
  Delete, Plus, Search
} from '@element-plus/icons-vue'
import {
  getCourseResources,
  getResourcePage,
  addResourceToCourse,
  removeResourceFromCourse
} from '@/resource'

const props = defineProps({
  modelValue: Boolean,
  courseId: Number
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 鏁版嵁
const linkedResources = ref([])
const allResources = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 杩囨护鍚庣殑璧勬簮鍒楄〃
const filteredResources = computed(() => {
  if (!searchKeyword.value) return allResources.value
  const keyword = searchKeyword.value.toLowerCase()
  return allResources.value.filter(item =>
    item.resourceName.toLowerCase().includes(keyword)
  )
})

// 妫鏌ヨ祫婧愭槸鍚﹀凡鍏宠仈
const isLinked = (resourceId) => {
  return linkedResources.value.some(item => item.resourceId === resourceId)
}

// 鑾峰彇宸插叧鑱旇祫婧?
const fetchLinkedResources = async () => {
  if (!props.courseId) return
  try {
    const res = await getCourseResources(props.courseId)
    linkedResources.value = res.data || []
  } catch (error) {
    console.error('鑾峰彇璇剧▼璧勬簮澶辫触:', error)
  }
}

// 鑾峰彇璧勬簮搴?
const fetchResourceList = async () => {
  try {
    const res = await getResourcePage({
      page: currentPage.value,
      size: pageSize.value
    })
    allResources.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('鑾峰彇璧勬簮鍒楄〃澶辫触:', error)
  }
}

// 娣诲姞璧勬簮鍒拌剧?
const handleAdd = async (resource) => {
  try {
    await addResourceToCourse(props.courseId, resource.resourceId)
    ElMessage.success('娣诲姞鎴愬姛')
    fetchLinkedResources()
    emit('success')
  } catch (error) {
    console.error('娣诲姞澶辫触:', error)
    ElMessage.error('娣诲姞澶辫触')
  }
}

// 浠庤剧▼绉婚櫎璧勬?
const handleRemove = async (resource) => {
  try {
    await ElMessageBox.confirm(
      `纭瀹氳佺Щ闄よ祫婧?"${resource.resourceName}" 鍚楋紵`,
      '纭璁ょЩ闄',
      { type: 'warning' }
    )
    await removeResourceFromCourse(props.courseId, resource.resourceId)
    ElMessage.success('绉婚櫎鎴愬姛')
    fetchLinkedResources()
    emit('success')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('绉婚櫎澶辫触:', error)
      ElMessage.error('绉婚櫎澶辫触')
    }
  }
}

// 鏍煎紡鍖栨枃浠跺ぇ灏?
const formatFileSize = (size) => {
  if (!size || size <= 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  let fileSize = size
  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024
    index++
  }
  return `${fileSize.toFixed(2)} ${units[index]}`
}

// 鑾峰彇绫诲瀷鏍囩
const getTypeLabel = (type) => {
  const labelMap = {
    video: '瑙嗛',
    document: '鏂囨。',
    image: '鍥剧墖',
    audio: '闊抽',
    other: '鍏朵粬'
  }
  return labelMap[type] || type
}

// 鐩戝惉寮圭獥鏄剧ず
watch(() => props.modelValue, (val) => {
  if (val && props.courseId) {
    fetchLinkedResources()
    fetchResourceList()
  }
})
</script>

<style scoped>
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
  font-weight: 500;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s;
}

.resource-item:hover {
  background: #e6f2ff;
}

.resource-item.linked {
  background: #f0f9ff;
  border: 1px solid #409eff;
}

.resource-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.resource-info .el-icon {
  color: #409eff;
  flex-shrink: 0;
}

.resource-info .info {
  flex: 1;
  min-width: 0;
}

.resource-info .name {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-info .meta {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.pagination-mini {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}
</style>
