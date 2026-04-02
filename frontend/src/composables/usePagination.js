import { ref, computed } from 'vue'

/**
 * 分页组合式函数
 * @param {Function} fetchFn - 获取数据的函数
 */
export function usePagination(fetchFn) {
  // 分页状态
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  const loading = ref(false)
  const data = ref([])

  // 分页参数
  const pagination = computed(() => ({
    currentPage: currentPage.value,
    pageSize: pageSize.value,
    total: total.value
  }))

  /**
   * 加载数据
   */
  const loadData = async (params = {}) => {
    loading.value = true
    try {
      const res = await fetchFn({
        current: currentPage.value,
        size: pageSize.value,
        ...params
      })
      data.value = res.data.records || res.data.list || []
      total.value = res.data.total || 0
      return res
    } catch (error) {
      console.error('加载数据失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 页码改变
   */
  const handleCurrentChange = (page) => {
    currentPage.value = page
    loadData()
  }

  /**
   * 每页条数改变
   */
  const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
    loadData()
  }

  /**
   * 刷新数据
   */
  const refresh = () => {
    currentPage.value = 1
    return loadData()
  }

  /**
   * 重置分页
   */
  const reset = () => {
    currentPage.value = 1
    pageSize.value = 10
    total.value = 0
    data.value = []
  }

  return {
    currentPage,
    pageSize,
    total,
    loading,
    data,
    pagination,
    loadData,
    handleCurrentChange,
    handleSizeChange,
    refresh,
    reset
  }
}
