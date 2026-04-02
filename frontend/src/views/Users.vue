<template>
  <div class="users">
    <!-- 现代风格页面标题 -->
    <div class="page-header-modern">
      <div class="header-content">
        <div class="title-badge">
          <el-icon class="badge-icon"><UserFilled /></el-icon>
        </div>
        <div class="title-text">
          <h1 class="page-title">用户管理</h1>
          <p class="page-subtitle">管理系统用户信息，包括学生、教师和管理员账号</p>
        </div>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ total }}</span>
          <span class="stat-label">总用户</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ activeUserCount }}</span>
          <span class="stat-label">已启用</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button class="btn-export" @click="exportUsers">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-button type="primary" class="btn-add" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-group search-group">
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户名、姓名或邮箱..."
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
          <el-icon class="filter-icon"><User /></el-icon>
          <el-select v-model="filterRole" placeholder="全部角色" clearable @change="handleSearch">
            <el-option label="学生" :value="1" />
            <el-option label="教师" :value="2" />
            <el-option label="家长" :value="3" />
            <el-option label="管理员" :value="4" />
          </el-select>
        </div>
        
        <div class="filter-group">
          <el-icon class="filter-icon"><CircleCheck /></el-icon>
          <el-select v-model="filterStatus" placeholder="全部状态" clearable @change="handleSearch">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </div>
        
        <el-button class="btn-reset" @click="resetFilters">
          <el-icon><RefreshRight /></el-icon>
          重置
        </el-button>
      </div>
    </div>

    <!-- 批量操作工具栏 -->
    <transition name="slide-down">
      <div class="batch-toolbar-modern" v-if="selectedUsers.length > 0">
        <div class="batch-info">
          <el-icon class="info-icon"><InfoFilled /></el-icon>
          <span>已选择 <strong>{{ selectedUsers.length }}</strong> 位用户</span>
        </div>
        <div class="batch-actions">
          <el-button type="success" plain size="small" @click="batchEnable">
            <el-icon><Check /></el-icon>启用
          </el-button>
          <el-button type="warning" plain size="small" @click="batchDisable">
            <el-icon><Close /></el-icon>禁用
          </el-button>
          <el-divider direction="vertical" />
          <el-button type="danger" plain size="small" @click="batchDelete">
            <el-icon><Delete /></el-icon>删除
          </el-button>
          <el-button link size="small" @click="clearSelection">
            取消选择
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 用户表格 -->
    <div class="table-card">
      <el-table
        ref="tableRef"
        :data="userList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        class="modern-table"
        :header-cell-style="headerCellStyle"
      >
        <el-table-column type="selection" width="48" />
        
        <el-table-column label="用户信息" min-width="220">
          <template #default="{ row }">
            <div class="user-cell">
              <div class="avatar-wrapper">
                <el-avatar :size="44" :src="row.avatar" class="user-avatar">
                  {{ row.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <div class="status-dot" :class="{ active: row.status === 1 }"></div>
              </div>
              <div class="user-meta">
                <div class="username">{{ row.username }}</div>
                <div class="realname">{{ row.realName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="联系方式" min-width="200">
          <template #default="{ row }">
            <div class="contact-cell">
              <div class="contact-item" v-if="row.email">
                <el-icon><Message /></el-icon>
                <span>{{ row.email }}</span>
              </div>
              <div class="contact-item" v-if="row.phone">
                <el-icon><Phone /></el-icon>
                <span>{{ row.phone }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="角色" width="110">
          <template #default="{ row }">
            <div class="role-badge" :class="getRoleClass(row.userType)">
              <el-icon v-if="row.userType === 4"><Setting /></el-icon>
              <el-icon v-else-if="row.userType === 2"><Reading /></el-icon>
              <el-icon v-else-if="row.userType === 1"><User /></el-icon>
              <el-icon v-else><UserFilled /></el-icon>
              <span>{{ getRoleLabel(row.userType) }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <div class="status-badge" :class="row.status === 1 ? 'enabled' : 'disabled'">
              <span class="status-dot-sm"></span>
              <span>{{ row.status === 1 ? '已启用' : '已禁用' }}</span>
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
        
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-tooltip content="查看详情" placement="top">
                <el-button circle size="small" @click="viewUserDetail(row)">
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="编辑" placement="top">
                <el-button circle size="small" type="primary" plain @click="editUser(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-dropdown trigger="click">
                <el-button circle size="small">
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="resetPassword(row)">
                      <el-icon><Key /></el-icon>重置密码
                    </el-dropdown-item>
                    <el-dropdown-item @click="toggleStatus(row)">
                      <el-icon><Switch /></el-icon>
                      {{ row.status === 1 ? '禁用账号' : '启用账号' }}
                    </el-dropdown-item>
                    <el-dropdown-item divided type="danger" @click="deleteUser(row)">
                      <el-icon><Delete /></el-icon>删除用户
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
            <el-option label="100" :value="100" />
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

    <!-- 用户详情抽屉 -->
    <el-drawer
      v-model="detailDrawerVisible"
      title="用户详情"
      size="420px"
      class="user-drawer-modern"
    >
      <div class="drawer-content" v-if="selectedUser">
        <div class="drawer-header-card">
          <div class="header-bg"></div>
          <div class="header-content">
            <div class="avatar-large-wrapper">
              <el-avatar :size="90" :src="selectedUser.avatar" class="avatar-large">
                {{ selectedUser.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <div class="status-ring" :class="{ active: selectedUser.status === 1 }"></div>
            </div>
            <h3 class="drawer-name">{{ selectedUser.realName || selectedUser.username }}</h3>
            <p class="drawer-username">@{{ selectedUser.username }}</p>
            <div class="drawer-badges">
              <div class="role-badge-large" :class="getRoleClass(selectedUser.userType)">
                <el-icon v-if="selectedUser.userType === 4"><Setting /></el-icon>
                <el-icon v-else-if="selectedUser.userType === 2"><Reading /></el-icon>
                <el-icon v-else-if="selectedUser.userType === 1"><User /></el-icon>
                <el-icon v-else><UserFilled /></el-icon>
                <span>{{ getRoleLabel(selectedUser.userType) }}</span>
              </div>
              <div class="status-badge-large" :class="selectedUser.status === 1 ? 'enabled' : 'disabled'">
                <span class="status-dot-lg"></span>
                <span>{{ selectedUser.status === 1 ? '账号正常' : '账号已禁用' }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="drawer-section">
          <h4 class="section-title">
            <el-icon><Document /></el-icon>
            基本信息
          </h4>
          <div class="info-list">
            <div class="info-row">
              <div class="info-label">
                <el-icon><Key /></el-icon>
                用户ID
              </div>
              <div class="info-value id-value">{{ selectedUser.userId }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <el-icon><Message /></el-icon>
                邮箱地址
              </div>
              <div class="info-value">{{ selectedUser.email || '未设置' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <el-icon><Phone /></el-icon>
                手机号码
              </div>
              <div class="info-value">{{ selectedUser.phone || '未设置' }}</div>
            </div>
          </div>
        </div>
        
        <div class="drawer-section">
          <h4 class="section-title">
            <el-icon><Clock /></el-icon>
            时间信息
          </h4>
          <div class="info-list">
            <div class="info-row">
              <div class="info-label">
                <el-icon><Calendar /></el-icon>
                创建时间
              </div>
              <div class="info-value">{{ formatDate(selectedUser.createTime) }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <el-icon><Timer /></el-icon>
                更新时间
              </div>
              <div class="info-value">{{ formatDate(selectedUser.updateTime) }}</div>
            </div>
          </div>
        </div>
        
        <div class="drawer-footer">
          <el-button type="primary" size="large" @click="editFromDetail">
            <el-icon><Edit /></el-icon>
            编辑用户
          </el-button>
          <el-button size="large" @click="detailDrawerVisible = false">
            关闭
          </el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        
        <el-form-item label="角色" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择角色" style="width: 100%">
            <el-option label="学生" :value="1" />
            <el-option label="教师" :value="2" />
            <el-option label="家长" :value="3" />
            <el-option label="管理员" :value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, createUser, updateUser, deleteUser as deleteUserApi } from '@/api/user'

const loading = ref(false)
const searchQuery = ref('')
const filterRole = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userList = ref([])
const tableRef = ref(null)
const selectedUsers = ref([])
const detailDrawerVisible = ref(false)
const selectedUser = ref(null)

// 获取角色标签
const getRoleLabel = (userType) => {
  const roleMap = {
    1: '学生',
    2: '教师',
    3: '家长',
    4: '管理员'
  }
  return roleMap[userType] || '未知'
}

// 获取角色类型样式
const getRoleType = (userType) => {
  const typeMap = {
    1: '',
    2: 'success',
    3: 'warning',
    4: 'danger'
  }
  return typeMap[userType] || ''
}

// 获取角色CSS类名
const getRoleClass = (userType) => {
  const classMap = {
    1: 'role-student',
    2: 'role-teacher',
    3: 'role-parent',
    4: 'role-admin'
  }
  return classMap[userType] || 'role-student'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化短日期
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

// 计算活跃用户数
const activeUserCount = computed(() => {
  return userList.value.filter(user => user.status === 1).length
})

// 表格头部样式
const headerCellStyle = () => {
  return {
    background: '#f8fafc',
    color: '#64748b',
    fontWeight: 600,
    fontSize: '13px'
  }
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      username: searchQuery.value || undefined,
      realName: searchQuery.value || undefined,
      userType: filterRole.value || undefined,
      status: filterStatus.value !== '' ? filterStatus.value : undefined
    }
    const res = await getUserPage(params)
    if (res.code === 200) {
      userList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

// 重置筛选
const resetFilters = () => {
  searchQuery.value = ''
  filterRole.value = ''
  filterStatus.value = ''
  currentPage.value = 1
  fetchUserList()
}

// 分页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUserList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchUserList()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 清空选择
const clearSelection = () => {
  tableRef.value?.clearSelection()
}

// 查看用户详情
const viewUserDetail = (row) => {
  selectedUser.value = row
  detailDrawerVisible.value = true
}

// 从详情编辑
const editFromDetail = () => {
  detailDrawerVisible.value = false
  editUser(selectedUser.value)
}

// 导出用户
const exportUsers = () => {
  ElMessage.success('导出功能开发中...')
}

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = ref({
  username: '',
  realName: '',
  email: '',
  phone: '',
  password: '',
  userType: 1,
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur', min: 6 }],
  userType: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  form.value = {
    username: '',
    realName: '',
    email: '',
    phone: '',
    password: '',
    userType: 1,
    status: 1
  }
  dialogVisible.value = true
}

// 编辑用户
const editUser = (row) => {
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
    const api = isEdit.value ? updateUser : createUser
    const res = await api(form.value)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      fetchUserList()
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 删除用户
const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '确认删除', {
      type: 'warning'
    })
    const res = await deleteUserApi(row.userId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchUserList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 重置密码
const resetPassword = (row) => {
  ElMessageBox.prompt(`请输入用户 "${row.username}" 的新密码`, '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^.{6,}$/,
    inputErrorMessage: '密码长度至少6位'
  }).then(({ value }) => {
    ElMessage.success(`密码重置成功（模拟）: ${value}`)
  }).catch(() => {})
}

// 切换状态
const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}用户 "${row.username}" 吗？`, '确认', {
      type: 'warning'
    })
    const res = await updateUser({ ...row, status: newStatus })
    if (res.code === 200) {
      ElMessage.success(`${actionText}成功`)
      fetchUserList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 批量启用
const batchEnable = async () => {
  try {
    await ElMessageBox.confirm(`确定要启用选中的 ${selectedUsers.value.length} 位用户吗？`, '确认', {
      type: 'warning'
    })
    ElMessage.success('批量启用成功（模拟）')
    clearSelection()
    fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量启用失败')
    }
  }
}

// 批量禁用
const batchDisable = async () => {
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedUsers.value.length} 位用户吗？`, '确认', {
      type: 'warning'
    })
    ElMessage.success('批量禁用成功（模拟）')
    clearSelection()
    fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量禁用失败')
    }
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedUsers.value.length} 位用户吗？此操作不可恢复！`, '确认删除', {
      type: 'danger'
    })
    ElMessage.success('批量删除成功（模拟）')
    clearSelection()
    fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.users {
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

.page-header-modern .header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-badge {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.btn-export {
  border-radius: 10px;
  padding: 10px 20px;
}

.btn-add {
  border-radius: 10px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.search-input-modern :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
}

.filter-icon {
  color: #64748b;
  font-size: 16px;
}

.btn-reset {
  margin-left: auto;
}

/* 批量操作工具栏 */
.batch-toolbar-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 16px 24px;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.batch-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
}

.batch-info .info-icon {
  color: #3b82f6;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 8px;
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

.modern-table :deep(.el-table__header) {
  border-radius: 10px;
}

/* 用户单元格 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  border: 2px solid #f1f5f9;
}

.status-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #94a3b8;
  border-radius: 50%;
  border: 2px solid white;
}

.status-dot.active {
  background: #22c55e;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-meta .username {
  font-weight: 600;
  font-size: 14px;
  color: #1e293b;
}

.user-meta .realname {
  font-size: 13px;
  color: #64748b;
}

/* 联系方式 */
.contact-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.contact-item .el-icon {
  font-size: 14px;
  color: #94a3b8;
}

/* 角色徽章 */
.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.role-student {
  background: #dbeafe;
  color: #1d4ed8;
}

.role-teacher {
  background: #dcfce7;
  color: #15803d;
}

.role-parent {
  background: #fef3c7;
  color: #b45309;
}

.role-admin {
  background: #fee2e2;
  color: #dc2626;
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

/* 抽屉样式 */
.user-drawer-modern :deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.drawer-content {
  padding: 24px;
}

.drawer-header-card {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  margin-bottom: 24px;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E");
  opacity: 0.3;
}

.avatar-large-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.avatar-large {
  border: 4px solid rgba(255, 255, 255, 0.3);
  font-size: 36px;
  font-weight: 600;
}

.status-ring {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  background: #94a3b8;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.9);
}

.status-ring.active {
  background: #22c55e;
}

.drawer-name {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.drawer-username {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin: 0 0 16px 0;
}

.drawer-badges {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.role-badge-large,
.status-badge-large {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  backdrop-filter: blur(10px);
}

.status-dot-lg {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

/* 抽屉内容区域 */
.drawer-section {
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px 0;
}

.section-title .el-icon {
  color: #64748b;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.info-value.id-value {
  font-family: monospace;
  color: #3b82f6;
}

/* 抽屉底部 */
.drawer-footer {
  display: flex;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
}

.drawer-footer .el-button {
  flex: 1;
}

/* 动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
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
