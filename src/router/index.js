import { createRouter, createWebHashHistory } from 'vue-router'
import BackendLayout from '@/components/BackendLayout.vue'
import AuthLayout from '@/components/AuthLayout.vue'

// 路由配置
const backendRoutes = [
  {
    path: '/back',
    component: BackendLayout,
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard.vue'),
        meta: {
          title: '数据分析',
          icon: 'PieChart'
        }
      }
,
    {
        path:'kenwledge',
        component: () => import('@/views/knowledge.vue'),
        meta: {
          title: '知识文章',
          icon: 'ChatLineSquare'
        }
      },
      {
        path:'consultations',
        component: () => import('@/views/consultations.vue'),
        meta: {
          title: '咨询记录',
          icon: 'Message'
        }
      },
      {
        path:'emotional',
        component: () => import('@/views/emotional.vue'),
        meta: {
          title: '情绪日志',
          icon: 'user'
        }
      }
    ]
  },
  {
    path:'/auth',
    component: AuthLayout,
    children: [
      {
        path:'login',
        component: () => import('@/views/login.vue'),
        meta: {
          title: '登录',
          icon: 'Login'
        }
      },
      {
        path:'register',
        component: () => import('@/views/register.vue'),
        meta: {
          title: '注册',
          icon: 'User'
        }
      }
    ]
  }
]
// 创建路由实例
const router = createRouter({
  history: createWebHashHistory(),
  routes: backendRoutes
})
// 导出路由实例
export default router