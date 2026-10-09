<template>
  <div>
    <PageHead title="情绪日志" />
    <el-form :inline="true" :model="query" style="margin-top: 16px">
      <el-form-item label="情绪">
        <el-input v-model="query.emotionTag" placeholder="如 焦虑" clearable />
      </el-form-item>
      <el-form-item label="开始日期">
        <el-date-picker v-model="query.startDate" type="date" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="diaryDate" label="日期" width="120" />
      <el-table-column prop="emotionTag" label="情绪" width="100" />
      <el-table-column prop="emotionScore" label="分数" width="80" />
      <el-table-column prop="content" label="内容" min-width="240" show-overflow-tooltip />
      <el-table-column prop="riskLevel" label="风险等级" width="100" />
      <el-table-column prop="suggestion" label="建议" min-width="220" show-overflow-tooltip />
    </el-table>
    <el-pagination
      style="margin-top: 20px"
      layout="prev, pager, next"
      :total="pagination.total"
      :page-size="pagination.pageSize"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import PageHead from '@/components/PageHead.vue'
import { emotionDiaryPage } from '@/api/admin'

const tableData = ref([])
const query = reactive({ emotionTag: '', startDate: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const load = async () => {
  const page = await emotionDiaryPage({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize,
    ...query
  })
  tableData.value = page.records || []
  pagination.total = page.total || 0
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  load()
}

onMounted(load)
</script>
