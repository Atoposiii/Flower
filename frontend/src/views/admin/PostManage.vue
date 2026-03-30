<template>
  <div class="post-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-card>
      <div slot="header">
        <el-form :inline="true">
          <el-form-item label="关键词">
            <el-input v-model="filters.keyword" placeholder="标题/内容" clearable size="small" @keyup.enter.native="handleSearch"></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="全部" clearable size="small" @change="handleSearch">
              <el-option label="正常" value="normal"></el-option>
              <el-option label="已隐藏" value="hidden"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="posts" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
        <el-table-column label="作者" width="100">
          <template slot-scope="scope">{{ scope.row.user && (scope.row.user.nickname || scope.row.user.username) }}</template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="80"></el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="80"></el-table-column>
        <el-table-column prop="commentCount" label="评论数" width="80"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.status === 'normal' ? 'success' : 'info'">
              {{ scope.row.status === 'normal' ? '正常' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160"></el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button type="text" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 编辑帖子对话框 -->
    <el-dialog title="编辑帖子" :visible.sync="editDialogVisible" width="60%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editForm.content" type="textarea" :rows="6"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="正常" value="normal"></el-option>
            <el-option label="隐藏" value="hidden"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminPosts',
  data() {
    return {
      posts: [],
      loading: false,
      filters: { keyword: '', status: '' },
      pagination: { page: 1, size: 10, total: 0 },
      editDialogVisible: false,
      editForm: { id: null, title: '', content: '', status: 'normal' }
    }
  },
  mounted() {
    this.loadPosts()
  },
  methods: {
    handleSearch() {
      this.pagination.page = 1
      this.loadPosts()
    },
    handleReset() {
      this.filters = { keyword: '', status: '' }
      this.pagination.page = 1
      this.loadPosts()
    },
    async loadPosts() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size
        }
        if (this.filters.keyword) params.keyword = this.filters.keyword
        if (this.filters.status) params.status = this.filters.status
        const res = await adminAPI.post.getPosts(params)
        this.posts = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载帖子列表失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadPosts()
    },
    handleView(row) {
      window.open(`/post/${row.id}`, '_blank')
    },
    showEditDialog(row) {
      this.editForm = { id: row.id, title: row.title, content: row.content, status: row.status || 'normal' }
      this.editDialogVisible = true
    },
    async handleSaveEdit() {
      try {
        await adminAPI.post.updatePost(this.editForm.id, {
          title: this.editForm.title,
          content: this.editForm.content,
          status: this.editForm.status
        })
        this.$message.success('更新成功')
        this.editDialogVisible = false
        this.loadPosts()
      } catch (error) {
        this.$message.error('更新失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个帖子吗?', '提示', { type: 'warning' })
        await adminAPI.post.deletePost(row.id)
        this.$message.success('删除成功')
        this.loadPosts()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('删除失败')
      }
    }
  }
}
</script>
