<template>
  <div class="consultation-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div slot="header">会话列表</div>
          <div
            v-for="session in sessions"
            :key="session.id"
            :class="['session-item', { active: currentSession && currentSession.id === session.id }]"
            @click="selectSession(session)">
            <p class="session-title">{{ session.title }}</p>
            <p class="session-user" v-if="session.user">
              {{ session.user.nickname || session.user.username }}
            </p>
            <p class="session-info">
              <el-tag size="mini" :type="session.channel === 'member' ? 'warning' : 'info'">
                {{ session.channel === 'member' ? '会员' : '普通' }}
              </el-tag>
              <span class="session-time">{{ formatTime(session.lastMessageTime) }}</span>
            </p>
          </div>
          <el-pagination
            small
            @current-change="handlePageChange"
            :current-page="pagination.page"
            :page-size="pagination.size"
            :total="pagination.total"
            layout="total, prev, pager, next">
          </el-pagination>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="chat-card">
          <div slot="header" v-if="currentSession">
            <span>{{ currentSession.title }}</span>
            <el-button style="float: right;" type="text" @click="handleCloseSession">关闭会话</el-button>
          </div>
          <div v-else slot="header">请选择一个会话</div>
          <div class="chat-messages" ref="chatMessages" v-if="currentSession">
            <div v-for="msg in messages" :key="msg.id" :class="['message', msg.senderType]">
              <div class="message-content">{{ msg.content }}</div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
          <div class="chat-input" v-if="currentSession && currentSession.status !== 'closed'">
            <el-input v-model="inputMessage" placeholder="请输入回复内容..." @keyup.enter.native="handleSend">
              <el-button slot="append" @click="handleSend">发送</el-button>
            </el-input>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminConsultations',
  data() {
    return {
      sessions: [],
      currentSession: null,
      messages: [],
      inputMessage: '',
      pagination: {
        page: 1,
        size: 20,
        total: 0
      },
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      sessionPollTimer: null,
      messagePollTimer: null
    }
  },
  mounted() {
    this.loadSessions()
    this.sessionPollTimer = setInterval(() => this.loadSessions(), 5000)
  },
  beforeDestroy() {
    if (this.sessionPollTimer) clearInterval(this.sessionPollTimer)
    if (this.messagePollTimer) clearInterval(this.messagePollTimer)
  },
  methods: {
    async loadSessions() {
      try {
        const res = await adminAPI.consultation.getSessions({
          page: this.pagination.page - 1,
          size: this.pagination.size
        })
        this.sessions = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载会话列表失败')
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadSessions()
    },
    async selectSession(session) {
      this.currentSession = session
      this.loadMessages()
      if (this.messagePollTimer) clearInterval(this.messagePollTimer)
      this.messagePollTimer = setInterval(() => {
        if (this.currentSession) this.loadMessages()
      }, 5000)
    },
    async loadMessages() {
      if (!this.currentSession) return
      try {
        const res = await adminAPI.consultation.getSessionMessages(this.currentSession.id)
        this.messages = res || []
        this.$nextTick(() => {
          const el = this.$refs.chatMessages
          if (el) el.scrollTop = el.scrollHeight
        })
      } catch (error) {
        console.error('加载消息失败', error)
      }
    },
    async handleSend() {
      if (!this.inputMessage.trim()) return
      try {
        await adminAPI.consultation.reply(this.currentSession.id, this.user.id, this.inputMessage)
        this.inputMessage = ''
        this.loadMessages()
      } catch (error) {
        this.$message.error('发送失败')
      }
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
    },
    async handleCloseSession() {
      try {
        await this.$confirm('确定要关闭这个会话吗?', '提示', { type: 'warning' })
        await adminAPI.consultation.closeSession(this.currentSession.id)
        this.$message.success('会话已关闭')
        this.loadSessions()
        this.currentSession = null
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
      }
    }
  }
}
</script>

<style scoped>
.session-item {
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
}
.session-item:hover, .session-item.active {
  background-color: #f5f7fa;
}
.session-title {
  margin: 0;
  font-weight: bold;
}
.session-user {
  margin: 2px 0 0;
  font-size: 12px;
  color: #606266;
}
.session-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 5px 0 0;
}
.session-time {
  font-size: 12px;
  color: #909399;
}
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
.message.admin { text-align: right; }
.message.user { text-align: left; }
.message-content {
  display: inline-block;
  padding: 10px 15px;
  border-radius: 8px;
  max-width: 70%;
}
.message.admin .message-content {
  background-color: #409EFF;
  color: white;
}
.message.user .message-content {
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
</style>
