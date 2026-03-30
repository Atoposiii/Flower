<template>
  <div class="login">
    <div class="container">
      <div class="login-form">
        <div class="login-icon">
          <span class="flower-emoji">🌼</span>
        </div>
        <h1 class="page-title">登录</h1>
        <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="80px" class="demo-ruleForm">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="loginForm.password" autocomplete="off" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('loginForm')" style="width: 100%" :loading="loading">登录</el-button>
          </el-form-item>
          <el-form-item>
            <div class="login-actions">
              <el-button @click="resetForm('loginForm')">重置</el-button>
              <el-button type="text" @click="$router.push('/register')">注册</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import userApi from '../api/modules/user'

export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    async submitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const response = await userApi.login({
              username: this.loginForm.username,
              password: this.loginForm.password
            })

            const userData = response.data
            
            this.$store.dispatch('user/login', {
              id: userData.id,
              username: userData.username,
              name: userData.nickname || userData.username,
              role: userData.role ? userData.role.toUpperCase() : 'USER',
              isMember: userData.isMember,
              memberExpireTime: userData.memberExpireTime,
              email: userData.email,
              phone: userData.phone,
              avatar: userData.avatar
            })
            
            this.$message.success('登录成功！')
            this.$router.push('/home')
          } catch (error) {
            console.error('登录失败:', error)
            this.$message.error('用户名或密码错误，请重试')
          } finally {
            this.loading = false
          }
        } else {
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('/登录壁纸.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.container {
  width: 100%;
  max-width: 800px;
  padding: 20px;
}

.login-icon {
  text-align: center;
  margin-bottom: 20px;
}

.flower-emoji {
  font-size: 48px;
}

.page-title {
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.login-form {
  max-width: 400px;
  margin: 0 auto;
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-actions {
  display: flex;
  justify-content: space-between;
}

@media (max-width: 768px) {
  .login-form {
    padding: 20px;
    margin: 0 20px;
  }
}
</style>
