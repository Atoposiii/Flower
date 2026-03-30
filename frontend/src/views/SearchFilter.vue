<template>
  <div class="search-filter">
    <Navbar />
    <div class="container">
      <div style="margin-bottom: 16px;">
        <el-button size="small" @click="$router.back()"><i class="el-icon-back"></i> 返回</el-button>
      </div>
      <h1 class="page-title">检索筛选</h1>
      
      <el-card class="filter-card">
        <h2>筛选条件</h2>
        <el-form :model="filterForm" label-width="100px">
          <el-form-item label="花卉名称">
            <el-input v-model="filterForm.name" placeholder="请输入花卉名称"></el-input>
          </el-form-item>
          <el-form-item label="科属">
            <el-input v-model="filterForm.family" placeholder="请输入科属"></el-input>
          </el-form-item>
          <el-form-item label="花色">
            <el-input v-model="filterForm.color" placeholder="请输入花色"></el-input>
          </el-form-item>
          <el-form-item label="花期">
            <el-input v-model="filterForm.floweringPeriod" placeholder="请输入花期"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchFlowers">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      
      <div class="search-results" v-if="flowers.length > 0">
        <h2 class="section-title">搜索结果</h2>
        <div class="flower-list">
          <el-card v-for="flower in flowers" :key="flower.id" class="flower-item">
            <div class="flower-content">
              <div class="flower-image">
                <img :src="flower.imageUrl" :alt="flower.name" />
              </div>
              <div class="flower-info">
                <h3>{{ flower.name }}</h3>
                <p class="scientific-name">{{ flower.scientificName }}</p>
                <p class="family">{{ flower.family }}</p>
                <p class="color">{{ flower.color }}</p>
                <p class="flowering-period">{{ flower.floweringPeriod }}</p>
                <el-button type="primary" size="small" @click="viewFlowerDetail(flower.id)">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      
      <div class="no-results" v-else>
        <p>暂无搜索结果</p>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '../components/Navbar.vue'
import flowerApi from '../api/modules/flower'

export default {
  components: {
    Navbar
  },
  data() {
    return {
      filterForm: {
        name: '',
        family: '',
        color: '',
        floweringPeriod: ''
      },
      flowers: []
    }
  },
  methods: {
    async searchFlowers() {
      try {
        const response = await flowerApi.searchFlowers(this.filterForm.name)
        this.flowers = response
      } catch (error) {
        console.error('搜索花卉失败:', error)
        this.$message.error('搜索失败，请重试')
      }
    },
    resetForm() {
      this.filterForm = {
        name: '',
        family: '',
        color: '',
        floweringPeriod: ''
      }
      this.flowers = []
    },
    viewFlowerDetail(id) {
      this.$router.push(`/flower-encyclopedia/${id}`)
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 30px;
  width: 100%;
  box-sizing: border-box;
}

.page-title {
  font-size: 32px;
  margin-bottom: 40px;
  color: #333;
  text-align: center;
  font-weight: 600;
}

.filter-card {
  margin-bottom: 40px;
}

.section-title {
  font-size: 22px;
  margin-bottom: 24px;
  color: #333;
  border-left: 4px solid #409EFF;
  padding-left: 15px;
  font-weight: 600;
}

.flower-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.flower-item {
  transition: all 0.3s ease;
}

.flower-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.flower-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.flower-image {
  width: 200px;
  height: 200px;
  margin-bottom: 16px;
  overflow: hidden;
  border-radius: 8px;
}

.flower-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.flower-image img:hover {
  transform: scale(1.1);
}

.flower-info {
  width: 100%;
}

.flower-info h3 {
  margin-bottom: 8px;
  color: #333;
}

.scientific-name {
  margin-bottom: 4px;
  color: #999;
  font-style: italic;
}

.family, .color, .flowering-period {
  margin-bottom: 4px;
  color: #666;
  font-size: 14px;
}

.no-results {
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 16px;
}

@media (max-width: 768px) {
  .flower-list {
    grid-template-columns: 1fr;
  }
}
</style>