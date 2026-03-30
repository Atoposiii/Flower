<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px" class="aside">
        <div class="logo">🌸 管理后台</div>
        <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-data-line"></i>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/flowers">
            <i class="el-icon-flower"></i>
            <span>花卉管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/posts">
            <i class="el-icon-document"></i>
            <span>帖子管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/comments">
            <i class="el-icon-chat-line-round"></i>
            <span>评论管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/consultations">
            <i class="el-icon-service"></i>
            <span>客服会话</span>
          </el-menu-item>
          <el-menu-item index="/admin/feedbacks">
            <i class="el-icon-warning"></i>
            <span>意见反馈</span>
          </el-menu-item>
          <el-menu-item index="/admin/volunteers">
            <i class="el-icon-user-solid"></i>
            <span>志愿者管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/activities">
            <i class="el-icon-calendar"></i>
            <span>活动管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/members">
            <i class="el-icon-magic-stick"></i>
            <span>会员管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/payments">
            <i class="el-icon-money"></i>
            <span>支付记录</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-content">
            <span class="title">{{ pageTitle }}</span>
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                管理员<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { userAPI } from '@/api/user'

export default {
  name: 'AdminLayout',
  data() {
    return {
      activeMenu: '/admin/dashboard'
    }
  },
  computed: {
    pageTitle() {
      const map = {
        '/admin/dashboard': '数据统计',
        '/admin/flowers': '花卉管理',
        '/admin/posts': '帖子管理',
        '/admin/comments': '评论管理',
        '/admin/consultations': '客服会话',
        '/admin/feedbacks': '意见反馈',
        '/admin/volunteers': '志愿者管理',
        '/admin/activities': '活动管理',
        '/admin/members': '会员管理',
        '/admin/payments': '支付记录',
        '/admin/users': '用户管理'
      }
      return map[this.$route.path] || '管理后台'
    }
  },
  mounted() {
    this.activeMenu = this.$route.path
  },
  methods: {
    async handleCommand(command) {
      if (command === 'logout') {
        try { await userAPI.logout() } catch (e) { /* ignore */ }
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        this.$store.dispatch('user/logout')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  background-color: #2b3a4a;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
</style>
