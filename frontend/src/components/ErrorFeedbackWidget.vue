<template>
  <div class="ef-widget" v-if="visible">
    <!-- 浮动按钮 -->
    <div class="ef-fab" @click="togglePanel" :class="{ active: panelOpen }">
      <i class="el-icon-warning-outline"></i>
      <span class="fab-label">错误反馈</span>
    </div>

    <!-- 反馈面板 -->
    <div class="ef-panel" v-if="panelOpen">
      <div class="ef-panel-header">
        <span>错误反馈</span>
        <i class="el-icon-close close-btn" @click="panelOpen = false"></i>
      </div>

      <!-- Tab 切换：提交反馈 / 我的反馈 -->
      <div class="ef-tabs">
        <span :class="['tab', { active: activeTab === 'submit' }]" @click="activeTab = 'submit'">提交反馈</span>
        <span :class="['tab', { active: activeTab === 'history' }]" @click="switchToHistory">我的反馈</span>
      </div>

      <!-- 提交反馈表单 -->
      <div class="ef-form" v-if="activeTab === 'submit'">
        <el-form :model="form" label-width="80px" size="small">
          <el-form-item label="错误类型">
            <el-select v-model="form.errorType" placeholder="请选择错误类型" style="width: 100%">
              <el-option label="花卉名称错误" value="name_error"></el-option>
              <el-option label="图片错误" value="image_error"></el-option>
              <el-option label="资料描述错误" value="description_error"></el-option>
              <el-option label="分类信息错误" value="category_error"></el-option>
              <el-option label="其他错误" value="other_error"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="错误描述">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述您发现的错误内容...">
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitFeedback" :loading="submitting" style="width: 100%">
              提交反馈
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 历史反馈列表 -->
      <div class="ef-history" v-if="activeTab === 'history'">
        <div v-if="historyLoading" class="loading-tip">
          <i class="el-icon-loading"></i> 加载中...
        </div>
        <div v-else-if="historyList.length === 0" class="empty-tip">
          暂无反馈记录
        </div>
        <div v-else class="history-list">
          <div v-for="item in historyList" :key="item.id" class="history-item">
            <div class="history-item-header">
              <span class="error-type-tag">{{ getTypeName(item.feedbackType) }}</span>
              <el-tag size="mini" :type="getStatusType(item.status)">{{ getStatusName(item.status) }}</el-tag>
            </div>
            <div class="history-content">{{ item.content }}</div>
            <div class="history-time">{{ formatTime(item.createTime) }}</div>
            <div class="admin-reply" v-if="item.adminReply">
              <div class="reply-label">管理员回复：</div>
              <div class="reply-content">{{ item.adminReply }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { feedbackAPI } from '@/api/member'

export default {
  name: 'ErrorFeedbackWidget',
  props: {
    flowerId: {
      type: Number,
      required: true
    },
    flowerName: {
      type: String,
      default: ''
    },
    visible: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      panelOpen: false,
      activeTab: 'submit',
      form: {
        errorType: '',
        description: ''
      },
      submitting: false,
      historyList: [],
      historyLoading: false,
      user: JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  methods: {
    togglePanel() {
      if (!this.user.id) {
        this.$message.warning('请先登录后再提交反馈')
        return
      }
      this.panelOpen = !this.panelOpen
    },
    async switchToHistory() {
      this.activeTab = 'history'
      await this.loadHistory()
    },
    async loadHistory() {
      if (!this.user.id) return
      this.historyLoading = true
      try {
        const res = await feedbackAPI.getUserFlowerFeedbacks(this.user.id, this.flowerId)
        this.historyList = res.content || []
      } catch (e) {
        this.$message.error('加载反馈记录失败')
      } finally {
        this.historyLoading = false
      }
    },
    async submitFeedback() {
      if (!this.user.id) {
        this.$message.warning('请先登录后再提交反馈')
        return
      }
      if (!this.form.description.trim()) {
        this.$message.warning('请填写错误描述')
        return
      }
      this.submitting = true
      try {
        await feedbackAPI.createFeedback(
          this.user.id,
          this.form.errorType || 'other_error',
          'flower',
          this.flowerId,
          this.flowerName,
          this.form.description
        )
        this.$message.success('反馈已提交，感谢您的贡献')
        this.form.errorType = ''
        this.form.description = ''
        this.panelOpen = false
      } catch (e) {
        if (e.response && e.response.status === 429) {
          this.$message.error('今日对该花卉的反馈次数已达上限')
        } else {
          this.$message.error('提交失败，请稍后重试')
        }
      } finally {
        this.submitting = false
      }
    },
    getTypeName(type) {
      const map = {
        name_error: '名称错误',
        image_error: '图片错误',
        description_error: '描述错误',
        category_error: '分类错误',
        other_error: '其他错误',
        error_report: '错误报告'
      }
      return map[type] || type
    },
    getStatusName(status) {
      const map = {
        pending: '待处理',
        processing: '处理中',
        resolved: '已完成',
        replied: '已回复',
        closed: '已关闭'
      }
      return map[status] || status
    },
    getStatusType(status) {
      const map = {
        pending: 'warning',
        processing: 'info',
        resolved: 'success',
        replied: 'success',
        closed: ''
      }
      return map[status] || 'info'
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.ef-widget {
  position: fixed;
  right: 20px;
  bottom: 30px;
  z-index: 1000;
}

.ef-fab {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #F56C6C;
  color: #fff;
  padding: 10px 16px;
  border-radius: 24px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
  transition: all 0.2s;
  font-size: 14px;
}

.ef-fab:hover, .ef-fab.active {
  background-color: #c0392b;
  box-shadow: 0 6px 16px rgba(245, 108, 108, 0.5);
}

.ef-fab i {
  font-size: 18px;
}

.ef-panel {
  position: absolute;
  bottom: 56px;
  right: 0;
  width: 320px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.ef-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #F56C6C;
  color: #fff;
  font-weight: bold;
}

.close-btn {
  cursor: pointer;
  font-size: 16px;
}

.ef-tabs {
  display: flex;
  border-bottom: 1px solid #eee;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.2s;
}

.tab.active {
  color: #F56C6C;
  border-bottom: 2px solid #F56C6C;
  font-weight: bold;
}

.ef-form {
  padding: 16px;
}

.ef-history {
  padding: 12px;
  max-height: 360px;
  overflow-y: auto;
}

.loading-tip, .empty-tip {
  text-align: center;
  color: #909399;
  font-size: 13px;
  padding: 20px 0;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
}

.history-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.error-type-tag {
  font-size: 12px;
  color: #606266;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}

.history-content {
  font-size: 13px;
  color: #303133;
  line-height: 1.5;
  margin-bottom: 4px;
}

.history-time {
  font-size: 11px;
  color: #c0c4cc;
}

.admin-reply {
  margin-top: 8px;
  padding: 8px;
  background: #f0f9eb;
  border-radius: 6px;
  border-left: 3px solid #67C23A;
}

.reply-label {
  font-size: 11px;
  color: #67C23A;
  font-weight: bold;
  margin-bottom: 4px;
}

.reply-content {
  font-size: 13px;
  color: #303133;
  line-height: 1.5;
}
</style>
