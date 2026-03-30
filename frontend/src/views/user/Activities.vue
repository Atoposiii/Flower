<template>
  <div class="activities">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 志愿活动</h2>
          <el-button @click="$router.push('/home')">返回首页</el-button>
        </div>
      </el-header>
      <el-main>
        <el-tabs v-model="activeTab" @tab-click="onTabClick">
          <el-tab-pane label="全部活动" name="all">
            <el-row :gutter="20">
              <el-col :span="8" v-for="activity in activities" :key="activity.id">
                <el-card shadow="hover" class="activity-card">
                  <h4>
                    {{ activity.title }}
                    <el-tag v-if="activity.isMemberOnly" type="warning" size="mini" style="margin-left:6px">会员专属</el-tag>
                  </h4>
                  <p class="activity-desc">{{ activity.description | ellipsis }}</p>
                  <div class="activity-info">
                    <p><i class="el-icon-time"></i> {{ activity.activityTime | formatDate }} - {{ activity.endTime | formatDate }}</p>
                    <p><i class="el-icon-location"></i> {{ activity.location }}</p>
                    <p><i class="el-icon-user"></i> 已报名: {{ activity.currentParticipants || 0 }}/{{ activity.maxParticipants }}</p>
                  </div>
                  <el-button type="primary" size="small" @click="handleRegister(activity)" :disabled="!canRegister(activity)">
                    {{ getButtonText(activity) }}
                  </el-button>
                </el-card>
              </el-col>
            </el-row>
            <div v-if="activities.length === 0" class="empty-tip">暂无进行中的活动</div>
          </el-tab-pane>

          <el-tab-pane label="我的报名" name="my">
            <div v-if="!isVolunteer" class="empty-tip">请先成为志愿者后参加活动</div>
            <el-table v-else :data="myRegistrations" stripe>
              <el-table-column prop="activityTitle" label="活动名称"></el-table-column>
              <el-table-column prop="activityLocation" label="地点" width="120"></el-table-column>
              <el-table-column label="活动时间" width="160">
                <template slot-scope="scope">{{ scope.row.activityTime | formatDate }}</template>
              </el-table-column>
              <el-table-column label="签到状态" width="120">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.signInStatus === 'signed'" type="success" size="mini">已签到</el-tag>
                  <el-tag v-else-if="isActivityEnded(scope.row)" type="danger" size="mini">未签到</el-tag>
                  <el-tag v-else type="info" size="mini">待签到</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="获得时长" width="100">
                <template slot-scope="scope">
                  <span v-if="scope.row.status === 'completed' && scope.row.actualHours > 0">{{ scope.row.actualHours }} 小时</span>
                  <span v-else-if="scope.row.signInStatus === 'signed'" class="text-pending">待发放</span>
                  <span v-else-if="isActivityEnded(scope.row)" class="text-warning">未签到，不计时长</span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template slot-scope="scope">
                  <el-button
                    v-if="scope.row.signInStatus === 'unsigned' && scope.row.status !== 'cancelled'"
                    type="primary" size="mini"
                    @click="handleSignIn(scope.row)">
                    签到
                  </el-button>
                  <el-button
                    v-if="scope.row.status === 'registered' && !isActivityEnded(scope.row)"
                    type="text" size="mini"
                    @click="handleCancel(scope.row)">
                    取消报名
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="isVolunteer && myRegistrations.length === 0" class="empty-tip">暂无报名记录</div>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { volunteerAPI } from '@/api/volunteer'

export default {
  name: 'Activities',
  filters: {
    ellipsis(value) {
      if (!value) return ''
      return value.length > 50 ? value.substring(0, 50) + '...' : value
    },
    formatDate(val) {
      if (!val) return '-'
      return new Date(val).toLocaleString('zh-CN')
    }
  },
  data() {
    return {
      activeTab: 'all',
      activities: [],
      myRegistrations: [],
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      isVolunteer: false,
      isMember: false,
      volunteerId: null
    }
  },
  mounted() {
    this.loadActivities()
    this.checkUserStatus()
  },
  methods: {
    async loadActivities() {
      try {
        const res = await volunteerAPI.getAvailableActivities()
        this.activities = res || []
      } catch (error) {
        this.$message.error('加载活动失败')
      }
    },
    async loadMyRegistrations() {
      if (!this.user.id) return
      try {
        const res = await volunteerAPI.getMyRegistrations(this.user.id)
        this.myRegistrations = res || []
      } catch (error) {
        this.$message.error('加载报名记录失败')
      }
    },
    async checkUserStatus() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) return
      try {
        const statusRes = await volunteerAPI.checkUserStatus(user.id)
        this.isVolunteer = !!statusRes.isVolunteer
        this.isMember = !!statusRes.isMember

        if (this.isVolunteer) {
          const volunteerRes = await volunteerAPI.getVolunteerInfo(user.id)
          this.volunteerId = volunteerRes && volunteerRes.id
        }
      } catch (error) {
        this.isVolunteer = false
        this.isMember = false
      }
    },
    onTabClick(tab) {
      if (tab.name === 'my') {
        this.loadMyRegistrations()
      }
    },
    isActivityEnded(row) {
      if (!row.activityEndTime) return false
      // 活动结束时间超过当前时间超过1小时才算真正结束
      return new Date(row.activityEndTime).getTime() + 3600000 < Date.now()
    },
    canRegister(activity) {
      if (!this.user.id) return false
      if (!this.isVolunteer) return false
      if (activity.isMemberOnly && !this.isMember) return false
      if (activity.currentParticipants >= activity.maxParticipants) {
        if (!this.isMember) return false
      }
      return true
    },
    getButtonText(activity) {
      if (!this.user.id) return '请先登录'
      if (!this.isVolunteer) return '成为志愿者后报名'
      if (activity.isMemberOnly && !this.isMember) return '会员专属'
      if (activity.currentParticipants >= activity.maxParticipants && !this.isMember) return '已满'
      return '报名参加'
    },
    async handleRegister(activity) {
      if (!this.user.id) {
        this.$message.warning('请先登录')
        return
      }
      if (!this.volunteerId) {
        this.$message.warning('请先成为志愿者')
        return
      }
      try {
        const res = await volunteerAPI.registerActivity(activity.id, this.user.id)
        if (res && res.success === false) {
          this.$message.error(res.message || '报名失败')
        } else {
          this.$message.success('报名成功')
          this.loadActivities()
        }
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '报名失败')
      }
    },
    async handleCancel(registration) {
      try {
        await this.$confirm('确定取消报名吗？', '提示', { type: 'warning' })
        await volunteerAPI.cancelRegistration(this.user.id, registration.activityId)
        this.$message.success('取消成功')
        this.loadMyRegistrations()
        this.loadActivities()
      } catch (error) {
        if (error !== 'cancel') {
          const msg = error.response && error.response.data && error.response.data.message
          this.$message.error(msg || '取消失败')
        }
      }
    },
    async handleSignIn(registration) {
      if (registration.signInStatus === 'signed') {
        this.$message.warning('您已签到，请勿重复操作')
        return
      }
      try {
        const res = await volunteerAPI.signInActivity(this.user.id, registration.activityId)
        if (res && res.success === false) {
          this.$message.error(res.message || '签到失败')
        } else {
          const hours = res && res.expectedHours ? res.expectedHours : 0
          this.$message.success(`签到成功！本次活动时长 ${hours} 小时`)
          this.loadMyRegistrations()
        }
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '签到失败')
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
.activity-card { margin-bottom: 20px; }
.activity-info { margin: 15px 0; }
.activity-info p { margin: 5px 0; color: #666; }
.empty-tip { text-align: center; color: #909399; padding: 40px 0; }
.text-pending { color: #E6A23C; }
.text-warning { color: #F56C6C; font-size: 12px; }
</style>
