<template>
  <div class="classes-container">
    <div class="page-header">
      <h2>閻濓拷楠囩＄悊</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>鏂板烇拷閻濓拷楠
      </el-button>
    </div>

    <!-- 鎼滄粎鍌ㄦ爮?-->
    <div class="search-bar">
      <el-select v-model="searchForm.gradeId" placeholder="閫夊嬪ㄥ勾绾" clearable @change="handleSearch">
        <el-option
          v-for="grade in gradeList"
          :key="grade.gradeId"
          :label="grade.gradeName"
          :value="grade.gradeId"
        />
      </el-select>
      <el-input
        v-model="searchForm.keyword"
        placeholder="鎼滄粎鍌ㄩ悵锟介獓鍚庡秶袨"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <!-- 閻濓拷楠囧垹妤勩 -->
    <el-table :data="classList" v-loading="loading" border stripe>
      <el-table-column type="index" width="50" />
      <el-table-column prop="className" label="鐝绾у悕绉" min-width="150" />
      <el-table-column prop="gradeName" label="鎵灞炲勾绾" width="120" />
      <el-table-column prop="teacherName" label="鐝涓讳换" width="120" />
      <el-table-column prop="studentCount" label="瀛︾敓浜烘暟" width="100">
        <template #default="{ row }">
          <el-tag type="info">{{ row.studentCount }} 浜</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="鎻忚堪" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="鐘舵佲偓? width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '鍚庯拷鏁' : '绂佺敤' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="閹垮秳缍" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">缂栨牞绶</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">鍒犵娀娅</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 鍒嗛〉 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 鏂板烇拷/缂栨牞绶瀵硅瘽妗?-->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="閻濓拷楠囧悗宥囆" prop="className">
          <el-input v-model="form.className" placeholder="璇风柉绶鍏銉у疆缁狙冩倳缁? />
        </el-form-item>
        <el-form-item label="閹碘偓鐏炵偛鍕剧痪? prop="gradeId">
          <el-select v-model="form.gradeId" placeholder="閫夊嬪ㄥ勾绾" style="width: 100%">
            <el-option
              v-for="grade in gradeList"
              :key="grade.gradeId"
              :label="grade.gradeName"
              :value="grade.gradeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="閻濓拷瀵屼互? prop="teacherName">
          <el-input v-model="form.teacherName" placeholder="璇风柉绶鍏銉у疆涓夎虫崲濮撳悕" />
        </el-form-item>
        <el-form-item label="鎻忚堪" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="璇风柉绶鍏銉﹀伎杩? />
        </el-form-item>
        <el-form-item label="鐘舵佲偓? prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">鍚庯拷鏁</el-radio>
            <el-radio :label="0">绂佺敤</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">鍙栨秷</el-button>
        <el-button type="primary" @click="handleSubmit">纭锟界暰</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getClassPage, createClass, updateClass, deleteClass } from '@/clazz'
import { getActiveGrades } from '@/grade'

const loading = ref(false)
const classList = ref([])
const gradeList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  gradeId: null,
  keyword: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)
const currentId = ref(null)

const form = reactive({
  className: '',
  gradeId: null,
  teacherName: '',
  description: '',
  status: 1
})

const rules = {
  className: [{ required: true, message: '璇风柉绶鍏銉у疆缁狙冩倳缁?, trigger: 'blur' }],
  gradeId: [{ required: true, message: '璇风兘鈧澶嬪ㄥ勾绾', trigger: 'change' }]
}

// 閼惧嘲褰囧勾绾у垹妤勩
const fetchGrades = async () => {
  try {
    const res = await getActiveGrades()
    if (res.code === 200) {
      gradeList.value = res.data
    }
  } catch (error) {
    console.error('閼惧嘲褰囧勾绾у垹妤勩冨け璐:', error)
  }
}

// 閼惧嘲褰囬悵锟介獓鍒犳勩
const fetchClasses = async () => {
  loading.value = true
  try {
    const params = {
      page: pageNum.value,
      size: pageSize.value,
      gradeId: searchForm.gradeId,
      keyword: searchForm.keyword
    }
    const res = await getClassPage(params)
    if (res.code === 200) {
      classList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('閼惧嘲褰囬悵锟介獓鍒犳勩冨け璐:', error)
    ElMessage.error('閼惧嘲褰囬悵锟介獓鍒犳勩冨け璐')
  } finally {
    loading.value = false
  }
}

// 鎼滄粎鍌
const handleSearch = () => {
  pageNum.value = 1
  fetchClasses()
}

// 鍒嗛〉
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchClasses()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchClasses()
}

// 鏂板烇拷
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '鏂板烇拷閻濓拷楠'
  Object.assign(form, {
    className: '',
    gradeId: null,
    teacherName: '',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

// 缂栨牞绶
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '缂栨牞绶閻濓拷楠'
  currentId.value = row.classId
  Object.assign(form, {
    className: row.className,
    gradeId: row.gradeId,
    teacherName: row.teacherName,
    description: row.description,
    status: row.status
  })
  dialogVisible.value = true
}

// 鎻愪氦
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (isEdit.value) {
      const res = await updateClass(currentId.value, form)
      if (res.code === 200) {
        ElMessage.success('鏇村瓨鏌婃垚鍔')
        dialogVisible.value = false
        fetchClasses()
      }
    } else {
      const res = await createClass(form)
      if (res.code === 200) {
        ElMessage.success('娣诲姞鎴愬姛')
        dialogVisible.value = false
        fetchClasses()
      }
    }
  } catch (error) {
    console.error('鎻愪氦澶辫触:', error)
    ElMessage.error('鎻愪氦澶辫触')
  }
}

// 鍒犵娀娅
const handleDelete = (row) => {
  ElMessageBox.confirm('纭锟界暰鐟曚礁鍨归櫎銈堬拷閻濓拷楠囧悗妤嬬吹', '鎻愭劗銇', {
    confirmButtonText: '纭锟界暰',
    cancelButtonText: '鍙栨秷',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteClass(row.classId)
      if (res.code === 200) {
        ElMessage.success('鍒犵娀娅庢垚鍔')
        fetchClasses()
      }
    } catch (error) {
      console.error('鍒犵娀娅庡け璐:', error)
      ElMessage.error('鍒犵娀娅庡け璐')
    }
  })
}

onMounted(() => {
  fetchGrades()
  fetchClasses()
})
</script>

<style scoped>
.classes-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.search-bar .el-select {
  width: 150px;
}

.search-bar .el-input {
  width: 300px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
