<template>
  <div class="flower-list">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <div style="display:flex;align-items:center;gap:12px;">
            <el-button size="small" @click="$router.back()"><i class="el-icon-back"></i> 返回</el-button>
            <h2 class="logo" style="margin:0;">🌸 花卉知识库</h2>
          </div>
          <el-menu mode="horizontal" :default-active="activeMenu" router>
            <el-menu-item index="/home">首页</el-menu-item>
            <el-menu-item index="/flowers">花卉知识</el-menu-item>
            <el-menu-item index="/community">互动社区</el-menu-item>
            <el-menu-item index="/volunteer">志愿者</el-menu-item>
          </el-menu>
        </div>
      </el-header>
      <el-main>
        <el-card>
          <el-form :inline="true" :model="filters">
            <el-form-item label="花卉名称">
              <el-input v-model="filters.name" placeholder="请输入名称" clearable></el-input>
            </el-form-item>
            <el-form-item label="科">
              <el-input v-model="filters.family" placeholder="请输入科" clearable></el-input>
            </el-form-item>
            <el-form-item label="花色">
              <el-input v-model="filters.color" placeholder="请输入花色" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-row :gutter="20" class="flower-grid">
          <el-col :span="6" v-for="flower in flowers" :key="flower.id">
            <el-card shadow="hover" class="flower-card" @click.native="$router.push(`/flower/${flower.id}`)">
              <img :src="flower.imageUrl" class="flower-image">
              <h4>{{ flower.name }}</h4>
              <p class="flower-scientific">{{ flower.scientificName }}</p>
              <div class="flower-tags">
                <el-tag size="mini" v-if="flower.flowerColor">{{ flower.flowerColor }}</el-tag>
                <el-tag size="mini" type="success" v-if="flower.family">{{ flower.family }}</el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-pagination
          @current-change="handlePageChange"
          :current-page="pagination.page"
          :page-size="pagination.size"
          :total="pagination.total"
          layout="total, prev, pager, next">
        </el-pagination>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { flowerAPI } from '@/api/flower'

export default {
  name: 'FlowerList',
  data() {
    return {
      flowers: [],
      filters: {
        name: '',
        family: '',
        color: ''
      },
      pagination: {
        page: 1,
        size: 8,
        total: 0
      }
    }
  },
  mounted() {
    this.loadFlowers()
  },
  methods: {
    async loadFlowers() {
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size,
          ...this.filters
        }
        const res = await flowerAPI.getFlowers(params)
        if (res.content) {
          this.flowers = res.content
          this.pagination.total = res.totalElements
        }
      } catch (error) {
        this.$message.error('加载花卉列表失败')
      }
    },
    handleSearch() {
      this.pagination.page = 1
      this.loadFlowers()
    },
    handleReset() {
      this.filters = { name: '', family: '', color: '' }
      this.handleSearch()
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadFlowers()
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
  height: 100%;
  padding: 0 20px;
}
.logo {
  margin-right: 50px;
  color: #67C23A;
}
.flower-grid {
  margin-top: 20px;
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
.flower-tags {
  margin-top: 8px;
}
</style>
