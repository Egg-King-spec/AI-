<template>
  <div class="register-page">
    <el-card class="register-card">
      <h2>注册心理健康助手账户</h2>
      <el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="formData.nickname" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit">注册</el-button>
      </el-form>
      <div class="footer">
        已有账号？<router-link to="/auth/login">去登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/admin'

const router = useRouter()
const formRef = ref()
const formData = reactive({
  username: '',
  email: '',
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: '',
  userType: 1
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为3到50个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度为6到50个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' }
  ]
}

const submit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (formData.password !== formData.confirmPassword) {
      ElMessage.error('两次输入密码不一致')
      return
    }
    await register(formData)
    ElMessage.success('注册成功，请登录')
    router.push('/auth/login')
  })
}
</script>

<style scoped>
.register-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f5f7fb; }
.register-card { width: 420px; border-radius: 12px; }
.footer { margin-top: 18px; text-align: center; color: #6b7280; }
</style>
