<template>
  <div class="skeleton-screen">
    <el-skeleton :rows="rows" animated v-if="type === 'list'" />
    <el-skeleton :rows="rows" animated v-else-if="type === 'card'">
      <template #template>
        <div style="display: flex; gap: 20px; flex-wrap: wrap;">
          <div v-for="i in rows" :key="i" style="width: 240px; margin-bottom: 20px;">
            <el-skeleton-item variant="image" style="width: 240px; height: 160px;" />
            <div style="margin-top: 12px;">
              <el-skeleton-item variant="h3" style="width: 70%;" />
              <el-skeleton-item variant="text" style="margin-top: 8px; width: 50%;" />
            </div>
          </div>
        </div>
      </template>
    </el-skeleton>
    <el-skeleton :rows="rows" animated v-else-if="type === 'table'">
      <template #template>
        <el-skeleton-item variant="text" style="width: 100%; height: 40px; margin-bottom: 10px;" v-for="i in rows" :key="i" />
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'list', // list, card, table
    validator: (value) => ['list', 'card', 'table'].includes(value)
  },
  rows: {
    type: Number,
    default: 3
  }
})
</script>

<style scoped>
.skeleton-screen {
  padding: 20px;
}
</style>
