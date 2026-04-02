<template>
  <div class="courses-page">
    <div class="page-header">
      <h2>璇剧▼绠＄悊</h2>
      <p class="subtitle">绠＄悊绯荤粺璇剧▼淇℃伅</p>
    </div>
    
    <!-- 鍔犺浇鐘舵佹樉绀洪ㄦ灦灞 -->
    <SkeletonScreen v-if="loading" type="table" :rows="8" />
    
    <el-card v-else>
      <template #header>
        <div class="card-header">
          <span>璇剧▼鍒楄〃</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon> 娣诲姞璇剧▼
          </el-button>
        </div>
      </template>
      
      <!-- 鎼滅储绛涢夊尯鍩?-->
      <div class="search-filter">
        <el-input
          v-model="searchForm.courseName"
          placeholder="璇剧▼鍚嶇О"
          clearable
          style="width: 180px; margin-right: 10px;"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="searchForm.courseCode"
          placeholder="璇剧▼缂栫爜"
          clearable
          style="width: 150px; margin-right: 10px;"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.gradeLevel"
          placeholder="骞寸骇"
          clearable
          style="width: 120px; margin-right: 10px;"
        >
          <el-option label="涓骞寸骇" :value="1" />
          <el-option label="浜屽勾绾? :value="2" />
          <el-option label="涓夊勾绾? :value="3" />
          <el-option label="鍥涘勾绾? :value="4" />
          <el-option label="浜斿勾绾? :value="5" />
          <el-option label="鍏骞寸? :value="6" />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="鐘舵?
          clearable
          style="width: 100px; margin-right: 10px;"
        >
          <el-option label="鍚鐢" :value="1" />
          <el-option label="绂佺敤" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">鏌ヨ</el-button>
        <el-button @click="resetSearch">閲嶇疆</el-button>
      </div>
      
      <el-table :data="courses" style="width: 100%" v-loading="loading">
        <el-table-column prop="courseId" label="ID" width="80" />
        <el-table-column prop="courseName" label="璇剧▼鍚嶇О" />
        <el-table-column prop="subjectName" label="瀛︾" />
        <el-table-column prop="gradeLevel" label="骞寸骇" />
        <el-table-column prop="credit" label="瀛﹀垎" width="80" />
        <el-table-column prop="difficulty" label="闅惧害" width="120">
          <template #default="scope">
            <el-rate :model-value="scope.row.difficulty" disabled show-score />
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
            <el-button size="small" @click="editCourse(scope.row)">缂栬緫</el-button>
            <el-button size="small" type="danger" @click="deleteCourse(scope.row)">鍒犻櫎</el-button>
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
    </el-card>
    
    <!-- 娣诲姞/缂栬緫璇剧▼瀵硅瘽妗?-->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
    >
      <el-form :model="courseForm" label-width="100px">
        <el-form-item label="璇剧▼鍚嶇О">
          <el-input v-model="courseForm.courseName" />
        </el-form-item>
        <el-form-item label="璇剧▼缂栫爜">
          <el-input v-model="courseForm.courseCode" />
        </el-form-item>
        <el-form-item label="璇剧▼鎻忚堪">
          <el-input v-model="courseForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="骞寸骇">
          <el-select v-model="courseForm.gradeLevel" style="width: 100%;">
            <el-option label="涓骞寸骇" :value="1" />
            <el-option label="浜屽勾绾? :value="2" />
            <el-option label="涓夊勾绾? :value="3" />
            <el-option label="鍥涘勾绾? :value="4" />
            <el-option label="浜斿勾绾? :value="5" />
            <el-option label="鍏骞寸? :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="瀛﹀垎">
          <el-input-number v-model="courseForm.credit" :min="0.5" :max="10" :step="0.5" />
        </el-form-item>
        <el-form-item label="闅惧害">
          <el-rate v-model="courseForm.difficulty" :max="5" />
        </el-form-item>
        <el-form-item label="鐘舵?>
          <el-radio-group v-model="courseForm.status">
            <el-radio :label="1">鍚鐢</el-radio>
            <el-radio :label="0">绂佺敤</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">鍙栨秷</el-button>
        <el-button type="primary" @click="saveCourse" :loading="saveLoading">纭瀹</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
import SkeletonScreen from '../components/SkeletonScreen.vue'

export default {
  name: 'Courses',
  components: { Plus, SkeletonScreen },
  data() {
    return {
      courses: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      searchForm: {
        courseName: '',
        courseCode: '',
        gradeLevel: null,
        status: null
      },
      dialogVisible: false,
      dialogTitle: '娣诲姞璇剧▼',
      saveLoading: false,
      courseForm: {
        courseId: null,
        courseName: '',
        courseCode: '',
        description: '',
        gradeLevel: 1,
        credit: 3.0,
        difficulty: 3,
        status: 1
      }
    }
  },
  mounted() {
    this.loadCourses()
  },
  methods: {
    async loadCourses() {
      this.loading = true
      try {
        // 鏋勫缓鏌ヨ㈠弬鏁
        const params = new URLSearchParams()
        params.append('page', this.currentPage)
        params.append('size', this.pageSize)
        if (this.searchForm.courseName) params.append('courseName', this.searchForm.courseName)
        if (this.searchForm.courseCode) params.append('courseCode', this.searchForm.courseCode)
        if (this.searchForm.gradeLevel !== null) params.append('gradeLevel', this.searchForm.gradeLevel)
        if (this.searchForm.status !== null) params.append('status', this.searchForm.status)
        
        const response = await fetch(`/courses/page?${params.toString()}`)
        const result = await response.json()
        console.log('API Response:', result)
        if (result.code === 200 && result.data) {
          this.courses = result.data.records || []
          this.total = result.data.total || result.data.records?.length || 0
          console.log('Courses loaded:', this.courses.length, 'Total:', this.total)
        } else {
          // 濡傛灉鍒嗛〉鎺ュ彛涓嶅彲鐢锛屽皾璇曚娇鐢ㄦ櫘閫氬垪琛ㄦ帴鍙?
          await this.loadCoursesFallback()
        }
      } catch (error) {
        console.error('鍔犺浇璇剧▼澶辫触:', error)
        // 浣跨敤澶囩敤鏂规
        await this.loadCoursesFallback()
      } finally {
        this.loading = false
      }
    },
    async loadCoursesFallback() {
      try {
        const response = await fetch('/courses')
        const result = await response.json()
        if (result.code === 200 && result.data) {
          // 鍓嶇鍒嗛〉
          const allCourses = result.data
          this.total = allCourses.length
          const start = (this.currentPage - 1) * this.pageSize
          const end = start + this.pageSize
          this.courses = allCourses.slice(start, end)
          console.log('Fallback - Courses loaded:', this.courses.length, 'Total:', this.total)
        }
      } catch (error) {
        console.error('澶囩敤鍔犺浇璇剧▼澶辫触:', error)
        this.$message.error('鍔犺浇璇剧▼澶辫触')
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadCourses()
    },
    resetSearch() {
      this.searchForm = {
        courseName: '',
        courseCode: '',
        gradeLevel: null,
        status: null
      }
      this.currentPage = 1
      this.loadCourses()
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    showAddDialog() {
      this.dialogTitle = '娣诲姞璇剧▼'
      this.courseForm = {
        courseId: null,
        courseName: '',
        courseCode: '',
        description: '',
        gradeLevel: 1,
        credit: 3.0,
        difficulty: 3,
        status: 1
      }
      this.dialogVisible = true
    },
    editCourse(course) {
      this.dialogTitle = '缂栬緫璇剧▼'
      this.courseForm = { ...course }
      this.dialogVisible = true
    },
    async saveCourse() {
      this.saveLoading = true
      try {
        const url = this.courseForm.courseId ? '/courses' : '/courses'
        const method = this.courseForm.courseId ? 'PUT' : 'POST'
        const response = await fetch(url, {
          method: method,
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.courseForm)
        })
        const result = await response.json()
        if (result.code === 200) {
          this.$message.success(this.courseForm.courseId ? '鏇存柊鎴愬姛' : '娣诲姞鎴愬姛')
          this.dialogVisible = false
          this.loadCourses()
        } else {
          this.$message.error(result.message || '鎿嶄綔澶辫触')
        }
      } catch (error) {
        console.error('淇濆瓨璇剧▼澶辫触:', error)
        this.$message.error('淇濆瓨澶辫触')
      } finally {
        this.saveLoading = false
      }
    },
    async deleteCourse(course) {
      this.$confirm(`纭瀹氬垹闄よ剧▼ ${course.courseName} 鍚楋紵`, '鎻愮ず', {
        confirmButtonText: '纭瀹',
        cancelButtonText: '鍙栨秷',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await fetch(`/courses/${course.courseId}`, { method: 'DELETE' })
          const result = await response.json()
          if (result.code === 200) {
            this.$message.success('鍒犻櫎鎴愬姛')
            this.loadCourses()
          } else {
            this.$message.error(result.message || '鍒犻櫎澶辫触')
          }
        } catch (error) {
          console.error('鍒犻櫎璇剧▼澶辫触:', error)
          this.$message.error('鍒犻櫎澶辫触')
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadCourses()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadCourses()
    }
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-filter {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>
