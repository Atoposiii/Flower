<template>
  <div class="volunteer">
    <Navbar />
    <div class="container">
      <div style="margin-bottom: 16px;">
        <el-button size="small" @click="$router.back()"><i class="el-icon-back"></i> 返回</el-button>
      </div>
      <h1 class="page-title">志愿服务</h1>
      
      <!-- 志愿者状态 -->
      <div class="volunteer-status" v-if="volunteerInfo">
        <el-card>
          <h2>我的志愿者信息</h2>
          <div class="volunteer-info">
            <div class="info-item">
              <span class="label">真实姓名：</span>
              <span class="value">{{ volunteerInfo.realName }}</span>
            </div>
            <div class="info-item">
              <span class="label">服务时长：</span>
              <span class="value">{{ volunteerInfo.serviceHours }} 小时</span>
            </div>
            <div class="info-item">
              <span class="label">状态：</span>
              <span class="value" :class="'status-' + volunteerInfo.status">{{ getStatusText(volunteerInfo.status) }}</span>
            </div>
            <div class="info-item" v-if="volunteerInfo.applicationTime">
              <span class="label">申请时间：</span>
              <span class="value">{{ formatDate(volunteerInfo.applicationTime) }}</span>
            </div>
            <div class="info-item" v-if="volunteerInfo.approvalTime">
              <span class="label">成为志愿者时间：</span>
              <span class="value">{{ formatDate(volunteerInfo.approvalTime) }}</span>
            </div>
          </div>
          <div style="margin-top: 20px;" v-if="volunteerInfo.status === 'approved'">
            <el-button type="danger" @click="quitVolunteer">退出志愿者</el-button>
          </div>
        </el-card>
      </div>
      
      <!-- 志愿者申请 -->
      <div class="volunteer-application" v-else>
        <el-card>
          <h2>成为志愿者</h2>
          <p>加入我们的志愿者团队，一起传播花卉知识，参与花卉保护活动！</p>
          <div style="margin-top: 20px;">
            <p><strong>您的姓名：</strong>{{ userInfo.name || userInfo.username }}</p>
          </div>
          <el-button type="primary" size="large" @click="submitApplication" style="margin-top: 20px;">申请成为志愿者</el-button>
        </el-card>
      </div>
      
      <!-- 志愿者活动 -->
      <div class="volunteer-activities">
        <h2 class="section-title">志愿者活动</h2>
        <div class="activity-list">
          <el-card v-for="activity in activities" :key="activity.id" class="activity-item">
            <h3>{{ activity.title }}</h3>
            <p class="activity-time">{{ activity.time }}</p>
            <p class="activity-location">{{ activity.location }}</p>
            <p class="activity-description">{{ activity.description }}</p>
            <div class="activity-actions">
              <el-button 
                :type="isActivityJoined(activity.id) ? 'warning' : 'primary'" 
                @click="isActivityJoined(activity.id) ? cancelActivity(activity.id) : joinActivity(activity.id)"
                :disabled="!isVolunteerApproved"
              >
                {{ !isVolunteerApproved ? '请先成为志愿者' : isActivityJoined(activity.id) ? '取消报名' : '报名参加' }}
              </el-button>
              <el-button type="text" @click="viewActivityDetail(activity)">查看详情</el-button>
            </div>
          </el-card>
        </div>
      </div>
      
      <!-- 志愿者排行榜 -->
      <div class="volunteer-ranking">
        <h2 class="section-title">志愿者排行榜</h2>
        <el-table :data="volunteerRanking" style="width: 100%">
          <el-table-column prop="rank" label="排名" width="80"></el-table-column>
          <el-table-column prop="user.username" label="志愿者" width="180">
            <template slot-scope="scope">
              {{ scope.row.realName || scope.row.user?.username || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="serviceHours" label="服务时长（小时）"></el-table-column>
          <el-table-column prop="lastServiceTime" label="最近服务时间">
            <template slot-scope="scope">
              {{ formatDate(scope.row.lastServiceTime) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      

      <el-dialog :visible.sync="showActivityDetailDialog" title="活动详情" width="650px">
        <div class="activity-detail" v-if="currentActivity">
          <h3>{{ currentActivity.title }}</h3>
          <div class="detail-item">
            <span class="label">活动时间：</span>
            <span class="value">{{ currentActivity.time }}</span>
          </div>
          <div class="detail-item">
            <span class="label">活动地点：</span>
            <span class="value">{{ currentActivity.location }}</span>
          </div>
          <div class="detail-item">
            <span class="label">活动描述：</span>
            <span class="value">{{ currentActivity.description }}</span>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showActivityDetailDialog = false">关闭</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import Navbar from '../components/Navbar.vue'
import volunteerApi from '../api/modules/volunteer'
import volunteerActivityApi from '../api/modules/volunteerActivity'

export default {
  components: {
    Navbar
  },
  data() {
    return {
      volunteerInfo: null,
      activities: [],
      volunteerRanking: [],
      showActivityDetailDialog: false,
      currentActivity: null,
      joinedActivities: JSON.parse(localStorage.getItem('joinedActivities')) || []
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.user.userInfo || {}
    },
    isVolunteerApproved() {
      return this.volunteerInfo && this.volunteerInfo.status === 'approved'
    }
  },
  mounted() {
    this.loadVolunteerInfo()
    this.loadActivities()
    this.loadVolunteerRanking()
  },
  methods: {
    async loadVolunteerInfo() {
      if (!this.userInfo.id) return
      
      try {
        const response = await volunteerApi.getVolunteerByUserId(this.userInfo.id)
        this.volunteerInfo = response
      } catch (error) {
        if (error.response && error.response.status !== 404) {
          console.error('加载志愿者信息失败:', error)
        }
        this.volunteerInfo = null
      }
    },
    async loadActivities() {
      try {
        const res = await volunteerActivityApi.getAllActivities()
        const activitiesArray = Array.isArray(res) ? res : []
        this.activities = activitiesArray.map(activity => ({
          ...activity,
          time: activity.activityTime ? new Date(activity.activityTime).toLocaleString() : '',
          location: activity.location || ''
        }))
      } catch (e) {
        console.error(e)
        this.$message.error('加载活动失败')
      }
    },
    async loadVolunteerRanking() {
      try {
        const response = await volunteerApi.getVolunteersByServiceHours()
        const rankingArray = Array.isArray(response) ? response : []
        this.volunteerRanking = rankingArray.map((item, index) => ({
          ...item,
          rank: index + 1
        }))
      } catch (error) {
        console.error('加载志愿者排行失败:', error)
        this.volunteerRanking = []
      }
    },
    getStatusText(status) {
      switch (status) {
        case 'pending':
          return '待审批'
        case 'approved':
          return '已批准'
        case 'rejected':
          return '已拒绝'
        case 'quit':
          return '已退出'
        default:
          return '未知状态'
      }
    },
    formatDate(date) {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    },
    async submitApplication() {
      try {
        const response = await volunteerApi.applyVolunteer({
          userId: this.userInfo.id,
          realName: this.userInfo.name || this.userInfo.username
        })
        this.volunteerInfo = response
        
        // 更新用户角色为志愿者
        this.$store.dispatch('user/updateUserInfo', {
          ...this.userInfo,
          role: 'VOLUNTEER'
        })
        
        this.$message.success('恭喜您成为志愿者！')
      } catch (error) {
        console.error('申请志愿者失败:', error)
        this.$message.error('申请失败，请重试')
      }
    },
    joinActivity(activityId) {
      if (!this.isVolunteerApproved) {
        this.$message.warning('请先成为志愿者！')
        return
      }
      if (this.isActivityJoined(activityId)) {
        this.$message.info('您已经报名过这个活动了！')
        return
      }
      this.joinedActivities.push(activityId)
      localStorage.setItem('joinedActivities', JSON.stringify(this.joinedActivities))
      this.$message.success('报名成功！')
    },
    cancelActivity(activityId) {
      this.$confirm('确定要取消报名吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = this.joinedActivities.indexOf(activityId)
        if (index > -1) {
          this.joinedActivities.splice(index, 1)
          localStorage.setItem('joinedActivities', JSON.stringify(this.joinedActivities))
          this.$message.success('已取消报名')
        }
      }).catch(() => {
      })
    },
    isActivityJoined(activityId) {
      return this.joinedActivities.includes(activityId)
    },
    viewActivityDetail(activity) {
      this.currentActivity = activity
      this.showActivityDetailDialog = true
    },
    async quitVolunteer() {
      try {
        await this.$confirm('确定要退出志愿者吗？退出后所有报名记录将被清除，志愿时长将清零。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await volunteerApi.quitVolunteer(this.volunteerInfo.id)
        this.volunteerInfo = null
        this.joinedActivities = []
        localStorage.removeItem('joinedActivities')
        this.$message.success('已成功退出志愿者')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退出志愿者失败:', error)
          this.$message.error('退出失败，请重试')
        }
      }
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

.volunteer-status {
  margin-bottom: 40px;
}

.volunteer-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  font-weight: bold;
  color: #333;
  min-width: 100px;
}

.value {
  color: #666;
}

.status-pending {
  color: #E6A23C;
  font-weight: bold;
}

.status-approved {
  color: #67C23A;
  font-weight: bold;
}

.status-rejected {
  color: #F56C6C;
  font-weight: bold;
}

.status-quit {
  color: #909399;
  font-weight: bold;
}

.volunteer-application {
  margin-bottom: 30px;
}

.volunteer-application p {
  margin-bottom: 20px;
  color: #666;
}

.section-title {
  font-size: 22px;
  margin-bottom: 24px;
  color: #333;
  border-left: 4px solid #409EFF;
  padding-left: 15px;
  font-weight: 600;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 40px;
}

.activity-item {
  padding: 24px;
}

.activity-item h3 {
  margin-bottom: 10px;
  color: #333;
}

.activity-time {
  margin-bottom: 5px;
  color: #409EFF;
  font-size: 14px;
}

.activity-location {
  margin-bottom: 10px;
  color: #999;
  font-size: 14px;
}

.activity-description {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #666;
}

.activity-actions {
  display: flex;
  gap: 10px;
}

.volunteer-ranking {
  margin-top: 30px;
}

@media (max-width: 768px) {
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .activity-actions {
    flex-direction: column;
  }
}
</style>
