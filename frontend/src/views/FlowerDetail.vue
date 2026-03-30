<template>
  <div class="flower-detail">
    <Navbar />
    <div class="container">
      <div class="back-button">
        <el-button @click="goBack" icon="el-icon-back">返回</el-button>
      </div>
      
      <div v-if="flower" class="flower-content">
        <!-- 头部：花名和学名 -->
        <div class="flower-header">
          <h1 class="flower-name">{{ flower.name }}</h1>
          <h2 class="flower-scientific-name">{{ flower.scientificName }}</h2>
          <div class="flower-aliases" v-if="flower.aliases">
            <el-tag size="small" type="info" v-for="alias in flower.aliases.split('、')" :key="alias" class="alias-tag">{{ alias }}</el-tag>
          </div>
        </div>
        
        <!-- 第一部分：基础档案 -->
        <div class="flower-main">
          <div class="flower-image">
            <img :src="flower.imageUrl" alt="花卉图片" />
          </div>
          <div class="flower-info">
            <el-card class="info-card">
              <div class="info-title">📋 基础档案</div>
              <div class="info-list">
                <div class="info-row">
                  <span class="info-tag">科属</span>
                  <span class="info-detail">{{ flower.family || '暂无信息' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-tag">植株类型</span>
                  <span class="info-detail">{{ flower.plantType || '暂无信息' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-tag">原产地</span>
                  <span class="info-detail">{{ flower.origin || '暂无信息' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-tag">生长环境</span>
                  <span class="info-detail">{{ flower.growthEnvironment || '暂无信息' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-tag">花色</span>
                  <span class="info-detail">{{ flower.color || '暂无信息' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-tag">花期</span>
                  <span class="info-detail">{{ flower.floweringPeriod || '暂无信息' }} {{ flower.floweringFrequency ? '(' + flower.floweringFrequency + ')' : '' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-tag">简介</span>
                  <span class="info-detail">{{ flower.description || '暂无信息' }}</span>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 第二部分：形态特征 -->
        <el-card class="section-card">
          <div class="section-title">🌿 形态特征</div>
          <div class="morphology-grid">
            <div class="morph-item" v-if="flower.plantHeight">
              <span class="morph-label">株高</span>
              <span class="morph-value">{{ flower.plantHeight }}</span>
            </div>
            <div class="morph-item" v-if="flower.stemFeatures">
              <span class="morph-label">茎干</span>
              <span class="morph-value">{{ flower.stemFeatures }}</span>
            </div>
            <div class="morph-item" v-if="flower.leafFeatures">
              <span class="morph-label">叶片</span>
              <span class="morph-value">{{ flower.leafFeatures }}</span>
            </div>
            <div class="morph-item" v-if="flower.flowerFeatures">
              <span class="morph-label">花朵</span>
              <span class="morph-value">{{ flower.flowerFeatures }}</span>
            </div>
            <div class="morph-item" v-if="flower.flowerStamenFeatures">
              <span class="morph-label">花蕊花萼</span>
              <span class="morph-value">{{ flower.flowerStamenFeatures }}</span>
            </div>
            <div class="morph-item" v-if="flower.fragranceType">
              <span class="morph-label">香气</span>
              <span class="morph-value">{{ flower.fragranceType }}</span>
            </div>
            <div class="morph-item" v-if="flower.fruitSeedFeatures">
              <span class="morph-label">果实种子</span>
              <span class="morph-value">{{ flower.fruitSeedFeatures }}</span>
            </div>
            <div class="morph-item full-width" v-if="flower.ornamentalFeatures">
              <span class="morph-label">观赏特点</span>
              <span class="morph-value">{{ flower.ornamentalFeatures }}</span>
            </div>
          </div>
        </el-card>

        <!-- 第三部分：生长与花期 -->
        <el-card class="section-card">
          <div class="section-title">🌱 生长与花期</div>
          <div class="growth-grid">
            <div class="growth-item" v-if="flower.suitableTemperature">
              <span class="growth-icon">🌡️</span>
              <span class="growth-label">适宜温度</span>
              <span class="growth-value">{{ flower.suitableTemperature }}</span>
            </div>
            <div class="growth-item" v-if="flower.lightRequirements">
              <span class="growth-icon">☀️</span>
              <span class="growth-label">光照需求</span>
              <span class="growth-value">{{ flower.lightRequirements }}</span>
            </div>
            <div class="growth-item" v-if="flower.waterRequirements">
              <span class="growth-icon">💧</span>
              <span class="growth-label">水分需求</span>
              <span class="growth-value">{{ flower.waterRequirements }}</span>
            </div>
            <div class="growth-item" v-if="flower.soilPreference">
              <span class="growth-icon">🪴</span>
              <span class="growth-label">土壤偏好</span>
              <span class="growth-value">{{ flower.soilPreference }}</span>
            </div>
            <div class="growth-item" v-if="flower.growthSpeed">
              <span class="growth-icon">📈</span>
              <span class="growth-label">生长速度</span>
              <span class="growth-value">{{ flower.growthSpeed }}</span>
            </div>
            <div class="growth-item" v-if="flower.suitableSeason">
              <span class="growth-icon">📅</span>
              <span class="growth-label">适宜种植</span>
              <span class="growth-value">{{ flower.suitableSeason }}</span>
            </div>
            <div class="growth-item" v-if="flower.plantingMethod">
              <span class="growth-icon">🏡</span>
              <span class="growth-label">种植方式</span>
              <span class="growth-value">{{ flower.plantingMethod }}</span>
            </div>
            <div class="growth-item full-width" v-if="flower.growthHabit">
              <span class="growth-icon">🌾</span>
              <span class="growth-label">生长习性</span>
              <span class="growth-value">{{ flower.growthHabit }}</span>
            </div>
          </div>
        </el-card>

        <!-- 第四部分：花语与文化寓意 -->
        <el-card class="section-card">
          <div class="section-title">🌸 花语与文化寓意</div>
          <div class="culture-content">
            <div class="culture-section" v-if="flower.flowerLanguage">
              <h4>💝 核心花语</h4>
              <p>{{ flower.flowerLanguage }}</p>
            </div>
            <div class="culture-section" v-if="flower.colorMeanings">
              <h4>🎨 不同颜色含义</h4>
              <p>{{ flower.colorMeanings }}</p>
            </div>
            <div class="culture-section" v-if="flower.suitableRecipients">
              <h4>👥 适合赠送对象</h4>
              <p>{{ flower.suitableRecipients }}</p>
            </div>
            <div class="culture-section" v-if="flower.suitableOccasions">
              <h4>🎉 适用场合</h4>
              <p>{{ flower.suitableOccasions }}</p>
            </div>
            <div class="culture-section" v-if="flower.legend">
              <h4>📖 传说故事</h4>
              <p>{{ flower.legend }}</p>
            </div>
            <div class="culture-section" v-if="flower.isCityFlower">
              <h4>🏛️ 市花/国花</h4>
              <p>{{ flower.isCityFlower }}</p>
            </div>
          </div>
        </el-card>

        <!-- 第五部分：用途价值 -->
        <el-card class="section-card">
          <div class="section-title">🎯 用途价值</div>
          <div class="usage-grid">
            <div class="usage-item" v-if="flower.uses">
              <span class="usage-icon">🌺</span>
              <span class="usage-label">观赏用途</span>
              <span class="usage-value">{{ flower.uses }}</span>
            </div>
            <div class="usage-item" v-if="flower.isEdible">
              <span class="usage-icon">🍽️</span>
              <span class="usage-label">食用价值</span>
              <span class="usage-value">{{ flower.isEdible }}</span>
            </div>
            <div class="usage-item" v-if="flower.medicinalValue">
              <span class="usage-icon">💊</span>
              <span class="usage-label">药用价值</span>
              <span class="usage-value">{{ flower.medicinalValue }}</span>
            </div>
          </div>
        </el-card>

        <!-- 第六部分：趣味小知识 -->
        <el-card class="section-card">
          <div class="section-title">💡 趣味小知识</div>
          <div class="fun-facts-list">
            <div class="fact-item" v-if="flower.funFacts">
              <span class="fact-icon">✨</span>
              <span class="fact-label">有趣特点：</span>
              <span class="fact-content">{{ flower.funFacts }}</span>
            </div>
          </div>
        </el-card>

        <!-- 第七部分：搭配与禁忌 -->
        <el-card class="section-card">
          <div class="section-title">⚠️ 搭配与禁忌</div>
          <div class="taboo-grid">
            <div class="taboo-item" v-if="flower.goodCompanions">
              <span class="taboo-icon">✅</span>
              <span class="taboo-label">适合搭配</span>
              <span class="taboo-value">{{ flower.goodCompanions }}</span>
            </div>
            <div class="taboo-item" v-if="flower.taboos">
              <span class="taboo-icon">❌</span>
              <span class="taboo-label">赠送禁忌</span>
              <span class="taboo-value">{{ flower.taboos }}</span>
            </div>
            <div class="taboo-item" v-if="flower.isToxic">
              <span class="taboo-icon">⚠️</span>
              <span class="taboo-label">安全性</span>
              <span class="taboo-value">{{ flower.isToxic }}</span>
            </div>
          </div>
        </el-card>
        
        <!-- 第八部分：养护管理（会员专享） -->
        <div class="service-section">
          <el-card class="service-card">
            <div class="service-header">
              <h3>🌱 养护管理</h3>
              <el-tag type="warning" size="small" v-if="!isMember">会员专享</el-tag>
            </div>
            
            <!-- 非会员显示提示 -->
            <div v-if="!isMember" class="member-lock">
              <i class="el-icon-lock"></i>
              <p>开通会员即可查看完整养护管理知识</p>
              <el-button type="warning" size="small" @click="goToMember">立即开通会员</el-button>
            </div>
            
            <!-- 会员显示完整养护内容 -->
            <div v-else class="care-content">
              <div class="care-grid">
                <div class="care-item" v-if="flower.wateringTips">
                  <span class="care-icon">💧</span>
                  <span class="care-label">浇水</span>
                  <span class="care-value">{{ flower.wateringTips }}</span>
                </div>
                <div class="care-item" v-if="flower.fertilizationTips">
                  <span class="care-icon">🧪</span>
                  <span class="care-label">施肥</span>
                  <span class="care-value">{{ flower.fertilizationTips }}</span>
                </div>
                <div class="care-item" v-if="flower.pruningTips">
                  <span class="care-icon">✂️</span>
                  <span class="care-label">修剪</span>
                  <span class="care-value">{{ flower.pruningTips }}</span>
                </div>
                <div class="care-item" v-if="flower.propagation">
                  <span class="care-icon">🌱</span>
                  <span class="care-label">繁殖</span>
                  <span class="care-value">{{ flower.propagation }}</span>
                </div>
                <div class="care-item" v-if="flower.pestControl">
                  <span class="care-icon">🐛</span>
                  <span class="care-label">病虫害</span>
                  <span class="care-value">{{ flower.pestControl }}</span>
                </div>
                <div class="care-item" v-if="flower.winterCare">
                  <span class="care-icon">❄️</span>
                  <span class="care-label">过冬</span>
                  <span class="care-value">{{ flower.winterCare }}</span>
                </div>
                <div class="care-item care-item-pair">
                  <div class="care-pair" v-if="flower.summerCare">
                    <span class="care-icon">☀️</span>
                    <span class="care-label">度夏</span>
                    <span class="care-value">{{ flower.summerCare }}</span>
                  </div>
                  <div class="care-pair" v-if="flower.commonProblems">
                    <span class="care-icon">❓</span>
                    <span class="care-label">常见问题</span>
                    <span class="care-value">{{ flower.commonProblems }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        
      </div>
      
      <!-- 客服咨询悬浮窗 -->
      <div class="service-float" @click="showConsultation">
        <span class="service-icon">💬</span>
        <span class="service-text">客服咨询</span>
      </div>
      
      <!-- 错误反馈悬浮窗 -->
      <div class="feedback-float" @click="showErrorReport">
        <span class="feedback-icon">⚠️</span>
        <span class="feedback-text">错误反馈</span>
      </div>
      
      <!-- 客服咨询对话框 -->
      <el-dialog title="💬 客服咨询" :visible.sync="consultationVisible" width="550px" @close="closeConsultation">
        <div class="simple-consultation">
          <!-- 频道切换 -->
          <div class="channel-tabs">
            <el-radio-group v-model="consultationType" size="small">
              <el-radio-button label="public">🌸 非会员频道</el-radio-button>
              <el-radio-button label="vip" :disabled="!isMember">💎 会员频道</el-radio-button>
            </el-radio-group>
            <span v-if="!isMember" class="member-tip" @click="goToMember">开通会员</span>
          </div>

          <!-- 提示信息 -->
          <div class="channel-notice" v-if="consultationType === 'public'">
            <el-alert type="info" :closable="false" show-icon>
              <template slot="title">
                <p>🌸 您好！请描述您的问题</p>
              </template>
            </el-alert>
          </div>
          <div class="channel-notice" v-else>
            <el-alert type="warning" :closable="false" show-icon>
              <template slot="title">
                <p>💎 会员专属服务：全方位养护咨询</p>
              </template>
            </el-alert>
          </div>

          <!-- 消息列表 -->
          <div class="message-list" ref="chatMessages">
            <div v-if="consultationMessages.length === 0" class="empty-tip">
              <p>请描述您的问题，客服将尽快回复</p>
            </div>
            <div v-for="(msg, index) in consultationMessages" :key="index" :class="['message-item', msg.type]">
              <span class="avatar">{{ msg.type === 'user' ? '🙋' : '💬' }}</span>
              <div class="message-bubble">
                <div class="message-text">{{ msg.content }}</div>
                <div class="message-time">{{ msg.time }}</div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="input-area">
            <el-input
              v-model="currentMessage"
              type="textarea"
              :rows="2"
              :placeholder="consultationType === 'vip' ? '描述您的养护问题...' : '请输入您的问题...'"
              @keyup.enter.ctrl="sendMessage"
            ></el-input>
            <el-button type="primary" @click="sendMessage" :disabled="!currentMessage.trim()">发送</el-button>
          </div>
        </div>
      </el-dialog>
      
      <!-- 错误反馈对话框 -->
      <el-dialog title="修改错误信息" :visible.sync="errorReportVisible" width="500px">
        <el-form :model="errorReportForm" label-width="80px">
          <el-form-item label="错误位置">
            <el-select v-model="errorReportForm.location" placeholder="请选择错误位置">
              <el-option label="基本信息" value="basic"></el-option>
              <el-option label="形态特征" value="morphology"></el-option>
              <el-option label="生长习性" value="growth"></el-option>
              <el-option label="花语寓意" value="language"></el-option>
              <el-option label="养护知识" value="care"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="错误描述">
            <el-input type="textarea" v-model="errorReportForm.description" rows="4" placeholder="请详细描述错误内容"></el-input>
          </el-form-item>
          <el-form-item label="正确信息">
            <el-input type="textarea" v-model="errorReportForm.correction" rows="4" placeholder="请输入正确的信息"></el-input>
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="errorReportForm.contact" placeholder="请输入您的联系方式（选填）"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="errorReportVisible = false">取消</el-button>
          <el-button type="primary" @click="submitErrorReport">提交</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Navbar from '../components/Navbar.vue'

export default {
  name: 'FlowerDetail',
  components: {
    Navbar
  },
  data() {
    return {
      flower: null,
      isMember: false,
      consultationVisible: false,
      diagnosisVisible: false,
      errorReportVisible: false,
      consultationType: 'public',
      consultationMessages: [],
      pollTimer: null,
      currentMessage: '',
      diagnosisForm: {
        symptoms: '',
        images: [],
        contact: ''
      },
      errorReportForm: {
        location: '',
        description: '',
        correction: '',
        contact: ''
      }
    }
  },
  created() {
    this.fetchFlowerDetail()
    this.checkMemberStatus()
  },
  methods: {
    async fetchFlowerDetail() {
      try {
        const flowerId = this.$route.params.id
        const response = await this.$http.get(`/flowers/${flowerId}`)
        this.flower = response.data || null
      } catch (error) {
        console.error('获取花卉详情失败:', error)
        this.flower = null
        this.$message.error('获取花卉详情失败')
      }
    },
    checkMemberStatus() {
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo) {
        const user = JSON.parse(userInfo)
        this.isMember = user.isMember || false
      }
    },
    goBack() {
      this.$router.push('/flower-encyclopedia')
    },
    goToMember() {
      this.$router.push('/member-center')
    },
    showConsultation() {
      this.consultationVisible = true
      this.consultationMessages = []
      this.currentMessage = ''
    },
    sendMessage() {
      if (!this.currentMessage.trim()) return

      const userMessage = {
        type: 'user',
        content: this.currentMessage,
        time: new Date().toLocaleTimeString()
      }
      this.consultationMessages.push(userMessage)
      const question = this.currentMessage
      this.currentMessage = ''

      this.$nextTick(() => {
        if (this.$refs.chatMessages) {
          this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight
        }
      })

      this.$http.post('/consultation/chat', {
        flowerId: this.flower?.id,
        flowerName: this.flower?.name,
        type: this.consultationType,
        message: question
      }).then(response => {
        const replyMessage = {
          type: 'service',
          content: response.data?.reply || '您的咨询已提交，客服人员将尽快回复您，请稍候...',
          time: new Date().toLocaleTimeString()
        }
        this.consultationMessages.push(replyMessage)
        this.currentConsultationId = this.flower?.id
        this.pollForReply()
        this.$nextTick(() => {
          if (this.$refs.chatMessages) {
            this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight
          }
        })
      }).catch(() => {
        const errorMessage = {
          type: 'service',
          content: '发送失败，请稍后重试',
          time: new Date().toLocaleTimeString()
        }
        this.consultationMessages.push(errorMessage)
      })
    },
    pollForReply() {
      if (this.pollTimer) {
        clearInterval(this.pollTimer)
      }
      this.pollTimer = setInterval(() => {
        this.checkForReply()
      }, 3000)
    },
    checkForReply() {
      this.$http.get('/consultation/list/all').then(response => {
        const list = response.data || []
        const myConsultation = list.find(item =>
          item.message === this.consultationMessages[0]?.content &&
          item.type === this.consultationType &&
          item.status === 'replied'
        )
        if (myConsultation && myConsultation.reply) {
          const lastMsg = this.consultationMessages[this.consultationMessages.length - 1]
          if (lastMsg?.content !== myConsultation.reply) {
            this.consultationMessages.push({
              type: 'service',
              content: myConsultation.reply,
              time: new Date().toLocaleTimeString()
            })
            this.$nextTick(() => {
              if (this.$refs.chatMessages) {
                this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight
              }
            })
          }
          if (this.pollTimer) {
            clearInterval(this.pollTimer)
            this.pollTimer = null
          }
        }
      })
    },
    closeConsultation() {
      if (this.pollTimer) {
        clearInterval(this.pollTimer)
        this.pollTimer = null
      }
      this.consultationVisible = false
      this.consultationMessages = []
      this.currentMessage = ''
    },
    showDiagnosis() {
      this.diagnosisVisible = true
    },
    showErrorReport() {
      this.errorReportVisible = true
    },
    async submitDiagnosis() {
      try {
        await this.$http.post('/diagnosis', {
          flowerId: this.flower.id,
          ...this.diagnosisForm
        })
        this.$message.success('诊断申请提交成功，专家会尽快为您诊断')
        this.diagnosisVisible = false
        this.diagnosisForm = { symptoms: '', images: [], contact: '' }
      } catch (error) {
        this.$message.error('提交失败，请稍后重试')
      }
    },
    async submitErrorReport() {
      try {
        await this.$http.post('/error-report', {
          flowerId: this.flower.id,
          flowerName: this.flower.name,
          ...this.errorReportForm
        })
        this.$message.success('感谢您的反馈，我们会尽快核实并修正')
        this.errorReportVisible = false
        this.errorReportForm = { location: '', description: '', correction: '', contact: '' }
      } catch (error) {
        this.$message.error('提交失败，请稍后重试')
      }
    },
    handleDiagnosisImageSuccess(response) {
      this.diagnosisForm.images.push(response.url)
    },
    beforeDiagnosisImageUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isPNG = file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG && !isPNG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return (isJPG || isPNG) && isLt2M
    }
  }
}
</script>

<style scoped>
.flower-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding-bottom: 50px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.back-button {
  margin-bottom: 20px;
}

.flower-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px 0;
}

.flower-name {
  font-size: 36px;
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 600;
}

.flower-scientific-name {
  font-size: 18px;
  color: #7f8c8d;
  font-style: italic;
  margin-bottom: 15px;
}

.flower-aliases {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.alias-tag {
  margin: 0;
}

.flower-main {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  align-items: stretch;
  justify-content: center;
}

.flower-image {
  flex: 0 0 350px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.flower-image img {
  width: 100%;
  height: 350px;
  object-fit: cover;
  display: block;
}

.flower-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  gap: 20px;
}

.flower-info .info-card {
  border-radius: 12px;
  width: 100%;
  border: none;
  flex-shrink: 0;
}

.flower-info .info-card ::v-deep .el-card__body {
  padding: 25px;
  display: flex;
  flex-direction: column;
  height: 350px;
  overflow-y: auto;
}

.info-title {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row.full-width {
  flex-direction: column;
  gap: 8px;
}

.info-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
}

.info-detail {
  color: #333;
  font-size: 15px;
  line-height: 1.6;
  flex: 1;
}

/* 区块卡片样式 */
.section-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.section-card ::v-deep .el-card__body {
  padding: 25px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e7ed;
}

/* 形态特征网格 */
.morphology-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.morph-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.morph-item.full-width {
  grid-column: 1 / -1;
}

.morph-label {
  font-weight: bold;
  color: #2c3e50;
  font-size: 14px;
}

.morph-value {
  color: #555;
  font-size: 14px;
  line-height: 1.6;
}

/* 生长与花期网格 */
.growth-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.growth-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.growth-item.full-width {
  grid-column: 1 / -1;
}

.growth-icon {
  font-size: 20px;
}

.growth-label {
  font-weight: bold;
  color: #2c3e50;
  font-size: 14px;
  white-space: nowrap;
}

.growth-value {
  color: #555;
  font-size: 14px;
  flex: 1;
}

/* 花语文化内容 */
.culture-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.culture-section {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.culture-section h4 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 16px;
}

.culture-section p {
  color: #555;
  line-height: 1.8;
  margin: 0;
}

/* 用途价值网格 */
.usage-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.usage-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.usage-icon {
  font-size: 24px;
}

.usage-label {
  font-weight: bold;
  color: #2c3e50;
  font-size: 15px;
  white-space: nowrap;
  min-width: 80px;
}

.usage-value {
  color: #555;
  font-size: 15px;
  line-height: 1.7;
  flex: 1;
}

/* 趣味小知识 */
.fun-facts-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.fact-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.fact-icon {
  font-size: 20px;
}

.fact-label {
  font-weight: bold;
  color: #2c3e50;
  font-size: 15px;
  white-space: nowrap;
}

.fact-content {
  color: #555;
  font-size: 15px;
  line-height: 1.7;
  flex: 1;
}

/* 搭配与禁忌 */
.taboo-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.taboo-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.taboo-icon {
  font-size: 24px;
}

.taboo-label {
  font-weight: bold;
  color: #2c3e50;
  font-size: 15px;
  white-space: nowrap;
  min-width: 80px;
}

.taboo-value {
  color: #555;
  font-size: 15px;
  line-height: 1.7;
  flex: 1;
}

/* 会员服务区域 */
.service-section {
  margin-bottom: 20px;
}

.service-card {
  border-radius: 12px;
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e7ed;
}

.service-header h3 {
  color: #2c3e50;
  font-size: 20px;
  margin: 0;
}

.member-lock {
  text-align: center;
  padding: 40px 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.member-lock i {
  font-size: 48px;
  color: #909399;
  margin-bottom: 15px;
}

.member-lock p {
  color: #606266;
  margin-bottom: 20px;
  font-size: 16px;
}

.consultation-notice ul {
  margin: 10px 0;
  padding-left: 20px;
}

.consultation-notice li {
  margin-bottom: 5px;
  line-height: 1.6;
}

.consultation-container {
  min-height: 400px;
}

.channel-select {
  text-align: center;
  padding: 20px;
}

.channel-select h3 {
  margin-bottom: 25px;
  color: #2c3e50;
}

.channel-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 20px;
}

.channel-btn {
  width: 200px;
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.channel-btn:disabled {
  opacity: 0.6;
}

.channel-icon {
  font-size: 30px;
  margin-bottom: 8px;
}

.channel-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.channel-desc {
  font-size: 12px;
  color: #909399;
}

.channel-notice {
  color: #909399;
  font-size: 14px;
  margin-top: 15px;
}

.chat-interface {
  display: flex;
  flex-direction: column;
  height: 450px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 15px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.chat-empty {
  text-align: center;
  color: #909399;
  padding: 30px;
}

.chat-empty .notice {
  color: #f56c6c;
  margin-top: 10px;
}

.message {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message.service {
  flex-direction: row;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #409eff;
}

.message.service .message-avatar {
  background: #67c23a;
}

.message-content {
  max-width: 70%;
  margin: 0 10px;
}

.message-text {
  padding: 10px 15px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
}

.message.user .message-text {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.service .message-text {
  background: white;
  color: #2c3e50;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-time {
  font-size: 11px;
  color: #909399;
  margin-top: 5px;
  text-align: right;
}

.message.user .message-time {
  text-align: left;
}

.quick-questions {
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

.quick-questions p {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.quick-btns {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chat-input {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.chat-input .el-textarea {
  flex: 1;
}

.simple-consultation {
  padding: 10px 0;
}

.channel-tabs {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.member-tip {
  color: #409eff;
  font-size: 13px;
  cursor: pointer;
}

.member-tip:hover {
  text-decoration: underline;
}

.channel-notice {
  margin-bottom: 15px;
}

.channel-notice .notice {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

.message-list {
  height: 300px;
  overflow-y: auto;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 60px 20px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-item .avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e7ed;
  font-size: 16px;
  flex-shrink: 0;
}

.message-item.user .avatar {
  background: #409eff;
  color: white;
}

.message-item.service .avatar {
  background: #67c23a;
  color: white;
}

.message-bubble {
  max-width: 75%;
  margin: 0 10px;
}

.message-text {
  padding: 10px 14px;
  border-radius: 12px;
  line-height: 1.5;
  word-break: break-word;
}

.message-item.user .message-text {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.service .message-text {
  background: white;
  color: #2c3e50;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.message-time {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
  text-align: right;
}

.message-item.user .message-time {
  text-align: left;
}

.input-area {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.input-area .el-textarea {
  flex: 1;
}

.care-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.care-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.care-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.care-item.full-width {
  grid-column: 1 / -1;
}

.care-item-pair {
  display: contents;
}

.care-pair {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  flex: 1;
}

.care-icon {
  font-size: 24px;
}

.care-label {
  font-weight: bold;
  color: #2c3e50;
  font-size: 14px;
}

.care-value {
  color: #555;
  font-size: 14px;
  line-height: 1.6;
}

.service-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  padding: 20px;
}

.loading {
  padding: 40px;
}

@media (max-width: 768px) {
  .flower-main {
    flex-direction: column;
  }
  
  .flower-image {
    flex: none;
    width: 100%;
  }
  
  .flower-image img {
    min-height: 250px;
  }
  
  .morphology-grid,
  .growth-grid,
  .care-grid {
    grid-template-columns: 1fr;
  }
}

.service-float {
  position: fixed;
  right: 20px;
  bottom: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 20px;
  border-radius: 25px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  z-index: 1000;
}

.service-float:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.service-icon {
  font-size: 20px;
}

.service-text {
  font-size: 14px;
  font-weight: bold;
}

.feedback-float {
  position: fixed;
  right: 20px;
  bottom: 50px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 12px 20px;
  border-radius: 25px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(245, 87, 108, 0.4);
  transition: all 0.3s ease;
  z-index: 1000;
}

.feedback-float:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(245, 87, 108, 0.6);
}

.feedback-icon {
  font-size: 18px;
}

.feedback-text {
  font-size: 14px;
  font-weight: bold;
}
</style>
