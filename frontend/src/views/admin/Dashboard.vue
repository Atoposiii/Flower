<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon users"><i class="el-icon-user"></i></div>
          <div class="stat-info">
            <p class="stat-label">用户总数</p>
            <p class="stat-value">{{ stats.totalUsers }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon flowers"><i class="el-icon-flower"></i></div>
          <div class="stat-info">
            <p class="stat-label">花卉总数</p>
            <p class="stat-value">{{ stats.totalFlowers }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon posts"><i class="el-icon-document"></i></div>
          <div class="stat-info">
            <p class="stat-label">帖子总数</p>
            <p class="stat-value">{{ stats.totalPosts }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon members"><i class="el-icon-magic-stick"></i></div>
          <div class="stat-info">
            <p class="stat-label">会员总数</p>
            <p class="stat-value">{{ stats.totalMembers }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <div slot="header">最近会员开通</div>
          <el-table :data="recentMembers" stripe>
            <el-table-column prop="nickname" label="用户"></el-table-column>
            <el-table-column prop="startTime" label="开通时间"></el-table-column>
            <el-table-column prop="endTime" label="到期时间"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">最近反馈</div>
          <el-table :data="recentFeedbacks" stripe>
            <el-table-column label="用户">
              <template slot-scope="scope">
                {{ (scope.row.user && (scope.row.user.nickname || scope.row.user.username)) || scope.row.nickname || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="类型">
              <template slot-scope="scope">
                {{ getFeedbackTypeName(scope.row.feedbackType) }}
              </template>
            </el-table-column>
            <el-table-column label="状态">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getFeedbackStatusType(scope.row.status)">{{ getFeedbackStatusName(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {
        totalUsers: 0,
        totalFlowers: 0,
        totalPosts: 0,
        totalMembers: 0
      },
      recentMembers: [],
      recentFeedbacks: []
    }
  },
  mounted() {
    this.loadStats()
    this.loadRecentMembers()
    this.loadRecentFeedbacks()
  },
  methods: {
    async loadStats() {
      try {
        const res = await adminAPI.user.getStatistics()
        this.stats.totalUsers = res.totalUsers || 0
      } catch (error) {
        console.error('加载统计失败', error)
      }
      try {
        const res = await adminAPI.flower.getFlowers({ page: 0, size: 1 })
        this.stats.totalFlowers = res.totalElements || 0
      } catch (error) {
        console.error('加载花卉统计失败', error)
      }
      try {
        const res = await adminAPI.post.getStatistics()
        this.stats.totalPosts = res.totalPosts || 0
      } catch (error) {
        console.error('加载帖子统计失败', error)
      }
      try {
        const res = await adminAPI.member.getStatistics()
        this.stats.totalMembers = res.totalMembers || 0
      } catch (error) {
        console.error('加载会员统计失败', error)
      }
    },
    async loadRecentMembers() {
      try {
        const res = await adminAPI.member.getMembers({ page: 0, size: 5 })
        this.recentMembers = res.content || []
      } catch (error) {
        console.error('加载最近会员失败', error)
      }
    },
    async loadRecentFeedbacks() {
      try {
        const res = await adminAPI.feedback.getFeedbacks({ page: 0, size: 5 })
        this.recentFeedbacks = res.content || []
      } catch (error) {
        console.error('加载最近反馈失败', error)
      }
    },
    getFeedbackTypeName(type) {
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
    getFeedbackStatusName(status) {
      const map = {
        pending: '待处理',
        processing: '处理中',
        replied: '已回复',
        resolved: '已完成',
        closed: '已关闭'
      }
      return map[status] || status
    },
    getFeedbackStatusType(status) {
      const map = {
        pending: 'warning',
        processing: 'info',
        replied: 'success',
        resolved: 'success',
        closed: ''
      }
      return map[status] || 'info'
    }
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 20px;
}

.stat-icon.users { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.flowers { background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%); }
.stat-icon.posts { background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%); }
.stat-icon.members { background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%); }

.stat-info {
  flex: 1;
}

.stat-label {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stat-value {
  margin: 5px 0 0;
  font-size: 24px;
  font-weight: bold;
}
</style>
