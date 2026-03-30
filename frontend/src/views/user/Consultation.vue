<template>
  <div class="consultation">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 客服咨询</h2>
          <el-button @click="$router.push('/home')">返回首页</el-button>
        </div>
      </el-header>
      <el-main>
        <el-row :gutter="20">
          <el-col :span="16">
            <el-card class="chat-card">
              <div slot="header">
                <span>与客服对话</span>
              </div>
              <div class="chat-messages" ref="chatMessages">
                <div v-for="msg in messages" :key="msg.id" :class="['message', msg.senderType]">
                  <div class="message-content">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.createTime }}</div>
                </div>
              </div>
              <div class="chat-input">
                <el-input v-model="inputMessage" placeholder="请输入消息..." @keyup.enter.native="handleSend">
                  <el-button slot="append" @click="handleSend">发送</el-button>
                </el-input>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <h4>选择咨询频道</h4>
              <el-radio-group v-model="channel" @change="handleChannelChange">
                <el-radio label="normal">非会员频道</el-radio>
                <el-radio label="member" :disabled="!isMember">会员频道</el-radio>
              </el-radio-group>
              <p class="channel-tip" v-if="channel === 'member'">会员专属通道，优先响应</p>
            </el-card>
            <el-card class="history-card">
              <h4>历史会话</h4>
              <el-list>
                <el-list-item v-for="session in sessions" :key="session.id" @click.native="loadSession(session.id)">
                  <span>{{ session.title }}</span>
                  <span class="session-time">{{ session.updateTime }}</span>
                </el-list-item>
              </el-list>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { consultationAPI } from '@/api/member'
import { memberAPI } from '@/api/member'

export default {
  name: 'Consultation',
  data() {
    return {
      channel: 'normal',
      isMember: false,
      currentSessionId: null,
      sessions: [],
      messages: [],
      inputMessage: '',
      user: JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  mounted() {
    this.checkMemberStatus()
    this.loadSessions()
    this.createNewSession()
  },
  methods: {
    async checkMemberStatus() {
      if (!this.user.id) return
      try {
        const res = await memberAPI.isMember(this.user.id)
        this.isMember = res === true
      } catch (error) {
        this.isMember = false
      }
    },
    async createNewSession() {
      if (!this.user.id) {
        this.$message.warning('请先登录')
        return
      }
      try {
        const title = this.channel === 'member' ? '会员咨询-' + new Date().toLocaleString() : '普通咨询-' + new Date().toLocaleString()
        const res = await consultationAPI.createSession(this.user.id, this.channel, title)
        this.currentSessionId = res.id || res
        this.loadMessages()
      } catch (error) {
        console.error('创建会话失败', error)
      }
    },
    async loadSessions() {
      if (!this.user.id) return
      try {
        const res = await consultationAPI.getUserSessions(this.user.id, { page: 0, size: 20 })
        this.sessions = res.content || []
      } catch (error) {
        console.error('加载会话失败', error)
      }
    },
    async loadSession(sessionId) {
      this.currentSessionId = sessionId
      this.loadMessages()
    },
    async loadMessages() {
      if (!this.currentSessionId) return
      try {
        const res = await consultationAPI.getMessages(this.currentSessionId)
        this.messages = res || []
      } catch (error) {
        console.error('加载消息失败', error)
      }
    },
    handleChannelChange() {
      this.createNewSession()
    },
    async handleSend() {
      if (!this.inputMessage.trim()) return
      if (!this.currentSessionId) {
        this.$message.warning('请先创建会话')
        return
      }
      try {
        await consultationAPI.sendMessage(
          this.currentSessionId,
          this.user.id,
          'user',
          this.inputMessage
        )
        this.inputMessage = ''
        this.loadMessages()
      } catch (error) {
        this.$message.error('发送失败')
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
.logo { color: #67C23A; }
.chat-card {
  height: 500px;
  display: flex;
  flex-direction: column;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}
.message {
  margin-bottom: 15px;
}
.message.user {
  text-align: right;
}
.message.admin {
  text-align: left;
}
.message-content {
  display: inline-block;
  padding: 10px 15px;
  border-radius: 8px;
  max-width: 70%;
}
.message.user .message-content {
  background-color: #409EFF;
  color: white;
}
.message.admin .message-content {
  background-color: #f0f0f0;
  color: #333;
}
.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
.chat-input {
  padding: 10px;
  border-top: 1px solid #eee;
}
.channel-tip {
  color: #E6A23C;
  font-size: 12px;
  margin-top: 10px;
}
.history-card {
  margin-top: 20px;
}
.session-time {
  font-size: 12px;
  color: #909399;
  float: right;
}
</style>
