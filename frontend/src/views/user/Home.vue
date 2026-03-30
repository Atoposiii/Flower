<template>
  <div class="home">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo">🌸 花卉科普平台</h1>
          <el-menu mode="horizontal" :default-active="activeMenu" router>
            <el-menu-item index="/home">首页</el-menu-item>
            <el-menu-item index="/flowers">花卉知识</el-menu-item>
            <el-menu-item index="/community">互动社区</el-menu-item>
            <el-menu-item index="/volunteer">志愿者</el-menu-item>
            <el-menu-item index="/activities">活动</el-menu-item>
            <el-menu-item index="/member">会员中心</el-menu-item>
          </el-menu>
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ user.nickname || user.username }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="consultation">客服咨询</el-dropdown-item>
                <el-dropdown-item command="feedback">意见反馈</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main>
        <div class="banner">
          <h2>欢迎来到花卉科普平台</h2>
          <p>探索花卉之美，学习养护知识，参与志愿者活动</p>
        </div>
        <div class="content-wrapper">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card shadow="hover">
                <i class="el-icon-flower icon flower-icon"></i>
                <h3>花卉知识</h3>
                <p>浏览{{ flowerCount }}种花卉详细信息</p>
                <el-button type="primary" size="small" @click="$router.push('/flowers')">立即查看</el-button>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <i class="el-icon-user-solid icon volunteer-icon"></i>
                <h3>志愿者活动</h3>
                <p>参与志愿服务，积累服务时长</p>
                <el-button type="success" size="small" @click="$router.push('/volunteer')">了解更多</el-button>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <i class="el-icon-magic-stick icon member-icon"></i>
                <h3>会员特权</h3>
                <p>开通会员享受更多专属权益</p>
                <el-button type="warning" size="small" @click="$router.push('/member')">开通会员</el-button>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <i class="el-icon-s-comment icon community-icon"></i>
                <h3>互动社区</h3>
                <p>发帖交流，分享花卉心得</p>
                <el-button type="info" size="small" @click="$router.push('/community')">进入社区</el-button>
              </el-card>
            </el-col>
          </el-row>
        </div>
        <div class="recent-flowers">
          <h3>推荐花卉</h3>
          <el-row :gutter="20">
            <el-col :span="6" v-for="flower in recommendedFlowers" :key="flower.id">
              <el-card shadow="hover" class="flower-card" @click.native="$router.push(`/flower/${flower.id}`)">
                <img :src="flower.imageUrl || 'https://via.placeholder.com/200x150?text=Flower'" class="flower-image">
                <h4>{{ flower.name }}</h4>
                <p class="flower-scientific">{{ flower.scientificName }}</p>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { flowerAPI } from '@/api/flower'
import { userAPI } from '@/api/user'

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      activeMenu: '/home',
      flowerCount: 0,
      recommendedFlowers: []
    }
  },
  mounted() {
    this.loadFlowers()
  },
  methods: {
    async loadFlowers() {
      try {
        const res = await flowerAPI.getFlowers({ page: 0, size: 4, isRecommended: true })
        if (res.content) {
          this.recommendedFlowers = res.content
          this.flowerCount = res.totalElements
        }
      } catch (error) {
        console.error('加载花卉失败', error)
      }
    },
    async handleCommand(command) {
      if (command === 'logout') {
        try { await userAPI.logout() } catch (e) { /* ignore */ }
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        this.$store.dispatch('user/logout')
        this.$router.push('/login')
      } else if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'consultation') {
        this.$router.push('/consultation')
      } else if (command === 'feedback') {
        this.$router.push('/feedback')
      }
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

.logo {
  color: #67C23A;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 20px;
  text-align: center;
  border-radius: 8px;
  margin-bottom: 30px;
}

.content-wrapper {
  padding: 20px 0;
}

.icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.flower-icon { color: #67C23A; }
.volunteer-icon { color: #409EFF; }
.member-icon { color: #E6A23C; }
.community-icon { color: #909399; }

.recent-flowers {
  margin-top: 40px;
}

.recent-flowers h3 {
  margin-bottom: 20px;
}

.flower-card {
  cursor: pointer;
  margin-bottom: 20px;
}

.flower-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.flower-scientific {
  color: #909399;
  font-size: 12px;
}
</style>
