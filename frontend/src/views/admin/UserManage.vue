<template>
  <div class="user-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-card>
      <div slot="header">
        <el-row :gutter="20" style="margin-bottom:16px">
          <el-col :span="6">
            <el-statistic title="用户总数" :value="stats.totalUsers"></el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="会员数" :value="stats.totalMembers || stats.memberUsers"></el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="志愿者数" :value="stats.totalVolunteers || stats.volunteerUsers"></el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="被封禁用户" :value="stats.bannedUsers"></el-statistic>
          </el-col>
        </el-row>
        <el-form :inline="true">
          <el-form-item label="用户名/昵称">
            <el-input v-model="filters.keyword" placeholder="请输入关键词" clearable @keyup.enter.native="handleSearch"></el-input>
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="filters.role" placeholder="全部" clearable @change="handleSearch" style="width:110px">
              <el-option label="普通用户" value="user"></el-option>
              <el-option label="管理员" value="admin"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="全部" clearable @change="handleSearch" style="width:110px">
              <el-option label="正常" value="active"></el-option>
              <el-option label="封禁" value="banned"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="nickname" label="昵称" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template slot-scope="scope">
            {{ getGenderName(scope.row.gender) }}
          </template>
        </el-table-column>
        <el-table-column label="会员" width="80">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.isMember ? 'warning' : 'info'">
              {{ scope.row.isMember ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="志愿者" width="80">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.isVolunteer ? 'success' : 'info'">
              {{ scope.row.isVolunteer ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.status === 'active' ? 'success' : 'danger'">
              {{ scope.row.status === 'active' ? '正常' : '封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160"></el-table-column>
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 'active'" type="text" style="color:#F56C6C" @click="handleBan(scope.row)">封禁</el-button>
            <el-button v-else type="text" @click="handleUnban(scope.row)">解封</el-button>
            <el-button v-if="scope.row.isVolunteer" type="text" @click="handleForceQuitVolunteer(scope.row)">退出志愿者</el-button>
            <el-button v-if="scope.row.isMember" type="text" @click="handleForceCancelMember(scope.row)">取消会员</el-button>
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
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminUsers',
  data() {
    return {
      users: [],
      loading: false,
      filters: {
        keyword: '',
        role: '',
        status: ''
      },
      stats: {
        totalUsers: 0,
        totalMembers: 0,
        totalVolunteers: 0,
        bannedUsers: 0
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.loadUsers()
    this.loadStats()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size
        }
        if (this.filters.keyword) params.keyword = this.filters.keyword
        if (this.filters.role) params.role = this.filters.role
        if (this.filters.status) params.status = this.filters.status
        const res = await adminAPI.user.getUsers(params)
        this.users = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载用户列表失败')
      } finally {
        this.loading = false
      }
    },
    async loadStats() {
      try {
        const res = await adminAPI.user.getStatistics()
        this.stats = {
          totalUsers: res.totalUsers || 0,
          totalMembers: res.totalMembers || res.memberUsers || 0,
          totalVolunteers: res.totalVolunteers || res.volunteerUsers || 0,
          bannedUsers: res.bannedUsers || 0
        }
      } catch (error) {
        console.error('加载统计数据失败', error)
      }
    },
    handleSearch() {
      this.pagination.page = 1
      this.loadUsers()
    },
    handleReset() {
      this.filters = { keyword: '', role: '', status: '' }
      this.pagination.page = 1
      this.loadUsers()
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadUsers()
    },
    getGenderName(gender) {
      const map = { 'male': '男', 'female': '女', 'secret': '保密' }
      return map[gender] || '未知'
    },
    async handleBan(row) {
      try {
        const { value: reason } = await this.$prompt('请输入封禁原因（选填）', '封禁用户', {
          confirmButtonText: '确定封禁',
          cancelButtonText: '取消',
          inputPlaceholder: '封禁原因',
          type: 'warning'
        })
        await adminAPI.user.banUser(row.id, reason || '')
        this.$message.success('封禁成功')
        this.loadUsers()
        this.loadStats()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
      }
    },
    async handleUnban(row) {
      try {
        await this.$confirm(`确定要解封用户 "${row.nickname || row.username}" 吗?`, '提示', { type: 'warning' })
        await adminAPI.user.unbanUser(row.id)
        this.$message.success('解封成功')
        this.loadUsers()
        this.loadStats()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
      }
    },
    async handleForceQuitVolunteer(row) {
      try {
        const { value: reason } = await this.$prompt('请输入强制退出原因（选填）', '强制退出志愿者', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPlaceholder: '原因'
        })
        await adminAPI.user.forceQuitVolunteer(row.id, reason || '')
        this.$message.success('操作成功')
        this.loadUsers()
        this.loadStats()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
      }
    },
    async handleForceCancelMember(row) {
      try {
        const { value: reason } = await this.$prompt('请输入取消原因（选填）', '强制取消会员', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPlaceholder: '原因'
        })
        await adminAPI.user.forceCancelMembership(row.id, reason || 'admin_cancel')
        this.$message.success('操作成功')
        this.loadUsers()
        this.loadStats()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
      }
    }
  }
}
</script>
