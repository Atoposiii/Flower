<template>
  <div class="flower-knowledge">
    <Navbar />
    <div class="container">
      <div style="margin-bottom: 16px;">
        <el-button size="small" @click="$router.back()"><i class="el-icon-back"></i> 返回</el-button>
      </div>
      <div class="title-container">
        <h1 class="page-title">花卉百科</h1>
        <h2 class="page-subtitle">Flower Encyclopedia</h2>
      </div>
      
      <!-- 检索筛选 -->
      <div class="search-filter">
        <el-card class="filter-card">
          <div class="filter-content">
            <!-- 搜索框 -->
            <div class="search-box">
              <el-input v-model="searchKeyword" placeholder="搜索花卉名称、学名" clearable style="max-width: 600px; width: 100%;">
              </el-input>
            </div>
            
            <!-- 高级筛选 -->
            <div class="advanced-filter">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-select v-model="filterForm.floweringPeriod" placeholder="选择花期" clearable>
                    <el-option label="春季" value="春季"></el-option>
                    <el-option label="夏季" value="夏季"></el-option>
                    <el-option label="秋季" value="秋季"></el-option>
                    <el-option label="冬季" value="冬季"></el-option>
                    <el-option label="全年" value="全年"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-select v-model="filterForm.color" placeholder="选择花色" clearable>
                    <el-option v-for="color in availableColors" :key="color" :label="color" :value="color"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-select v-model="filterForm.family" placeholder="选择科属" clearable>
                    <el-option v-for="family in categories" :key="family" :label="family" :value="family"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-select v-model="sortBy" placeholder="排序方式" clearable>
                    <el-option label="默认" value="default"></el-option>
                    <el-option label="花期" value="floweringPeriod"></el-option>
                    <el-option label="名称" value="name"></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </div>
            
            <!-- 筛选按钮 -->
            <div class="filter-buttons">
              <el-button type="primary" @click="applyFilters">筛选</el-button>
              <el-button @click="resetFilters">重置</el-button>
            </div>
          </div>
        </el-card>
      </div>
      

      
      <!-- 花卉列表 -->
      <div class="flower-list">
        <el-card v-for="flower in flowers" :key="flower.id" class="flower-item">
          <div class="flower-content">
            <div class="flower-image">
              <img :src="flower.imageUrl" alt="花卉图片" />
            </div>
            <div class="flower-info">
              <h3>{{ flower.name }}</h3>
              <p><strong>学名：</strong>{{ flower.scientificName }}</p>
              <p><strong>科属：</strong>{{ flower.family }}</p>
              <p><strong>原产地：</strong>{{ flower.origin }}</p>
              <p><strong>花期：</strong>{{ flower.floweringPeriod }}</p>
              <p><strong>花色：</strong>{{ flower.color }}</p>
              <p class="flower-description">{{ flower.description ? flower.description.substring(0, 150) : '暂无描述' }}...</p>
              <el-button type="primary" @click="viewFlowerDetail(flower.id)">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="total"
        />
        <div class="page-info">
          共 {{ totalPages }} 页
        </div>
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
      flowers: [],
      originalFlowers: [],
      categories: [],
      activeCategory: '全部',
      currentPage: 1,
      pageSize: 5,
      total: 0,
      searchKeyword: '',
      filterForm: {
        floweringPeriod: '',
        color: '',
        family: ''
      },
      sortBy: '',
      availableFloweringPeriods: [],
      availableColors: [],
      availableFamilies: []
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  mounted() {
    this.loadFlowers()
  },
  methods: {
    async loadFlowers() {
      try {
        const res = await flowerApi.getAllFlowers()
        this.originalFlowers = Array.isArray(res.data) ? res.data : (Array.isArray(res) ? res : [])
        this.total = this.originalFlowers.length
        // 分页处理
        const startIndex = (this.currentPage - 1) * this.pageSize
        const endIndex = startIndex + this.pageSize
        this.flowers = this.originalFlowers.slice(startIndex, endIndex)
        // 提取筛选选项
        this.extractFilterOptions()
      } catch (e) {
        console.error(e)
        this.$message.error('加载花卉数据失败')
      }
    },
    extractFilterOptions() {
      // 提取所有不重复的科属
      const families = [...new Set(this.originalFlowers.map(f => f.family).filter(Boolean))]
      this.categories = families.sort()
      
      // 提取所有不重复的花期（只提取月份部分）
      const floweringPeriods = [...new Set(this.originalFlowers.map(f => f.floweringPeriod).filter(Boolean))]
      this.availableFloweringPeriods = floweringPeriods.sort()
      
      // 定义有效的花色列表（只包含真正的花色）
      const validFlowerColors = ['红色', '粉色', '白色', '黄色', '橙色', '紫色', '蓝色', '复色', '多色', '杂色']
      
      // 提取所有不重复的花色（可能包含多个颜色，需要拆分）
      const allColors = []
      this.originalFlowers.forEach(f => {
        if (f.color) {
          // 花色可能是 "红色、粉色、白色" 这样的格式，需要拆分
          const colors = f.color.split(/[、,，]/).map(c => c.trim()).filter(Boolean)
          allColors.push(...colors)
        }
      })
      
      // 过滤出有效的花色
      this.availableColors = [...new Set(allColors)]
        .filter(color => validFlowerColors.includes(color))
        .sort()
    },
    filterByCategory(category) {
      this.activeCategory = category
      // 实际项目中根据分类筛选花卉
      if (category === '全部') {
        this.loadFlowers()
      } else {
        // this.$axios.get(`/flowers?family=${category}`).then(response => {
        //   this.flowers = response.data
        //   this.total = response.data.length
        // })
      }
    },
    viewFlowerDetail(id) {
      // 跳转到花卉详情页
      this.$router.push(`/flower-encyclopedia/${id}`)
    },
    handleSizeChange(val) {
      this.pageSize = val
      // 重新加载数据
      this.loadFlowers()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      // 重新加载数据
      this.loadFlowers()
    },
    // 检索筛选相关方法
    searchFlowers() {
      console.log('Searching flowers with keyword:', this.searchKeyword)
      // 实际项目中根据关键词搜索花卉
      // this.$axios.get(`/flowers?keyword=${this.searchKeyword}`).then(response => {
      //   this.flowers = response.data
      //   this.total = response.data.length
      // })
    },
    applyFilters() {
      console.log('Applying filters:', this.filterForm)
      console.log('Sorting by:', this.sortBy)
      
      // 从originalFlowers获取原始数据，确保每次筛选都基于完整数据
      let filteredFlowers = [...this.originalFlowers]
      
      // 根据搜索关键词筛选
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filteredFlowers = filteredFlowers.filter(flower => 
          flower.name.toLowerCase().includes(keyword) ||
          flower.scientificName.toLowerCase().includes(keyword)
        )
      }
      
      // 根据花期筛选
      if (this.filterForm.floweringPeriod) {
        const period = this.filterForm.floweringPeriod
        filteredFlowers = filteredFlowers.filter(flower => {
          const floweringPeriod = flower.floweringPeriod
          if (period === '春季') {
            return floweringPeriod.includes('春季') || floweringPeriod.includes('3月') || floweringPeriod.includes('4月') || floweringPeriod.includes('5月')
          } else if (period === '夏季') {
            return floweringPeriod.includes('夏季') || floweringPeriod.includes('6月') || floweringPeriod.includes('7月') || floweringPeriod.includes('8月') || floweringPeriod.includes('9月')
          } else if (period === '秋季') {
            return floweringPeriod.includes('秋季') || floweringPeriod.includes('9月') || floweringPeriod.includes('10月') || floweringPeriod.includes('11月')
          } else if (period === '冬季') {
            return floweringPeriod.includes('冬季') || floweringPeriod.includes('12月') || floweringPeriod.includes('1月') || floweringPeriod.includes('2月')
          } else if (period === '全年') {
            return floweringPeriod.includes('全年') || floweringPeriod.includes('春季至秋季') || floweringPeriod.includes('10月至次年4月')
          }
          return true
        })
      }
      
      // 根据花色筛选
      if (this.filterForm.color) {
        filteredFlowers = filteredFlowers.filter(flower => 
          flower.color.includes(this.filterForm.color)
        )
      }
      
      // 根据科属筛选
      if (this.filterForm.family) {
        filteredFlowers = filteredFlowers.filter(flower => 
          flower.family === this.filterForm.family
        )
      }
      
      // 根据排序方式排序
      if (this.sortBy) {
        filteredFlowers.sort((a, b) => {
          if (this.sortBy === 'floweringPeriod') {
            return a.floweringPeriod.localeCompare(b.floweringPeriod)
          } else if (this.sortBy === 'name') {
            return a.name.localeCompare(b.name)
          }
          return 0
        })
      }
      
      // 更新筛选结果
      this.total = filteredFlowers.length
      this.currentPage = 1 // 重置到第一页
      // 分页处理
      const startIndex = (this.currentPage - 1) * this.pageSize
      const endIndex = startIndex + this.pageSize
      this.flowers = filteredFlowers.slice(startIndex, endIndex)
      
      // 实际项目中根据筛选条件过滤花卉
      // this.$axios.get('/flowers', {
      //   params: {
      //     keyword: this.searchKeyword,
      //     floweringPeriod: this.filterForm.floweringPeriod,
      //     color: this.filterForm.color,
      //     family: this.filterForm.family,
      //     sortBy: this.sortBy
      //   }
      // }).then(response => {
      //   this.flowers = response.data
      //   this.total = response.data.length
      // })
    },
    resetFilters() {
      this.searchKeyword = ''
      this.filterForm = {
        floweringPeriod: '',
        color: '',
        family: ''
      }
      this.sortBy = ''
      console.log('Filters reset')
      this.loadFlowers()
    }
  }
}
</script>

<style scoped>
.flower-knowledge {
  min-height: 100vh;
  background-color: #e8f5e8;
  display: flex;
  flex-direction: column;
}

.title-container {
  text-align: center;
  margin-bottom: 40px;
}

.container {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  width: 100%;
  box-sizing: border-box;
}

.page-title {
  font-size: 80px;
  margin-bottom: 10px;
  color: #333;
  text-align: center;
  font-weight: normal;
  letter-spacing: 100px;
  font-family: 'Brush Script MT', 'Lucida Handwriting', 'STKaiti', 'KaiTi', cursive, sans-serif;
  text-shadow: 3px 3px 0px rgba(0, 0, 0, 0.1), 
               -1px -1px 0px rgba(0, 0, 0, 0.05);
  transform: skew(-2deg, 1deg);
}

.page-subtitle {
  font-size: 45px;
  margin-top: 0;
  color: #666;
  text-align: center;
  font-weight: normal;
  letter-spacing: 12px;
  font-family: 'Brush Script MT', 'Lucida Handwriting', 'Arial', sans-serif;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.search-filter {
  margin-bottom: 30px;
}

.filter-card {
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.filter-content {
  padding: 30px;
}

.search-box {
  margin-bottom: 30px;
}

.search-box .el-input {
  max-width: 600px;
  width: 100%;
  margin: 0 auto;
}

/* 隐藏搜索框的下拉箭头 */
.search-box .el-input__icon.el-icon-arrow-down {
  display: none !important;
}

.search-box .el-input__inner {
  background-color: transparent;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  color: #000000 !important;
}

.advanced-filter {
  margin-bottom: 30px;
}

.advanced-filter .el-row {
  margin: 0 -15px;
}

.advanced-filter .el-col {
  padding: 0 15px;
}

.advanced-filter .el-select {
  width: 100%;
  max-width: 200px;
}

.filter-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.filter-buttons .el-button {
  min-width: 100px;
  padding: 10px 20px;
}



.flower-list {
  display: flex;
  flex-direction: column;
  gap: 30px;
  padding: 0 20px;
}

.flower-item {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.flower-item {
  transition: all 0.3s ease;
}

.flower-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.flower-content {
  display: flex;
  gap: 30px;
  padding: 25px;
}

.flower-image {
  flex: 0 0 350px;
}

.flower-image img {
  width: 100%;
  height: 350px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.flower-info {
  flex: 1;
  padding: 10px 0;
}

.flower-info h3 {
  font-size: 22px;
  margin-bottom: 20px;
  color: #333;
  font-weight: 600;
}

.flower-info p {
  margin-bottom: 12px;
  color: #666;
  line-height: 1.6;
  font-size: 15px;
}

.flower-description {
  margin-top: 15px;
  font-size: 14px;
  color: #999;
  line-height: 1.5;
}

.flower-info .el-button {
  margin-top: 20px;
  padding: 8px 20px;
}

.pagination {
  margin-top: 50px;
  margin-bottom: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 20px;
}

.pagination .el-pagination {
  margin: 0 10px 0 0 !important;
}

.page-info {
  font-size: 14px;
  color: #666;
  margin: 0 !important;
}

.pagination .el-pagination {
  margin-top: 0;
}

@media (max-width: 768px) {
  .flower-content {
    flex-direction: column;
  }
  
  .flower-image {
    flex: 1;
  }
  
  .flower-image img {
    width: 100%;
    height: 350px;
  }
}
</style>