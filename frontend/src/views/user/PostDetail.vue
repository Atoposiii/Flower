<template>
  <div class="post-detail">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">帖子详情</h2>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </el-header>
      <el-main>
        <!-- 评论区 -->
        <el-card class="comment-section">
          <h3>评论 ({{ comments.length }})</h3>

          <!-- 发表评论 -->
          <div v-if="currentUser.id" class="comment-form">
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
            ></el-input>
            <el-button
              type="primary"
              style="margin-top:10px"
              :loading="submittingComment"
              @click="submitComment"
            >发表评论</el-button>
          </div>
          <p v-else class="login-tip">
            <el-button type="text" @click="$router.push('/login')">登录</el-button>后才能发表评论
          </p>

          <!-- 评论列表 -->
          <div v-loading="commentsLoading" class="comment-list">
            <div v-if="comments.length === 0 && !commentsLoading" class="empty-tip">暂无评论，来发表第一条吧</div>

            <el-card v-for="comment in comments" :key="comment.id" class="comment-item">
              <div v-if="comment.status === 'deleted'" class="deleted-tip">该评论已删除</div>
              <template v-else>
                <div class="comment-header">
                  <strong>{{ comment.nickname }}</strong>
                  <span class="comment-time">{{ comment.createTime | formatDate }}</span>
                  <el-button
                    v-if="currentUser.id && comment.userId === currentUser.id"
                    type="text" size="mini" class="delete-btn"
                    @click="deleteComment(comment.id)"
                  >删除</el-button>
                </div>
                <p class="comment-content">{{ comment.content }}</p>
                <div class="comment-actions">
                  <el-button
                    :type="comment.liked ? 'primary' : 'text'"
                    size="mini"
                    @click="toggleCommentLike(comment)"
                  >点赞 ({{ comment.likeCount || 0 }})</el-button>
                  <el-button type="text" size="mini" @click="openReplyForm(comment.id)">回复</el-button>
                </div>

                <!-- 回复输入框 -->
                <div v-if="replyingTo === comment.id" class="reply-form">
                  <el-input
                    v-model="newReply"
                    placeholder="写下你的回复..."
                    size="small"
                  ></el-input>
                  <el-button
                    type="primary" size="mini"
                    style="margin-left:8px"
                    :loading="submittingReply"
                    @click="submitReply(comment.id)"
                  >发送</el-button>
                  <el-button size="mini" @click="replyingTo = null">取消</el-button>
                </div>
              </template>

              <!-- 二级回复 -->
              <div v-if="comment.replies && comment.replies.length" class="replies">
                <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                  <div v-if="reply.status === 'deleted'" class="deleted-tip small">该回复已删除</div>
                  <template v-else>
                    <div class="reply-header">
                      <strong>{{ reply.nickname }}:</strong>
                      <span class="reply-content">{{ reply.content }}</span>
                      <span class="reply-time">{{ reply.createTime | formatDate }}</span>
                    </div>
                    <div class="reply-actions">
                      <el-button
                        :type="reply.liked ? 'primary' : 'text'"
                        size="mini"
                        @click="toggleReplyLike(reply)"
                      >点赞 ({{ reply.likeCount || 0 }})</el-button>
                      <el-button
                        v-if="currentUser.id && reply.userId === currentUser.id"
                        type="text" size="mini" class="delete-btn"
                        @click="deleteReply(reply.id)"
                      >删除</el-button>
                    </div>
                  </template>
                </div>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { postAPI, commentAPI, replyAPI } from '@/api/post'

export default {
  name: 'PostDetail',
  filters: {
    formatDate(value) {
      if (!value) return ''
      const d = new Date(value)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    }
  },
  data() {
    return {
      postId: null,
      post: null,
      postLoading: true,
      currentUser: JSON.parse(localStorage.getItem('user') || '{}'),

      comments: [],
      commentsLoading: false,

      newComment: '',
      submittingComment: false,

      replyingTo: null,
      newReply: '',
      submittingReply: false
    }
  },
  computed: {
    isAuthor() {
      return this.currentUser.id && this.post && this.post.userId === this.currentUser.id
    }
  },
  async mounted() {
    this.postId = parseInt(this.$route.params.id)
    await this.loadPost()
    this.loadComments()
  },
  methods: {
    async loadPost() {
      this.postLoading = true
      try {
        this.post = await postAPI.getPostDetail(this.postId, this.currentUser.id || null)
      } catch (e) {
        this.$message.error('加载帖子失败')
      } finally {
        this.postLoading = false
      }
    },

    async loadComments() {
      this.commentsLoading = true
      try {
        const res = await commentAPI.getCommentsTree(this.postId, this.currentUser.id || null)
        this.comments = Array.isArray(res) ? res : []
      } catch (e) {
        this.$message.error('加载评论失败')
      } finally {
        this.commentsLoading = false
      }
    },

    async submitComment() {
      const content = this.newComment.trim()
      if (!content) {
        this.$message.warning('请输入评论内容')
        return
      }
      this.submittingComment = true
      try {
        await commentAPI.createComment(this.postId, this.currentUser.id, content)
        this.newComment = ''
        this.$message.success('评论成功')
        await this.loadComments()
        if (this.post) this.post.commentCount = (this.post.commentCount || 0) + 1
      } catch (e) {
        this.$message.error('评论失败，请重试')
      } finally {
        this.submittingComment = false
      }
    },

    async deleteComment(commentId) {
      try {
        await this.$confirm('确定删除该评论?', '提示', { type: 'warning' })
        await commentAPI.deleteComment(commentId, this.currentUser.id)
        this.$message.success('删除成功')
        await this.loadComments()
        if (this.post) this.post.commentCount = Math.max(0, (this.post.commentCount || 1) - 1)
      } catch (e) {
        if (e !== 'cancel') this.$message.error('删除失败')
      }
    },

    async toggleCommentLike(comment) {
      if (!this.currentUser.id) { this.$message.warning('请先登录'); return }
      try {
        if (comment.liked) {
          await commentAPI.unlikeComment(comment.id, this.currentUser.id)
          comment.liked = false
          comment.likeCount = Math.max(0, (comment.likeCount || 1) - 1)
        } else {
          await commentAPI.likeComment(comment.id, this.currentUser.id)
          comment.liked = true
          comment.likeCount = (comment.likeCount || 0) + 1
        }
      } catch (e) {
        this.$message.error(e?.response?.data?.message || '操作失败')
      }
    },

    openReplyForm(commentId) {
      if (!this.currentUser.id) { this.$message.warning('请先登录'); return }
      this.replyingTo = commentId
      this.newReply = ''
    },

    async submitReply(commentId) {
      const content = this.newReply.trim()
      if (!content) {
        this.$message.warning('请输入回复内容')
        return
      }
      this.submittingReply = true
      try {
        await replyAPI.createReply(commentId, this.currentUser.id, content)
        this.replyingTo = null
        this.newReply = ''
        this.$message.success('回复成功')
        await this.loadComments()
      } catch (e) {
        this.$message.error('回复失败，请重试')
      } finally {
        this.submittingReply = false
      }
    },

    async toggleReplyLike(reply) {
      if (!this.currentUser.id) { this.$message.warning('请先登录'); return }
      try {
        if (reply.liked) {
          await replyAPI.unlikeReply(reply.id, this.currentUser.id)
          reply.liked = false
          reply.likeCount = Math.max(0, (reply.likeCount || 1) - 1)
        } else {
          await replyAPI.likeReply(reply.id, this.currentUser.id)
          reply.liked = true
          reply.likeCount = (reply.likeCount || 0) + 1
        }
      } catch (e) {
        this.$message.error(e?.response?.data?.message || '操作失败')
      }
    },

    async deleteReply(replyId) {
      try {
        await this.$confirm('确定删除该回复?', '提示', { type: 'warning' })
        await replyAPI.deleteReply(replyId, this.currentUser.id)
        this.$message.success('删除成功')
        await this.loadComments()
      } catch (e) {
        if (e !== 'cancel') this.$message.error('删除失败')
      }
    },

    async handleLike() {
      if (!this.currentUser.id) { this.$message.warning('请先登录'); return }
      try {
        if (this.post.liked) {
          await postAPI.unlikePost(this.post.id, this.currentUser.id)
          this.post.liked = false
          this.post.likeCount = Math.max(0, (this.post.likeCount || 1) - 1)
        } else {
          await postAPI.likePost(this.post.id, this.currentUser.id)
          this.post.liked = true
          this.post.likeCount = (this.post.likeCount || 0) + 1
        }
      } catch (e) {
        this.$message.error(e?.response?.data?.message || '操作失败')
      }
    },

    async handleDelete() {
      try {
        await this.$confirm('确定要删除这个帖子吗?', '提示', { type: 'warning' })
        await postAPI.deletePost(this.post.id, this.currentUser.id)
        this.$message.success('删除成功')
        this.$router.push('/community')
      } catch (e) {
        if (e !== 'cancel') this.$message.error('删除失败')
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
.post-meta { display: flex; gap: 20px; color: #909399; margin-top: 10px; flex-wrap: wrap; }
.post-content { margin: 15px 0; line-height: 1.8; white-space: pre-wrap; }
.post-actions { margin-top: 20px; display: flex; gap: 10px; }
.comment-section { margin-top: 20px; }
.comment-form { margin-bottom: 20px; }
.login-tip { color: #909399; margin-bottom: 20px; }
.empty-tip { color: #C0C4CC; text-align: center; padding: 20px 0; }
.comment-list { margin-top: 15px; }
.comment-item { margin-bottom: 15px; }
.comment-header { display: flex; gap: 10px; align-items: center; }
.comment-time { color: #909399; font-size: 12px; margin-left: auto; }
.delete-btn { color: #F56C6C !important; }
.comment-content { margin: 8px 0 5px; }
.comment-actions { display: flex; gap: 5px; }
.reply-form { margin-top: 10px; display: flex; align-items: center; }
.replies { margin-top: 10px; padding-left: 20px; border-left: 3px solid #eee; }
.reply-item { padding: 6px 0; font-size: 14px; }
.reply-header { display: flex; align-items: baseline; flex-wrap: wrap; gap: 6px; }
.reply-content { color: #333; }
.reply-time { color: #909399; font-size: 12px; }
.reply-actions { display: flex; gap: 5px; margin-top: 2px; }
.deleted-tip { color: #C0C4CC; font-style: italic; padding: 4px 0; }
.deleted-tip.small { font-size: 12px; }
</style>
