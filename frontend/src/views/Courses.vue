<template>
  <div class="courses">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">课程管理</h1>
        <p class="page-subtitle">管理系统课程信息，包括课程创建、编辑和发布</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加课程
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索课程名称或编号"
        class="search-input"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="filterCategory" placeholder="全部分类" clearable>
        <el-option label="编程开发" value="programming" />
        <el-option label="数据科学" value="data-science" />
        <el-option label="人工智能" value="ai" />
        <el-option label="前端开发" value="frontend" />
      </el-select>
      
      <el-select v-model="filterStatus" placeholder="全部状态" clearable>
        <el-option label="已发布" value="published" />
        <el-option label="草稿" value="draft" />
        <el-option label="已下线" value="offline" />
      </el-select>
    </div>

    <!-- 课程卡片列表 -->
    <div class="courses-grid">
      <div 
        v-for="course in filteredCourses" 
        :key="course.id"
        class="course-card"
      >
        <div class="course-cover">
          <img :src="course.coverImage || `https://placehold.co/300x180/e4e7ed/909399?text=${encodeURIComponent(course.name)}`" :alt="course.name" />
          <div class="course-status" :class="course.status">
            {{ getStatusLabel(course.status) }}
          </div>
        </div>
        
        <div class="course-content">
          <h3 class="course-title">{{ course.name }}</h3>
          <p class="course-desc">{{ course.description }}</p>
          
          <div class="course-meta">
            <span class="meta-item">
              <el-icon><User /></el-icon>
              {{ course.studentCount }} 人学习
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              {{ course.duration }} 小时
            </span>
            <span class="meta-item">
              <el-icon><Star /></el-icon>
              {{ course.rating }} 分
            </span>
          </div>
          
          <div class="course-footer">
            <div class="course-category">
              <el-tag size="small">{{ getCategoryLabel(course.category) }}</el-tag>
            </div>
            <div class="course-actions">
              <el-button type="primary" link @click="editCourse(course)">
                编辑
              </el-button>
              <el-button type="danger" link @click="deleteCourse(course)">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="filteredCourses.length === 0" description="暂无课程数据" />

    <!-- 分页 -->
    <div class="pagination" v-if="filteredCourses.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[8, 16, 24, 32]"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑课程' : '添加课程'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入课程名称" />
        </el-form-item>
        
        <el-form-item label="课程分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="编程开发" value="programming" />
            <el-option label="数据科学" value="data-science" />
            <el-option label="人工智能" value="ai" />
            <el-option label="前端开发" value="frontend" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="课程时长" prop="duration">
          <el-input-number v-model="form.duration" :min="1" :max="100" style="width: 200px" />
          <span class="unit">小时</span>
        </el-form-item>
        
        <el-form-item label="课程状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="published">已发布</el-radio>
            <el-radio label="draft">草稿</el-radio>
            <el-radio label="offline">已下线</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="课程封面">
          <el-upload
            class="cover-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleCoverChange"
          >
            <img v-if="form.cover" :src="form.cover" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <el-icon><Plus /></el-icon>
              <span>上传封面</span>
            </div>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="课程简介" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入课程简介"
          />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCourses, createCourse as apiCreateCourse, updateCourse as apiUpdateCourse, deleteCourse as apiDeleteCourse } from '@/api/course'
    const loading = ref(false)
    const searchQuery = ref('')
    const filterCategory = ref('')
    const filterStatus = ref('')
    const currentPage = ref(1)
    const pageSize = ref(8)
    const total = ref(0)
    
    // 课程数据
    const courses = ref([])
    
    // 从后端获取课程列表
    const fetchCourses = async () => {
      loading.value = true
      try {
        const res = await getAllCourses()
        if (res.code === 200) {
          // 映射后端字段到前端字段
          courses.value = res.data.map(course => ({
            id: course.courseId,
            name: course.courseName,
            description: course.description,
            category: mapCategory(course.courseName),
            duration: course.totalLessons || 20,
            status: mapStatus(course.status),
            studentCount: Math.floor(Math.random() * 2000) + 100, // 临时使用随机数
            rating: (Math.random() * 1.5 + 3.5).toFixed(1), // 临时使用随机评分
            coverImage: course.coverImage || ''
          }))
          total.value = courses.value.length
        } else {
          ElMessage.error(res.message || '获取课程列表失败')
        }
      } catch (error) {
        console.error('获取课程列表失败:', error)
        ElMessage.error('获取课程列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 映射状态码到状态字符串
    const mapStatus = (status) => {
      const statusMap = { 0: 'draft', 1: 'published', 2: 'offline' }
      return statusMap[status] || 'draft'
    }
    
    // 反向映射状态字符串到状态码
    const reverseMapStatus = (status) => {
      const statusMap = { draft: 0, published: 1, offline: 2 }
      return statusMap[status] || 0
    }
    
    // 根据课程名称映射分类
    const mapCategory = (courseName) => {
      if (!courseName) return 'programming'
      const name = courseName.toLowerCase()
      if (name.includes('python') || name.includes('java') || name.includes('web') || name.includes('flutter')) return 'programming'
      if (name.includes('数据') || name.includes('åæ')) return 'data-science'
      if (name.includes('机器') || name.includes('åä¸') || name.includes('区块链')) return 'ai'
      if (name.includes('ui') || name.includes('è®¾è®¡') || name.includes('视觉')) return 'frontend'
      return 'programming'
    }

    // 筛选后的课程
    const filteredCourses = computed(() => {
      let result = courses.value
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(course => 
          course.name.toLowerCase().includes(query) ||
          course.id.toString().includes(query)
        )
      }
      
      if (filterCategory.value) {
        result = result.filter(course => course.category === filterCategory.value)
      }
      
      if (filterStatus.value) {
        result = result.filter(course => course.status === filterStatus.value)
      }
      
      total.value = result.length
      
      // 分页
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return result.slice(start, end)
    })

    // 获取分类标签
    const getCategoryLabel = (category) => {
      const categoryMap = {
        programming: '编程开发',
        'data-science': '数据科学',
        ai: '人工智能',
        frontend: '前端开发'
      }
      return categoryMap[category] || category
    }

    // 获取状态标签
    const getStatusLabel = (status) => {
      const statusMap = {
        published: '已发布',
        draft: '草稿',
        offline: '已下线'
      }
      return statusMap[status] || status
    }

    // 对话框
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const submitting = ref(false)
    const formRef = ref(null)
    const form = ref({
      name: '',
      category: '',
      duration: 1,
      status: 'draft',
      cover: '',
      description: ''
    })

    const rules = {
      name: [
        { required: true, message: '请输入课程名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      category: [
        { required: true, message: '请选择课程分类', trigger: 'change' }
      ],
      duration: [
        { required: true, message: '请输入课程时长', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入课程简介', trigger: 'blur' }
      ]
    }

    // 显示添加对话框
    const showAddDialog = () => {
      isEdit.value = false
      form.value = {
        name: '',
        category: '',
        duration: 1,
        status: 'draft',
        cover: '',
        description: ''
      }
      dialogVisible.value = true
    }

    // 编辑课程
    const editCourse = (course) => {
      isEdit.value = true
      form.value = {
        id: course.id,
        name: course.name,
        category: course.category,
        duration: course.duration,
        status: course.status,
        cover: course.coverImage || '',
        description: course.description
      }
      dialogVisible.value = true
    }

    // 提交表单
    const submitForm = async () => {
      const valid = await formRef.value?.validate().catch(() => false)
      if (!valid) return
      
      submitting.value = true
      
      try {
        // 构建后端需要的字段格式
        const courseData = {
          courseId: isEdit.value ? form.value.id : undefined,
          courseName: form.value.name,
          description: form.value.description,
          totalLessons: form.value.duration,
          status: reverseMapStatus(form.value.status),
          coverImage: form.value.cover,
          teacherId: 1 // 临时使用默认教师ID
        }
        
        if (isEdit.value) {
          const res = await apiUpdateCourse(courseData)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            await fetchCourses() // 刷新列表
          } else {
            ElMessage.error(res.message || '更新失败')
          }
        } else {
          const res = await apiCreateCourse(courseData)
          if (res.code === 200) {
            ElMessage.success('添加成功')
            await fetchCourses() // 刷新列表
          } else {
            ElMessage.error(res.message || '添加失败')
          }
        }
        
        dialogVisible.value = false
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }

    // 删除课程
    const deleteCourse = (course) => {
      ElMessageBox.confirm(
        `确定要删除课程 "${course.name}" 吗？`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        try {
          const res = await apiDeleteCourse(course.id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await fetchCourses() // 刷新列表
          } else {
            ElMessage.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      }).catch(() => {})
    }

    // 处理封面上传
    const handleCoverChange = (file) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        form.value.cover = e.target.result
      }
      reader.readAsDataURL(file.raw)
    }

    onMounted(() => {
      fetchCourses()
    })

</script>

<style scoped>
.courses {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-content h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.header-content p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 筛选区域 */
.filter-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-input {
  width: 300px;
}

/* 课程网格 */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.course-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.course-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-status {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.course-status.published {
  background: #67c23a;
}

.course-status.draft {
  background: #909399;
}

.course-status.offline {
  background: #f56c6c;
}

.course-content {
  padding: 16px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-desc {
  font-size: 13px;
  color: #666;
  margin: 0 0 12px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}

.course-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e8e8e8;
}

/* 封面上传 */
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 120px;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.cover-placeholder .el-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.unit {
  margin-left: 8px;
  color: #666;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: flex-end;
}

/* 响应式 */
@media (max-width: 1200px) {
  .courses-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .courses-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .courses-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
  }
  
  .search-input {
    width: 100%;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>
