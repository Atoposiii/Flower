<template>
  <div class="member-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="会员列表" name="list">
        <el-card>
          <div slot="header">
            <el-row :gutter="20" style="margin-bottom:16px">
              <el-col :span="4">
                <el-statistic title="总会员数" :value="stats.total"></el-statistic>
              </el-col>
              <el-col :span="4">
                <el-statistic title="待审核" :value="stats.pending"></el-statistic>
              </el-col>
              <el-col :span="4">
                <el-statistic title="有效会员" :value="stats.active"></el-statistic>
              </el-col>
              <el-col :span="4">
                <el-statistic title="已过期" :value="stats.expired"></el-statistic>
              </el-col>
              <el-col :span="4">
                <el-statistic title="已退会" :value="stats.cancelled"></el-statistic>
              </el-col>
            </el-row>
            <el-form :inline="true">
              <el-form-item label="状态">
                <el-select v-model="filters.status" placeholder="全部" clearable @change="handleSearch">
                  <el-option label="待审核" value="pending"></el-option>
                  <el-option label="有效" value="active"></el-option>
                  <el-option label="已过期" value="expired"></el-option>
                  <el-option label="已退会" value="cancelled"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="members" stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="60"></el-table-column>
            <el-table-column label="用户昵称" width="120">
              <template slot-scope="scope">{{ scope.row.user && scope.row.user.nickname }}</template>
            </el-table-column>
            <el-table-column prop="memberLevel" label="会员等级" width="100"></el-table-column>
            <el-table-column prop="joinDate" label="开通时间" width="160">
              <template slot-scope="scope">{{ scope.row.joinDate | formatDate }}</template>
            </el-table-column>
            <el-table-column prop="expiryDate" label="到期时间" width="160">
              <template slot-scope="scope">{{ scope.row.expiryDate | formatDate }}</template>
            </el-table-column>
            <el-table-column prop="totalPaid" label="已缴费" width="90">
              <template slot-scope="scope">¥{{ scope.row.totalPaid || '0.00' }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template slot-scope="scope">
                <el-button v-if="scope.row.status === 'pending'" type="text" size="mini" @click="handleApprove(scope.row)">通过</el-button>
                <el-button v-if="scope.row.status === 'pending'" type="text" size="mini" style="color:#F56C6C" @click="handleReject(scope.row)">拒绝</el-button>
                <el-button v-if="scope.row.status === 'active'" type="text" size="mini" style="color:#F56C6C" @click="handleSuspend(scope.row)">停用</el-button>
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

      <el-tab-pane label="退款申请" name="refund">
        <el-card>
          <div slot="header">
            <el-form :inline="true">
              <el-form-item label="状态">
                <el-select v-model="refundFilters.status" placeholder="全部" clearable @change="loadRefundRequests">
                  <el-option label="待处理" value="pending"></el-option>
                  <el-option label="已通过" value="approved"></el-option>
                  <el-option label="已拒绝" value="rejected"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadRefundRequests">刷新</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="refundRequests" stripe v-loading="refundLoading">
            <el-table-column prop="id" label="ID" width="60"></el-table-column>
            <el-table-column prop="username" label="用户名" width="120"></el-table-column>
            <el-table-column prop="reason" label="退款原因" show-overflow-tooltip></el-table-column>
            <el-table-column prop="refundAmount" label="退款金额" width="100">
              <template slot-scope="scope">¥{{ scope.row.refundAmount || '9.90' }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getRefundStatusType(scope.row.status)">{{ getRefundStatusName(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="160">
              <template slot-scope="scope">{{ scope.row.createTime | formatDate }}</template>
            </el-table-column>
            <el-table-column label="操作" width="160">
              <template slot-scope="scope">
                <template v-if="scope.row.status === 'pending'">
                  <el-button type="text" size="mini" @click="handleApproveRefund(scope.row)">批准退款</el-button>
                  <el-button type="text" size="mini" style="color:#F56C6C" @click="handleRejectRefund(scope.row)">拒绝</el-button>
                </template>
                <span v-else style="color:#909399">已处理</span>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="refundRequests.length === 0 && !refundLoading" style="text-align:center;color:#909399;padding:20px">暂无退款申请</div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminMembers',
  filters: {
    formatDate(val) {
      if (!val) return '-'
      return new Date(val).toLocaleDateString('zh-CN')
    }
  },
  data() {
    return {
      activeTab: 'list',
      members: [],
      loading: false,
      filters: { status: '' },
      stats: { total: 0, pending: 0, active: 0, expired: 0, cancelled: 0 },
      pagination: { page: 1, size: 10, total: 0 },
      refundRequests: [],
      refundLoading: false,
      refundFilters: { status: '' }
    }
  },
  watch: {
    activeTab(tab) {
      if (tab === 'refund') this.loadRefundRequests()
    }
  },
  mounted() {
    this.loadMembers()
    this.loadStats()
  },
  methods: {
    async loadMembers() {
      this.loading = true
      try {
        const res = await adminAPI.member.getMembers({
          page: this.pagination.page - 1,
          size: this.pagination.size,
          status: this.filters.status
        })
        this.members = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载会员列表失败')
      } finally {
        this.loading = false
      }
    },
    async loadStats() {
      try {
        const res = await adminAPI.member.getStatistics()
        this.stats = {
          total: res.totalMembers || 0,
          pending: res.pendingMembers || 0,
          active: res.activeMembers || 0,
          expired: res.expiredMembers || 0,
          cancelled: res.cancelledMembers || 0
        }
      } catch (error) {
        console.error('加载统计数据失败', error)
      }
    },
    async loadRefundRequests() {
      this.refundLoading = true
      try {
        const res = await adminAPI.consultation.getRefundRequests({ status: this.refundFilters.status })
        this.refundRequests = res.content || []
      } catch (error) {
        this.$message.error('加载退款申请失败')
      } finally {
        this.refundLoading = false
      }
    },
    handleSearch() {
      this.pagination.page = 1
      this.loadMembers()
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadMembers()
    },
    getStatusType(status) {
      const map = { 'pending': 'warning', 'active': 'success', 'expired': 'danger', 'cancelled': 'info' }
      return map[status] || 'info'
    },
    getStatusName(status) {
      const map = { 'pending': '待审核', 'active': '有效', 'expired': '已过期', 'cancelled': '已退会' }
      return map[status] || status
    },
    getRefundStatusType(status) {
      const map = { 'pending': 'warning', 'approved': 'success', 'rejected': 'danger' }
      return map[status] || 'info'
    },
    getRefundStatusName(status) {
      const map = { 'pending': '待处理', 'approved': '已批准', 'rejected': '已拒绝' }
      return map[status] || status
    },
    async handleApprove(row) {
      try {
        await adminAPI.member.approveMember(row.id)
        this.$message.success('审核通过')
        this.loadMembers()
        this.loadStats()
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '操作失败')
      }
    },
    async handleReject(row) {
      try {
        await this.$confirm('确定要拒绝该会员申请吗?', '提示', { type: 'warning' })
        await adminAPI.member.rejectMember(row.id)
        this.$message.success('已拒绝')
        this.loadMembers()
        this.loadStats()
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '操作失败')
        }
      }
    },
    async handleSuspend(row) {
      try {
        await this.$confirm('确定要停用该会员吗?', '提示', { type: 'warning' })
        await adminAPI.member.suspendMember(row.id)
        this.$message.success('会员已停用')
        this.loadMembers()
        this.loadStats()
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '操作失败')
        }
      }
    },
    async handleApproveRefund(row) {
      try {
        await this.$confirm(`确定要批准用户 "${row.username}" 的退款申请吗？将退款 ¥${row.refundAmount || '9.90'}`, '确认退款', { type: 'warning' })
        await adminAPI.consultation.approveRefundRequest(row.id)
        this.$message.success('退款已批准并处理')
        this.loadRefundRequests()
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '操作失败')
        }
      }
    },
    async handleRejectRefund(row) {
      try {
        const { value: rejectReason } = await this.$prompt('请输入拒绝原因（选填）', '拒绝退款', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPlaceholder: '拒绝原因'
        })
        await adminAPI.consultation.rejectRefundRequest(row.id, rejectReason || '')
        this.$message.success('已拒绝退款申请')
        this.loadRefundRequests()
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '操作失败')
        }
      }
    }
  }
}
</script>
