<template>
  <div class="dashboard-page">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card class="metric-card">
          <div class="metric-label">{{ card.label }}</div>
          <div class="metric-value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="15">
        <el-card>
          <template #header>近 7 日咨询会话</template>
          <div class="bar-chart">
            <div class="bar-item" v-for="(date, index) in data.chartDates" :key="date">
              <div class="bar-wrap">
                <div class="bar" :style="{ height: barHeight(data.chartSessions[index]) }"></div>
              </div>
              <span>{{ date }}</span>
              <strong>{{ data.chartSessions[index] || 0 }}</strong>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card>
          <template #header>情绪分布</template>
          <el-empty v-if="emotionItems.length === 0" description="暂无情绪分析数据" />
          <div v-else class="emotion-list">
            <div class="emotion-item" v-for="item in emotionItems" :key="item.name">
              <span>{{ item.name }}</span>
              <el-progress :percentage="item.percentage" :show-text="false" />
              <strong>{{ item.count }}</strong>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { dashboardOverview } from '@/api/admin'

const data = ref({
  userCount: 0,
  sessionCount: 0,
  messageCount: 0,
  articleCount: 0,
  todaySessionCount: 0,
  chartDates: [],
  chartSessions: [],
  emotionDistribution: {}
})

const cards = computed(() => [
  { label: '用户总数', value: data.value.userCount || 0 },
  { label: '咨询会话', value: data.value.sessionCount || 0 },
  { label: '消息总数', value: data.value.messageCount || 0 },
  { label: '知识文章', value: data.value.articleCount || 0 }
])

const maxSessions = computed(() => Math.max(1, ...(data.value.chartSessions || [1])))
const emotionItems = computed(() => {
  const distribution = data.value.emotionDistribution || {}
  const total = Object.values(distribution).reduce((sum, value) => sum + Number(value || 0), 0)
  return Object.entries(distribution).map(([name, count]) => ({
    name,
    count,
    percentage: total === 0 ? 0 : Math.round((Number(count) / total) * 100)
  }))
})

const barHeight = (value = 0) => {
  return Math.max(4, Math.round((Number(value) / maxSessions.value) * 150)) + 'px'
}

onMounted(async () => {
  data.value = await dashboardOverview()
})
</script>

<style scoped>
.dashboard-page { padding: 4px; }
.metric-card { border-radius: 8px; }
.metric-label { color: #6b7280; font-size: 14px; }
.metric-value { margin-top: 10px; font-size: 28px; font-weight: 700; color: #111827; }
.chart-row { margin-top: 16px; }
.bar-chart { display: flex; align-items: end; gap: 18px; min-height: 215px; padding: 10px 8px 0; }
.bar-item { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 8px; color: #6b7280; }
.bar-wrap { height: 160px; display: flex; align-items: end; }
.bar { width: 32px; background: linear-gradient(180deg, #fb923c, #f59e0b); border-radius: 6px 6px 0 0; }
.emotion-list { display: flex; flex-direction: column; gap: 16px; }
.emotion-item { display: grid; grid-template-columns: 72px 1fr 32px; align-items: center; gap: 10px; }
</style>
