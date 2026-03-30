<template>
  <div class="volunteer-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="志愿者列表" name="list">
        <el-card>
          <div slot="header">
            <el-form :inline="true">
              <el-form-item label="状态">
                <el-select v-model="filters.status" placeholder="全部" clearable @change="handleSearch">
                  <el-option label="待审核" value="pending"></el-option>
                  <el-option label="已认证" value="approved"></el-option>
                  <el-option label="已拒绝" value="rejected"></el-option>
                  <el-option label="已退出" value="quit"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="volunteers" stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="60"></el-table-column>
            <el-table-column label="用户昵称" width="120">
              <template slot-scope="scope">{{ scope.row.user && scope.row.user.nickname }}</template>
            </el-table-column>
            <el-table-column prop="realName" label="真实姓名" width="100"></el-table-column>
            <el-table-column prop="skills" label="技能特长" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column prop="totalServiceHours" label="服务时长(h)" width="110"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applicationTime" label="申请时间" width="160">
              <template slot-scope="scope">{{ scope.row.applicationTime | formatDate }}</template>
            </el-table-column>
            <el-table-column label="操作" width="260">
              <template slot-scope="scope">
                <el-button v-if="scope.row.status === 'pending'" type="text" size="mini" @click="handleApprove(scope.row)">通过</el-button>
                <el-button v-if="scope.row.status === 'pending'" type="text" size="mini" style="color:#F56C6C" @click="handleReject(scope.row)">拒绝</el-button>
                <el-button v-if="scope.row.status === 'approved'" type="text" size="mini" @click="showActivityRecords(scope.row)">参与记录</el-button>
                <el-button v-if="scope.row.status === 'approved'" type="text" size="mini" @click="showAddRecord(scope.row)">添加记录</el-button>
                <el-button v-if="scope.row.status === 'approved'" type="text" size="mini" @click="showAdjustHours(scope.row)">调整时长</el-button>
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

      <el-tab-pane label="服务时长排行榜" name="ranking">
        <el-card>
          <div slot="header">
            <span>志愿者服务时长排行榜</span>
            <el-button style="float:right" size="small" @click="loadRanking">刷新</el-button>
          </div>
          <el-table :data="ranking" stripe v-loading="rankingLoading">
            <el-table-column type="index" label="排名" width="60"></el-table-column>
            <el-table-column label="用户昵称" width="120">
              <template slot-scope="scope">{{ scope.row.user && scope.row.user.nickname }}</template>
            </el-table-column>
            <el-table-column prop="realName" label="真实姓名" width="100"></el-table-column>
            <el-table-column prop="totalServiceHours" label="服务时长(h)" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getStatusType(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="showAdjustHours(scope.row)">调整时长</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 活动参与记录对话框 -->
    <el-dialog :title="`${currentVolunteer && currentVolunteer.realName || '志愿者'} 的活动参与记录`" :visible.sync="recordDialogVisible" width="700px">
      <el-table :data="activityRecords" stripe v-loading="recordLoading">
        <el-table-column prop="activityName" label="活动名称"></el-table-column>
        <el-table-column prop="participationTime" label="参与时间" width="160">
          <template slot-scope="scope">{{ scope.row.participationTime | formatDate }}</template>
        </el-table-column>
        <el-table-column prop="participationStatus" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.participationStatus === 'participated' ? 'success' : 'info'">
              {{ scope.row.participationStatus === 'participated' ? '已参与' : scope.row.participationStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="serviceHours" label="服务时长(h)" width="110"></el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
      </el-table>
      <div v-if="activityRecords.length === 0 && !recordLoading" style="text-align:center;color:#909399;padding:20px">暂无活动参与记录</div>
    </el-dialog>

    <!-- 添加活动记录对话框 -->
    <el-dialog title="添加活动参与记录" :visible.sync="addRecordDialogVisible" width="500px">
      <el-form :model="newRecord" label-width="100px">
        <el-form-item label="活动名称" required>
          <el-input v-model="newRecord.activityName" placeholder="请输入活动名称"></el-input>
        </el-form-item>
        <el-form-item label="服务时长">
          <el-input-number v-model="newRecord.serviceHours" :min="0" :max="999" placeholder="小时数"></el-input-number>
          <span style="margin-left:8px;color:#909399">小时</span>
        </el-form-item>
        <el-form-item label="参与时间">
          <el-date-picker v-model="newRecord.participationTime" type="datetime" placeholder="选择参与时间" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="newRecord.remark" type="textarea" :rows="2" placeholder="备注（选填）"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="addRecordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddRecord">确认添加</el-button>
      </span>
    </el-dialog>

    <!-- 调整服务时长对话框 -->
    <el-dialog title="调整服务时长" :visible.sync="adjustHoursDialogVisible" width="400px">
      <el-form label-width="100px">
        <el-form-item label="志愿者">
          <span>{{ currentVolunteer && (currentVolunteer.realName || (currentVolunteer.user && currentVolunteer.user.nickname)) }}</span>
        </el-form-item>
        <el-form-item label="当前时长">
          <span>{{ currentVolunteer && currentVolunteer.totalServiceHours }} 小时</span>
        </el-form-item>
        <el-form-item label="新时长">
          <el-input-number v-model="newHours" :min="0" :max="9999"></el-input-number>
          <span style="margin-left:8px;color:#909399">小时</span>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="adjustHoursDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdjustHours">确认调整</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminVolunteers',
  filters: {
    formatDate(val) {
      if (!val) return '-'
      return new Date(val).toLocaleString('zh-CN')
    }
  },
  data() {
    return {
      activeTab: 'list',
      volunteers: [],
      loading: false,
      filters: { status: '' },
      pagination: { page: 1, size: 10, total: 0 },
      ranking: [],
      rankingLoading: false,
      recordDialogVisible: false,
      addRecordDialogVisible: false,
      adjustHoursDialogVisible: false,
      activityRecords: [],
      recordLoading: false,
      currentVolunteer: null,
      newRecord: { activityName: '', serviceHours: 0, participationTime: null, remark: '' },
      newHours: 0
    }
  },
  watch: {
    activeTab(tab) {
      if (tab === 'ranking') this.loadRanking()
    }
  },
  mounted() {
    this.loadVolunteers()
  },
  methods: {
    async loadVolunteers() {
      this.loading = true
      try {
        const res = await adminAPI.volunteer.getVolunteers({
          page: this.pagination.page - 1,
          size: this.pagination.size,
          status: this.filters.status
        })
        this.volunteers = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载志愿者列表失败')
      } finally {
        this.loading = false
      }
    },
    async loadRanking() {
      this.rankingLoading = true
      try {
        const res = await adminAPI.volunteer.getRanking()
        this.ranking = res.content || []
      } catch (error) {
        this.$message.error('加载排行榜失败')
      } finally {
        this.rankingLoading = false
      }
    },
    handleSearch() {
      this.pagination.page = 1
      this.loadVolunteers()
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadVolunteers()
    },
    getStatusType(status) {
      const map = { 'pending': 'warning', 'approved': 'success', 'rejected': 'danger', 'quit': 'info' }
      return map[status] || 'info'
    },
    getStatusName(status) {
      const map = { 'pending': '待审核', 'approved': '已认证', 'rejected': '已拒绝', 'quit': '已退出' }
      return map[status] || status
    },
    async handleApprove(row) {
      try {
        await adminAPI.volunteer.approveVolunteerCheck(row.id)
        this.$message.success('审核通过')
        this.loadVolunteers()
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '操作失败')
      }
    },
    async handleReject(row) {
      try {
        await this.$confirm('确定要拒绝该志愿者申请吗?', '提示', { type: 'warning' })
        await adminAPI.volunteer.rejectVolunteer(row.id)
        this.$message.success('已拒绝')
        this.loadVolunteers()
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '操作失败')
        }
      }
    },
    async showActivityRecords(row) {
      this.currentVolunteer = row
      this.recordDialogVisible = true
      this.recordLoading = true
      try {
        const res = await adminAPI.volunteer.getVolunteerActivityRecords(row.id)
        this.activityRecords = res.content || []
      } catch (error) {
        this.$message.error('加载记录失败')
      } finally {
        this.recordLoading = false
      }
    },
    showAddRecord(row) {
      this.currentVolunteer = row
      this.newRecord = { activityName: '', serviceHours: 0, participationTime: null, remark: '' }
      this.addRecordDialogVisible = true
    },
    async handleAddRecord() {
      if (!this.newRecord.activityName) {
        this.$message.warning('请填写活动名称')
        return
      }
      try {
        await adminAPI.volunteer.addVolunteerActivityRecord(this.currentVolunteer.id, this.newRecord)
        this.$message.success('活动参与记录添加成功')
        this.addRecordDialogVisible = false
      } catch (error) {
        this.$message.error('添加失败')
      }
    },
    showAdjustHours(row) {
      this.currentVolunteer = row
      this.newHours = row.totalServiceHours || 0
      this.adjustHoursDialogVisible = true
    },
    async handleAdjustHours() {
      try {
        await adminAPI.volunteer.updateVolunteerHours(this.currentVolunteer.id, this.newHours)
        this.$message.success('服务时长调整成功')
        this.adjustHoursDialogVisible = false
        this.loadVolunteers()
        if (this.activeTab === 'ranking') this.loadRanking()
      } catch (error) {
        this.$message.error('调整失败')
      }
    }
  }
}
</script>
