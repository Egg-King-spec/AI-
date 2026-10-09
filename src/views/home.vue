<template>
  <div class="home-page">
    <h1>心理健康 AI 助手</h1>
    <p>从知识库开始了解情绪管理，或进入对话页面记录今天的感受。</p>
    <div class="actions">
      <el-button type="primary" @click="$router.push('/knowledge')">心理知识库</el-button>
      <el-button @click="$router.push('/back/dashboard')">进入管理后台</el-button>
    </div>
  </div>
</template>

<style scoped>
.home-page { max-width: 920px; margin: 0 auto; padding: 96px 24px; }
.actions { display: flex; gap: 12px; margin-top: 24px; }
</style>
