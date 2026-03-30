<template>
  <div class="profile">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 个人中心</h2>
          <el-button @click="$router.push('/home')">返回首页</el-button>
        </div>
      </el-header>
      <el-main>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="个人信息" name="info">
            <el-card>
              <el-form :model="profileForm" :rules="profileRules" ref="profileForm" label-width="100px">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="profileForm.username" disabled></el-input>
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio label="male">男</el-radio>
                    <el-radio label="female">女</el-radio>
                    <el-radio label="secret">保密</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="个性签名" prop="bio">
                  <el-input v-model="profileForm.bio" type="textarea" :rows="3" placeholder="请输入个性签名"></el-input>
                </el-form-item>
                <el-form-item label="会员状态">
                  <el-tag :type="memberStatusTagType">{{ memberStatusLabel }}</el-tag>
                  <el-button type="text" v-if="!memberHasApplied" @click="$router.push('/member')" style="margin-left: 10px;">申请会员</el-button>
                  <el-button type="text" v-else @click="$router.push('/member')" style="margin-left: 10px;">查看详情</el-button>
                </el-form-item>
                <el-form-item label="志愿者状态">
                  <el-tag :type="volunteerStatusTagType">{{ volunteerStatusLabel }}</el-tag>
                  <el-button type="text" v-if="!volunteerHasApplied" @click="$router.push('/volunteer')" style="margin-left: 10px;">申请志愿者</el-button>
                  <el-button type="text" v-else @click="$router.push('/volunteer')" style="margin-left: 10px;">查看详情</el-button>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>
          <el-tab-pane label="账户管理" name="account">
            <el-card>
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
              <el-divider></el-divider>
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
              <el-divider></el-divider>
              <h3>账户操作</h3>
              <el-button type="danger" @click="handleLogout">退出登录</el-button>
              <el-button type="warning" @click="handleSwitchAccount">切换账号</el-button>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { userAPI } from '@/api/user'
import api from '@/api/request'

export default {
  name: 'Profile',
  data() {
    const validateNickname = async (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入昵称'))
      } else {
        callback()
      }
    }
    const validatePass = (rule, value, callback) => {
      if (value.length < 6) {
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
      activeTab: 'info',
      profileForm: {
        username: '',
        nickname: '',
        gender: 'secret',
        bio: ''
      },
      profileRules: {
        nickname: [{ required: true, validator: validateNickname, trigger: 'blur' }]
      },
      emailForm: {
        email: ''
      },
      emailRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
      },
      currentEmail: '',
      isMember: false,
      isVolunteer: false,
      memberHasApplied: false,
      memberStatus: '',
      volunteerHasApplied: false,
      volunteerStatus: ''
    }
  },
  computed: {
    memberStatusTagType() {
      const map = { 'active': 'success', 'pending': 'warning', 'cancelled': 'danger', 'expired': 'info' }
      return map[this.memberStatus] || 'info'
    },
    memberStatusLabel() {
      if (!this.memberHasApplied) return '未申请'
      const map = { 'active': '有效会员', 'pending': '审核中', 'cancelled': '已拒绝', 'expired': '已过期' }
      return map[this.memberStatus] || this.memberStatus
    },
    volunteerStatusTagType() {
      const map = { 'approved': 'success', 'pending': 'warning', 'rejected': 'danger' }
      return map[this.volunteerStatus] || 'info'
    },
    volunteerStatusLabel() {
      if (!this.volunteerHasApplied) return '未申请'
      const map = { 'approved': '已认证', 'pending': '审核中', 'rejected': '已拒绝' }
      return map[this.volunteerStatus] || this.volunteerStatus
    }
  },
  mounted() {
    this.loadUserInfo()
    this.checkMemberStatus()
    this.checkVolunteerStatus()
  },
  methods: {
    async loadUserInfo() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) return
      try {
        const res = await userAPI.getUserProfile(user.id)
        this.profileForm = {
          username: res.username,
          nickname: res.nickname || '',
          gender: res.gender || 'secret',
          bio: res.bio || ''
        }
        this.currentEmail = res.email || ''
      } catch (error) {
        this.$message.error('加载用户信息失败')
      }
    },
    async checkMemberStatus() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) return
      try {
        const res = await api.get('/member/user/status', { params: { userId: user.id } })
        this.memberHasApplied = res.hasApplied
        this.memberStatus = res.status || ''
        this.isMember = res.status === 'active'
      } catch (error) {
        this.memberHasApplied = false
      }
    },
    async checkVolunteerStatus() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) return
      try {
        const res = await api.get('/volunteer/user/status', { params: { userId: user.id } })
        this.volunteerHasApplied = res.hasApplied
        this.volunteerStatus = res.status || ''
        this.isVolunteer = res.status === 'approved'
      } catch (error) {
        this.volunteerHasApplied = false
      }
    },
    async handleUpdateProfile() {
      const valid = await new Promise(resolve => this.$refs.profileForm.validate(v => resolve(v)))
      if (!valid) return
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      try {
        const res = await userAPI.updateProfile(user.id, {
          nickname: this.profileForm.nickname,
          gender: this.profileForm.gender,
          bio: this.profileForm.bio
        })
        if (res && res.success === false) {
          this.$message.error(res.message || '保存失败')
          return
        }
        this.$message.success('保存成功')
        user.nickname = this.profileForm.nickname
        user.gender = this.profileForm.gender
        user.bio = this.profileForm.bio
        localStorage.setItem('user', JSON.stringify(user))
        this.$store.dispatch('user/updateUserInfo', user)
      } catch (error) {
        const status = error.response && error.response.status
        const msg = error.response && error.response.data && error.response.data.message
        if (status === 409) {
          this.$message.error('该网名已被使用，请更换')
        } else {
          this.$message.error(msg || '保存失败')
        }
      }
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
      this.$confirm('确定要切换账号吗?', '提示', {
        type: 'warning'
      }).then(() => {
        this.handleLogout()
      }).catch(() => {})
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
</style>
