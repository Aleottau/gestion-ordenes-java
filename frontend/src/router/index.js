import { createRouter, createWebHistory } from 'vue-router'
import authRouter from '@/modules/auth/authRouter'
import ordersRouter from '@/modules/orders/ordersRouter'

const routes = [
  { path: '/', redirect: '/login' },
  ...authRouter,
  ...ordersRouter
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
