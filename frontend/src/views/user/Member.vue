<template>
  <div class="member-page">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 会员中心</h2>
          <el-button @click="$router.push('/home')">返回首页</el-button>
        </div>
      </el-header>
      <el-main>
        <!-- 未登录 -->
        <div v-if="!currentUser.id" class="not-logged-in">
          <el-result icon="info" title="请先登录" sub-title="登录后即可查看会员信息、开通会员">
            <template slot="extra">
              <el-button type="primary" @click="$router.push('/login')">立即登录</el-button>
            </template>
          </el-result>
        </div>

        <el-row v-else :gutter="24">
          <!-- 左侧：会员状态卡片 -->
          <el-col :span="16">
            <!-- 到期提醒 -->
            <el-alert
              v-if="isMemberActive && daysUntilExpiry !== null && daysUntilExpiry <= 7"
              :title="`会员将于 ${daysUntilExpiry} 天后到期，请及时续费！`"
              type="warning"
              :closable="false"
              show-icon
              style="margin-bottom: 16px;"
            />

            <!-- 退款倒计时横幅 -->
            <div v-if="refundCountdown > 0" class="refund-banner">
              <span>支付成功！如需退款，请在 <strong class="countdown-num">{{ refundCountdown }}</strong> 秒内操作</span>
              <el-button type="danger" size="small" plain @click="handleRefund">申请退款</el-button>
            </div>

            <!-- 未申请 -->
            <el-card v-if="!hasApplied">
              <div class="member-intro">
                <h3>申请成为会员</h3>
                <p class="price">¥9.9 <span>/ 年</span></p>
                <ul class="benefits-list">
                  <li><i class="el-icon-check"></i> 查看完整养护信息</li>
                  <li><i class="el-icon-check"></i> 会员专属活动参与权</li>
                  <li><i class="el-icon-check"></i> 会员专属客服咨询通道</li>
                  <li><i class="el-icon-check"></i> 志愿者活动满员时优先参加</li>
                  <li><i class="el-icon-check"></i> 更多会员专属权益...</li>
                </ul>
                <el-form :model="applyForm" label-width="100px" style="margin-top:20px;text-align:left">
                  <el-form-item label="真实姓名">
                    <el-input v-model="applyForm.realName" placeholder="请输入真实姓名"></el-input>
                  </el-form-item>
                  <el-form-item label="联系电话">
                    <el-input v-model="applyForm.phone" placeholder="请输入联系电话"></el-input>
                  </el-form-item>
                  <el-form-item label="申请原因">
                    <el-input v-model="applyForm.reason" type="textarea" :rows="2" placeholder="请简述申请原因（选填）"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="warning" size="large" @click="handleApply">提交申请</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-card>

            <!-- 待审核 -->
            <el-card v-else-if="memberStatus === 'pending'">
              <el-result icon="info" title="申请审核中" sub-title="您的会员申请已提交，请等待管理员审核" />
            </el-card>

            <!-- 已拒绝 -->
            <el-card v-else-if="memberStatus === 'cancelled' && !isMemberActive">
              <el-result icon="error" title="申请未通过" sub-title="您的会员申请未通过审核，如有疑问请联系管理员" />
            </el-card>

            <!-- 审核通过但未付费 -->
            <el-card v-else-if="memberStatus === 'approved_unpaid'">
              <el-result icon="success" title="审核已通过！">
                <template slot="extra">
                  <p style="color:#606266;margin-bottom:16px;">完成支付后即可激活会员权益</p>
                  <el-button
                    type="primary"
                    size="large"
                    :disabled="!paymentEnabled"
                    @click="showPaymentModal = true"
                  >{{ paymentEnabled ? '立即支付 ¥9.9' : '支付功能维护中' }}</el-button>
                </template>
              </el-result>
            </el-card>

            <!-- 有效会员 -->
            <el-card v-else-if="isMemberActive">
              <h3>我的会员</h3>
              <el-descriptions :column="2" border style="margin-top:16px;">
                <el-descriptions-item label="会员状态">
                  <el-tag type="success">有效</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="剩余天数">
                  <span :class="{ 'text-warning': daysUntilExpiry <= 7 }">{{ daysUntilExpiry }} 天</span>
                </el-descriptions-item>
                <el-descriptions-item label="开通时间">{{ memberRecord.joinDate | formatDate }}</el-descriptions-item>
                <el-descriptions-item label="到期时间">{{ memberRecord.expiryDate | formatDate }}</el-descriptions-item>
              </el-descriptions>
              <div class="member-actions">
                <el-button
                  type="primary"
                  :disabled="!paymentEnabled"
                  @click="openRenewModal"
                >续费会员</el-button>
                <el-button
                  v-if="refundCountdown <= 0"
                  type="text"
                  style="color:#909399;font-size:12px;"
                  @click="handleContactServiceRefund"
                >联系客服退款</el-button>
              </div>
            </el-card>

            <!-- 已过期/已退款 -->
            <el-card v-else-if="memberStatus === 'refunded' || memberStatus === 'expired'">
              <el-result icon="warning" :title="memberStatus === 'refunded' ? '已退款' : '会员已过期'">
                <template slot="extra">
                  <el-button type="warning" @click="hasApplied = false">重新申请</el-button>
                </template>
              </el-result>
            </el-card>

            <!-- 会员权益展示（始终显示） -->
            <el-card class="benefits-card" style="margin-top:20px;">
              <h3>会员权益</h3>
              <div class="benefits-grid">
                <div
                  v-for="benefit in benefitsList"
                  :key="benefit.id"
                  class="benefit-item"
                  :class="{ 'benefit-locked': !isMemberActive }"
                  @click="handleBenefitClick(benefit)"
                >
                  <div class="benefit-icon-wrap">
                    <span class="benefit-emoji">{{ benefit.icon || '🌸' }}</span>
                    <span v-if="!isMemberActive" class="lock-badge">🔒</span>
                  </div>
                  <h4>{{ benefit.title }}</h4>
                  <p>{{ benefit.description }}</p>
                  <el-button
                    v-if="benefit.linkUrl"
                    size="mini"
                    :type="isMemberActive ? 'primary' : 'info'"
                    plain
                    style="margin-top:8px;"
                  >{{ isMemberActive ? '立即使用' : '开通会员' }}</el-button>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 右侧：会须知 + 开通入口 -->
          <el-col :span="8">
            <el-card>
              <h4>会员须知</h4>
              <ul class="rules">
                <li>申请后需等待管理员审核</li>
                <li>审核通过后完成支付激活会员</li>
                <li>会员有效期为1年</li>
                <li><strong>支付后2分钟内可自助退款</strong></li>
                <li>志愿者与会员身份可同时持有</li>
              </ul>
            </el-card>

            <el-card v-if="!isMemberActive" style="margin-top:16px;text-align:center;">
              <div class="upgrade-card">
                <p class="upgrade-title">开通年度会员</p>
                <p class="upgrade-price">¥9.9 <span>/ 年</span></p>
                <el-button
                  type="warning"
                  size="large"
                  style="width:100%;margin-top:12px;"
                  :disabled="!paymentEnabled"
                  @click="handleUpgradeClick"
                >{{ paymentEnabled ? '立即开通' : '功能维护中' }}</el-button>
                <p class="refund-note">支付后2分钟内可退款</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>

    <!-- 支付弹窗 -->
    <el-dialog
      :title="isRenew ? '续费会员' : '开通会员'"
      :visible.sync="showPaymentModal"
      width="460px"
      :close-on-click-modal="false"
      @close="onPaymentDialogClose"
    >
      <!-- Step 1: 选择支付方式 -->
      <div v-if="payStep === 'select'" class="pay-dialog-body">
        <div class="plan-box">
          <div class="plan-name">年度会员</div>
          <div class="plan-price">¥9.9 <span class="plan-unit">/ 年</span></div>
        </div>
        <div class="method-section">
          <p class="method-label">选择支付方式</p>
          <el-radio-group v-model="selectedPayMethod" class="method-group">
            <el-radio label="wechat" class="method-radio">💚 微信支付</el-radio>
            <el-radio label="alipay" class="method-radio">💙 支付宝</el-radio>
            <el-radio label="bank_card" class="method-radio">💳 银行卡</el-radio>
          </el-radio-group>
        </div>
        <el-alert
          title="支付后2分钟内可自助退款，过期不可退"
          type="warning"
          :closable="false"
          show-icon
          style="margin-top:16px;"
        />
      </div>

      <!-- Step 2: 模拟支付中 -->
      <div v-if="payStep === 'paying'" class="pay-dialog-body" style="text-align:center;padding:24px 0;">
        <i class="el-icon-loading" style="font-size:40px;color:#409EFF;"></i>
        <p style="margin:16px 0 8px;font-size:16px;">正在处理支付...</p>
        <p style="color:#909399;font-size:13px;margin-bottom:20px;">模拟支付环境，点击下方按钮完成支付</p>
        <el-button type="success" @click="simulatePaySuccess">确认支付成功</el-button>
      </div>

      <!-- Step 3: 支付成功 + 退款倒计时 -->
      <div v-if="payStep === 'success'" class="pay-dialog-body" style="text-align:center;padding:16px 0;">
        <div style="font-size:48px;margin-bottom:12px;">✅</div>
        <p style="font-size:18px;font-weight:bold;color:#67C23A;margin-bottom:16px;">会员开通成功！</p>
        <div v-if="dialogCountdown > 0" class="refund-tip-box">
          <p>如需退款，请在 <strong class="countdown-num">{{ dialogCountdown }}</strong> 秒内点击退款</p>
          <el-button type="danger" size="small" plain @click="handleRefundFromDialog">申请退款</el-button>
        </div>
        <div v-else>
          <p style="color:#909399;font-size:13px;">退款窗口已关闭</p>
        </div>
      </div>

      <span slot="footer">
        <template v-if="payStep === 'select'">
          <el-button @click="showPaymentModal = false">取消</el-button>
          <el-button type="primary" :loading="payLoading" @click="startPayment">立即支付</el-button>
        </template>
        <template v-if="payStep === 'success'">
          <el-button type="primary" @click="showPaymentModal = false">完成</el-button>
        </template>
      </span>
    </el-dialog>

    <!-- 非会员引导弹窗 -->
    <el-dialog title="开通会员" :visible.sync="showUpgradeGuide" width="380px" center>
      <div style="text-align:center;padding:10px 0;">
        <p>该功能为会员专属权益</p>
        <p class="upgrade-price" style="margin:12px 0;">¥9.9 <span style="font-size:14px;color:#909399;">/ 年</span></p>
        <p style="color:#909399;font-size:13px;">支付后2分钟内可退款</p>
      </div>
      <span slot="footer">
        <el-button @click="showUpgradeGuide = false">取消</el-button>
        <el-button type="primary" @click="goToPayFromGuide">立即开通</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api/request'
import { memberAPI, featureAPI } from '@/api/member'

export default {
  name: 'Member',
  filters: {
    formatDate(val) {
      if (!val) return '-'
      return new Date(val).toLocaleDateString('zh-CN')
    }
  },
  data() {
    return {
      // 会员状态
      hasApplied: false,
      memberStatus: '',
      memberRecord: {},
      // 支付弹窗
      showPaymentModal: false,
      isRenew: false,
      payStep: 'select', // select | paying | success
      selectedPayMethod: 'wechat',
      payLoading: false,
      currentOrderNumber: null,
      currentOrderId: null,
      // 退款倒计时（页面横幅）
      refundCountdown: 0,
      refundTimer: null,
      // 弹窗内倒计时
      dialogCountdown: 0,
      dialogTimer: null,
      // 功能开关
      paymentEnabled: true,
      // 权益列表
      benefitsList: [],
      // 非会员引导弹窗
      showUpgradeGuide: false,
      // 申请表单
      applyForm: { realName: '', phone: '', reason: '' }
    }
  },
  computed: {
    currentUser() {
      return JSON.parse(localStorage.getItem('user') || '{}')
    },
    isMemberActive() {
      if (this.memberStatus !== 'active') return false
      if (!this.memberRecord.expiryDate) return true
      return new Date(this.memberRecord.expiryDate) > new Date()
    },
    daysUntilExpiry() {
      if (!this.memberRecord.expiryDate) return null
      const diff = new Date(this.memberRecord.expiryDate) - new Date()
      return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
    }
  },
  mounted() {
    this.loadMemberStatus()
    this.loadBenefits()
    this.loadFeatureFlags()
  },
  beforeDestroy() {
    if (this.refundTimer) clearInterval(this.refundTimer)
    if (this.dialogTimer) clearInterval(this.dialogTimer)
  },
  methods: {
    async loadFeatureFlags() {
      try {
        const res = await featureAPI.getFeatures()
        this.paymentEnabled = res.paymentEnabled !== false
      } catch (e) { /* 默认开启 */ }
    },
    async loadMemberStatus() {
      if (!this.currentUser.id) return
      try {
        const res = await api.get('/member/user/status', { params: { userId: this.currentUser.id } })
        const data = res.data || res
        this.hasApplied = data.hasApplied
        if (data.hasApplied) {
          this.memberStatus = data.status
          this.memberRecord = data
        }
      } catch (e) {
        this.hasApplied = false
      }
    },
    async loadBenefits() {
      try {
        const res = await memberAPI.getBenefits()
        const list = Array.isArray(res) ? res : (res.data || res)
        if (list && list.length > 0) {
          this.benefitsList = list
        } else {
          this.benefitsList = this.defaultBenefits()
        }
      } catch (e) {
        this.benefitsList = this.defaultBenefits()
      }
    },
    defaultBenefits() {
      return [
        { id: 1, title: '花卉百科', description: '查看花卉完整养护教程', icon: '📚', linkUrl: '/flowers' },
        { id: 2, title: '专属活动', description: '参与会员专属志愿活动', icon: '☀️', linkUrl: '/activities' },
        { id: 3, title: '专属客服', description: '享受优先客服响应', icon: '💬', linkUrl: '/consultation' },
        { id: 4, title: '优先参加', description: '活动满员时优先参加', icon: '⭐', linkUrl: '/activities' },
        { id: 5, title: '个人档案', description: '创建个人花卉档案', icon: '📓', linkUrl: '/profile' },
        { id: 6, title: '免费种子', description: '定期收到免费花卉种子', icon: '🌱', linkUrl: '/activities' }
      ]
    },
    handleBenefitClick(benefit) {
      if (!this.isMemberActive) {
        this.showUpgradeGuide = true
        return
      }
      if (benefit.linkUrl) {
        this.$router.push(benefit.linkUrl)
      }
    },
    handleUpgradeClick() {
      if (!this.hasApplied) {
        this.$message.info('请先提交会员申请，审核通过后即可付费')
        return
      }
      this.isRenew = false
      this.payStep = 'select'
      this.showPaymentModal = true
    },
    goToPayFromGuide() {
      this.showUpgradeGuide = false
      this.handleUpgradeClick()
    },
    openRenewModal() {
      this.isRenew = true
      this.payStep = 'select'
      this.showPaymentModal = true
    },
    async handleApply() {
      if (!this.currentUser.id) {
        this.$message.warning('请先登录')
        return
      }
      if (!this.applyForm.realName) {
        this.$message.warning('请填写真实姓名')
        return
      }
      try {
        await api.post('/member/user/apply', {
          userId: this.currentUser.id,
          ...this.applyForm
        })
        this.$message.success('会员申请已提交，请等待管理员审核')
        this.loadMemberStatus()
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '申请失败')
      }
    },
    async startPayment() {
      this.payLoading = true
      console.log('[支付] startPayment 开始, userId:', this.currentUser.id, 'isRenew:', this.isRenew, 'payMethod:', this.selectedPayMethod)
      try {
        const res = await memberAPI.createOrder(this.currentUser.id, this.selectedPayMethod)
        console.log('[支付] createOrder 响应:', JSON.stringify(res))
        if (res.success) {
          this.currentOrderNumber = res.orderNumber
          this.currentOrderId = res.orderId
          console.log('[支付] 订单创建成功, orderNumber:', this.currentOrderNumber, 'orderId:', this.currentOrderId)
          this.payStep = 'paying'
        } else {
          console.warn('[支付] createOrder 失败:', res.message)
          this.$message.error(res.message || '创建订单失败')
        }
      } catch (e) {
        console.error('[支付] createOrder 异常:', e)
        this.$message.error('创建订单失败，请重试')
      } finally {
        this.payLoading = false
      }
    },
    async simulatePaySuccess() {
      const transactionId = 'TXN' + Date.now()
      console.log('[支付] simulatePaySuccess, orderNumber:', this.currentOrderNumber, 'orderId:', this.currentOrderId, 'transactionId:', transactionId)
      try {
        const res = await memberAPI.paymentCallback(this.currentOrderNumber, transactionId)
        console.log('[支付] paymentCallback 响应:', JSON.stringify(res))
        if (res.success) {
          console.log('[支付] 支付成功, 进入 success 步骤, currentOrderId:', this.currentOrderId)
          this.payStep = 'success'
          this.startDialogCountdown()
          this.startRefundCountdown()
          await this.loadMemberStatus()
        } else {
          console.warn('[支付] paymentCallback 失败:', res.message)
          this.$message.error(res.message || '支付确认失败')
        }
      } catch (e) {
        console.error('[支付] paymentCallback 异常:', e)
        this.$message.error('支付确认失败，请重试')
      }
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
    startDialogCountdown() {
      this.dialogCountdown = 120
      if (this.dialogTimer) clearInterval(this.dialogTimer)
      this.dialogTimer = setInterval(() => {
        if (this.dialogCountdown > 0) {
          this.dialogCountdown--
        } else {
          clearInterval(this.dialogTimer)
        }
      }, 1000)
    },
    async handleRefund() {
      console.log('[退款] handleRefund 触发, currentOrderId:', this.currentOrderId, 'currentOrderNumber:', this.currentOrderNumber, 'memberStatus:', this.memberStatus, 'refundCountdown:', this.refundCountdown)
      try {
        await this.$confirm('确定要退款吗？退款后会员权益将立即失效。', '退款确认', {
          confirmButtonText: '确定退款',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (e) {
        console.log('[退款] 用户取消确认')
        return // user cancelled
      }
      console.log('[退款] 用户确认退款, currentOrderId:', this.currentOrderId)
      if (!this.currentOrderId) {
        console.error('[退款] currentOrderId 为空，无法退款。当前组件状态:', {
          currentOrderId: this.currentOrderId,
          currentOrderNumber: this.currentOrderNumber,
          isRenew: this.isRenew,
          payStep: this.payStep,
          memberStatus: this.memberStatus
        })
        this.$message.error('未找到订单信息，请重新支付')
        return
      }
      try {
        console.log('[退款] 调用 refundOrder, orderId:', this.currentOrderId)
        const res = await memberAPI.refundOrder(this.currentOrderId)
        console.log('[退款] refundOrder 响应:', JSON.stringify(res))
        if (res.success) {
          if (this.refundTimer) clearInterval(this.refundTimer)
          this.refundCountdown = 0
          this.$message.success('退款成功！')
          await this.loadMemberStatus()
        } else {
          console.warn('[退款] refundOrder 失败:', res.message)
          this.$message.error(res.message || '退款失败')
        }
      } catch (e) {
        console.error('[退款] refundOrder 异常:', e)
        this.$message.error('退款失败，请重试')
      }
    },
    async handleRefundFromDialog() {
      await this.handleRefund()
      this.showPaymentModal = false
    },
    onPaymentDialogClose() {
      if (this.payStep === 'success') {
        // 关闭弹窗时退款倒计时继续在页面横幅显示
      }
    },
    async handleCancelMember() {
      try {
        await this.$confirm('确定要申请退会吗？', '申请退会', { type: 'warning' })
        await api.post(`/members/cancel/${this.currentUser.id}`, null, { params: { reason: 'user_request' } })
        this.$message.success('退会申请已提交')
        this.loadMemberStatus()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
      }
    },
    async handleContactServiceRefund() {
      try {
        const { value: reason } = await this.$prompt(
          '请说明退款原因，客服将在24小时内处理您的申请',
          '申请客服退款',
          {
            confirmButtonText: '提交申请',
            cancelButtonText: '取消',
            inputPlaceholder: '请输入退款原因（必填）',
            inputValidator: (val) => val && val.trim() ? true : '请输入退款原因'
          }
        )
        await api.post('/admin/consultation/member-refund-request', null, {
          params: {
            userId: this.currentUser.id,
            reason: reason.trim()
          }
        })
        this.$message.success('退款申请已提交，客服将在24小时内处理')
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '提交失败，请重试')
        }
      }
    }
  },
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
.logo { color: #67C23A; }

.not-logged-in {
  max-width: 600px;
  margin: 60px auto;
}

.member-intro { text-align: center; }
.price {
  font-size: 48px;
  color: #E6A23C;
  margin: 20px 0;
}
.price span { font-size: 16px; color: #909399; }

.benefits-list { list-style: none; text-align: left; margin: 20px 0; padding: 0; }
.benefits-list li { padding: 8px 0; color: #666; }
.benefits-list li i { color: #67C23A; margin-right: 10px; }

.member-actions { margin-top: 20px; display: flex; gap: 10px; }

.refund-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff9e6;
  border: 1px solid #ffd04b;
  border-radius: 6px;
  padding: 10px 16px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}
.countdown-num {
  color: #F56C6C;
  font-size: 18px;
}
.text-warning { color: #E6A23C; font-weight: bold; }

/* 权益网格 */
.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
  margin-top: 16px;
}
.benefit-item {
  text-align: center;
  padding: 16px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.benefit-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.benefit-locked { opacity: 0.7; background: #f9f9f9; }
.benefit-icon-wrap { position: relative; display: inline-block; margin-bottom: 8px; }
.benefit-emoji { font-size: 36px; }
.lock-badge {
  position: absolute;
  bottom: -4px;
  right: -10px;
  font-size: 14px;
}
.benefit-item h4 { margin: 6px 0 4px; font-size: 14px; color: #303133; }
.benefit-item p { font-size: 12px; color: #909399; margin: 0; }

/* 右侧升级卡片 */
.upgrade-card { padding: 8px 0; }
.upgrade-title { font-size: 16px; font-weight: bold; color: #303133; margin-bottom: 8px; }
.upgrade-price { font-size: 36px; font-weight: bold; color: #F56C6C; }
.refund-note { font-size: 12px; color: #909399; margin-top: 8px; }

/* 规则 */
.rules { list-style: none; padding: 0; }
.rules li { padding: 8px 0; color: #666; font-size: 14px; border-bottom: 1px solid #f0f0f0; }
.rules li:last-child { border-bottom: none; }

/* 支付弹窗 */
.pay-dialog-body { padding: 8px 0; }
.plan-box { text-align: center; margin-bottom: 20px; }
.plan-name { font-size: 16px; color: #303133; margin-bottom: 6px; }
.plan-price { font-size: 36px; font-weight: bold; color: #F56C6C; }
.plan-unit { font-size: 14px; color: #909399; }
.method-label { font-size: 14px; color: #606266; margin-bottom: 10px; }
.method-group { display: flex; flex-direction: column; gap: 10px; }
.method-radio {
  padding: 10px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 15px;
}
.refund-tip-box {
  background: #fff9e6;
  border: 1px solid #ffd04b;
  border-radius: 6px;
  padding: 12px 16px;
  display: inline-block;
}
.refund-tip-box p { margin: 0 0 10px; font-size: 14px; color: #606266; }
</style>
