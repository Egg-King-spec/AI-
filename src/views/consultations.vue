<template>
  <div>
    <PageHead title="咨询记录" />
    <el-table :data="tableData" style="width: 100%; margin-top: 20px">
      <el-table-column prop="id" label="会话ID" width="100" />
      <el-table-column prop="username" label="用户" width="140" />
      <el-table-column prop="sessionTitle" label="会话标题" min-width="200" />
      <el-table-column prop="messageCount" label="消息数" width="100" />
      <el-table-column prop="durationMinutes" label="时长(分钟)" width="120" />
      <el-table-column prop="startedAt" label="开始时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button text type="primary" @click="showMessages(scope.row)">查看消息</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 20px"
      layout="prev, pager, next"
      :total="pagination.total"
      :page-size="pagination.pageSize"
      @current-change="handlePageChange"
    />
    <el-dialog v-model="dialogVisible" title="会话消息" width="720px">
      <div class="message-list">
        <div v-for="message in messages" :key="message.id" class="message-item">
          <strong>{{ message.senderType === 1 ? '用户' : 'AI' }}</strong>
          <p>{{ message.content }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import PageHead from '@/components/PageHead.vue'
import { consultationPage } from '@/api/admin'
import { getSessionDetail } from '@/api/frontend'

const tableData = ref([])
const messages = ref([])
const dialogVisible = ref(false)
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const load = async () => {
  const page = await consultationPage({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  })
  tableData.value = page.records || []
  pagination.total = page.total || 0
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  load()
}

const showMessages = async (row) => {
  messages.value = await getSessionDetail('session_' + row.id)
  dialogVisible.value = true
}

onMounted(load)
</script>

<style scoped>
.message-list { max-height: 460px; overflow-y: auto; }
.message-item { padding: 10px 0; border-bottom: 1px solid #f0f0f0; }
.message-item p { margin-top: 6px; white-space: pre-wrap; color: #4b5563; }
</style>
