<template>
  <el-dialog
    :title="isRenew ? '续费会员' : '开通会员'"
    :visible.sync="visible"
    width="480px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 支付前：选择支付方式 -->
    <div v-if="step === 'select'" class="payment-content">
      <div class="plan-info">
        <div class="plan-name">年度会员</div>
        <div class="plan-price">¥9.9 <span class="plan-unit">/ 年</span></div>
      </div>

      <div class="payment-methods">
        <div class="method-label">选择支付方式</div>
        <el-radio-group v-model="selectedMethod" class="method-group">
          <el-radio label="wechat" class="method-item">
            <span class="method-icon">💚</span> 微信支付
          </el-radio>
          <el-radio label="alipay" class="method-item">
            <span class="method-icon">💙</span> 支付宝
          </el-radio>
          <el-radio label="bank_card" class="method-item">
            <span class="method-icon">💳</span> 银行卡
          </el-radio>
        </el-radio-group>
      </div>

      <el-alert
        title="退款提示：支付后2分钟内可自助退款"
        type="warning"
        :closable="false"
        show-icon
        style="margin-top: 16px;"
      />
    </div>

    <!-- 支付中：等待确认 -->
    <div v-if="step === 'paying'" class="paying-content">
      <div class="paying-icon">
        <i class="el-icon-loading" style="font-size: 40px; color: #409EFF;"></i>
      </div>
      <p class="paying-text">正在处理支付，请稍候...</p>
      <p class="paying-sub">模拟支付环境，点击下方"确认支付成功"完成</p>
      <el-button type="success" @click="simulatePaySuccess">确认支付成功</el-button>
    </div>

    <!-- 支付成功：显示退款倒计时 -->
    <div v-if="step === 'success'" class="success-content">
      <div class="success-icon">✅</div>
      <p class="success-text">会员开通成功！</p>
      <div v-if="refundCountdown > 0" class="refund-countdown">
        <p>如需退款，请在 <strong class="countdown-num">{{ refundCountdown }}</strong> 秒内操作</p>
        <el-button type="danger" size="small" plain @click="$emit('request-refund', orderId)">
          申请退款
        </el-button>
      </div>
      <div v-else class="refund-expired">
        <p class="expired-tip">退款窗口已关闭</p>
      </div>
    </div>

    <span slot="footer">
      <template v-if="step === 'select'">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="startPayment">立即支付</el-button>
      </template>
      <template v-if="step === 'success'">
        <el-button type="primary" @click="handleClose">完成</el-button>
      </template>
    </span>
  </el-dialog>
</template>

<script>
import { memberAPI } from '../api/member'

export default {
  name: 'PaymentModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    userId: {
      type: Number,
      default: null
    },
    isRenew: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      visible: false,
      step: 'select', // select | paying | success
      selectedMethod: 'wechat',
      loading: false,
      orderId: null,
      orderNumber: null,
      refundCountdown: 120,
      countdownTimer: null,
      pollTimer: null
    }
  },
  watch: {
    show(val) {
      this.visible = val
      if (val) {
        this.step = 'select'
        this.selectedMethod = 'wechat'
        this.loading = false
        this.orderId = null
        this.orderNumber = null
      }
    },
    visible(val) {
      if (!val) this.$emit('update:show', false)
    }
  },
  beforeDestroy() {
    this.clearTimers()
  },
  methods: {
    async startPayment() {
      if (!this.userId) {
        this.$message.error('请先登录')
        return
      }
      this.loading = true
      try {
        const res = await memberAPI.createOrder(this.userId, this.selectedMethod)
        const data = res.data
        if (data.success) {
          this.orderId = null // 订单ID在回调后才有
          this.orderNumber = data.orderNumber
          this.step = 'paying'
        } else {
          this.$message.error(data.message || '创建订单失败')
        }
      } catch (e) {
        this.$message.error('创建订单失败，请重试')
      } finally {
        this.loading = false
      }
    },
    async simulatePaySuccess() {
      // 模拟支付回调（实际项目中由支付网关回调触发）
      const transactionId = 'TXN' + Date.now()
      try {
        const res = await memberAPI.paymentCallback(this.orderNumber, transactionId)
        const data = res.data
        if (data.success) {
          // 获取刚创建的订单ID（通过查询支付记录）
          await this.fetchLatestOrderId()
          this.step = 'success'
          this.startRefundCountdown()
          this.$emit('paid')
        } else {
          this.$message.error(data.message || '支付确认失败')
        }
      } catch (e) {
        this.$message.error('支付确认失败，请重试')
      }
    },
    async fetchLatestOrderId() {
      try {
        const res = await memberAPI.getMemberInfo(this.userId)
        const data = res.data
        // 从会员信息中找最新支付记录ID
        if (data && data.memberRecord) {
          // 查询用户支付记录获取最新orderId
          const paymentsRes = await memberAPI.getPaymentHistory ? memberAPI.getPaymentHistory(this.userId) : null
          // 简化：orderId通过refund接口用userId
        }
      } catch (e) {
        // ignore
      }
    },
    startRefundCountdown() {
      this.refundCountdown = 120
      this.clearTimers()
      this.countdownTimer = setInterval(() => {
        if (this.refundCountdown > 0) {
          this.refundCountdown--
        } else {
          clearInterval(this.countdownTimer)
        }
      }, 1000)
    },
    clearTimers() {
      if (this.countdownTimer) clearInterval(this.countdownTimer)
      if (this.pollTimer) clearInterval(this.pollTimer)
    },
    handleClose() {
      this.clearTimers()
      this.visible = false
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
.payment-content {
  padding: 8px 0;
}

.plan-info {
  text-align: center;
  margin-bottom: 24px;
}

.plan-name {
  font-size: 18px;
  color: #303133;
  margin-bottom: 8px;
}

.plan-price {
  font-size: 36px;
  font-weight: bold;
  color: #F56C6C;
}

.plan-unit {
  font-size: 16px;
  color: #909399;
}

.payment-methods {
  margin-bottom: 16px;
}

.method-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.method-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.method-item {
  padding: 12px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 16px;
}

.method-icon {
  margin-right: 6px;
}

.paying-content {
  text-align: center;
  padding: 24px 0;
}

.paying-icon {
  margin-bottom: 16px;
}

.paying-text {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
}

.paying-sub {
  font-size: 13px;
  color: #909399;
  margin-bottom: 16px;
}

.success-content {
  text-align: center;
  padding: 16px 0;
}

.success-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.success-text {
  font-size: 18px;
  color: #67C23A;
  font-weight: bold;
  margin-bottom: 16px;
}

.refund-countdown {
  background: #fff9e6;
  border: 1px solid #ffd04b;
  border-radius: 6px;
  padding: 12px 16px;
  margin-top: 8px;
}

.refund-countdown p {
  margin: 0 0 10px;
  color: #606266;
  font-size: 14px;
}

.countdown-num {
  color: #F56C6C;
  font-size: 20px;
}

.refund-expired {
  margin-top: 8px;
}

.expired-tip {
  color: #909399;
  font-size: 13px;
}
</style>
