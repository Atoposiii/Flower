<template>
  <div class="comment-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="评论管理" name="comments">
        <el-card>
          <div slot="header">
            <el-form :inline="true">
              <el-form-item label="关键词">
                <el-input v-model="filters.keyword" placeholder="评论内容/用户名" clearable size="small" @keyup.enter.native="handleSearch"></el-input>
              </el-form-item>
              <el-form-item label="帖子ID">
                <el-input v-model="filters.postId" placeholder="帖子ID" clearable size="small" style="width:100px" @keyup.enter.native="handleSearch"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
                <el-button size="small" @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="comments" stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="60"></el-table-column>
            <el-table-column label="评论者" width="100">
              <template slot-scope="scope">{{ scope.row.user && (scope.row.user.nickname || scope.row.user.username) || scope.row.nickname }}</template>
            </el-table-column>
            <el-table-column prop="content" label="评论内容" show-overflow-tooltip></el-table-column>
            <el-table-column label="所属帖子" width="80">
              <template slot-scope="scope">{{ scope.row.post && scope.row.post.id || scope.row.postId }}</template>
            </el-table-column>
            <el-table-column prop="likeCount" label="点赞数" width="80"></el-table-column>
            <el-table-column prop="createTime" label="评论时间" width="160"></el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="showReplies(scope.row)">查看回复</el-button>
                <el-button type="text" size="mini" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            @current-change="handlePageChange"
            :current-page="pagination.page"
            :page-size="pagination.size"
            :total="pagination.total"
            layout="total, prev, pager, next">
          </el-pagination>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="回复管理" name="replies">
        <el-card>
          <div slot="header">
            <el-form :inline="true">
              <el-form-item label="评论ID">
                <el-input v-model="replyFilters.commentId" placeholder="评论ID" clearable size="small" style="width:100px" @keyup.enter.native="loadReplies"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="small" @click="loadReplies">搜索</el-button>
                <el-button size="small" @click="replyFilters.commentId = ''; loadReplies()">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="replies" stripe v-loading="replyLoading">
            <el-table-column prop="id" label="ID" width="60"></el-table-column>
            <el-table-column label="回复者" width="100">
              <template slot-scope="scope">{{ scope.row.user && (scope.row.user.nickname || scope.row.user.username) }}</template>
            </el-table-column>
            <el-table-column prop="content" label="回复内容" show-overflow-tooltip></el-table-column>
            <el-table-column label="所属评论" width="80">
              <template slot-scope="scope">{{ scope.row.comment && scope.row.comment.id || scope.row.commentId }}</template>
            </el-table-column>
            <el-table-column prop="likeCount" label="点赞数" width="80"></el-table-column>
            <el-table-column prop="createTime" label="时间" width="160"></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" size="mini" style="color:#F56C6C" @click="handleDeleteReply(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            @current-change="handleReplyPageChange"
            :current-page="replyPagination.page"
            :page-size="replyPagination.size"
            :total="replyPagination.total"
            layout="total, prev, pager, next">
          </el-pagination>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 查看回复对话框 -->
    <el-dialog :title="`评论 #${currentComment && currentComment.id} 的回复`" :visible.sync="replyDialogVisible" width="600px">
      <el-table :data="commentReplies" stripe v-loading="commentReplyLoading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column label="回复者" width="100">
          <template slot-scope="scope">{{ scope.row.user && (scope.row.user.nickname || scope.row.user.username) }}</template>
        </el-table-column>
        <el-table-column prop="content" label="回复内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="时间" width="160"></el-table-column>
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button type="text" size="mini" style="color:#F56C6C" @click="handleDeleteReplyFromDialog(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="commentReplies.length === 0 && !commentReplyLoading" style="text-align:center;color:#909399;padding:20px">暂无回复</div>
    </el-dialog>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminComments',
  data() {
    return {
      activeTab: 'comments',
      comments: [],
      loading: false,
      filters: { keyword: '', postId: '' },
      pagination: { page: 1, size: 10, total: 0 },
      replies: [],
      replyLoading: false,
      replyFilters: { commentId: '' },
      replyPagination: { page: 1, size: 10, total: 0 },
      replyDialogVisible: false,
      commentReplies: [],
      commentReplyLoading: false,
      currentComment: null
    }
  },
  watch: {
    activeTab(tab) {
      if (tab === 'replies') this.loadReplies()
    }
  },
  mounted() {
    this.loadComments()
  },
  methods: {
    handleSearch() {
      this.pagination.page = 1
      this.loadComments()
    },
    handleReset() {
      this.filters = { keyword: '', postId: '' }
      this.pagination.page = 1
      this.loadComments()
    },
    async loadComments() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size
        }
        if (this.filters.keyword) params.keyword = this.filters.keyword
        if (this.filters.postId) params.postId = this.filters.postId
        const res = await adminAPI.comment.getComments(params)
        this.comments = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载评论列表失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadComments()
    },
    async loadReplies() {
      this.replyLoading = true
      try {
        const params = {
          page: this.replyPagination.page - 1,
          size: this.replyPagination.size
        }
        if (this.replyFilters.commentId) params.commentId = this.replyFilters.commentId
        const res = await adminAPI.reply.getReplies(params)
        this.replies = res.content || []
        this.replyPagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载回复列表失败')
      } finally {
        this.replyLoading = false
      }
    },
    handleReplyPageChange(page) {
      this.replyPagination.page = page
      this.loadReplies()
    },
    async showReplies(row) {
      this.currentComment = row
      this.replyDialogVisible = true
      this.commentReplyLoading = true
      try {
        const res = await adminAPI.reply.getCommentReplies(row.id)
        this.commentReplies = res.content || []
      } catch (error) {
        this.$message.error('加载回复失败')
      } finally {
        this.commentReplyLoading = false
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个评论吗?', '提示', { type: 'warning' })
        await adminAPI.comment.deleteComment(row.id)
        this.$message.success('删除成功')
        this.loadComments()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('删除失败')
      }
    },
    async handleDeleteReply(row) {
      try {
        await this.$confirm('确定要删除这条回复吗?', '提示', { type: 'warning' })
        await adminAPI.reply.deleteReply(row.id)
        this.$message.success('删除成功')
        this.loadReplies()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('删除失败')
      }
    },
    async handleDeleteReplyFromDialog(row) {
      try {
        await this.$confirm('确定要删除这条回复吗?', '提示', { type: 'warning' })
        await adminAPI.reply.deleteReply(row.id)
        this.$message.success('删除成功')
        this.showReplies(this.currentComment)
      } catch (error) {
        if (error !== 'cancel') this.$message.error('删除失败')
      }
    }
  }
}
</script>
