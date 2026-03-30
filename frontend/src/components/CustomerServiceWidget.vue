<template>
  <div class="cs-widget" v-if="visible">
    <!-- 浮动按钮 -->
    <div class="cs-fab" @click="togglePanel" :class="{ active: panelOpen }">
      <i class="el-icon-service"></i>
      <span class="fab-label">客服咨询</span>
    </div>

    <!-- 咨询面板 -->
    <div class="cs-panel" v-if="panelOpen">
      <div class="cs-panel-header">
        <span>客服咨询</span>
        <i class="el-icon-close close-btn" @click="panelOpen = false"></i>
      </div>

      <!-- 频道选择 -->
      <div class="cs-channel-select" v-if="!currentChannel">
        <div class="channel-item" @click="selectChannel('non-member')">
          <i class="el-icon-chat-line-round"></i>
          <div>
            <div class="channel-name">非会员频道</div>
            <div class="channel-desc">普通用户咨询通道</div>
          </div>
        </div>
        <div class="channel-item" :class="{ locked: !isMember }" @click="selectChannel('member')">
          <i :class="isMember ? 'el-icon-star-on' : 'el-icon-lock'"></i>
          <div>
            <div class="channel-name">会员频道</div>
            <div class="channel-desc" v-if="isMember">会员专属，优先响应</div>
            <div class="channel-desc locked-desc" v-else>开通会员后可使用</div>
          </div>
        </div>
      </div>

      <!-- 对话界面 -->
      <div class="cs-chat" v-if="currentChannel">
        <div class="chat-header">
          <i class="el-icon-arrow-left back-btn" @click="currentChannel = null; currentSessionId = null; messages = []"></i>
          <el-tag size="mini" :type="currentChannel === 'member' ? 'warning' : 'info'">
            {{ currentChannel === 'member' ? '会员频道' : '非会员频道' }}
          </el-tag>
        </div>

        <div class="chat-messages" ref="chatMessages">
          <div v-if="messages.length === 0" class="empty-msg">暂无消息，发送第一条消息开始咨询</div>
          <div v-for="msg in messages" :key="msg.id" :class="['message', msg.senderType]">
            <div class="message-bubble">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
        </div>

        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            placeholder="请输入消息..."
            size="small"
            @keyup.enter.native="sendMessage">
            <el-button slot="append" size="small" @click="sendMessage" :loading="sending">发送</el-button>
          </el-input>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { consultationAPI, memberAPI } from '@/api/member'

export default {
  name: 'CustomerServiceWidget',
  props: {
    flowerId: {
      type: Number,
      required: true
    },
    visible: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      panelOpen: false,
      currentChannel: null,
      currentSessionId: null,
      messages: [],
      inputMessage: '',
      sending: false,
      isMember: false,
      pollTimer: null,
      user: JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  mounted() {
    this.checkMemberStatus()
  },
  beforeDestroy() {
    this.stopPolling()
  },
  methods: {
    togglePanel() {
      console.log('[CS] togglePanel, user:', this.user)
      if (!this.user.id) {
        this.$message.warning('请先登录后再使用客服咨询')
        return
      }
      this.panelOpen = !this.panelOpen
      console.log('[CS] panelOpen:', this.panelOpen)
    },
    async checkMemberStatus() {
      console.log('[CS] checkMemberStatus, user.id:', this.user.id)
      if (!this.user.id) return
      try {
        const res = await memberAPI.isMember(this.user.id)
        console.log('[CS] isMember 结果:', res)
        this.isMember = res === true
      } catch (e) {
        console.error('[CS] isMember 失败:', e)
        this.isMember = false
      }
    },
    async selectChannel(channel) {
      if (channel === 'member' && !this.isMember) {
        this.$message.warning('请先开通会员后再使用会员频道')
        return
      }
      this.currentChannel = channel
      console.log('[CS] 选择频道:', channel, '花卉ID:', this.flowerId, '用户ID:', this.user.id)
      await this.loadOrCreateSession()
    },
    async loadOrCreateSession() {
      try {
        console.log('[CS] 查询已有会话 flowerId:', this.flowerId, 'userId:', this.user.id, 'channel:', this.currentChannel)
        const res = await consultationAPI.getUserFlowerSessions(this.flowerId, this.user.id, this.currentChannel)
        console.log('[CS] 查询会话结果:', res)
        const sessions = res.content || []
        const openSession = sessions.find(s => s.status === 'open')
        if (openSession) {
          this.currentSessionId = openSession.id
          console.log('[CS] 使用已有会话 sessionId:', this.currentSessionId)
        } else {
          const title = `花卉咨询-${this.flowerId}-${this.currentChannel === 'member' ? '会员' : '普通'}`
          console.log('[CS] 创建新会话 title:', title)
          const session = await consultationAPI.createFlowerSession(this.user.id, this.currentChannel, this.flowerId, title)
          console.log('[CS] 创建会话结果:', session)
          this.currentSessionId = session.id
          console.log('[CS] 新会话 sessionId:', this.currentSessionId)
        }
        await this.loadMessages()
        this.startPolling()
      } catch (e) {
        console.error('[CS] loadOrCreateSession 失败:', e, e.response)
        if (e.response && e.response.status === 403) {
          this.$message.error('请先开通会员')
          this.currentChannel = null
        } else {
          this.$message.error('加载会话失败: ' + (e.response?.data?.error || e.message || '未知错误'))
        }
      }
    },
    async loadMessages() {
      if (!this.currentSessionId) {
        console.warn('[CS] loadMessages: currentSessionId 为空，跳过')
        return
      }
      console.log('[CS] 加载消息 sessionId:', this.currentSessionId)
      try {
        const res = await consultationAPI.getMessages(this.currentSessionId)
        console.log('[CS] 消息列表:', res)
        this.messages = res || []
        this.$nextTick(() => {
          const el = this.$refs.chatMessages
          if (el) el.scrollTop = el.scrollHeight
        })
      } catch (e) {
        console.error('[CS] loadMessages 失败:', e, e.response)
      }
    },
    async sendMessage() {
      console.log('[CS] sendMessage 触发, inputMessage:', JSON.stringify(this.inputMessage), 'currentSessionId:', this.currentSessionId)
      if (!this.inputMessage.trim()) {
        console.warn('[CS] 消息为空，不发送')
        return
      }
      if (!this.currentSessionId) {
        console.warn('[CS] currentSessionId 为空，无法发送，尝试重新创建会话')
        this.$message.warning('会话未就绪，请重新选择频道')
        return
      }
      this.sending = true
      try {
        console.log('[CS] 调用 sendMessage API sessionId:', this.currentSessionId, 'userId:', this.user.id, 'content:', this.inputMessage)
        const res = await consultationAPI.sendMessage(this.currentSessionId, this.user.id, 'user', this.inputMessage)
        console.log('[CS] 发送成功:', res)
        this.inputMessage = ''
        await this.loadMessages()
      } catch (e) {
        console.error('[CS] 发送消息失败:', e, e.response)
        this.$message.error('发送失败: ' + (e.response?.data?.error || e.message || '请检查控制台'))
      } finally {
        this.sending = false
      }
    },
    startPolling() {
      this.stopPolling()
      this.pollTimer = setInterval(() => {
        if (this.currentSessionId && this.panelOpen && this.currentChannel) {
          this.loadMessages()
        }
      }, 5000)
    },
    stopPolling() {
      if (this.pollTimer) {
        clearInterval(this.pollTimer)
        this.pollTimer = null
      }
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.cs-widget {
  position: fixed;
  right: 20px;
  bottom: 100px;
  z-index: 1000;
}

.cs-fab {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #409EFF;
  color: #fff;
  padding: 10px 16px;
  border-radius: 24px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.2s;
  font-size: 14px;
}

.cs-fab:hover, .cs-fab.active {
  background-color: #337ecc;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.5);
}

.cs-fab i {
  font-size: 18px;
}

.cs-panel {
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

.cs-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #409EFF;
  color: #fff;
  font-weight: bold;
}

.close-btn {
  cursor: pointer;
  font-size: 16px;
}

.cs-channel-select {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.channel-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.channel-item:hover:not(.locked) {
  border-color: #409EFF;
  background: #ecf5ff;
}

.channel-item.locked {
  opacity: 0.6;
  cursor: not-allowed;
  background: #f5f7fa;
}

.channel-item i {
  font-size: 24px;
  color: #409EFF;
}

.channel-item.locked i {
  color: #909399;
}

.channel-name {
  font-weight: bold;
  font-size: 14px;
}

.channel-desc {
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
}

.locked-desc {
  color: #E6A23C;
}

.cs-chat {
  display: flex;
  flex-direction: column;
  height: 380px;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-bottom: 1px solid #eee;
}

.back-btn {
  cursor: pointer;
  font-size: 18px;
  color: #409EFF;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.empty-msg {
  text-align: center;
  color: #909399;
  font-size: 13px;
  margin-top: 20px;
}

.message {
  display: flex;
  flex-direction: column;
}

.message.user {
  align-items: flex-end;
}

.message.admin {
  align-items: flex-start;
}

.message-bubble {
  max-width: 75%;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-word;
}

.message.user .message-bubble {
  background: #409EFF;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message.admin .message-bubble {
  background: #f0f2f5;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 3px;
}

.chat-input {
  padding: 10px;
  border-top: 1px solid #eee;
}
</style>
