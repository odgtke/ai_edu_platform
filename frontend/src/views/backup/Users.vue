<template>
  <div class="users-page">
    <!-- 椤甸潰鏍囬 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">鐢ㄦ埛绠＄悊</h1>
        <p class="page-subtitle">绠＄悊绯荤粺鐢ㄦ埛淇℃伅锛屽寘鎷瀛︾敓銆佹暀甯堝拰绠＄悊鍛樿处鎴?/p>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="btn-gradient" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          娣诲姞鐢ㄦ埛
        </el-button>
      </div>
    </div>
    
    <!-- 鎼滅储绛涢夊崱鐗?-->
    <div class="search-card">
      <div class="search-filter">
        <div class="filter-item">
          <el-input
            v-model="searchForm.username"
            placeholder="鐢ㄦ埛鍚?
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="filter-item">
          <el-input
            v-model="searchForm.realName"
            placeholder="鐪熷疄濮撳悕"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Avatar /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="filter-item">
          <el-select v-model="searchForm.userType" placeholder="鐢ㄦ埛绫诲瀷" clearable>
            <el-option label="瀛︾敓" :value="1">
              <el-tag size="small" type="primary">瀛︾敓</el-tag>
            </el-option>
            <el-option label="鏁欏笀" :value="2">
              <el-tag size="small" type="success">鏁欏笀</el-tag>
            </el-option>
            <el-option label="绠＄悊鍛? :value="3">
              <el-tag size="small" type="danger">绠＄悊鍛?/el-tag>
            </el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <el-select v-model="searchForm.status" placeholder="鐘舵? clearable>
            <el-option label="鍚鐢" :value="1" />
            <el-option label="绂佺敤" :value="0" />
          </el-select>
        </div>
        <div class="filter-actions">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            鏌ヨ
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><RefreshRight /></el-icon>
            閲嶇疆
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 鏁版嵁琛ㄦ牸鍗＄墖 -->
    <div class="table-card">
      
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="鐢ㄦ埛鍚? />
        <el-table-column prop="realName" label="鐪熷疄濮撳悕" />
        <el-table-column prop="email" label="閭绠" />
        <el-table-column prop="phone" label="鐢佃瘽" />
        <el-table-column prop="userType" label="鐢ㄦ埛绫诲瀷" width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeType(scope.row.userType)">
              {{ getUserTypeLabel(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="鐘舵? width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '鍚鐢' : '绂佺敤' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="鍒涘缓鏃堕棿" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="鎿嶄綔" width="180">
          <template #default="scope">
            <el-button size="small" @click="editUser(scope.row)">缂栬緫</el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row)">鍒犻櫎</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
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
    </div>
    
    <!-- 娣诲姞/缂栬緫鐢ㄦ埛瀵硅瘽妗?-->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="鐢ㄦ埛鍚?>
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="鐪熷疄濮撳悕">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="閭绠">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="鐢佃瘽">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="鐢ㄦ埛绫诲瀷">
          <el-select v-model="userForm.userType" style="width: 100%;">
            <el-option label="瀛︾敓" :value="1" />
            <el-option label="鏁欏笀" :value="2" />
            <el-option label="绠＄悊鍛? :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="鐘舵?>
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">鍚鐢</el-radio>
            <el-radio :label="0">绂佺敤</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">鍙栨秷</el-button>
        <el-button type="primary" @click="saveUser" :loading="saveLoading">纭瀹</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Plus, User, Avatar, Search, RefreshRight } from '@element-plus/icons-vue'

export default {
  name: 'Users',
  components: { Plus, User, Avatar, Search, RefreshRight },
  data() {
    return {
      users: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      searchForm: {
        username: '',
        realName: '',
        userType: null,
        status: null
      },
      dialogVisible: false,
      dialogTitle: '娣诲姞鐢ㄦ埛',
      saveLoading: false,
      userForm: {
        userId: null,
        username: '',
        realName: '',
        email: '',
        phone: '',
        userType: 1,
        status: 1
      }
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        // 鏋勫缓鏌ヨ㈠弬鏁
        const params = new URLSearchParams()
        params.append('page', this.currentPage)
        params.append('size', this.pageSize)
        if (this.searchForm.username) params.append('username', this.searchForm.username)
        if (this.searchForm.realName) params.append('realName', this.searchForm.realName)
        if (this.searchForm.userType !== null) params.append('userType', this.searchForm.userType)
        if (this.searchForm.status !== null) params.append('status', this.searchForm.status)
        
        const response = await fetch(`/users/page?${params.toString()}`)
        const result = await response.json()
        console.log('API Response:', result)
        if (result.code === 200 && result.data) {
          this.users = result.data.records || []
          this.total = result.data.total || result.data.records?.length || 0
          console.log('Users loaded:', this.users.length, 'Total:', this.total)
        } else {
          // 濡傛灉鍒嗛〉鎺ュ彛涓嶅彲鐢锛屽皾璇曚娇鐢ㄦ櫘閫氬垪琛ㄦ帴鍙?
          await this.loadUsersFallback()
        }
      } catch (error) {
        console.error('鍔犺浇鐢ㄦ埛澶辫触:', error)
        // 浣跨敤澶囩敤鏂规
        await this.loadUsersFallback()
      } finally {
        this.loading = false
      }
    },
    async loadUsersFallback() {
      try {
        const response = await fetch('/users')
        const result = await response.json()
        if (result.code === 200 && result.data) {
          // 鍓嶇鍒嗛〉
          const allUsers = result.data
          this.total = allUsers.length
          const start = (this.currentPage - 1) * this.pageSize
          const end = start + this.pageSize
          this.users = allUsers.slice(start, end)
          console.log('Fallback - Users loaded:', this.users.length, 'Total:', this.total)
        }
      } catch (error) {
        console.error('澶囩敤鍔犺浇鐢ㄦ埛澶辫触:', error)
        this.$message.error('鍔犺浇鐢ㄦ埛澶辫触')
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadUsers()
    },
    resetSearch() {
      this.searchForm = {
        username: '',
        realName: '',
        userType: null,
        status: null
      }
      this.currentPage = 1
      this.loadUsers()
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    getUserTypeLabel(type) {
      const labels = { 1: '瀛︾敓', 2: '鏁欏笀', 3: '绠＄悊鍛? }
      return labels[type] || '鏈鐭'
    },
    getUserTypeType(type) {
      const types = { 1: 'primary', 2: 'success', 3: 'warning' }
      return types[type] || 'info'
    },
    showAddDialog() {
      this.dialogTitle = '娣诲姞鐢ㄦ埛'
      this.userForm = {
        userId: null,
        username: '',
        realName: '',
        email: '',
        phone: '',
        userType: 1,
        status: 1
      }
      this.dialogVisible = true
    },
    editUser(user) {
      this.dialogTitle = '缂栬緫鐢ㄦ埛'
      this.userForm = { ...user }
      this.dialogVisible = true
    },
    async saveUser() {
      this.saveLoading = true
      try {
        const url = this.userForm.userId ? '/users' : '/users'
        const method = this.userForm.userId ? 'PUT' : 'POST'
        const response = await fetch(url, {
          method: method,
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.userForm)
        })
        const result = await response.json()
        if (result.code === 200) {
          this.$message.success(this.userForm.userId ? '鏇存柊鎴愬姛' : '娣诲姞鎴愬姛')
          this.dialogVisible = false
          this.loadUsers()
        } else {
          this.$message.error(result.message || '鎿嶄綔澶辫触')
        }
      } catch (error) {
        console.error('淇濆瓨鐢ㄦ埛澶辫触:', error)
        this.$message.error('淇濆瓨澶辫触')
      } finally {
        this.saveLoading = false
      }
    },
    async deleteUser(user) {
      this.$confirm(`纭瀹氬垹闄ょ敤鎴 ${user.realName} 鍚楋紵`, '鎻愮ず', {
        confirmButtonText: '纭瀹',
        cancelButtonText: '鍙栨秷',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await fetch(`/users/${user.userId}`, { method: 'DELETE' })
          const result = await response.json()
          if (result.code === 200) {
            this.$message.success('鍒犻櫎鎴愬姛')
            this.loadUsers()
          } else {
            this.$message.error(result.message || '鍒犻櫎澶辫触')
          }
        } catch (error) {
          console.error('鍒犻櫎鐢ㄦ埛澶辫触:', error)
          this.$message.error('鍒犻櫎澶辫触')
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadUsers()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadUsers()
    }
  }
}
</script>

<style scoped>
/* ============================================
   鐢ㄦ埛绠＄悊椤甸潰 - 鏂拌捐¤勮?
   ============================================ */

.users-page {
  max-width: 1600px;
  margin: 0 auto;
}

/* 椤甸潰鏍囬 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-5);
}

.page-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

.btn-gradient {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  border: none;
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.btn-gradient:hover {
  background: linear-gradient(135deg, var(--primary-400) 0%, var(--primary-500) 100%);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* 鎼滅储鍗＄墖 */
.search-card {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  padding: var(--space-5);
  margin-bottom: var(--space-5);
}

.search-filter {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
  align-items: flex-end;
}

.filter-item {
  min-width: 160px;
}

.filter-item :deep(.el-input__wrapper),
.filter-item :deep(.el-select .el-input__wrapper) {
  border-radius: var(--radius-md);
}

.filter-actions {
  display: flex;
  gap: var(--space-2);
  margin-left: auto;
}

.filter-actions .el-button {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

/* 琛ㄦ牸鍗＄墖 */
.table-card {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  padding: var(--space-5);
}

.table-card :deep(.el-table) {
  border-radius: var(--radius-md);
  overflow: hidden;
}

/* 鍒嗛〉 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--space-5);
  padding-top: var(--space-4);
  border-top: 1px solid var(--border-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.search-filter {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}
</style>
