<template>
  <div class="membership">
    <Navbar />
    <div class="container">
      <!-- 客服服务图标 -->
      <div class="customer-service-container">
        <div class="service-icon" @click="toggleServiceOptions">
          <el-button type="default" circle size="large">
            <i class="el-icon-headset" style="color: #409EFF; font-size: 24px;"></i>
          </el-button>
        </div>
        
        <!-- 客服服务选项 -->
        <div v-if="showServiceOptions" class="service-options-dropdown">
          <div class="service-content">
            <div class="service-buttons">
              <div class="service-item primary" @click="openConsultation">
                <span style="font-size: 18px; margin-right: 6px;">🌸</span>
                会员养护咨询
              </div>
              <div class="service-item success" @click="openOtherConsultation">
                <span style="font-size: 18px; margin-right: 6px;">❓</span>
                其他问题咨询
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div style="margin-bottom: 16px;">
        <el-button size="small" @click="$router.back()"><i class="el-icon-back"></i> 返回</el-button>
      </div>
      <h1 class="page-title">会员中心</h1>
      
      <!-- 会员状态 -->
      <div class="member-status" v-if="isLoggedIn">
        <el-card>
          <!-- 到期提醒横幅 -->
          <el-alert
            v-if="memberInfo.isMember && daysUntilExpiry !== null && daysUntilExpiry <= 7 && daysUntilExpiry >= 0"
            :title="`会员将于 ${daysUntilExpiry} 天后到期，请及时续费`"
            type="warning"
            :closable="false"
            show-icon
            style="margin-bottom: 16px;"
          />
          <el-alert
            v-if="memberInfo.status === 'refunded' || memberInfo.status === 'cancelled'"
            title="会员已到期，开通会员继续享受专属权益"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 16px;"
          />

          <h2>我的会员信息</h2>
          <div class="member-info">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userInfo.username || '' }}</span>
            </div>
            <div class="info-item">
              <span class="label">会员状态：</span>
              <span class="value" :class="memberStatusClass">{{ memberStatusText }}</span>
            </div>
            <div class="info-item" v-if="memberInfo.expiryDate">
              <span class="label">会员到期时间：</span>
              <span class="value">{{ formatDate(memberInfo.expiryDate) }}</span>
            </div>
            <div class="info-item" v-if="memberInfo.isMember && daysUntilExpiry !== null">
              <span class="label">剩余天数：</span>
              <span class="value" :class="{ 'text-warning': daysUntilExpiry <= 7 }">{{ daysUntilExpiry }} 天</span>
            </div>
            <div class="info-item" v-if="memberInfo.joinDate">
              <span class="label">开通时间：</span>
              <span class="value">{{ formatDate(memberInfo.joinDate) }}</span>
            </div>
          </div>

          <!-- 退款倒计时（支付成功后2分钟内显示） -->
          <div v-if="refundCountdown > 0" class="refund-banner">
            <span>支付成功！如需退款，请在 <strong class="countdown-num">{{ refundCountdown }}</strong> 秒内操作</span>
            <el-button type="danger" size="small" plain @click="handleRefund">申请退款</el-button>
          </div>

          <div class="member-actions">
            <el-button
              v-if="!memberInfo.isMember"
              type="primary"
              size="large"
              :disabled="!paymentEnabled"
              @click="openPaymentModal(false)"
            >{{ paymentEnabled ? '立即开通会员' : '支付功能维护中' }}</el-button>
            <template v-else>
              <el-button type="primary" size="large" @click="openPaymentModal(true)">续费会员</el-button>
            </template>
          </div>
        </el-card>
      </div>
      
      <!-- 非登录状态 -->
      <div class="non-login" v-else>
        <el-card>
          <h2>会员中心</h2>
          <p>登录后查看您的会员信息，享受更多会员特权！</p>
          <el-button type="primary" size="large" @click="$router.push('/login')">立即登录</el-button>
        </el-card>
      </div>
      
      <!-- 会员权益（使用 MemberBenefits 组件） -->
      <MemberBenefits
        :benefits="benefits"
        :is-member="memberInfo.isMember"
        :is-expired="isExpired"
        @upgrade="openPaymentModal(false)"
        @renew="openPaymentModal(true)"
      />

      <!-- 支付弹窗 -->
      <PaymentModal
        :show.sync="showPaymentModal"
        :user-id="userInfo.id"
        :is-renew="isRenew"
        @paid="onPaymentSuccess"
        @request-refund="handleRefundByOrderId"
        @close="showPaymentModal = false"
      />
    </div>
    
    <!-- 会员养护咨询服务小窗口 -->
    <div v-if="dialogVisible.consultation" class="small-dialog-overlay" @click="dialogVisible.consultation = false">
      <div class="small-dialog consultation-dialog" @click.stop>
        <div class="small-dialog-header">
          <h3 class="small-dialog-title">会员养护咨询服务</h3>
          <button class="small-dialog-close" @click="dialogVisible.consultation = false">×</button>
        </div>
        <div class="small-dialog-content chat-content">
          <!-- 聊天消息区域 -->
          <div class="chat-messages">
            <div 
              v-for="message in consultationData.messages" 
              :key="message.id"
              :class="['chat-message', message.sender]"
            >
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
                <div class="message-time">{{ message.timestamp }}</div>
              </div>
            </div>
          </div>
          <!-- 消息输入区域 -->
          <div class="chat-input-area">
            <textarea 
              v-model="consultationData.inputMessage"
              placeholder="请输入您的问题..."
              class="chat-input"
              rows="2"
              @keyup.enter.ctrl="sendMessage"
            ></textarea>
            <button 
              class="send-button"
              @click="sendMessage"
              :disabled="!consultationData.inputMessage.trim()"
            >
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 其他问题咨询小窗口 -->
    <div v-if="dialogVisible.otherConsultation" class="small-dialog-overlay" @click="dialogVisible.otherConsultation = false">
      <div class="small-dialog consultation-dialog" @click.stop>
        <div class="small-dialog-header">
          <h3 class="small-dialog-title">其他问题咨询</h3>
          <button class="small-dialog-close" @click="dialogVisible.otherConsultation = false">×</button>
        </div>
        <div class="small-dialog-content chat-content">
          <!-- 聊天消息区域 -->
          <div class="chat-messages">
            <div 
              v-for="message in otherConsultationData.messages" 
              :key="message.id"
              :class="['chat-message', message.sender]"
            >
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
                <div class="message-time">{{ message.timestamp }}</div>
              </div>
            </div>
          </div>
          <!-- 消息输入区域 -->
          <div class="chat-input-area">
            <textarea 
              v-model="otherConsultationData.inputMessage"
              placeholder="请输入您的问题..."
              class="chat-input"
              rows="2"
              @keyup.enter.ctrl="sendOtherConsultationMessage"
            ></textarea>
            <button 
              class="send-button"
              @click="sendOtherConsultationMessage"
              :disabled="!otherConsultationData.inputMessage.trim()"
            >
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '../components/Navbar.vue'
import MemberBenefits from '../components/MemberBenefits.vue'
import PaymentModal from '../components/PaymentModal.vue'
import { memberAPI, featureAPI } from '../api/member'

export default {
  components: {
    Navbar,
    MemberBenefits,
    PaymentModal
  },
  data() {
    return {
      benefits: [], // 会员权益列表（从后端加载）
      privileges: [], // 会员特权（保留兼容）
      showPaymentModal: false, // 支付弹窗
      isRenew: false, // 是否续费
      paymentEnabled: true, // 支付功能开关
      memberInfo: { isMember: false, status: null, expiryDate: null, joinDate: null }, // 会员信息
      refundCountdown: 0, // 退款倒计时
      refundTimer: null, // 退款倒计时定时器
      lastOrderId: null, // 最近支付的订单ID
      showMembershipDialog: false, // 保留兼容
      paymentMethod: 'alipay', // 保留兼容
      showServiceOptions: false, // 客服服务选项
      // 对话框相关数据
      dialogVisible: {
        consultation: false,
        otherConsultation: false
      },
      // 咨询聊天数据
      consultationData: {
        messages: [
          {
            id: 1,
            content: '您好！欢迎使用会员养护咨询服务，我是您的专属花卉顾问，有什么可以帮助您的吗？',
            sender: 'system',
            timestamp: new Date().toLocaleTimeString()
          }
        ],
        inputMessage: ''
      },
      // 其他问题咨询聊天数据
      otherConsultationData: {
        messages: [
          {
            id: 1,
            content: '您好！欢迎咨询，请问有什么可以帮助您的吗？',
            sender: 'system',
            timestamp: new Date().toLocaleTimeString()
          }
        ],
        inputMessage: ''
      },
      // 花卉养护知识数据
      flowers: [
        {
          name: '玫瑰',
          aliases: ['玫瑰', '月季花', '月季'],
          careTips: {
            soil: '玫瑰适合生长在肥沃、排水良好的土壤中，pH值在6.0-6.5之间。',
            watering: '保持土壤湿润，但不要积水。夏季每天浇水1-2次，冬季减少浇水频率。',
            sunlight: '玫瑰需要充足的阳光，每天至少6小时的直射光。',
            fertilization: '春季和秋季每月施肥1次，使用均衡的复合肥。',
            pruning: '冬季休眠期进行修剪，剪去病枝、弱枝和交叉枝。',
            pestControl: '定期检查病虫害，及时防治蚜虫、白粉病等常见问题。'
          }
        },
        {
          name: '牡丹',
          aliases: ['牡丹', '牡丹花'],
          careTips: {
            soil: '牡丹适合生长在肥沃、排水良好的沙壤土中，pH值在6.5-7.5之间。忌粘性土壤和积水。',
            watering: '牡丹耐旱怕涝，浇水要适量。春季和秋季保持土壤湿润，夏季减少浇水，避免积水烂根。',
            sunlight: '牡丹需要充足的阳光，每天至少5小时的直射光。但夏季高温时需要适当遮阴，避免强光灼伤。',
            fertilization: '每年施肥3次：春季萌芽前施氮肥，花后施复合肥，秋季施有机肥。',
            pruning: '花后及时剪去残花，冬季剪去病枝、弱枝。牡丹的修剪不宜过重，以免影响开花。',
            pestControl: '牡丹常见的病虫害有褐斑病、根腐病、蚜虫等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '郁金香',
          aliases: ['郁金香', '郁香'],
          careTips: {
            soil: '郁金香适合生长在肥沃、排水良好的沙壤土中，pH值在6.0-7.0之间。',
            watering: '保持土壤湿润，但不要积水。生长初期和花期需要较多水分，其他时期减少浇水。',
            sunlight: '郁金香需要充足的阳光，每天至少6小时的直射光。光照不足会导致开花减少，花色变淡。',
            fertilization: '种植前施足基肥，生长期每2-3周施肥1次，使用稀薄的液肥。',
            pruning: '花后及时剪去残花，保留叶片进行光合作用，为球根积累养分。',
            pestControl: '郁金香常见的病虫害有蚜虫、灰霉病、鳞茎腐烂病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '风信子',
          aliases: ['风信子', '洋水仙', '西洋水仙'],
          careTips: {
            soil: '风信子适合生长在肥沃、排水良好的沙壤土中，pH值在6.0-7.0之间。',
            watering: '保持土壤湿润，但不要积水。水培时要注意水位，避免球根腐烂。',
            sunlight: '风信子需要充足的阳光，每天至少6小时的直射光。光照不足会导致开花减少，花色变淡。',
            fertilization: '生长期每2-3周施肥1次，使用稀薄的液肥。水培时可添加专用营养液。',
            pruning: '花后及时剪去残花，保留叶片进行光合作用，为球根积累养分。',
            pestControl: '风信子常见的病虫害有蚜虫、灰霉病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '百合',
          aliases: ['百合', '百合花'],
          careTips: {
            soil: '百合适合生长在肥沃、排水良好的沙壤土中，pH值在5.5-6.5之间。忌粘性土壤和积水。',
            watering: '保持土壤湿润，但不要积水。生长初期和花期需要较多水分，其他时期减少浇水。',
            sunlight: '百合需要充足的阳光，每天至少6小时的直射光。但夏季高温时需要适当遮阴。',
            fertilization: '生长期每2-3周施肥1次，使用稀薄的液肥。花期前增施磷钾肥，促进花芽分化。',
            pruning: '花后及时剪去残花，保留叶片进行光合作用，为鳞茎积累养分。',
            pestControl: '百合常见的病虫害有蚜虫、红蜘蛛、叶斑病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '向日葵',
          aliases: ['向日葵', '葵花', '太阳花'],
          careTips: {
            soil: '向日葵适合生长在肥沃、排水良好的土壤中，pH值在6.0-7.5之间。',
            watering: '向日葵耐旱，但生长期间需要充足的水分。特别是在花期和籽粒形成期，要保持土壤湿润。',
            sunlight: '向日葵需要充足的阳光，每天至少8小时的直射光。光照不足会导致植株徒长，花盘变小。',
            fertilization: '种植前施足基肥，生长期每2-3周施肥1次，使用稀薄的液肥。花期前增施磷钾肥，促进花盘发育。',
            pruning: '向日葵一般不需要过多修剪，只需要及时去除底部的老叶和病叶，以利通风透光。',
            pestControl: '向日葵常见的病虫害有蚜虫、棉铃虫、叶斑病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '绣球花',
          aliases: ['绣球花', '八仙花', '绣球'],
          careTips: {
            soil: '绣球花适合生长在肥沃、排水良好的酸性土壤中，pH值在5.0-6.0之间。土壤酸碱度会影响花色，酸性土壤开蓝色花，碱性土壤开粉色花。',
            watering: '绣球花喜湿润，要保持土壤湿润，但不要积水。夏季每天浇水1-2次，冬季减少浇水频率。',
            sunlight: '绣球花耐阴，适合在半阴环境中生长，每天接受2-4小时的散射光即可。避免强光直射，以免灼伤叶片。',
            fertilization: '生长期每10-15天施肥1次，使用稀薄的液肥。花期前增施磷钾肥，促进花芽分化。',
            pruning: '花后及时剪去残花，促进新枝萌发。冬季休眠期进行修剪，剪去病枝、弱枝和交叉枝。',
            pestControl: '绣球花常见的病虫害有蚜虫、红蜘蛛、叶斑病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '菊花',
          aliases: ['菊花', '秋菊'],
          careTips: {
            soil: '菊花适合生长在肥沃、排水良好的沙壤土中，pH值在6.0-7.0之间。',
            watering: '菊花喜湿润，要保持土壤湿润。夏季每天浇水1-2次，冬季减少浇水频率。',
            sunlight: '菊花需要充足的阳光，每天至少6小时的直射光。光照不足会导致植株徒长，开花减少。',
            fertilization: '生长期每10-15天施肥1次，使用稀薄的液肥。花期前增施磷钾肥，促进花芽分化。',
            pruning: '定期摘心，促进分枝。花后及时剪去残花，减少养分消耗。',
            pestControl: '菊花常见的病虫害有蚜虫、红蜘蛛、白粉病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '桂花',
          aliases: ['桂花', '木犀', '九里香'],
          careTips: {
            soil: '桂花适合生长在肥沃、排水良好的酸性土壤中，pH值在5.5-6.5之间。',
            watering: '桂花耐旱，要保持土壤湿润，但不要积水。夏季每天浇水1次，冬季减少浇水频率。',
            sunlight: '桂花需要充足的阳光，每天至少6小时的直射光。光照不足会导致开花减少，香气变淡。',
            fertilization: '每年施肥3次：春季萌芽前施氮肥，花后施复合肥，秋季施有机肥。',
            pruning: '花后及时剪去残花，冬季剪去病枝、弱枝和交叉枝。桂花的修剪不宜过重，以免影响开花。',
            pestControl: '桂花常见的病虫害有蚜虫、红蜘蛛、叶斑病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '月季',
          aliases: ['月季', '月季花', '月月红'],
          careTips: {
            soil: '月季适合生长在肥沃、排水良好的土壤中，pH值在6.0-6.5之间。',
            watering: '保持土壤湿润，但不要积水。夏季每天浇水1-2次，冬季减少浇水频率。',
            sunlight: '月季需要充足的阳光，每天至少6小时的直射光。光照不足会导致开花减少，花朵变小。',
            fertilization: '春季和秋季每月施肥1次，使用均衡的复合肥。花期前增施磷钾肥，促进花芽分化。',
            pruning: '冬季休眠期进行修剪，剪去病枝、弱枝和交叉枝。花后及时剪去残花，促进新枝萌发。',
            pestControl: '月季常见的病虫害有蚜虫、白粉病、黑斑病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '长寿花',
          aliases: ['长寿花', '寿星花'],
          careTips: {
            soil: '长寿花适合生长在肥沃、排水良好的沙壤土中，pH值在6.0-6.5之间。可使用多肉专用土。',
            watering: '长寿花耐旱，要保持土壤微干。夏季每3-5天浇水1次，冬季每7-10天浇水1次。',
            sunlight: '长寿花需要充足的阳光，每天至少4-6小时的直射光。光照不足会导致开花减少，花色变淡。',
            fertilization: '生长期每2-3周施肥1次，使用稀薄的液肥。花期前增施磷钾肥，促进花芽分化。',
            pruning: '花后及时剪去残花，促进新枝萌发。定期修剪，保持植株形态美观。',
            pestControl: '长寿花常见的病虫害有蚜虫、介壳虫、白粉病等。要注意通风透光，定期喷洒药剂防治。'
          }
        },
        {
          name: '仙客来',
          aliases: ['仙客来', '兔耳花', '兔子花'],
          careTips: {
            soil: '仙客来适合生长在肥沃、排水良好的土壤中，pH值在6.0-6.5之间。',
            watering: '保持土壤湿润，但不要积水。浇水时注意不要淋到叶心，以免腐烂。',
            sunlight: '仙客来耐阴，适合在明亮的散射光环境中生长，避免强光直射。',
            fertilization: '生长期每2周施肥1次，使用稀薄的液肥。花期停止施肥。',
            pruning: '花后及时剪去残花，剪去枯黄的老叶。',
            pestControl: '仙客来常见的病虫害有灰霉病、软腐病、蚜虫等。要注意通风透光，避免高温高湿。'
          }
        }
      ]
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.user.userInfo || {}
    },
    isLoggedIn() {
      return this.$store.state.user.isLoggedIn
    },
    isMember() {
      return this.memberInfo.isMember
    },
    isExpired() {
      if (!this.memberInfo.expiryDate) return false
      return new Date(this.memberInfo.expiryDate) < new Date()
    },
    daysUntilExpiry() {
      if (!this.memberInfo.expiryDate) return null
      const diff = new Date(this.memberInfo.expiryDate) - new Date()
      return Math.ceil(diff / (1000 * 60 * 60 * 24))
    },
    memberStatusText() {
      const s = this.memberInfo.status
      if (s === 'active' && !this.isExpired) return '有效会员'
      if (this.isExpired || s === 'cancelled' || s === 'refunded') return '已到期'
      if (s === 'pending') return '待审核'
      return '非会员'
    },
    memberStatusClass() {
      const s = this.memberInfo.status
      if (s === 'active' && !this.isExpired) return 'status-member'
      if (this.isExpired || s === 'cancelled' || s === 'refunded') return 'status-expired'
      return 'status-non-member'
    },
    canCancelMembership() {
      return this.refundCountdown > 0
    }
  },
  mounted() {
    this.loadPrivileges()
    this.loadBenefits()
    this.loadMemberInfo()
    this.loadFeatureFlags()
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
    if (this.refundTimer) clearInterval(this.refundTimer)
  },
  methods: {
    // 客服服务相关方法
    toggleServiceOptions(event) {
      event.stopPropagation() // 阻止事件冒泡，避免触发点击外部关闭
      this.showServiceOptions = !this.showServiceOptions
    },
    handleClickOutside(event) {
      // 检查点击是否在客服服务容器外部
      const container = document.querySelector('.customer-service-container')
      if (container && !container.contains(event.target)) {
        this.showServiceOptions = false
      }
    },
    openConsultation() {
      // 检查是否为会员
      if (!this.isMember) {
        this.showServiceOptions = false
        this.$message({
          message: '该服务仅对会员开放，请先开通会员',
          type: 'warning',
          duration: 3000
        })
        return
      }
      
      // 打开会员养护咨询服务对话框
      this.showServiceOptions = false
      this.dialogVisible.consultation = true
      // 延迟滚动到底部，确保DOM已经渲染
      setTimeout(() => {
        this.scrollToBottom()
      }, 100)
    },
    openOtherConsultation() {
      // 打开其他问题咨询对话框
      this.showServiceOptions = false
      this.dialogVisible.otherConsultation = true
      // 延迟滚动到底部，确保DOM已经渲染
      setTimeout(() => {
        this.scrollToBottomOther()
      }, 100)
    },
    // 发送消息
    sendMessage() {
      const message = this.consultationData.inputMessage.trim()
      if (!message) return
      
      // 添加用户消息
      const userMessage = {
        id: Date.now(),
        content: message,
        sender: 'user',
        timestamp: new Date().toLocaleTimeString()
      }
      this.consultationData.messages.push(userMessage)
      
      // 清空输入框
      this.consultationData.inputMessage = ''
      
      // 查找匹配的花卉
      let matchedFlower = null
      for (const flower of this.flowers) {
        for (const alias of flower.aliases) {
          if (message.includes(alias)) {
            matchedFlower = flower
            break
          }
        }
        if (matchedFlower) break
      }
      
      // 模拟系统回复
      setTimeout(() => {
        let systemMessage
        if (matchedFlower) {
          // 回复详细的花卉养护知识
          const careTips = matchedFlower.careTips
          const replyContent = `${matchedFlower.name}的详细养护知识：\n\n🌱 土壤要求：${careTips.soil}\n\n💧 浇水管理：${careTips.watering}\n\n☀️ 光照需求：${careTips.sunlight}\n\n🌿 施肥方法：${careTips.fertilization}\n\n✂️ 修剪技巧：${careTips.pruning}\n\n🐛 病虫害防治：${careTips.pestControl}\n\n如果您还有其他问题，欢迎继续咨询！`
          systemMessage = {
            id: Date.now() + 1,
            content: replyContent,
            sender: 'system',
            timestamp: new Date().toLocaleTimeString()
          }
        } else {
          // 默认回复
          systemMessage = {
            id: Date.now() + 1,
            content: '感谢您的咨询！您可以问我具体的花卉名称，比如"玫瑰怎么种"、"牡丹的养护方法"等，我会为您提供详细的养护知识。',
            sender: 'system',
            timestamp: new Date().toLocaleTimeString()
          }
        }
        this.consultationData.messages.push(systemMessage)
        
        // 滚动到底部
        this.scrollToBottom()
      }, 1000)
      
      // 滚动到底部
      this.scrollToBottom()
    },
    // 滚动到底部
    scrollToBottom() {
      setTimeout(() => {
        const chatMessages = document.querySelector('.chat-messages')
        if (chatMessages) {
          chatMessages.scrollTop = chatMessages.scrollHeight
        }
      }, 100)
    },
    // 发送其他问题咨询消息
    sendOtherConsultationMessage() {
      const message = this.otherConsultationData.inputMessage.trim()
      if (!message) return
      
      // 添加用户消息
      const userMessage = {
        id: Date.now(),
        content: message,
        sender: 'user',
        timestamp: new Date().toLocaleTimeString()
      }
      this.otherConsultationData.messages.push(userMessage)
      
      // 清空输入框
      this.otherConsultationData.inputMessage = ''
      
      // 分析用户问题并回复
      const lowerMessage = message.toLowerCase()
      let replyContent = ''
      
      // 内容纠错相关
      if (lowerMessage.includes('错误') || lowerMessage.includes('纠错') || lowerMessage.includes('不对') || lowerMessage.includes('错了')) {
        replyContent = '感谢您的反馈！我们已经记录了您的内容纠错建议，客服人员会尽快核实并上报修改。如果您能提供更多具体信息（比如具体是哪个花卉、哪部分内容有误），将帮助我们更快地处理问题。'
      }
      // 会员开通相关
      else if (lowerMessage.includes('开通会员') || lowerMessage.includes('怎么开通') || lowerMessage.includes('成为会员')) {
        replyContent = '开通会员很简单！您可以在会员中心点击"开通会员"按钮，选择支付方式完成支付即可。开通后您将享受：\n\n🌸 专属花卉养护咨询\n🌸 优先客服服务\n🌸 更多精彩内容\n\n如有疑问，欢迎继续咨询！'
      }
      // 会员价格相关
      else if (lowerMessage.includes('多少钱') || lowerMessage.includes('价格') || lowerMessage.includes('费用')) {
        replyContent = '关于会员价格，您可以在会员中心点击"开通会员"查看具体的会员价格和权益详情。我们会不定期推出优惠活动，敬请关注！'
      }
      // 退会退费相关
      else if (lowerMessage.includes('退会') || lowerMessage.includes('退费') || lowerMessage.includes('退款')) {
        replyContent = '关于退会退费：\n\n⏰ 两分钟内误开可退会退费，过期不可退\n\n如果您是在开通后两分钟内误操作，可以联系客服申请退会退费。请您理解，超过两分钟后将无法办理退费。如有特殊情况，请与客服详细沟通。'
      }
      // 会员权益相关
      else if (lowerMessage.includes('权益') || lowerMessage.includes('好处') || lowerMessage.includes('特权')) {
        replyContent = '会员权益包括：\n\n🌸 专属花卉养护咨询 - 专业的花卉养护建议\n🌸 优先客服服务 - 会员问题优先处理\n🌸 更多精彩内容 - 丰富的花卉知识和养护技巧\n\n开通会员，让您的花卉养护更轻松！'
      }
      // 账号相关
      else if (lowerMessage.includes('账号') || lowerMessage.includes('登录') || lowerMessage.includes('注册')) {
        replyContent = '关于账号问题：\n\n🔐 登录问题：请检查您的用户名和密码是否正确\n📱 注册问题：如果无法注册，请检查网络连接或联系客服\n🔑 忘记密码：可以通过"忘记密码"功能重置密码\n\n如果问题仍未解决，请描述具体问题，我们会帮您进一步处理。'
      }
      // 志愿者相关问题
      else if (lowerMessage.includes('志愿者') || lowerMessage.includes('志愿') || lowerMessage.includes('义工') || lowerMessage.includes('报名') || lowerMessage.includes('活动')) {
        replyContent = '关于志愿服务：\n\n🤝 如何成为志愿者：您可以在"志愿服务"页面点击"成为志愿者"按钮进行报名\n📋 志愿者活动：我们会不定期组织花卉养护、科普宣传等志愿活动\n🏆 志愿者福利：参与活动可获得积分奖励，积分可用于兑换礼品\n📊 排行榜：您可以在志愿服务页面查看志愿者排行榜\n\n如有更多问题，欢迎继续咨询！'
      }
      // 使用问题
      else if (lowerMessage.includes('怎么用') || lowerMessage.includes('使用') || lowerMessage.includes('操作')) {
        replyContent = '使用指南：\n\n🌷 花卉百科：浏览各种花卉的详细信息\n💬 互动社区：分享您的养花经验\n🤝 志愿服务：参与志愿活动\n💎 会员中心：开通会员，享受专属服务\n\n如果您有具体的使用问题，请告诉我，我会为您详细解答！'
      }
      // 其他常见问题
      else if (lowerMessage.includes('你好') || lowerMessage.includes('您好') || lowerMessage.includes('hi')) {
        replyContent = '您好！欢迎咨询！我可以帮您解答：\n\n📝 内容纠错问题\n💳 会员开通和退费\n🎁 会员权益\n🤝 志愿服务问题\n🔧 账号和使用问题\n\n请告诉我您需要什么帮助？'
      }
      // 默认回复
      else {
        replyContent = '感谢您的咨询！您的问题已经记录，客服人员会尽快为您处理。如果您的问题比较紧急，也可以直接联系在线客服。'
      }
      
      // 模拟系统回复
      setTimeout(() => {
        const systemMessage = {
          id: Date.now() + 1,
          content: replyContent,
          sender: 'system',
          timestamp: new Date().toLocaleTimeString()
        }
        this.otherConsultationData.messages.push(systemMessage)
        
        // 滚动到底部
        this.scrollToBottomOther()
      }, 1000)
      
      // 滚动到底部
      this.scrollToBottomOther()
    },
    // 滚动到底部（其他问题咨询）
    scrollToBottomOther() {
      setTimeout(() => {
        const chatMessages = document.querySelectorAll('.chat-messages')[1]
        if (chatMessages) {
          chatMessages.scrollTop = chatMessages.scrollHeight
        }
      }, 100)
    },
    async loadFeatureFlags() {
      try {
        const res = await featureAPI.getFeatures()
        this.paymentEnabled = res.data.paymentEnabled !== false
      } catch (e) {
        // 默认开启
      }
    },
    async loadMemberInfo() {
      if (!this.userInfo.id) return
      try {
        const res = await memberAPI.getMemberInfo(this.userInfo.id)
        const data = res.data
        const record = data.memberRecord
        if (record) {
          this.memberInfo = {
            isMember: data.isMember,
            status: record.status,
            expiryDate: record.expiryDate,
            joinDate: record.joinDate
          }
        } else {
          this.memberInfo = { isMember: false, status: null, expiryDate: null, joinDate: null }
        }
      } catch (e) {
        // ignore
      }
    },
    async loadBenefits() {
      try {
        const res = await memberAPI.getBenefits()
        if (res.data && res.data.length > 0) {
          this.benefits = res.data
        } else {
          this.benefits = this.getDefaultBenefits()
        }
      } catch (e) {
        this.benefits = this.getDefaultBenefits()
      }
    },
    getDefaultBenefits() {
      return [
        { id: 1, title: '专属花卉百科', description: '访问会员专属的花卉百科，获取更多专业的花卉养护知识。', icon: '📚', linkUrl: '/flowers' },
        { id: 2, title: '优先参与活动', description: '优先参与我们组织的花卉讲座、种植活动等线下活动。', icon: '☀️', linkUrl: '/activities' },
        { id: 3, title: '专家在线咨询', description: '享受专家在线咨询服务，解答您的花卉养护问题。', icon: '💬', linkUrl: '/consultation' },
        { id: 4, title: '会员专属优惠', description: '购买花卉相关产品时享受会员专属优惠价格。', icon: '🏷️', linkUrl: '/flowers' },
        { id: 5, title: '个人花卉档案', description: '创建个人花卉档案，记录您的养花历程和经验。', icon: '📓', linkUrl: '/profile' },
        { id: 6, title: '免费花卉种子', description: '定期收到免费的花卉种子，尝试种植不同的花卉。', icon: '🌱', linkUrl: '/activities' }
      ]
    },
    loadPrivileges() {
      // 保留兼容，benefits 已由 loadBenefits 加载
      this.privileges = this.getDefaultBenefits()
    },
    openPaymentModal(renew) {
      if (!this.isLoggedIn) {
        this.$router.push('/login')
        return
      }
      this.isRenew = renew
      this.showPaymentModal = true
    },
    async onPaymentSuccess() {
      this.$message.success('会员开通成功！')
      await this.loadMemberInfo()
      // 启动退款倒计时
      this.startRefundCountdown()
    },
    startRefundCountdown() {
      this.refundCountdown = 120
      if (this.refundTimer) clearInterval(this.refundTimer)
      this.refundTimer = setInterval(() => {
        if (this.refundCountdown > 0) {
          this.refundCountdown--
        } else {
          clearInterval(this.refundTimer)
        }
      }, 1000)
    },
    async handleRefund() {
      if (!this.userInfo.id) return
      this.$confirm('确定要退会退款吗？退款后会员权益将立即失效。', '退款确认', {
        confirmButtonText: '确定退款',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await memberAPI.refundMember(this.userInfo.id, '用户主动退款')
          const data = res.data
          if (data.success) {
            if (this.refundTimer) clearInterval(this.refundTimer)
            this.refundCountdown = 0
            this.$message.success('退款成功！')
            await this.loadMemberInfo()
          } else {
            this.$message.error(data.message || '退款失败')
          }
        } catch (e) {
          this.$message.error('退款失败，请重试')
        }
      }).catch(() => {})
    },
    async handleRefundByOrderId(orderId) {
      // PaymentModal 触发的退款（通过 orderId）
      if (!orderId) {
        await this.handleRefund()
        return
      }
      this.$confirm('确定要退款吗？退款后会员权益将立即失效。', '退款确认', {
        confirmButtonText: '确定退款',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await memberAPI.refundOrder(orderId)
          const data = res.data
          if (data.success) {
            if (this.refundTimer) clearInterval(this.refundTimer)
            this.refundCountdown = 0
            this.showPaymentModal = false
            this.$message.success('退款成功！')
            await this.loadMemberInfo()
          } else {
            this.$message.error(data.message || '退款失败')
          }
        } catch (e) {
          this.$message.error('退款失败，请重试')
        }
      }).catch(() => {})
    },
    formatDate(date) {
      if (!date) return '-'
      return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
    },
    async payForMembership() {
      // 保留兼容
      this.openPaymentModal(false)
    },
    cancelMembership() {
      this.handleRefund()
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

/* 客服服务样式 */
.customer-service-container {
  position: fixed;
  right: 20px;
  top: 200px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.service-icon {
  margin-bottom: 10px;
}

.service-icon .el-button {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 服务选项下拉菜单 */
.service-options-dropdown {
  background-color: #FFFFFF;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  padding: 8px;
  min-width: 180px;
  margin-right: 10px;
  margin-top: 5px;
  animation: dropdown-fade 0.3s ease-in-out;
  position: relative;
}

@keyframes dropdown-fade {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.service-content {
  text-align: center;
}

.service-content .service-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
  font-family: 'Arial', sans-serif;
}

.service-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.service-item {
  width: 100%;
  border-radius: 4px;
  padding: 8px 16px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
  color: #333;
  cursor: pointer;
}

.service-item:hover {
  background-color: #F5F5F5;
  color: #333;
}

.service-item.primary {
  background-color: transparent;
  color: #333;
}

.service-item.success {
  background-color: transparent;
  color: #333;
}

/* 小窗口样式 */
.small-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.small-dialog {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 650px;
  animation: dialog-fade 0.3s ease-in-out;
  position: relative;
}

@keyframes dialog-fade {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.small-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eaeaea;
}

.small-dialog-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.small-dialog-close {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.small-dialog-close:hover {
  background-color: #f5f5f5;
  color: #333;
}

.small-dialog-content {
  padding: 16px;
}

.consultation-info,
.other-consultation-info {
  margin-bottom: 16px;
}

.consultation-info p,
.other-consultation-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.small-form {
  width: 100%;
}

.form-item {
  margin-bottom: 12px;
}

.form-item label {
  display: block;
  margin-bottom: 4px;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.small-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #eaeaea;
  background-color: #f9f9f9;
  border-radius: 0 0 8px 8px;
}

.dialog-button {
  padding: 6px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
}

.dialog-button.cancel {
  background-color: white;
  color: #606266;
}

.dialog-button.cancel:hover {
  background-color: #f5f5f5;
  color: #303133;
}

.dialog-button.primary {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.dialog-button.primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

/* 聊天界面样式 */
.chat-content {
  display: flex;
  flex-direction: column;
  height: 580px;
  padding: 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-message {
  max-width: 80%;
  display: flex;
}

.chat-message.user {
  align-self: flex-end;
  justify-content: flex-end;
}

.chat-message.system {
  align-self: flex-start;
  justify-content: flex-start;
}

.message-content {
  padding: 10px 14px;
  border-radius: 18px;
  position: relative;
}

.chat-message.user .message-content {
  background-color: #409EFF;
  color: white;
  border-bottom-right-radius: 4px;
}

.chat-message.system .message-content {
  background-color: #f0f0f0;
  color: #333;
  border-bottom-left-radius: 4px;
}

.message-text {
  font-size: 16px;
  line-height: 1.7;
  word-wrap: break-word;
  white-space: pre-line;
}

.message-time {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 4px;
  text-align: right;
}

.chat-message.system .message-time {
  color: rgba(0, 0, 0, 0.5);
  text-align: left;
}

.chat-input-area {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #eaeaea;
  background-color: #f9f9f9;
}

.chat-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  resize: none;
  font-size: 14px;
  line-height: 1.4;
  min-height: 40px;
  max-height: 120px;
  overflow-y: auto;
}

.chat-input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.send-button {
  padding: 0 20px;
  border: none;
  border-radius: 20px;
  background-color: #409eff;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  align-self: flex-end;
  height: 40px;
}

.send-button:hover:not(:disabled) {
  background-color: #66b1ff;
}

.send-button:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}

.page-title {
  font-size: 32px;
  margin-bottom: 40px;
  color: #333;
  text-align: center;
  font-weight: 600;
}

.member-status {
  margin-bottom: 40px;
}

.member-info {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  font-weight: bold;
  color: #333;
  min-width: 100px;
}

.value {
  color: #666;
}

.status-member {
  color: #67C23A;
  font-weight: bold;
}

.status-non-member {
  color: #F56C6C;
}

.status-expired {
  color: #909399;
  font-weight: bold;
}

.text-warning {
  color: #E6A23C;
  font-weight: bold;
}

.refund-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff9e6;
  border: 1px solid #ffd04b;
  border-radius: 6px;
  padding: 10px 16px;
  margin: 16px 0;
  font-size: 14px;
  color: #606266;
}

.countdown-num {
  color: #F56C6C;
  font-size: 18px;
}

.member-actions {
  margin-top: 20px;
  text-align: center;
}

.non-login {
  margin-bottom: 30px;
  text-align: center;
}

.non-login p {
  margin-bottom: 20px;
  color: #666;
}

.section-title {
  font-size: 22px;
  margin-bottom: 24px;
  color: #333;
  border-left: 4px solid #409EFF;
  padding-left: 15px;
  font-weight: 600;
}

.privilege-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.privilege-item {
  display: flex;
  gap: 15px;
  padding: 20px;
}

.privilege-icon {
  flex-shrink: 0;
}

.emoji-icon {
  font-size: 48px;
  line-height: 1;
}

.privilege-content {
  flex: 1;
}

.privilege-content h3 {
  margin-bottom: 10px;
  color: #333;
}

.privilege-content p {
  line-height: 1.5;
  color: #666;
}



.dialog-content {
  text-align: center;
}

.dialog-content h3 {
  margin-bottom: 10px;
  color: #333;
}

.dialog-content .plan-price {
  font-size: 24px;
  font-weight: bold;
  color: #F56C6C;
  margin-bottom: 20px;
}

.payment-methods {
  margin-top: 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.payment-methods h4 {
  margin-bottom: 10px;
  color: #333;
  font-size: 18px !important;
}

.payment-methods >>> .el-radio__label {
  font-size: 17px !important;
}

.refund-tip {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.refund-tip >>> .el-alert {
  text-align: center;
}

.refund-tip >>> .el-alert__title {
  font-size: 16px;
}

.refund-tip >>> .el-alert__description p {
  font-size: 15px;
  margin: 0;
}

.footer-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
}

@media (max-width: 768px) {
  .member-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .privilege-list {
    grid-template-columns: 1fr;
  }
  
  .plan-list {
    grid-template-columns: 1fr;
  }
}
</style>
