<template>
  <div class="account-management">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 账户管理</h2>
          <el-button @click="$router.push('/home')">返回首页</el-button>
        </div>
      </el-header>
      <el-main>
        <el-card class="account-card">
          <h3>修改邮箱</h3>
          <el-form :model="emailForm" :rules="emailRules" ref="emailForm" label-width="100px">
            <el-form-item label="当前邮箱">
              <el-input v-model="currentEmail" disabled></el-input>
            </el-form-item>
            <el-form-item label="新邮箱" prop="email">
              <el-input v-model="emailForm.email" placeholder="请输入新邮箱"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateEmail">修改邮箱</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="account-card">
          <h3>修改密码</h3>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="account-card">
          <h3>账户操作</h3>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
          <el-button type="warning" @click="handleSwitchAccount" style="margin-left: 12px;">切换账号</el-button>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { userAPI } from '@/api/user'

export default {
  name: 'AccountManagement',
  data() {
    const validatePass = (rule, value, callback) => {
      if (!value || value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      currentEmail: '',
      emailForm: { email: '' },
      emailRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      passwordForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    loadUserInfo() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) {
        this.$router.push('/login')
        return
      }
      userAPI.getUserProfile(user.id).then(res => {
        this.currentEmail = res.email || ''
      }).catch(() => {
        this.$message.error('加载用户信息失败')
      })
    },
    async handleUpdateEmail() {
      const valid = await new Promise(resolve => this.$refs.emailForm.validate(v => resolve(v)))
      if (!valid) return
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      try {
        const res = await userAPI.updateEmail(user.id, this.emailForm.email)
        if (res && res.success === false) {
          this.$message.error(res.message || '邮箱修改失败')
          return
        }
        this.$message.success('邮箱修改成功')
        this.currentEmail = this.emailForm.email
        this.emailForm.email = ''
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '邮箱修改失败')
      }
    },
    async handleUpdatePassword() {
      const valid = await new Promise(resolve => this.$refs.passwordForm.validate(v => resolve(v)))
      if (!valid) return
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      try {
        const res = await userAPI.updatePasswordWithOld(user.id, this.passwordForm.oldPassword, this.passwordForm.newPassword)
        if (res && res.success === false) {
          this.$message.error(res.message || '密码修改失败')
          return
        }
        this.$message.success('密码修改成功')
        this.passwordForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '密码修改失败')
      }
    },
    async handleLogout() {
      try { await userAPI.logout() } catch (e) { /* ignore */ }
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      this.$store.dispatch('user/logout')
      this.$router.push('/login')
    },
    handleSwitchAccount() {
      this.$confirm('确定要切换账号吗？', '提示', { type: 'warning' })
        .then(() => this.handleLogout())
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 0;
}
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}
.logo { color: #67C23A; }
.account-card {
  max-width: 600px;
  margin: 0 auto 20px;
}
.account-card h3 {
  margin-bottom: 20px;
  color: #333;
}
</style>
