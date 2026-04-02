<template>
  <div class="clazz">
    <!-- 页面标题 -->
    <div class="page-header-modern">
      <div class="header-content">
        <div class="title-badge">
          <el-icon class="badge-icon"><School /></el-icon>
        </div>
        <div class="title-text">
          <h1 class="page-title">班级管理</h1>
          <p class="page-subtitle">管理学校班级信息，包括班级创建、编辑和学生分配</p>
        </div>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ total }}</span>
          <span class="stat-label">总班级</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ totalStudents }}</span>
          <span class="stat-label">学生总数</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="btn-add" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加班级
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-group search-group">
          <el-input
            v-model="searchQuery"
            placeholder="搜索班级名称..."
            class="search-input-modern"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        
        <div class="filter-group">
          <el-icon class="filter-icon"><Collection /></el-icon>
          <el-select v-model="filterGrade" placeholder="全部年级" clearable @change="handleSearch">
            <el-option label="一年级" :value="1" />
            <el-option label="二年级" :value="2" />
            <el-option label="三年级" :value="3" />
            <el-option label="四年级" :value="4" />
            <el-option label="五年级" :value="5" />
            <el-option label="六年级" :value="6" />
          </el-select>
        </div>
        
        <div class="filter-group">
          <el-icon class="filter-icon"><CircleCheck /></el-icon>
          <el-select v-model="filterStatus" placeholder="全部状态" clearable @change="handleSearch">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </div>
        
        <el-button class="btn-reset" @click="resetFilters">
          <el-icon><RefreshRight /></el-icon>
          重置
        </el-button>
      </div>
    </div>

    <!-- 班级表格 -->
    <div class="table-card">
      <el-table
        ref="tableRef"
        :data="clazzList"
        v-loading="loading"
        class="modern-table"
        :header-cell-style="headerCellStyle"
      >
        <el-table-column type="index" label="序号" width="80" :index="indexMethod" />
        
        <el-table-column label="班级名称" min-width="180">
          <template #default="{ row }">
            <div class="class-cell">
              <div class="class-icon">
                <el-icon><School /></el-icon>
              </div>
              <div class="class-info">
                <div class="class-name">{{ row.className }}</div>
                <div class="class-id">ID: {{ row.classId }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="年级" width="120">
          <template #default="{ row }">
            <el-tag :type="getGradeType(row.gradeId)" size="small">
              {{ getGradeLabel(row.gradeId) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="班主任" min-width="150">
          <template #default="{ row }">
            <div class="teacher-cell" v-if="row.teacherId">
              <el-avatar :size="32" :src="row.teacherAvatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span>{{ row.teacherName || '未分配' }}</span>
            </div>
            <span v-else class="text-gray">未分配</span>
          </template>
        </el-table-column>
        
        <el-table-column label="学生数" width="100">
          <template #default="{ row }">
            <div class="student-count">
              <el-icon><UserFilled /></el-icon>
              <span>{{ row.studentCount || 0 }} 人</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <div class="status-badge" :class="row.status === 1 ? 'enabled' : 'disabled'">
              <span class="status-dot-sm"></span>
              <span>{{ row.status === 1 ? '正常' : '停用' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" width="150">
          <template #default="{ row }">
            <div class="time-cell">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDateShort(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-tooltip content="编辑" placement="top">
                <el-button circle size="small" type="primary" plain @click="editClass(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="查看学生" placement="top">
                <el-button circle size="small" @click="viewStudents(row)">
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              <el-dropdown trigger="click">
                <el-button circle size="small">
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="toggleStatus(row)">
                      <el-icon><Switch /></el-icon>
                      {{ row.status === 1 ? '停用班级' : '启用班级' }}
                    </el-dropdown-item>
                    <el-dropdown-item divided type="danger" @click="deleteClass(row)">
                      <el-icon><Delete /></el-icon>删除班级
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-modern">
        <div class="pagination-info">
          共 <strong>{{ total }}</strong> 条记录，每页 
          <el-select v-model="pageSize" size="small" class="page-size-select" @change="handleSizeChange">
            <el-option label="10" :value="10" />
            <el-option label="20" :value="20" />
            <el-option label="50" :value="50" />
          </el-select>
          条
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :total="total"
          :page-size="pageSize"
          layout="prev, pager, next, jumper"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加/编辑班级对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑班级' : '添加班级'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称，如：一年级1班" />
        </el-form-item>
        
        <el-form-item label="所属年级" prop="gradeId">
          <el-select v-model="form.gradeId" placeholder="请选择年级" style="width: 100%">
            <el-option label="一年级" :value="1" />
            <el-option label="二年级" :value="2" />
            <el-option label="三年级" :value="3" />
            <el-option label="四年级" :value="4" />
            <el-option label="五年级" :value="5" />
            <el-option label="六年级" :value="6" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="班主任" prop="teacherId">
          <el-select
            v-model="form.teacherId"
            placeholder="请选择班主任"
            style="width: 100%"
            clearable
          >
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.userId"
              :label="teacher.realName || teacher.username"
              :value="teacher.userId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="班级状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 学生列表抽屉 -->
    <el-drawer
      v-model="studentDrawerVisible"
      :title="`${selectedClass?.className || ''} - 学生列表`"
      size="600px"
    >
      <div class="student-list" v-if="studentList.length > 0">
        <div v-for="student in studentList" :key="student.userId" class="student-item">
          <el-avatar :size="40" :src="student.avatar">
            {{ student.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <div class="student-info">
            <div class="student-name">{{ student.realName || student.username }}</div>
            <div class="student-meta">{{ student.email || '暂无邮箱' }}</div>
          </div>
          <el-tag size="small" :type="student.status === 1 ? 'success' : 'info'">
            {{ student.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </div>
      </div>
      <el-empty v-else description="暂无学生" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClassPage, createClass, updateClass, deleteClass as deleteClassApi } from '@/api/clazz'
import { getAllUsers } from '@/api/user'

const loading = ref(false)
const searchQuery = ref('')
const filterGrade = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const clazzList = ref([])
const teacherList = ref([])

// 计算总学生数
const totalStudents = computed(() => {
  return clazzList.value.reduce((sum, item) => sum + (item.studentCount || 0), 0)
})

// 获取年级标签
const getGradeLabel = (gradeId) => {
  const gradeMap = {
    1: '一年级',
    2: '二年级',
    3: '三年级',
    4: '四年级',
    5: '五年级',
    6: '六年级'
  }
  return gradeMap[gradeId] || '未知'
}

// 获取年级类型
const getGradeType = (gradeId) => {
  const typeMap = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'danger',
    5: 'info',
    6: ''
  }
  return typeMap[gradeId] || ''
}

// 格式化日期
const formatDateShort = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 计算序号
const indexMethod = (index) => {
  return (currentPage.value - 1) * pageSize.value + index + 1
}

// 表格头部样式
const headerCellStyle = () => {
  return {
    background: '#f8fafc',
    color: '#64748b',
    fontWeight: 600,
    fontSize: '13px'
  }
}

// 获取班级列表
const fetchClassList = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      className: searchQuery.value || undefined,
      gradeId: filterGrade.value || undefined,
      status: filterStatus.value !== '' ? filterStatus.value : undefined
    }
    const res = await getClassPage(params)
    if (res.code === 200) {
      clazzList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取班级列表失败:', error)
    ElMessage.error('获取班级列表失败')
  } finally {
    loading.value = false
  }
}

// 获取教师列表
const fetchTeacherList = async () => {
  try {
    const res = await getAllUsers()
    if (res.code === 200) {
      teacherList.value = res.data.filter(user => user.userType === 2) || []
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchClassList()
}

// 重置筛选
const resetFilters = () => {
  searchQuery.value = ''
  filterGrade.value = ''
  filterStatus.value = ''
  currentPage.value = 1
  fetchClassList()
}

// 分页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchClassList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchClassList()
}

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = ref({
  className: '',
  gradeId: '',
  teacherId: '',
  status: 1
})

const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  gradeId: [{ required: true, message: '请选择年级', trigger: 'change' }]
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  form.value = {
    className: '',
    gradeId: '',
    teacherId: '',
    status: 1
  }
  dialogVisible.value = true
}

// 编辑班级
const editClass = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const api = isEdit.value ? 
      () => updateClass(form.value.classId, form.value) : 
      () => createClass(form.value)
    const res = await api()
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      fetchClassList()
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 删除班级
const deleteClass = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除班级 "${row.className}" 吗？`, '确认删除', {
      type: 'warning'
    })
    const res = await deleteClassApi(row.classId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchClassList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 切换状态
const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}班级 "${row.className}" 吗？`, '确认', {
      type: 'warning'
    })
    const res = await updateClass(row.classId, { ...row, status: newStatus })
    if (res.code === 200) {
      ElMessage.success(`${actionText}成功`)
      fetchClassList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 学生列表
const studentDrawerVisible = ref(false)
const selectedClass = ref(null)
const studentList = ref([])

const viewStudents = (row) => {
  selectedClass.value = row
  studentList.value = [] // 实际应从后端获取
  studentDrawerVisible.value = true
  ElMessage.info(`查看 ${row.className} 的学生列表`)
}

onMounted(() => {
  fetchClassList()
  fetchTeacherList()
})
</script>

<style scoped>
.clazz {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
}

/* 页面头部 */
.page-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 24px 28px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-badge {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-icon {
  font-size: 28px;
  color: white;
}

.title-text h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.title-text p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 统计信息 */
.header-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e2e8f0;
}

/* 操作按钮 */
.header-actions {
  display: flex;
  gap: 12px;
}

.btn-add {
  border-radius: 10px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
}

/* 筛选卡片 */
.filter-card {
  background: white;
  padding: 20px 24px;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group.search-group {
  flex: 1;
  min-width: 280px;
}

.search-input-modern {
  width: 100%;
}

.filter-icon {
  color: #64748b;
  font-size: 16px;
}

.btn-reset {
  margin-left: auto;
}

/* 表格卡片 */
.table-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.modern-table {
  margin-bottom: 20px;
}

/* 班级单元格 */
.class-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.class-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.class-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.class-name {
  font-weight: 600;
  font-size: 14px;
  color: #1e293b;
}

.class-id {
  font-size: 12px;
  color: #64748b;
}

/* 教师单元格 */
.teacher-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.text-gray {
  color: #94a3b8;
}

/* 学生数 */
.student-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #64748b;
}

/* 状态徽章 */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.enabled {
  background: #dcfce7;
  color: #15803d;
}

.status-badge.disabled {
  background: #f1f5f9;
  color: #64748b;
}

.status-dot-sm {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

/* 时间单元格 */
.time-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

/* 操作单元格 */
.action-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 分页 */
.pagination-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.pagination-info {
  font-size: 14px;
  color: #64748b;
}

.pagination-info strong {
  color: #1e293b;
  font-weight: 600;
}

.page-size-select {
  width: 80px;
  margin: 0 8px;
}

/* 学生列表 */
.student-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.student-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 10px;
}

.student-info {
  flex: 1;
}

.student-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 2px;
}

.student-meta {
  font-size: 12px;
  color: #64748b;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header-modern {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .header-stats {
    order: -1;
  }
  
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-group.search-group {
    min-width: auto;
  }
  
  .btn-reset {
    margin-left: 0;
  }
}
</style>
