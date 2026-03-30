<template>
  <div class="community">
    <Navbar />
    <div class="container">
      <div style="margin-bottom: 16px;">
        <el-button size="small" @click="$router.back()"><i class="el-icon-back"></i> 返回</el-button>
      </div>
      <h1 class="page-title">互动社区</h1>

      <!-- 发布帖子按钮 -->
      <div class="post-actions">
        <el-button type="primary" size="large" @click="showPostDialog = true">发布帖子</el-button>
      </div>
      
      <!-- 帖子列表 -->
      <div class="post-list">
        <el-card v-for="post in posts" :key="post.id" class="post-item">
          <div class="post-header">
            <div class="user-info">
              <div>
                <h4>{{ post.user.name || post.user.username }}</h4>
                <p class="post-time">{{ formatDate(post.createTime) }}</p>
              </div>
            </div>
          </div>
          <div class="post-content">
            <h3>{{ post.title }}</h3>
            <p>{{ post.content }}</p>
          </div>
          <div class="post-footer">
            <div class="post-stats">
              <el-button 
                type="text" 
                @click="likePost(post.id)"
                :class="{ 'liked-button': isLiked(post.likedBy) }"
              >
                <i class="el-icon-thumb"></i> {{ post.likeCount }}
              </el-button>
              <el-button type="text" @click="viewComments(post.id)">
                <i class="el-icon-chat-dot-round"></i> {{ post.commentCount }}
              </el-button>
              <el-button type="text">
                <i class="el-icon-view"></i> {{ post.viewCount }}
              </el-button>
            </div>
            <div>
              <el-button type="primary" size="small" @click="viewPostDetail(post.id)">查看详情</el-button>
              <el-button 
                v-if="post.user.id === currentUserId" 
                type="danger" 
                size="small" 
                @click="deletePost(post.id)"
              >
                删除
              </el-button>
            </div>
          </div>
          
          <!-- 评论区 -->
          <div class="comments-section" v-if="showComments === post.id">
            <h4>热门评论 (点赞最高前五名)</h4>
            <div class="comment-form">
              <el-input
                type="textarea"
                :rows="2"
                placeholder="写下你的评论..."
                v-model="commentContent"
              ></el-input>
              <el-button type="primary" size="small" @click="addComment(post.id)">发表评论</el-button>
            </div>
            <div class="comment-list">
              <div v-for="comment in getSortedComments(post.comments)" :key="comment.id" class="comment-item">
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-user">{{ comment.user.name || comment.user.username }}</span>
                    <div class="comment-actions">
                      <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                      <el-button 
                        type="text" 
                        size="small" 
                        @click="likeComment(post.id, comment.id)"
                        :class="{ 'liked-button': isLiked(comment.likedBy) }"
                      >
                        <i class="el-icon-thumb"></i> {{ comment.likeCount || 0 }}
                      </el-button>
                      <el-button 
                        type="text" 
                        size="small" 
                        @click="toggleReply(post.id, comment.id)"
                      >
                        回复
                      </el-button>
                      <el-button 
                        v-if="comment.user.id === currentUserId" 
                        type="text" 
                        size="small" 
                        @click="deleteComment(post.id, comment.id)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                  <p>{{ comment.content }}</p>
                  
                  <!-- 回复输入框 -->
                  <div v-if="activeReply.postId === post.id && activeReply.commentId === comment.id" class="reply-form">
                    <el-input
                      type="textarea"
                      :rows="2"
                      placeholder="回复评论..."
                      v-model="replyContent"
                    ></el-input>
                    <div class="reply-actions">
                      <el-button type="text" size="small" @click="cancelReply">取消</el-button>
                      <el-button type="primary" size="small" @click="addReply(post.id, comment.id)">回复</el-button>
                    </div>
                  </div>
                  
                  <!-- 回复列表 -->
                  <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                    <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                      <div class="reply-content">
                        <div class="reply-header">
                          <span class="reply-user">{{ reply.user.name || reply.user.username }}</span>
                          <div class="reply-actions">
                            <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
                            <span style="font-size:10px;color:#ff6b6b;margin-right:5px;display:none;">[回复ID:{{reply.user.id}} | 您的ID:{{currentUserId}}]</span>
                            <el-button 
                              v-if="reply.user.id === currentUserId" 
                              type="text" 
                              size="small" 
                              @click="deleteReply(post.id, comment.id, reply.id)"
                            >
                              删除
                            </el-button>
                          </div>
                        </div>
                        <p>{{ reply.content }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="total"
        />
        <div class="page-info">
          共 {{ totalPages }} 页
        </div>
      </div>
      
      <!-- 发布帖子对话框 -->
      <el-dialog
        title="发布帖子"
        :visible.sync="showPostDialog"
        width="600px"
        top="10vh"
      >
        <div style="margin-bottom:20px;padding:10px;background:#f5f7fa;border-radius:4px;">
          <span>发布者：</span>
          <strong style="color:#409EFF;">{{ currentUsername }}</strong>
        </div>
        <el-form :model="postForm" label-width="70px">
          <el-form-item label="标题">
            <el-input v-model="postForm.title" placeholder="请输入帖子标题" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input
              type="textarea"
              :rows="6"
              placeholder="请输入帖子内容"
              v-model="postForm.content"
            ></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showPostDialog = false">取消</el-button>
          <el-button type="primary" @click="createPost">发布</el-button>
        </span>
      </el-dialog>

      <!-- 帖子详情对话框 -->
      <el-dialog
        :title="currentPost ? currentPost.title : '帖子详情'"
        :visible.sync="showPostDetailDialog"
        width="700px"
        top="10vh"
      >
        <div v-if="currentPost" class="post-detail">
          <div class="post-detail-header">
            <div class="user-info">
              <div>
                <h3>{{ currentPost.user.name || currentPost.user.username }}</h3>
                <p class="post-time">{{ formatDate(currentPost.createTime) }}</p>
              </div>
            </div>
            <div class="post-detail-stats">
              <span><i class="el-icon-view"></i> {{ currentPost.viewCount }}</span>
              <el-button 
                type="text" 
                @click="likePost(currentPost.id)"
                :class="{ 'liked-button': isLiked(currentPost.likedBy) }"
                style="padding: 0; margin: 0;"
              >
                <i class="el-icon-thumb"></i> {{ currentPost.likeCount }}
              </el-button>
              <span><i class="el-icon-chat-dot-round"></i> {{ currentPost.commentCount }}</span>
            </div>
          </div>
          <div class="post-detail-content">
            <pre style="white-space: pre-wrap; font-family: inherit; margin: 0;">{{ currentPost.content }}</pre>
          </div>
          
          <!-- 详情页评论区 -->
          <div class="detail-comments-section">
            <h4>热门评论 (点赞最高前五名)</h4>
            <div class="comment-form">
              <el-input
                type="textarea"
                :rows="2"
                placeholder="写下你的评论..."
                v-model="commentContent"
              ></el-input>
              <el-button type="primary" size="small" @click="addComment(currentPost.id)">发表评论</el-button>
            </div>
            <div class="comment-list">
              <div v-for="comment in getSortedComments(currentPost.comments)" :key="comment.id" class="comment-item">
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-user">{{ comment.user.name || comment.user.username }}</span>
                    <div class="comment-actions">
                      <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                      <el-button 
                        type="text" 
                        size="small" 
                        @click="likeComment(currentPost.id, comment.id)"
                        :class="{ 'liked-button': isLiked(comment.likedBy) }"
                      >
                        <i class="el-icon-thumb"></i> {{ comment.likeCount || 0 }}
                      </el-button>
                      <el-button 
                        type="text" 
                        size="small" 
1                        @click="toggleReply(currentPost.id, comment.id)"
                      >
                        回复
                      </el-button>
                      <el-button 
                        v-if="comment.user.id === currentUserId" 
                        type="text" 
                        size="small" 
                        @click="deleteComment(currentPost.id, comment.id)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                  <p>{{ comment.content }}</p>
                  
                  <!-- 回复输入框 -->
                  <div v-if="activeReply.postId === currentPost.id && activeReply.commentId === comment.id" class="reply-form">
                    <el-input
                      type="textarea"
                      :rows="2"
                      placeholder="回复评论..."
                      v-model="replyContent"
                    ></el-input>
                    <div class="reply-actions">
                      <el-button type="text" size="small" @click="cancelReply">取消</el-button>
                      <el-button type="primary" size="small" @click="addReply(currentPost.id, comment.id)">回复</el-button>
                    </div>
                  </div>
                  
                  <!-- 回复列表 -->
                  <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                    <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                      <div class="reply-content">
                        <div class="reply-header">
                          <span class="reply-user">{{ reply.user.name || reply.user.username }}</span>
                          <div class="reply-actions">
                            <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
                            <span style="font-size:10px;color:#ff6b6b;margin-right:5px;display:none;">[回复ID:{{reply.user.id}} | 您的ID:{{currentUserId}}]</span>
                            <el-button 
                              v-if="reply.user.id === currentUserId" 
                              type="text" 
                              size="small" 
                              @click="deleteReply(currentPost.id, comment.id, reply.id)"
                            >
                              删除
                            </el-button>
                          </div>
                        </div>
                        <p>{{ reply.content }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="currentPost.comments.length === 0" class="no-comments">
                暂无评论，快来抢沙发吧！
              </div>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button v-if="currentPost && currentPost.user.id === currentUserId" type="danger" @click="deletePost(currentPost.id); showPostDetailDialog = false">删除</el-button>
          <el-button @click="showPostDetailDialog = false">关闭</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Navbar from '../components/Navbar.vue'
import postApi from '../api/modules/post'

export default {
  components: {
    Navbar
  },
  data() {
    return {
      posts: [],
      originalPosts: [],
      currentPage: 1,
      pageSize: 5,
      total: 0,
      showPostDialog: false,
      postForm: {
        title: '',
        content: ''
      },
      showComments: null,
      commentContent: '',
      showPostDetailDialog: false,
      currentPost: null,
      defaultAvatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar&image_size=square',
      activeReply: {
        postId: null,
        commentId: null
      },
      replyContent: ''
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    },
    userInfo() {
      return this.$store.state.user.userInfo || {}
    },
    isLoggedIn() {
      return this.$store.state.user.isLoggedIn
    },
    currentUsername() {
      if (this.userInfo) {
        return this.userInfo.name || this.userInfo.nickname || this.userInfo.username || '当前用户'
      }
      return '当前用户'
    },
    currentUserId() {
      if (this.userInfo) {
        return this.userInfo.id || 1
      }
      return 1
    }
  },
  watch: {
    userInfo: {
      handler(newVal, oldVal) {
        if (newVal && oldVal && newVal.name !== oldVal.name) {
          this.updateUserNameInPosts(oldVal.id, newVal.name)
        }
      },
      deep: true
    }
  },
  mounted() {
    // 加载帖子列表
    this.loadPosts()
  },
  methods: {
    updateUserNameInPosts(userId, newName) {
      // 更新所有帖子中的用户名
      this.originalPosts.forEach(post => {
        // 更新帖子作者名
        if (post.user.id === userId) {
          post.user.name = newName
        }
        // 更新评论中的用户名
        if (post.comments) {
          post.comments.forEach(comment => {
            if (comment.user.id === userId) {
              comment.user.name = newName
            }
            // 更新回复中的用户名
            if (comment.replies) {
              comment.replies.forEach(reply => {
                if (reply.user.id === userId) {
                  reply.user.name = newName
                }
              })
            }
          })
        }
      })
      // 更新当前页显示
      this.applyPagination()
      this.savePostsToLocalStorage()
    },
    savePostsToLocalStorage() {
      localStorage.setItem('flowerPosts', JSON.stringify(this.originalPosts))
    },
    async loadPosts() {
      try {
        const res = await postApi.getAllPosts()
        const postsArray = Array.isArray(res) ? res : []
        this.originalPosts = postsArray.map(post => ({
          ...post,
          user: post.user || { id: post.userId || 1, name: '用户' + (post.userId || 1), username: 'user' + (post.userId || 1) },
          viewCount: post.viewCount || 0,
          likeCount: post.likeCount || 0,
          likedBy: [],
          commentCount: post.commentCount || 0,
          createTime: post.createTime ? new Date(post.createTime) : new Date(),
          comments: []
        }))
        this.total = this.originalPosts.length
        this.applyPagination()
      } catch (e) {
        console.error(e)
        this.$message.error('加载帖子失败')
      }
    },
    applyPagination() {
      // 分页处理
      const startIndex = (this.currentPage - 1) * this.pageSize
      const endIndex = startIndex + this.pageSize
      this.posts = this.originalPosts.slice(startIndex, endIndex)
    },
    formatDate(date) {
      return new Date(date).toLocaleString()
    },
    async createPost() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        const newPost = {
          title: this.postForm.title,
          content: this.postForm.content,
          userId: this.currentUserId
        }
        
        const res = await postApi.createPost(newPost)
        const postData = res
        
        const displayName = this.currentUsername || this.userInfo?.username || '用户'
        const newPostWithUser = {
          ...postData,
          user: {
            id: postData.userId || this.currentUserId,
            name: displayName,
            username: displayName
          },
          viewCount: 0,
          likeCount: 0,
          likedBy: [],
          commentCount: 0,
          comments: []
        }
        
        this.originalPosts.unshift(newPostWithUser)
        this.total++
        this.currentPage = 1
        this.applyPagination()
        this.showPostDialog = false
        this.postForm = { title: '', content: '' }
        this.$message.success('发布成功！')
      } catch (e) {
        console.error(e)
        this.$message.error('发布失败，请重试')
      }
    },
    likePost(postId) {
      // 模拟点赞/取消点赞
      const post = this.originalPosts.find(p => p.id === postId)
      if (post) {
        const userId = this.currentUserId // 当前用户ID
        const userIndex = post.likedBy.indexOf(userId)
        if (userIndex === -1) {
          // 未点赞，添加点赞
          post.likedBy.push(userId)
          post.likeCount++
        } else {
          // 已点赞，取消点赞
          post.likedBy.splice(userIndex, 1)
          post.likeCount--
        }
        this.savePostsToLocalStorage()
      }
      
      // 实际项目中调用API
      // this.$axios.put(`/posts/${postId}/like`).then(response => {
      //   const post = this.originalPosts.find(p => p.id === postId)
      //   if (post) {
      //     post.likeCount = response.data.likeCount
      //   }
      // })
    },
    viewComments(postId) {
      this.showComments = this.showComments === postId ? null : postId
    },
    addComment(postId) {
      // 模拟添加评论
      const post = this.originalPosts.find(p => p.id === postId)
      if (post && this.commentContent) {
        const newComment = {
          id: post.comments.length + 1,
          content: this.commentContent,
          user: {
            id: this.currentUserId,
            name: this.currentUsername,
            username: this.userInfo?.username || this.currentUsername,
            avatar: this.userInfo?.avatar || this.defaultAvatar
          },
          createTime: new Date(),
          likeCount: 0,
          likedBy: [],
          replies: []
        }
        post.comments.push(newComment)
        post.commentCount++
        this.commentContent = ''
        this.savePostsToLocalStorage()
      }
      
      // 实际项目中调用API
      // this.$axios.post('/comments', {
      //   postId: postId,
      //   content: this.commentContent
      // }).then(response => {
      //   const post = this.originalPosts.find(p => p.id === postId)
      //   if (post) {
      //     post.comments.push(response.data)
      //     post.commentCount++
      //     this.commentContent = ''
      //   }
      // })
    },
    viewPostDetail(postId) {
      // 先从posts中找，再从originalPosts中找
      let post = this.posts.find(p => p.id === postId)
      if (!post) {
        post = this.originalPosts.find(p => p.id === postId)
      }
      if (post) {
        // 增加浏览量
        post.viewCount++
        this.savePostsToLocalStorage()
        this.currentPost = post
        this.showPostDetailDialog = true
      }
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.applyPagination()
    },
    deleteComment(postId, commentId) {
      // 从originalPosts中找到帖子
      const post = this.originalPosts.find(p => p.id === postId)
      if (post) {
        // 找到评论索引并删除
        const commentIndex = post.comments.findIndex(c => c.id === commentId)
        if (commentIndex !== -1) {
          post.comments.splice(commentIndex, 1)
          post.commentCount--
          this.savePostsToLocalStorage()
        }
      }
    },
    deletePost(postId) {
      this.$confirm('确定要删除这条帖子吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const postIndex = this.originalPosts.findIndex(p => p.id === postId)
        if (postIndex !== -1) {
          this.originalPosts.splice(postIndex, 1)
          this.total--
          this.applyPagination()
          this.savePostsToLocalStorage()
          this.$message.success('删除帖子成功！')
        }
      }).catch(() => {
        // 用户取消删除
      })
    },
    deleteReply(postId, commentId, replyId) {
      // 从originalPosts中找到帖子
      const post = this.originalPosts.find(p => p.id === postId)
      if (post) {
        // 找到评论
        const comment = post.comments.find(c => c.id === commentId)
        if (comment && comment.replies) {
          // 找到回复索引并删除
          const replyIndex = comment.replies.findIndex(r => r.id === replyId)
          if (replyIndex !== -1) {
            comment.replies.splice(replyIndex, 1)
            this.savePostsToLocalStorage()
            this.$message.success('删除成功！')
          }
        }
      }
    },
    likeComment(postId, commentId) {
      // 模拟点赞/取消点赞
      const post = this.originalPosts.find(p => p.id === postId)
      if (post) {
        // 找到评论
        const comment = post.comments.find(c => c.id === commentId)
        if (comment) {
          const userId = this.currentUserId // 当前用户ID
          const userIndex = comment.likedBy.indexOf(userId)
          if (userIndex === -1) {
            // 未点赞，添加点赞
            comment.likedBy.push(userId)
            comment.likeCount++
          } else {
            // 已点赞，取消点赞
            comment.likedBy.splice(userIndex, 1)
            comment.likeCount--
          }
          this.savePostsToLocalStorage()
        }
      }
    },
    toggleReply(postId, commentId) {
      if (this.activeReply.postId === postId && this.activeReply.commentId === commentId) {
        this.cancelReply()
      } else {
        this.activeReply = { postId, commentId }
        this.replyContent = ''
      }
    },
    cancelReply() {
      this.activeReply = { postId: null, commentId: null }
      this.replyContent = ''
    },
    addReply(postId, commentId) {
      // 模拟添加回复
      if (!this.replyContent) return
      
      const post = this.originalPosts.find(p => p.id === postId)
      if (post) {
        const comment = post.comments.find(c => c.id === commentId)
        if (comment) {
          if (!comment.replies) {
            comment.replies = []
          }
          const newReply = {
            id: comment.replies.length + 1,
            content: this.replyContent,
            user: {
            id: this.currentUserId,
            name: this.currentUsername,
            username: this.userInfo?.username || this.currentUsername,
            avatar: this.userInfo?.avatar || this.defaultAvatar
          },
            createTime: new Date()
          }
          comment.replies.push(newReply)
          this.savePostsToLocalStorage()
          this.cancelReply()
        }
      }
    },
    isLiked(likedBy) {
      if (!likedBy) return false
      return likedBy.includes(this.currentUserId)
    },
    getSortedComments(comments) {
      // 按点赞数降序排序，取前五名
      return [...comments]
        .sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
        .slice(0, 5)
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 30px;
  width: 100%;
  box-sizing: border-box;
}

.page-title {
  font-size: 32px;
  margin-bottom: 40px;
  color: #333;
  text-align: center;
  font-weight: 600;
}

.post-actions {
  margin-bottom: 40px;
  text-align: right;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-item {
  transition: all 0.3s ease;
}

.post-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.post-header {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info h4 {
  margin: 0;
  color: #333;
}

.post-time {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.post-content {
  margin-bottom: 20px;
}

.post-content h3 {
  margin-bottom: 10px;
  color: #333;
}

.post-content p {
  line-height: 1.6;
  color: #666;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.post-stats {
  display: flex;
  gap: 20px;
}

.comments-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.comments-section h4 {
  margin-bottom: 15px;
  color: #333;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  display: flex;
  gap: 10px;
}

.comment-content {
  flex: 1;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.comment-user {
  font-weight: bold;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.pagination {
  margin-top: 60px;
  margin-bottom: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 20px;
}

.pagination .el-pagination {
  margin: 0 10px 0 0 !important;
}

.page-info {
  font-size: 14px;
  color: #666;
  margin: 0 !important;
}

.pagination .el-pagination {
  margin-top: 0;
}

.default-avatar {
  background-color: #409EFF;
}

/* 帖子详情样式 */
.post-detail {
  padding: 20px 0;
}

.post-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.post-detail-header .user-info h3 {
  margin: 0;
  color: #333;
}

.post-detail-stats {
  display: flex;
  gap: 25px;
  color: #666;
}

.post-detail-stats span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.post-detail-content {
  margin-bottom: 40px;
  line-height: 1.8;
  color: #333;
  font-size: 16px;
}

.detail-comments-section {
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.detail-comments-section h4 {
  margin-bottom: 20px;
  color: #333;
}

.no-comments {
  text-align: center;
  color: #999;
  padding: 30px;
}

@media (max-width: 768px) {
  .post-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .post-stats {
    width: 100%;
    justify-content: space-between;
  }
}

.post-stats .el-button,
.comment-actions .el-button {
  color: #909399 !important;
}

.post-stats .el-button i,
.comment-actions .el-button i {
  color: #909399 !important;
}

.post-stats .el-button.liked-button,
.comment-actions .el-button.liked-button,
.post-detail-stats .el-button.liked-button {
  color: #409EFF !important;
}

.post-stats .el-button.liked-button i,
.comment-actions .el-button.liked-button i,
.post-detail-stats .el-button.liked-button i {
  color: #409EFF !important;
}

.post-detail-stats .el-button {
  color: #909399 !important;
}

.post-detail-stats .el-button i {
  color: #909399 !important;
}

.post-stats .el-button i.el-icon-thumb {
  font-size: 18px;
  margin-right: 4px;
  vertical-align: middle;
}

.comment-actions .el-button i.el-icon-thumb {
  font-size: 16px;
  margin-right: 3px;
  vertical-align: middle;
}

/* 回复样式 */
.reply-form {
  margin-top: 10px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.reply-actions {
  margin-top: 10px;
  text-align: right;
}

.reply-list {
  margin-top: 10px;
  padding-left: 40px;
}

.reply-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.reply-user {
  font-weight: bold;
  color: #409EFF;
  font-size: 13px;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-header .reply-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-header .reply-actions .el-button {
  padding: 0;
  margin: 0;
  font-size: 12px;
}

.reply-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}
</style>
