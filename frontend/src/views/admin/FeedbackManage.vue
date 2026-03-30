<template>
  <div class="feedback-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-card>
      <div slot="header" class="card-header">
        <span>错误反馈管理</span>
        <div class="filter-area">
          <el-select v-model="statusFilter" placeholder="按状态筛选" clearable size="small" @change="handleFilterChange" style="width: 140px">
            <el-option label="全部" value=""></el-option>
            <el-option label="待处理" value="pending"></el-option>
            <el-option label="处理中" value="processing"></el-option>
            <el-option label="已回复" value="replied"></el-option>
            <el-option label="已完成" value="resolved"></el-option>
          </el-select>
        </div>
      </div>
      <el-table :data="feedbacks" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column label="反馈用户" width="120">
          <template slot-scope="scope">
            {{ (scope.row.user && (scope.row.user.nickname || scope.row.user.username)) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="类型" width="120">
          <template slot-scope="scope">
            {{ getTypeName(scope.row.feedbackType) }}
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="关联花卉" width="120"></el-table-column>
        <el-table-column prop="content" label="反馈内容" show-overflow-tooltip></el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="scope">
            <el-tag size="mini" :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="150">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button
              v-if="scope.row.status === 'pending'"
              type="text"
              size="mini"
              style="color: #E6A23C"
              @click="handleStartProcess(scope.row)">
              开始处理
            </el-button>
            <el-button
              v-if="scope.row.status !== 'resolved'"
              type="text"
              size="mini"
              style="color: #67C23A"
              @click="handleReply(scope.row)">
              回复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @current-change="handlePageChange"
        :current-page="pagination.page"
        :page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        style="margin-top: 16px">
      </el-pagination>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="反馈详情" :visible.sync="detailVisible" width="560px">
      <div v-if="currentFeedback" class="feedback-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="反馈ID">{{ currentFeedback.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="mini" :type="getStatusType(currentFeedback.status)">{{ getStatusName(currentFeedback.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交用户">{{ currentFeedback.user && (currentFeedback.user.nickname || currentFeedback.user.username) }}</el-descriptions-item>
          <el-descriptions-item label="错误类型">{{ getTypeName(currentFeedback.feedbackType) }}</el-descriptions-item>
          <el-descriptions-item label="关联花卉" :span="2">
            {{ currentFeedback.targetName }}
            <el-button
              v-if="currentFeedback.targetId"
              type="text"
              size="mini"
              style="margin-left: 8px"
              @click="goToFlowerEdit(currentFeedback.targetId)">
              前往修改花卉资料
            </el-button>
          </el-descriptions-item>
          <el-descriptions-item label="反馈内容" :span="2">{{ currentFeedback.content }}</el-descriptions-item>
          <el-descriptions-item label="提交时间" :span="2">{{ formatTime(currentFeedback.createTime) }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentFeedback.adminReply" class="admin-reply-block">
          <div class="reply-title">管理员回复：</div>
          <div class="reply-content">{{ currentFeedback.adminReply }}</div>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentFeedback && currentFeedback.status === 'pending'"
          type="warning"
          @click="handleStartProcess(currentFeedback); detailVisible = false">
          开始处理
        </el-button>
        <el-button
          v-if="currentFeedback && currentFeedback.status !== 'resolved'"
          type="primary"
          @click="detailVisible = false; handleReply(currentFeedback)">
          回复并完成
        </el-button>
      </span>
    </el-dialog>

    <!-- 回复对话框 -->
    <el-dialog title="回复并完成" :visible.sync="replyVisible" width="500px">
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.adminReply"
            type="textarea"
            :rows="5"
            placeholder="请填写处理结果和回复内容...">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReply" :loading="replying">提交回复</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminFeedbacks',
  data() {
    return {
      feedbacks: [],
      loading: false,
      statusFilter: '',
      detailVisible: false,
      replyVisible: false,
      replying: false,
      currentFeedback: null,
      replyForm: {
        adminReply: ''
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.loadFeedbacks()
  },
  methods: {
    async loadFeedbacks() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size
        }
        if (this.statusFilter) params.status = this.statusFilter
        const res = await adminAPI.feedback.getFeedbacks(params)
        this.feedbacks = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载反馈列表失败')
      } finally {
        this.loading = false
      }
    },
    handleFilterChange() {
      this.pagination.page = 1
      this.loadFeedbacks()
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadFeedbacks()
    },
    handleViewDetail(row) {
      this.currentFeedback = row
      this.detailVisible = true
    },
    async handleStartProcess(row) {
      try {
        await adminAPI.feedback.updateStatus(row.id, 'processing')
        this.$message.success('已标记为处理中')
        this.loadFeedbacks()
      } catch (e) {
        this.$message.error('操作失败')
      }
    },
    handleReply(row) {
      this.currentFeedback = row
      this.replyForm.adminReply = ''
      this.replyVisible = true
    },
    async handleSubmitReply() {
      if (!this.replyForm.adminReply.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      this.replying = true
      try {
        await adminAPI.feedback.reply(this.currentFeedback.id, this.replyForm.adminReply)
        this.$message.success('回复成功，反馈已标记为已完成')
        this.replyVisible = false
        this.loadFeedbacks()
      } catch (e) {
        this.$message.error('回复失败')
      } finally {
        this.replying = false
      }
    },
    goToFlowerEdit(flowerId) {
      this.$router.push(`/admin/flowers?edit=${flowerId}`)
    },
    getTypeName(type) {
      const map = {
        name_error: '名称错误',
        image_error: '图片错误',
        description_error: '描述错误',
        category_error: '分类错误',
        other_error: '其他错误',
        error_report: '错误报告',
        flower_error: '花卉资料错误',
        function_issue: '功能问题',
        content_suggestion: '内容建议',
        other: '其他问题'
      }
      return map[type] || type
    },
    getStatusName(status) {
      const map = {
        pending: '待处理',
        processing: '处理中',
        replied: '已回复',
        resolved: '已完成',
        closed: '已关闭'
      }
      return map[status] || status
    },
    getStatusType(status) {
      const map = {
        pending: 'warning',
        processing: 'info',
        replied: 'success',
        resolved: 'success',
        closed: ''
      }
      return map[status] || 'info'
    },
    formatTime(time) {
      if (!time) return '-'
      const d = new Date(time)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.feedback-detail {
  padding: 4px 0;
}

.admin-reply-block {
  margin-top: 16px;
  padding: 12px;
  background: #f0f9eb;
  border-radius: 6px;
  border-left: 4px solid #67C23A;
}

.reply-title {
  font-size: 13px;
  color: #67C23A;
  font-weight: bold;
  margin-bottom: 6px;
}

.reply-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
}
</style>
