<template>
  <div class="container">
    <div class="title">
        <div class="back-home">
            <el-icon><Back /></el-icon>
            <span>返回首页</span>
        </div>
        <div class="title-text">
            <h2>登录您的账户</h2>
            <p>请输入您的登录信息</p>
        </div>
    </div>
    <div class="form-container">
        <el-form 
            ref="ruleFormRef"
            :model="formData"
            :rules="rules"
            label-position="top"
        >
            <el-form-item label="用户名或邮箱" prop="username">
                <el-input v-model="formData.username" size="large" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="formData.password" size="large" placeholder="请输入密码" type="password" show-password />
            </el-form-item>
            <el-button class="btn" type="primary" size="large" @click="submitForm(ruleFormRef)">登录</el-button>
        </el-form>
        <div class="footer">
            <p>还没有账号？<router-link to="/auth/register">去注册</router-link></p>
        </div>
     </div>  
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const ruleFormRef=ref()

const formData = ref({
    username: '',
    password: ''
})

const rules = reactive({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ]
})

const submitForm = (formEl) => {
    if(!formEl) return
    formEl.validate((valid,fields) => {
        if (valid) {
            console.log(fields)
            // 登录逻辑
        } 
    })
}
</script>

<style scoped lang="scss">
.container {
    width:384px;
    .title{
        .back-home{
            margin-bottom: 60px;
        }
        .title-text{
            text-align: center;
           h2{
            font-size: 32px;
            margin-bottom: 10px;
           }
           p{
            font-size: 18px;
            color:#6b7280
           }
        }
    }
    .form-container{
        margin-top:30px;
        .btn{
            margin-top:40px;
            width: 100%;
        }
        .footer{
            padding:30px;
            text-align: center;
        }
    }
}
</style>