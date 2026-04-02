import { ref } from 'vue'

/**
 * 鐞涖劍鐗哥紒鍕値瀵繐鍤遍弫?
 */
export function useTable() {
  // 闁鑵戠悰?
  const selectedRows = ref([])
  
  // 鐞涖劍鐗搁崝鐘烘祰閻樿埖?
  const tableLoading = ref(false)

  /**
   * 闁瀚ㄩ弨鐟板綁
   */
  const handleSelectionChange = (rows) => {
    selectedRows.value = rows
  }

  /**
   * 閼惧嘲褰囬柅澶夎厬鐞涘瞼娈慖D閸掓銆?
   */
  const getSelectedIds = () => {
    return selectedRows.value.map(row => row.id)
  }

  /**
   * 閺勯崥锕傚鑵戦弫鐗堝祦
   */
  const hasSelection = () => {
    return selectedRows.value.length > 0
  }

  /**
   * 濞撳懐鈹栭柅澶嬪
   */
  const clearSelection = () => {
    selectedRows.value = []
  }

  return {
    selectedRows,
    tableLoading,
    handleSelectionChange,
    getSelectedIds,
    hasSelection,
    clearSelection
  }
}
