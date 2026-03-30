<template>
  <div class="flower-detail">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 花卉详情</h2>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </el-header>
      <el-main>
        <el-card v-if="flower">
          <div class="flower-header-row">
            <img :src="flower.imageUrl || 'https://via.placeholder.com/400x300?text=Flower'" class="flower-image">
            <div class="flower-info">
              <h2>{{ flower.name }}</h2>
              <p class="scientific-name">{{ flower.scientificName }}</p>
              <el-tag v-if="flower.family">科: {{ flower.family }}</el-tag>
              <el-tag v-if="flower.flowerColor" type="success">花色: {{ flower.flowerColor }}</el-tag>
              <p class="description">{{ flower.description }}</p>
            </div>
          </div>
          <el-tabs>
            <el-tab-pane label="基本信息">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="别名">{{ flower.aliases || '无' }}</el-descriptions-item>
                <el-descriptions-item label="属">{{ flower.genus || '无' }}</el-descriptions-item>
                <el-descriptions-item label="类别">{{ flower.category || '无' }}</el-descriptions-item>
                <el-descriptions-item label="花期">{{ flower.floweringPeriod || '无' }}</el-descriptions-item>
                <el-descriptions-item label="花香">{{ flower.flowerFragrance || '无' }}</el-descriptions-item>
                <el-descriptions-item label="花语">{{ flower.flowerLanguage || '无' }}</el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="形态特征">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="株高">{{ flower.plantHeight || '无' }}</el-descriptions-item>
                <el-descriptions-item label="冠幅">{{ flower.crownWidth || '无' }}</el-descriptions-item>
                <el-descriptions-item label="叶形">{{ flower.leafShape || '无' }}</el-descriptions-item>
                <el-descriptions-item label="叶色">{{ flower.leafColor || '无' }}</el-descriptions-item>
                <el-descriptions-item label="花型">{{ flower.flowerType || '无' }}</el-descriptions-item>
                <el-descriptions-item label="果期">{{ flower.fruitPeriod || '无' }}</el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="分布与文化">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="原产地">{{ flower.origin || '无' }}</el-descriptions-item>
                <el-descriptions-item label="分布区域">{{ flower.distribution || '无' }}</el-descriptions-item>
                <el-descriptions-item label="文化寓意">{{ flower.culturalMeaning || '无' }}</el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="养护信息" v-if="isMember">
              <el-alert title="会员专属内容" type="success" :closable="false" show-icon>
                以下是完整养护信息，仅对会员开放。
              </el-alert>
              <el-descriptions :column="1" border style="margin-top: 20px;">
                <el-descriptions-item label="栽培难度">{{ flower.cultivationDifficulty || '无' }}</el-descriptions-item>
                <el-descriptions-item label="适宜季节">{{ flower.suitableSeason || '无' }}</el-descriptions-item>
                <el-descriptions-item label="光照要求">{{ flower.sunlightRequirement || '无' }}</el-descriptions-item>
                <el-descriptions-item label="水分要求">{{ flower.waterRequirement || '无' }}</el-descriptions-item>
                <el-descriptions-item label="土壤要求">{{ flower.soilRequirement || '无' }}</el-descriptions-item>
                <el-descriptions-item label="温度要求">{{ flower.temperatureRequirement || '无' }}</el-descriptions-item>
                <el-descriptions-item label="施肥要点">{{ flower.fertilizationTechnique || '无' }}</el-descriptions-item>
                <el-descriptions-item label="修剪要点">{{ flower.pruningTechnique || '无' }}</el-descriptions-item>
                <el-descriptions-item label="病虫害防治">{{ flower.pestControl || '无' }}</el-descriptions-item>
                <el-descriptions-item label="养护要点">{{ flower.maintenanceTips || '无' }}</el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="养护信息" v-else>
              <el-alert title="开通会员查看" type="warning" :closable="false" show-icon>
                完整养护信息仅对会员开放，开通会员即可查看。
              </el-alert>
              <el-button type="warning" @click="$router.push('/member')" style="margin-top: 20px;">开通会员</el-button>
            </el-tab-pane>
          </el-tabs>
        </el-card>
        <!-- 浮动窗体由组件挂载在页面右下角 -->
      </el-main>
    </el-container>

    <!-- 客服咨询浮动窗体 -->
    <customer-service-widget
      v-if="flower && flower.id && chatEnabled"
      :flower-id="Number(flower.id)"
    />

    <!-- 错误反馈浮动窗体 -->
    <error-feedback-widget
      v-if="flower && flower.id && feedbackEnabled"
      :flower-id="Number(flower.id)"
      :flower-name="flower.name || ''"
    />
  </div>
</template>

<script>
import { flowerAPI } from '@/api/flower'
import { memberAPI, featureAPI } from '@/api/member'
import CustomerServiceWidget from '@/components/CustomerServiceWidget.vue'
import ErrorFeedbackWidget from '@/components/ErrorFeedbackWidget.vue'

export default {
  name: 'FlowerDetail',
  components: { CustomerServiceWidget, ErrorFeedbackWidget },
  data() {
    return {
      flower: null,
      isMember: false,
      chatEnabled: true,
      feedbackEnabled: true
    }
  },
  mounted() {
    this.loadFeatureFlags()
    this.checkMemberStatus()
    this.loadFlowerDetail()
  },
  methods: {
    async loadFeatureFlags() {
      try {
        const res = await featureAPI.getFeatures()
        this.chatEnabled = res.chatEnabled !== false
        this.feedbackEnabled = res.feedbackEnabled !== false
      } catch (e) {
        // 默认开启
      }
    },
    async loadFlowerDetail() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      const id = this.$route.params.id
      try {
        if (this.isMember && user.id) {
          const res = await flowerAPI.getFlowerDetailForMember(id, user.id)
          this.flower = res.flower || res
        } else {
          const res = await flowerAPI.getFlowerDetail(id, user.id)
          this.flower = res.flower || res
        }
      } catch (error) {
        this.$message.error('加载花卉详情失败')
      }
    },
    async checkMemberStatus() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (user.id) {
        try {
          const res = await memberAPI.isMember(user.id)
          this.isMember = res === true
        } catch (error) {
          this.isMember = false
        }
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
}
.flower-header-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}
.flower-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}
.flower-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.flower-info h2 {
  margin: 0;
}
.scientific-name {
  color: #909399;
  font-style: italic;
  margin: 0;
}
.flower-info .el-tag {
  margin-right: 4px;
}
.description {
  margin: 0;
  line-height: 1.6;
}
</style>
