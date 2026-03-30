<template>
  <div class="community">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <div style="display:flex;align-items:center;gap:12px;">
            <el-button size="small" @click="$router.back()"><i class="el-icon-back"></i> 返回</el-button>
            <h2 class="logo" style="margin:0;">🌸 互动社区</h2>
          </div>
          <el-button type="primary" @click="handleOpenPostDialog">发布帖子</el-button>
        </div>
      </el-header>
      <el-main>
        <el-row :gutter="20">
          <el-col :span="16">
            <el-card v-for="post in posts" :key="post.id" class="post-card" shadow="hover">
              <div class="post-header">
                <h3 @click="$router.push(`/post/${post.id}`)">{{ post.title }}</h3>
                <el-tag size="mini">{{ post.nickname }}</el-tag>
              </div>
              <p class="post-content">{{ post.content | ellipsis }}</p>
              <div class="post-footer">
                <span><i class="el-icon-view"></i> {{ post.viewCount || 0 }}</span>
                <span><i class="el-icon-chat-line-square"></i> {{ post.commentCount || 0 }}</span>
                <span><i class="el-icon-star"></i> {{ post.likeCount || 0 }}</span>
                <el-button
                  :type="post.liked ? 'primary' : 'default'"
                  size="mini"
                  @click="handleLike(post)">
                  {{ post.liked ? '取消点赞' : '点赞' }}
                </el-button>
              </div>
            </el-card>
            <el-pagination
              @current-change="handlePageChange"
              :current-page="pagination.page"
              :page-size="pagination.size"
              :total="pagination.total"
              layout="total, prev, pager, next">
            </el-pagination>
          </el-col>
          <el-col :span="8">
            <el-card>
              <h4>社区公告</h4>
              <p>欢迎大家分享花卉养护心得!</p>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
      <el-dialog title="发布帖子" :visible.sync="showPostDialog" width="50%">
        <el-form :model="postForm">
          <el-form-item label="标题">
            <el-input v-model="postForm.title" placeholder="请输入标题"></el-input>
          </el-form-item>
          <el-form-item label="内容">
            <el-input v-model="postForm.content" type="textarea" :rows="5" placeholder="请输入内容"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="showPostDialog = false">取消</el-button>
          <el-button type="primary" @click="handlePublish">发布</el-button>
        </span>
      </el-dialog>
    </el-container>
  </div>
</template>

<script>
import { postAPI } from '@/api/post'

export default {
  name: 'Community',
  data() {
    return {
      posts: [],
      showPostDialog: false,
      postForm: {
        title: '',
        content: ''
      },
      pagination: {
        page: 1,
        size: 20,
        total: 0
      },
      user: JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  mounted() {
    this.loadPosts()
  },
  filters: {
    ellipsis(value) {
      if (!value) return ''
      return value.length > 100 ? value.substring(0, 100) + '...' : value
    }
  },
  methods: {
    async loadPosts() {
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size
        }
        if (this.user.id) {
          params.userId = this.user.id
        }
        const res = await postAPI.getPosts(params)
        if (res.content) {
          this.posts = res.content
          this.pagination.total = res.totalElements
        }
      } catch (error) {
        this.$message.error('加载帖子失败')
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadPosts()
    },
    handleOpenPostDialog() {
      if (!this.user.id) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.showPostDialog = true
    },
    async handlePublish() {
      if (!this.postForm.title.trim()) {
        this.$message.warning('请输入标题')
        return
      }
      if (!this.postForm.content.trim()) {
        this.$message.warning('请输入内容')
        return
      }
      try {
        await postAPI.createPost(this.user.id, this.postForm.title, this.postForm.content)
        this.$message.success('发布成功')
        this.showPostDialog = false
        this.postForm = { title: '', content: '' }
        this.loadPosts()
      } catch (error) {
        this.$message.error('发布失败')
      }
    },
    async handleLike(post) {
      if (!this.user.id) {
        this.$message.warning('请先登录')
        return
      }
      try {
        if (post.liked) {
          await postAPI.unlikePost(post.id, this.user.id)
          post.liked = false
          post.likeCount = Math.max(0, (post.likeCount || 1) - 1)
        } else {
          await postAPI.likePost(post.id, this.user.id)
          post.liked = true
          post.likeCount = (post.likeCount || 0) + 1
        }
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '操作失败')
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
.post-card { margin-bottom: 15px; }
.post-header { display: flex; justify-content: space-between; align-items: center; }
.post-header h3 { cursor: pointer; color: #409EFF; margin: 0; }
.post-content { margin: 10px 0; color: #666; }
.post-footer { display: flex; gap: 15px; align-items: center; color: #909399; }
</style>
