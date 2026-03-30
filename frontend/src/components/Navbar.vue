<template>
  <div class="navbar-container">
    <div class="navbar-content">
      <div class="nav-main">
        <!-- Logo部分 -->
        <div class="logo-section">
          <div class="logo-icon">
            <span class="flower-icon">🌼</span>
          </div>
          <div class="logo-text">
            <span class="brand-name">Flower World</span>
          </div>
        </div>
        
        <!-- 导航菜单部分 -->
        <el-menu 
          :default-active="activeIndex" 
          class="el-menu-demo" 
          mode="horizontal" 
          @select="handleSelect" 
          background-color="transparent" 
          text-color="#000000" 
          active-text-color="#000000"
        >
          <el-menu-item index="/home" class="menu-item">首页导航</el-menu-item>
          <el-menu-item index="/flower-encyclopedia" class="menu-item">花卉百科</el-menu-item>
          <el-menu-item index="/community" class="menu-item">互动社区</el-menu-item>
          <el-menu-item index="/activities" class="menu-item">志愿服务</el-menu-item>
          <el-menu-item index="/member" class="menu-item">会员中心</el-menu-item>
          <el-menu-item index="/admin" class="menu-item" v-if="isAdmin">管理员后台</el-menu-item>
        </el-menu>
      </div>
      
      <!-- 用户中心部分 -->
      <div class="user-section">
        <el-dropdown trigger="click">
          <div class="user-avatar-icon">
            <span>👤</span>
          </div>
          <el-dropdown-menu slot="dropdown" class="simple-dropdown-menu">
            <el-dropdown-item @click.native="goToProfile" v-if="isLoggedIn">个人中心</el-dropdown-item>
            <el-dropdown-item @click.native="goToAccountManagement" v-if="isLoggedIn">账户管理</el-dropdown-item>
            <el-dropdown-item @click.native="handleSwitchAccount" v-if="isLoggedIn">切换账号</el-dropdown-item>
            <el-dropdown-item @click.native="handleLogout" v-if="isLoggedIn" style="color: #F56C6C;">退出登录</el-dropdown-item>
            <el-dropdown-item @click.native="goToLogin" v-if="!isLoggedIn">登录</el-dropdown-item>
            <el-dropdown-item @click.native="goToRegister" v-if="!isLoggedIn">注册</el-dropdown-item>
            <el-dropdown-item @click.native="goToAdmin" v-if="isAdmin">管理员后台</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeIndex: '/',
      activeIndex2: '/'
    }
  },
  computed: {
    isLoggedIn() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      return !!user.id
    },
    isAdmin() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      return user.role === 'ADMIN'
    }
  },
  mounted() {
    // 设置当前激活的菜单项
    this.activeIndex = this.$route.path
  },
  watch: {
    // 监听路由变化，更新激活的菜单项
    $route(to) {
      this.activeIndex = to.path
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      // 跳转到对应的路由
      this.$router.push(key)
    },
    goToProfile() {
      console.log('Going to profile...')
      this.$router.push('/profile')
    },
    goToLogin() {
      this.$router.push('/login')
    },
    goToRegister() {
      this.$router.push('/register')
    },
    goToAdmin() {
      this.$router.push('/admin')
    },
    goToAccountManagement() {
      this.$router.push('/account')
    },
    handleLogout() {
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
/* 核心容器：保持原有样式，新增统一的垂直对齐基准 */
.navbar-container {
  background-color: #FFFACD;
  border-bottom: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  width: 100%;
}

/* 内容容器：核心对齐层，统一行高和flex垂直居中 */
.navbar-content {
  display: flex;
  align-items: center; /* 强制所有子元素垂直居中 */
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px; /* 微调高度，让对齐更舒适 */
  line-height: 70px; /* 统一行高，作为垂直对齐基准 */
  position: relative;
}

/* 主导航区域：确保和容器行高一致 */
.nav-main {
  display: flex;
  align-items: center; /* 子元素（logo+菜单）垂直居中 */
  flex: 1;
  height: 100%; /* 继承容器高度 */
  line-height: inherit; /* 继承容器行高 */
}

/* Logo部分：精准对齐，和菜单文字基线一致 */
.logo-section {
  display: flex;
  align-items: center; /* logo图标和文字垂直居中 */
  margin-right: 40px;
  height: 100%; /* 继承高度，确保行高生效 */
  line-height: inherit;
}

.logo-icon {
  font-size: 48px;
  margin-right: 12px;
  /* 图标和文字基线对齐 */
  display: inline-block;
  vertical-align: middle;
}

.logo-text .brand-name {
  font-size: 36px;
  font-weight: bold;
  color: #b392f5;
  font-family: 'Arial', sans-serif;
  /* 文字和容器行高对齐 */
  display: inline-block;
  vertical-align: middle;
  line-height: normal; /* 取消文字自身行高，继承容器 */
}

/* 导航菜单核心优化：解决Element UI默认行高不一致问题 */
.el-menu-demo {
  flex: 1;
  display: flex;
  justify-content: flex-start;
  margin: 0;
  border-bottom: none !important;
  height: 100%; /* 继承高度 */
  line-height: inherit; /* 继承容器行高 */
}

/* 菜单选项：统一行高和对齐，消除Element UI默认样式干扰 */
.menu-item {
  font-size: 16px !important;
  padding: 0 20px !important; /* 去掉上下padding，用行高控制垂直间距 */
  margin: 0 8px !important;
  border-radius: 4px !important;
  transition: all 0.3s ease;
  height: 100% !important; /* 强制继承菜单高度 */
  line-height: 70px !important; /* 和容器行高完全一致，实现对齐 */
  display: flex;
  align-items: center; /* 菜单文字在item内垂直居中 */
  justify-content: center;
}

/* 菜单hover/激活样式：保持美观，不影响对齐 */
.menu-item:hover,
.menu-item.is-active {
  background-color: #bd9eec !important;
  color: #FFFFFF !important;
}

/* 用户中心部分：和整体对齐保持一致 */
.user-section {
  display: flex;
  align-items: center; /* 子元素垂直居中 */
  height: 100%; /* 继承高度 */
  line-height: inherit; /* 继承行高 */
}



/* 用户头像区域：独立样式，不影响整体对齐 */
.user-avatar-icon {
  font-size: 28px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-left: 20px;
}

.user-avatar-icon:hover {
  transform: scale(1.1);
  color: #bd9eec;
}

.user-avatar {
  font-size: 24px;
  margin-right: 5px;
}

.user-count {
  font-size: 14px;
  color: #fff;
  font-weight: bold;
}

/* 子菜单样式：保持原有功能，优化交互 */
::v-deep .el-submenu .el-menu {
  background-color: #FFFFFF !important;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-top: 0 !important; /* 消除子菜单和导航栏的间距 */
}

::v-deep .el-submenu .el-menu-item {
  padding: 10px 20px !important;
  height: 45px !important; /* 子菜单单独设置高度 */
  line-height: 45px !important; /* 子菜单行高匹配高度 */
}

::v-deep .el-submenu .el-menu-item:hover {
  background-color: #F5F5F5 !important;
  color: #333 !important;
}

/* 简单风格下拉菜单样式 */
::v-deep .simple-dropdown-menu {
  background-color: #FFFFFF !important;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  min-width: 120px;
}

::v-deep .simple-dropdown-menu .el-dropdown-menu__item {
  padding: 8px 16px !important;
  height: 36px !important;
  line-height: 36px !important;
  font-size: 14px;
  color: #333;
}

::v-deep .simple-dropdown-menu .el-dropdown-menu__item:hover {
  background-color: #F5F5F5 !important;
  color: #333 !important;
}
</style>