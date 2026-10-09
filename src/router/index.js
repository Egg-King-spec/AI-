import { createRouter, createWebHashHistory } from 'vue-router'
import BackendLayout from '@/components/BackendLayout.vue'
import AuthLayout from '@/components/AuthLayout.vue'

const backendRoutes = [
  {
    path: '/back',
    component: BackendLayout,
    children: [
      { path: 'dashboard', component: () => import('@/views/dashboard.vue'), meta: { title: '数据分析', icon: 'PieChart' } },
      { path: 'knowledge', component: () => import('@/views/knowledge.vue'), meta: { title: '知识文章', icon: 'ChatLineSquare' } },
      { path: 'consultations', component: () => import('@/views/consultations.vue'), meta: { title: '咨询记录', icon: 'Message' } },
      { path: 'emotional', component: () => import('@/views/emotional.vue'), meta: { title: '情绪日志', icon: 'User' } }
    ]
  },
  {
    path: '/auth',
    component: AuthLayout,
    children: [
      { path: 'login', component: () => import('@/views/login.vue'), meta: { title: '登录', icon: 'Login' } },
      { path: 'register', component: () => import('@/views/register.vue'), meta: { title: '注册', icon: 'User' } }
    ]
  }
]

const routes = [
  { path: '/', redirect: '/auth/login' },
  ...backendRoutes,
  { path: '/knowledge', component: () => import('@/views/frontendKnowledge.vue') },
  { path: '/knowledge/article/:id', component: () => import('@/views/articleDetail.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/auth/login' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
