<template>
  <div class="feedback">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 意见反馈</h2>
          <el-button @click="$router.push('/home')">返回首页</el-button>
        </div>
      </el-header>
      <el-main>
        <el-card>
          <h3>提交反馈</h3>
          <el-form :model="feedbackForm" label-width="120px">
            <el-form-item label="反馈类型">
              <el-select v-model="feedbackForm.feedbackType" placeholder="请选择反馈类型">
                <el-option label="花卉资料错误" value="flower_error"></el-option>
                <el-option label="功能问题" value="function_issue"></el-option>
                <el-option label="内容建议" value="content_suggestion"></el-option>
                <el-option label="其他问题" value="other"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关联内容" v-if="feedbackForm.feedbackType === 'flower_error'">
              <el-input v-model="feedbackForm.targetName" placeholder="请输入花卉名称"></el-input>
            </el-form-item>
            <el-form-item label="反馈内容">
              <el-input v-model="feedbackForm.content" type="textarea" :rows="5" placeholder="请详细描述您的问题或建议..."></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit">提交反馈</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card class="history-card">
          <h3>我的反馈记录</h3>
          <el-table :data="feedbackList" stripe>
            <el-table-column prop="feedbackType" label="类型">
              <template slot-scope="scope">
                {{ getTypeName(scope.row.feedbackType) }}
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间"></el-table-column>
            <el-table-column prop="adminReply" label="回复内容"></el-table-column>
          </el-table>
          <el-pagination
            @current-change="handlePageChange"
            :current-page="pagination.page"
            :page-size="pagination.size"
            :total="pagination.total"
            layout="total, prev, pager, next">
          </el-pagination>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { feedbackAPI } from '@/api/member'

export default {
  name: 'Feedback',
  data() {
    return {
      feedbackForm: {
        feedbackType: 'flower_error',
        targetType: 'flower',
        targetId: null,
        targetName: '',
        content: ''
      },
      feedbackList: [],
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      user: JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  mounted() {
    this.loadFeedbacks()
  },
  methods: {
    async loadFeedbacks() {
      if (!this.user.id) return
      try {
        const res = await feedbackAPI.getUserFeedbacks(this.user.id, {
          page: this.pagination.page - 1,
          size: this.pagination.size
        })
        if (res.content) {
          this.feedbackList = res.content
          this.pagination.total = res.totalElements
        }
      } catch (error) {
        console.error('加载反馈记录失败', error)
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadFeedbacks()
    },
    getTypeName(type) {
      const map = {
        'flower_error': '花卉资料错误',
        'function_issue': '功能问题',
        'content_suggestion': '内容建议',
        'other': '其他问题'
      }
      return map[type] || type
    },
    getStatusType(status) {
      const map = {
        'pending': 'warning',
        'processing': 'info',
        'replied': 'success',
        'closed': ''
      }
      return map[status] || 'info'
    },
    async handleSubmit() {
      if (!this.user.id) {
        this.$message.warning('请先登录')
        return
      }
      if (!this.feedbackForm.content.trim()) {
        this.$message.warning('请输入反馈内容')
        return
      }
      try {
        await feedbackAPI.createFeedback(
          this.user.id,
          this.feedbackForm.feedbackType,
          this.feedbackForm.targetType,
          this.feedbackForm.targetId,
          this.feedbackForm.targetName,
          this.feedbackForm.content
        )
        this.$message.success('反馈已提交')
        this.feedbackForm = {
          feedbackType: 'flower_error',
          targetType: 'flower',
          targetId: null,
          targetName: '',
          content: ''
        }
        this.loadFeedbacks()
      } catch (error) {
        this.$message.error('提交失败')
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
.history-card { margin-top: 20px; }
</style>
