<template>
  <div class="activity-manage">
    <div style="margin-bottom: 16px;">
      <el-button size="small" @click="$router.push('/admin/dashboard')"><i class="el-icon-back"></i> 返回</el-button>
    </div>
    <el-card>
      <div slot="header">
        <el-button type="primary" @click="showDialog('add')">添加活动</el-button>
      </div>
      <el-table :data="activities" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="title" label="活动标题"></el-table-column>
        <el-table-column prop="location" label="地点" width="120"></el-table-column>
        <el-table-column label="开始时间" width="160">
          <template slot-scope="scope">{{ scope.row.activityTime | formatDate }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="160">
          <template slot-scope="scope">{{ scope.row.endTime | formatDate }}</template>
        </el-table-column>
        <el-table-column prop="maxParticipants" label="最大人数" width="80"></el-table-column>
        <el-table-column prop="currentParticipants" label="已报名" width="80"></el-table-column>
        <el-table-column label="类型" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.isMemberOnly ? 'warning' : 'success'">
              {{ scope.row.isMemberOnly ? '会员专属' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <el-button type="text" @click="showDialog('edit', scope.row)">编辑</el-button>
            <el-button type="text" @click="showSignInDialog(scope.row)">签到记录</el-button>
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

    <!-- 添加/编辑活动对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="activityForm" label-width="120px">
        <el-form-item label="活动标题">
          <el-input v-model="activityForm.title"></el-input>
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="activityForm.description" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="activityForm.location"></el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="activityForm.activityTime" type="datetime" placeholder="选择开始时间" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="activityForm.endTime" type="datetime" placeholder="选择结束时间" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
        </el-form-item>
        <el-form-item label="最大参与人数">
          <el-input-number v-model="activityForm.maxParticipants" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="活动类型">
          <el-radio-group v-model="activityForm.isMemberOnly">
            <el-radio :label="false">普通活动</el-radio>
            <el-radio :label="true">会员专属</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </span>
    </el-dialog>

    <!-- 签到记录对话框 -->
    <el-dialog :title="`签到记录 - ${currentActivity && currentActivity.title}`" :visible.sync="signInDialogVisible" width="700px">
      <div class="signin-stats" v-if="signInData">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic title="总报名人数" :value="signInData.totalParticipants || 0"></el-statistic>
          </el-col>
          <el-col :span="8">
            <el-statistic title="已签到" :value="signInData.signedInCount || 0"></el-statistic>
          </el-col>
          <el-col :span="8">
            <el-statistic title="未签到" :value="signInData.unsignedCount || 0"></el-statistic>
          </el-col>
        </el-row>
      </div>
      <el-table :data="signInList" stripe v-loading="signInLoading" style="margin-top:16px">
        <el-table-column label="志愿者昵称">
          <template slot-scope="scope">
            {{ scope.row.volunteer && scope.row.volunteer.user && scope.row.volunteer.user.nickname }}
          </template>
        </el-table-column>
        <el-table-column label="签到状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.signInStatus === 'signed' ? 'success' : 'info'" size="mini">
              {{ scope.row.signInStatus === 'signed' ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到时间" width="160">
          <template slot-scope="scope">{{ scope.row.signInTime | formatDate }}</template>
        </el-table-column>
        <el-table-column label="预计时长" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.actualHours">{{ scope.row.actualHours }} 小时</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="发放状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'completed' ? 'success' : 'info'" size="mini">
              {{ scope.row.status === 'completed' ? '已发放' : '待发放' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer">
        <el-button @click="signInDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleGrantHours" :loading="grantLoading">发放时长</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import adminAPI from '@/api/admin'

export default {
  name: 'AdminActivities',
  filters: {
    formatDate(val) {
      if (!val) return '-'
      return new Date(val).toLocaleString('zh-CN')
    }
  },
  data() {
    return {
      activities: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '添加活动',
      isEdit: false,
      activityId: null,
      activityForm: {
        title: '',
        description: '',
        location: '',
        activityTime: '',
        endTime: '',
        maxParticipants: 30,
        isMemberOnly: false
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      signInDialogVisible: false,
      currentActivity: null,
      signInData: null,
      signInList: [],
      signInLoading: false,
      grantLoading: false
    }
  },
  mounted() {
    this.loadActivities()
  },
  methods: {
    async loadActivities() {
      this.loading = true
      try {
        const res = await adminAPI.volunteer.getActivities({
          page: this.pagination.page - 1,
          size: this.pagination.size
        })
        this.activities = res.content || []
        this.pagination.total = res.totalElements || 0
      } catch (error) {
        this.$message.error('加载活动列表失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.pagination.page = page
      this.loadActivities()
    },
    showDialog(type, row) {
      if (type === 'add') {
        this.dialogTitle = '添加活动'
        this.isEdit = false
        this.activityForm = {
          title: '', description: '', location: '',
          activityTime: '', endTime: '', maxParticipants: 30, isMemberOnly: false
        }
      } else {
        this.dialogTitle = '编辑活动'
        this.isEdit = true
        this.activityId = row.id
        this.activityForm = { ...row }
      }
      this.dialogVisible = true
    },
    async handleSave() {
      try {
        if (this.isEdit) {
          await adminAPI.volunteer.updateActivity(this.activityId, this.activityForm)
          this.$message.success('更新成功')
        } else {
          await adminAPI.volunteer.createActivity(this.activityForm)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.loadActivities()
      } catch (error) {
        this.$message.error('保存失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个活动吗?', '提示', { type: 'warning' })
        await adminAPI.volunteer.deleteActivity(row.id)
        this.$message.success('删除成功')
        this.loadActivities()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('删除失败')
      }
    },
    async showSignInDialog(row) {
      this.currentActivity = row
      this.signInDialogVisible = true
      this.signInLoading = true
      try {
        const res = await adminAPI.volunteer.getSignInRecords(row.id)
        this.signInData = res
        this.signInList = res.participants || []
      } catch (error) {
        this.$message.error('加载签到记录失败')
      } finally {
        this.signInLoading = false
      }
    },
    async handleGrantHours() {
      try {
        await this.$confirm('确定为所有已签到用户发放志愿时长吗？已发放的记录将被跳过。', '发放时长', {
          type: 'warning',
          confirmButtonText: '确定发放',
          cancelButtonText: '取消'
        })
        this.grantLoading = true
        const res = await adminAPI.volunteer.grantHours(this.currentActivity.id)
        this.$message.success(res.message || '发放成功')
        await this.showSignInDialog(this.currentActivity)
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '发放失败')
        }
      } finally {
        this.grantLoading = false
      }
    }
  }
}
</script>

<style scoped>
.signin-stats {
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 8px;
}
</style>
