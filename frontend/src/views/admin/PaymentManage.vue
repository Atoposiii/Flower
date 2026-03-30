<template>
  <div class="payment-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-card>
      <div slot="header">
        <el-row :gutter="20" style="margin-bottom:16px">
          <el-col :span="4">
            <el-statistic title="总收入" :value="stats.totalRevenue">
              <template slot="prefix">¥</template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="总退款" :value="stats.totalRefund">
              <template slot="prefix">¥</template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="净收益" :value="stats.netRevenue">
              <template slot="prefix">¥</template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="微信支付" :value="stats.wechatRevenue">
              <template slot="prefix">¥</template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="支付宝" :value="stats.alipayRevenue">
              <template slot="prefix">¥</template>
            </el-statistic>
          </el-col>
          <el-col :span="4">
            <el-statistic title="银行卡" :value="stats.bankcardRevenue">
              <template slot="prefix">¥</template>
            </el-statistic>
          </el-col>
        </el-row>
      </div>
      <el-form :inline="true">
        <el-form-item label="支付方式">
          <el-select v-model="filters.method" placeholder="全部" clearable @change="handleSearch">
            <el-option label="微信" value="wechat"></el-option>
            <el-option label="支付宝" value="alipay"></el-option>
            <el-option label="银行卡" value="bankcard"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="filters.type" placeholder="全部" clearable @change="handleSearch">
            <el-option label="支付" value="payment"></el-option>
            <el-option label="退款" value="refund"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="待支付" value="pending"></el-option>
            <el-option label="已退款" value="refunded"></el-option>
            <el-option label="失败" value="failed"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="payments" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column label="用户" width="100">
          <template slot-scope="scope">{{ scope.row.user && (scope.row.user.nickname || scope.row.user.username) || scope.row.nickname }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.amount < 0 ? '#F56C6C' : '#67C23A' }">
              ¥{{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template slot-scope="scope">
            {{ getMethodName(scope.row.paymentMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refundReason" label="备注" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
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
  name: 'AdminPayments',
  data() {
    return {
      payments: [],
      loading: false,
      filters: {
        method: '',
        type: '',
        status: ''
      },
      stats: {
        totalRevenue: 0,
        totalRefund: 0,
        netRevenue: 0,
        wechatRevenue: 0,
        alipayRevenue: 0,
        bankcardRevenue: 0
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.loadPayments()
    this.loadStats()
  },
  methods: {
    handleSearch() {
      this.pagination.page = 1
      this.loadPayments()
    },
    handleReset() {
      this.filters = { method: '', type: '', status: '' }
      this.pagination.page = 1
      this.loadPayments()
    },
    async loadPayments() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.page - 1,
          size: this.pagination.size
        }
        if (this.filters.method) params.paymentMethod = this.filters.method
        if (this.filters.status) params.paymentStatus = this.filters.status
        const res = await adminAPI.payment.getPayments(params)
        let content = res.content || []
        // Client-side filter for type (payment=positive amount, refund=negative)
        if (this.filters.type === 'payment') {
          content = content.filter(p => p.amount >= 0)
        } else if (this.filters.type === 'refund') {
          content = content.filter(p => p.amount < 0 || p.status === 'refunded')
        }
        this.payments = content
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载支付记录失败')
      } finally {
        this.loading = false
      }
    },
    async loadStats() {
      try {
        const res = await adminAPI.payment.getStatistics()
        this.stats = {
          totalRevenue: res.totalRevenue || 0,
          totalRefund: res.totalRefund || 0,
          netRevenue: res.netRevenue || (res.totalRevenue || 0) - (res.totalRefund || 0),
          wechatRevenue: res.wechatRevenue || 0,
          alipayRevenue: res.alipayRevenue || 0,
          bankcardRevenue: res.bankcardRevenue || 0
        }
      } catch (error) {
        console.error('加载统计数据失败', error)
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadPayments()
    },
    getMethodName(method) {
      const map = { 'wechat': '微信', 'alipay': '支付宝', 'bankcard': '银行卡' }
      return map[method] || method
    },
    getStatusType(status) {
      const map = { 'completed': 'success', 'pending': 'warning', 'refunded': 'info', 'failed': 'danger' }
      return map[status] || 'info'
    },
    getStatusName(status) {
      const map = { 'completed': '已完成', 'pending': '待支付', 'refunded': '已退款', 'failed': '失败' }
      return map[status] || status
    }
  }
}
</script>
