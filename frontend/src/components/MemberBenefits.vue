<template>
  <div class="member-benefits">
    <h2 class="section-title">会员权益</h2>
    <div class="benefits-grid">
      <div
        v-for="benefit in benefits"
        :key="benefit.id"
        class="benefit-card"
        :class="{ 'benefit-locked': !isMember }"
      >
        <div class="benefit-icon">
          <span class="emoji-icon">{{ benefit.icon || '🌸' }}</span>
          <span v-if="!isMember" class="lock-icon">🔒</span>
        </div>
        <div class="benefit-content">
          <h3>{{ benefit.title }}</h3>
          <p>{{ benefit.description }}</p>
        </div>
        <div class="benefit-action">
          <el-button
            v-if="benefit.linkUrl"
            size="small"
            :type="isMember ? 'primary' : 'info'"
            :plain="!isMember"
            @click="handleJump(benefit)"
          >
            {{ isMember ? '立即使用' : '开通会员' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 非会员引导弹窗 -->
    <el-dialog
      title="开通会员"
      :visible.sync="showUpgradeDialog"
      width="400px"
      center
    >
      <div class="upgrade-content">
        <p>该功能为会员专属权益，开通会员即可使用</p>
        <p class="price-hint">¥9.9 / 年，两分钟内可退款</p>
      </div>
      <span slot="footer">
        <el-button @click="showUpgradeDialog = false">取消</el-button>
        <el-button type="primary" @click="goUpgrade">立即开通</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'MemberBenefits',
  props: {
    benefits: {
      type: Array,
      default: () => []
    },
    isMember: {
      type: Boolean,
      default: false
    },
    isExpired: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      showUpgradeDialog: false
    }
  },
  methods: {
    handleJump(benefit) {
      if (!this.isMember || this.isExpired) {
        if (this.isExpired) {
          this.$message.warning('会员已到期，请续费后使用')
          this.$emit('renew')
        } else {
          this.showUpgradeDialog = true
        }
        return
      }
      if (benefit.linkUrl) {
        this.$router.push(benefit.linkUrl)
      }
    },
    goUpgrade() {
      this.showUpgradeDialog = false
      this.$emit('upgrade')
    }
  }
}
</script>

<style scoped>
.member-benefits {
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

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.benefit-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: box-shadow 0.3s;
}

.benefit-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.benefit-locked {
  opacity: 0.75;
  background: #f9f9f9;
}

.benefit-icon {
  position: relative;
  display: inline-block;
  width: 48px;
}

.emoji-icon {
  font-size: 40px;
  line-height: 1;
}

.lock-icon {
  position: absolute;
  bottom: -4px;
  right: -8px;
  font-size: 16px;
}

.benefit-content h3 {
  margin: 0 0 6px;
  font-size: 16px;
  color: #303133;
}

.benefit-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.benefit-action {
  margin-top: auto;
}

.upgrade-content {
  text-align: center;
  padding: 10px 0;
}

.upgrade-content p {
  margin: 8px 0;
  color: #606266;
}

.price-hint {
  font-size: 18px;
  font-weight: bold;
  color: #F56C6C !important;
}
</style>
