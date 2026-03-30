<template>
  <div class="home">
    <Navbar />
    <div class="banner">
      <div class="banner-content">
        <h1>花卉科普网站</h1>
        <p>探索花卉世界的奥秘，开启绿色生活</p>
        <el-button type="primary" size="large" @click="toFlowerKnowledge">开始探索</el-button>
      </div>
    </div>
    
    <div class="container">
      <div class="section">
        <h2 class="section-title">推荐花卉</h2>
        <div class="flower-list">
          <div v-for="flower in recommendedFlowers" :key="flower.id" class="flower-item">
            <el-card :body-style="{ padding: '0px' }" shadow="hover">
              <div class="flower-image-container">
                <img :src="flower.imageUrl" class="flower-image" alt="花卉图片" />
                <div class="flower-overlay">
                  <el-button type="primary" size="small" @click="viewFlowerDetail(flower.id)">查看详情</el-button>
                </div>
              </div>
              <div class="flower-info">
                <h3>{{ flower.name }}</h3>
                <p>{{ flower.scientificName }}</p>
                <p>{{ flower.family }}</p>
              </div>
            </el-card>
          </div>
        </div>
      </div>
      
      <div class="section">
        <h2 class="section-title">热门社区帖子</h2>
        <div class="post-list">
          <el-card v-for="post in hotPosts" :key="post.id" class="post-item" shadow="hover">
            <h3>{{ post.title }}</h3>
            <p class="post-content">{{ post.content.substring(0, 100) }}...</p>
            <div class="post-meta">
              <span>作者:{{ post.user.name || post.user.username }}</span>
              <span>浏览:{{ post.viewCount }}</span>
              <span>点赞:{{ post.likeCount || 0 }}</span>
              <span>评论:{{ post.commentCount }}</span>
              <span>发布时间:{{ formatDate(post.createTime) }}</span>
              <el-button type="primary" size="small" @click="viewPostDetail(post.id)">查看详情</el-button>
            </div>
          </el-card>
        </div>
      </div>
      
      <div class="section">
        <h2 class="section-title">志愿者活动</h2>
        <div class="volunteer-activity">
          <el-card shadow="hover" class="volunteer-card">
            <div class="volunteer-image">
              <img src="/花卉讲座活动.jpg" alt="花卉讲座活动" />
            </div>
            <h3>花卉讲座活动</h3>
            <p>时间:2026年6月6日 14:00-16:00</p>
            <p>地点:花卉园</p>
            <p>内容:邀请专家讲解花卉知识，分享种植技巧</p>
            <el-button type="primary" @click="joinVolunteer">报名参与</el-button>
          </el-card>
          <el-card shadow="hover" class="volunteer-card">
            <div class="volunteer-image">
              <img src="/花卉种植活动.jpg" alt="社区种植活动" />
            </div>
            <h3>花卉种植活动</h3>
            <p>时间:2026年6月6日 9:00-11:00</p>
            <p>地点:花卉园</p>
            <p>内容:组织大家一起种植花卉，美化花园环境</p>
            <el-button type="primary" @click="joinVolunteer">报名参与</el-button>
          </el-card>
        </div>
      </div>
      
      <div class="section">
        <h2 class="section-title">会员权益</h2>
        <div class="membership-benefits">
          <el-card shadow="hover" class="benefit-card clickable-card" @click.native="toFlowerKnowledge">
            <div class="benefit-icon">
              <i class="el-icon-notebook-2"></i>
            </div>
            <h3>专业百科</h3>
            <p>解锁会员专享的花卉百科，获取更多专业资料</p>
          </el-card>
          <el-card shadow="hover" class="benefit-card">
            <div class="benefit-icon">
              <i class="el-icon-trophy"></i>
            </div>
            <h3>优先参与活动</h3>
            <p>优先参与花卉讲座、种植活动等线下活动</p>
          </el-card>
          <el-card shadow="hover" class="benefit-card">
            <div class="benefit-icon">
              <i class="el-icon-service"></i>
            </div>
            <h3>一对一咨询</h3>
            <p>享受专业一对一花卉养护咨询服务</p>
          </el-card>
        </div>
      </div>
    </div>
    
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于我们</h3>
          <p>花卉科普网站致力于传播花卉知识，促进花卉爱好者之间的交流与分享</p>
        </div>
        <div class="footer-section">
          <h3>网站导航</h3>
          <ul>
            <li><a href="#">花卉百科</a></li>
            <li><a href="#">互动社区</a></li>
            <li><a href="#">志愿者活动</a></li>
            <li><a href="#">会员中心</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h3>联系方式</h3>
          <p>邮箱:我是大帅哥@flowerknowledge.com</p>
          <p>电话:123-4567-8910</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 花卉科普网站 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script>
import Navbar from '../components/Navbar.vue'

export default {
  components: {
    Navbar
  },
  data() {
    return {
      recommendedFlowers: [],
      hotPosts: []
    }
  },
  mounted() {
    this.loadRecommendedFlowers()
    this.loadHotPosts()
  },
  activated() {
    // 每次进入首页时重新加载热门帖子数据
    this.loadHotPosts()
  },
  methods: {
    loadRecommendedFlowers() {
      this.recommendedFlowers = [
        {
          id: 1,
          name: '玫瑰',
          scientificName: 'Rosa rugosa',
          family: '蔷薇科',
          imageUrl: '/Users/zhenglei/CodeHub/flowerwebsite/frontend/public/玫瑰.jpg'
        },
        {
          id: 2,
          name: '牡丹',
          scientificName: 'Paeonia suffruticosa',
          family: '毛茛科',
          imageUrl: '/Users/zhenglei/CodeHub/flowerwebsite/frontend/public/牡丹.jpg'
        },
        {
          id: 3,
          name: '菊花',
          scientificName: 'Chrysanthemum morifolium',
          family: '菊科',
          imageUrl: '/Users/zhenglei/CodeHub/flowerwebsite/frontend/public/菊花.jpg'
        }
      ]
    },
    loadHotPosts() {
      // 从localStorage加载帖子数据
      const savedPosts = localStorage.getItem('flowerPosts')
      if (savedPosts) {
        const posts = JSON.parse(savedPosts).map(post => ({
          ...post,
          createTime: new Date(post.createTime)
        }))
        // 只显示花艺师(ID:1)和花卉爱好者(ID:2)的帖子，按点赞数降序排序，取前5条
        this.hotPosts = posts
          .filter(post => post.user.id === 1 || post.user.id === 2)
          .sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
          .slice(0, 5)
      } else {
        // 如果localStorage中没有数据，使用默认数据
        this.hotPosts = [
          {
            id: 1,
            title: '如何让玫瑰花开得更鲜艳',
            content: '玫瑰是一种美丽的花卉，但是很多人不知道如何让玫瑰花开得更鲜艳。其实只要注意以下几点，就可以让玫瑰花开得更鲜艳...',
            user: { id: 1, name: '花艺师', username: 'flower_fairy' },
            viewCount: 666,
            likeCount: 56,
            commentCount: 666,
            createTime: new Date()
          },
          {
            id: 2,
            title: '牡丹的种植技巧',
            content: '牡丹是我国的国花，深受人们喜爱。种植牡丹要注意以下几点：选择肥沃的土壤、适当的光照、合理的浇水...',
            user: { id: 2, name: '花卉爱好者', username: 'flower_expert' },
            viewCount: 888,
            likeCount: 45,
            commentCount: 88,
            createTime: new Date()
          }
        ]
      }
    },
    viewFlowerDetail(id) {
      this.$router.push('/flower-encyclopedia')
    },
    viewPostDetail(id) {
      this.$router.push('/community')
    },
    joinVolunteer() {
      this.$router.push('/volunteer-service')
    },
    toFlowerKnowledge() {
      this.$router.push('/flower-encyclopedia')
    },
    formatDate(date) {
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style scoped>
.banner {
  background-image: url('/首页背景图.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  padding: 120px 0;
  text-align: center;
  margin-bottom: 50px;
  position: relative;
}

.banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
  color: white;
}

.banner h1 {
  font-size: 72px;
  margin-bottom: 30px;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  letter-spacing: 12px;
}

.banner p {
  font-size: 32px;
  margin-bottom: 40px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  letter-spacing: 6px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 30px;
  width: 100%;
  box-sizing: border-box;
}

.section {
  margin-bottom: 70px;
}

.section-title {
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
  border-left: 4px solid #409EFF;
  padding-left: 15px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 60px;
  height: 3px;
  background-color: #409EFF;
}

.flower-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
  justify-content: center;
}

.flower-item {
  transition: transform 0.3s ease;
}

.flower-item:hover {
  transform: translateY(-5px);
}

.flower-image-container {
  position: relative;
  overflow: hidden;
}

.flower-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.flower-image-container:hover .flower-image {
  transform: scale(1.05);
}

.flower-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.flower-image-container:hover .flower-overlay {
  opacity: 1;
}

.flower-info {
  padding: 20px;
}

.flower-info h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 20px;
}

.flower-info p {
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-item {
  padding: 25px;
  transition: transform 0.3s ease;
}

.post-item:hover {
  transform: translateY(-5px);
}

.post-item h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 20px;
}

.post-content {
  margin-bottom: 20px;
  color: #666;
  line-height: 1.6;
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
  font-size: 14px;
  color: #999;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.volunteer-activity {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 30px;
  justify-content: center;
}

.volunteer-card {
  transition: transform 0.3s ease;
}

.volunteer-card:hover {
  transform: translateY(-5px);
}

.volunteer-image {
  margin-bottom: 20px;
  overflow: hidden;
  /* 核心：不强制固定比例，让容器适配图片原始比例 */
  width: 100%;
  background-color: #f8f9fa; /* 浅灰背景兜底 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.volunteer-image img {
  width: 100%;
  /* 关键：height设为auto，保留图片原始宽高比 */
  height: auto;
  max-height: 300px; /* 限制最大高度，避免图片过高导致布局失衡 */
  object-fit: contain; /* 完整显示图片，不裁剪文字 */
  transition: transform 0.5s ease;
}

.volunteer-card:hover .volunteer-image img {
  transform: scale(1.05); /* 保留hover放大效果 */
}

.volunteer-card h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 20px;
}

.volunteer-card p {
  margin-bottom: 10px;
  color: #666;
  line-height: 1.5;
}

.membership-benefits {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
  justify-content: center;
}

.benefit-card {
  text-align: center;
  padding: 30px;
  transition: transform 0.3s ease;
}

.benefit-card:hover {
  transform: translateY(-5px);
}

.clickable-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.clickable-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.benefit-icon {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 20px;
}

.benefit-card h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 20px;
}

.benefit-card p {
  color: #666;
  line-height: 1.5;
}

.footer {
  background-color: #FFFACD;
  color: #333;
  padding: 50px 0 20px;
  margin-top: 80px;
}

.footer-content {
  display: flex;
  flex-wrap: wrap;
  gap: 50px;
  justify-content: space-around;
  margin-bottom: 30px;
}

.footer-section {
  flex: 1;
  min-width: 250px;
  max-width: 350px;
}

.footer-section h3 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #b392f5;
}

.footer-section p {
  color: #000000;
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section ul li a {
  color: #000000;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-section ul li a:hover {
  color: #409EFF;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
  color: #000000;
  font-size: 14px;
}

@media (max-width: 768px) {
  .banner {
    padding: 80px 0;
  }
  
  .banner h1 {
    font-size: 36px;
  }
  
  .banner p {
    font-size: 16px;
  }
  
  .flower-list {
    flex-direction: column;
    align-items: center;
  }
  
  .flower-item {
    max-width: 100%;
  }
  
  .volunteer-activity {
    flex-direction: column;
    align-items: center;
  }
  
  .volunteer-card {
    max-width: 100%;
  }
  
  .membership-benefits {
    flex-direction: column;
    align-items: center;
  }
  
  .benefit-card {
    max-width: 100%;
  }
  
  .footer-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>
