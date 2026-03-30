<template>
  <div class="register">
    <div class="container">
      <div class="register-form">
        <h1 class="page-title">注册</h1>
        <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="80px" class="demo-ruleForm">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="registerForm.password" autocomplete="off" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="registerForm.confirmPassword" autocomplete="off" placeholder="请确认密码"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('registerForm')" style="width: 100%">注册</el-button>
          </el-form-item>
          <el-form-item>
            <div class="register-actions">
              <el-button @click="resetForm('registerForm')">重置</el-button>
              <el-button type="text" @click="$router.push('/login')">已有账号？立即登录</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        email: '',
        phone: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.registerForm.password) {
                callback(new Error('两次输入密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 模拟注册
          this.$message.success('注册成功！')
          this.$router.push('/login')
          
          // 实际项目中调用API
          // this.$axios.post('/users/register', this.registerForm).then(response => {
          //   this.$message.success('注册成功！请登录')
          //   this.$router.push('/login')
          // }).catch(() => {
          //   this.$message.error('注册失败，请稍后重试')
          // })
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
.register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('/注册壁纸.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.container {
  width: 100%;
  max-width: 800px;
  padding: 20px;
}

.page-title {
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.register-form {
  max-width: 400px;
  margin: 0 auto;
  background-color: rgba(255, 255, 255, 0);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.register-actions {
  display: flex;
  justify-content: space-between;
}

@media (max-width: 768px) {
  .register-form {
    padding: 20px;
    margin: 0 20px;
  }
}
</style>
