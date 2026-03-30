<template>
  <div class="volunteer">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2 class="logo">🌸 志愿者管理</h2>
          <el-button @click="$router.push('/home')">返回首页</el-button>
        </div>
      </el-header>
      <el-main>
        <!-- 未成为志愿者 -->
        <el-card v-if="!isVolunteer && volunteerStatus !== 'quit'">
          <h3>成为志愿者</h3>
          <p>加入我们，参与志愿活动，积累服务时长！</p>
          <el-button type="primary" @click="handleBecome">立即成为志愿者</el-button>
        </el-card>

        <!-- 已退出 -->
        <el-card v-else-if="volunteerStatus === 'quit'">
          <el-result icon="warning" title="您已退出志愿者" sub-title="退出后志愿时长已归零，如需重新参与请重新加入">
            <template slot="extra">
              <el-button type="primary" @click="handleBecome">重新加入志愿者</el-button>
            </template>
          </el-result>
        </el-card>

        <!-- 已认证 -->
        <el-card v-else-if="volunteerStatus === 'approved'">
          <h3>志愿者信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="志愿者编号">{{ volunteerInfo.volunteerId }}</el-descriptions-item>
            <el-descriptions-item label="累计服务时长">{{ volunteerInfo.totalServiceHours || 0 }} 小时</el-descriptions-item>
            <el-descriptions-item label="加入时间">{{ volunteerInfo.applicationTime | formatDate }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag type="success">已认证</el-tag>
            </el-descriptions-item>
          </el-descriptions>
          <el-button type="danger" @click="handleQuit" style="margin-top: 20px;">退出志愿者</el-button>
        </el-card>

        <el-card class="ranking-card">
          <h3>志愿时长排行榜</h3>
          <el-table :data="ranking" stripe>
            <el-table-column prop="rank" label="排名" width="80"></el-table-column>
            <el-table-column label="昵称">
              <template slot-scope="scope">{{ scope.row.user && scope.row.user.nickname }}</template>
            </el-table-column>
            <el-table-column prop="serviceHours" label="服务时长(小时)"></el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { volunteerAPI } from '@/api/volunteer'

export default {
  name: 'Volunteer',
  filters: {
    formatDate(val) {
      if (!val) return '-'
      return new Date(val).toLocaleString('zh-CN')
    }
  },
  data() {
    return {
      isVolunteer: false,
      volunteerStatus: '',
      volunteerInfo: {},
      ranking: []
    }
  },
  mounted() {
    this.loadVolunteerStatus()
    this.loadRanking()
  },
  methods: {
    async loadVolunteerStatus() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) return
      try {
        const res = await volunteerAPI.getVolunteerInfo(user.id)
        if (res && res.id) {
          this.isVolunteer = res.status === 'approved'
          this.volunteerStatus = res.status
          this.volunteerInfo = {
            volunteerId: res.id,
            totalServiceHours: res.totalServiceHours,
            serviceHours: res.serviceHours,
            applicationTime: res.applicationTime,
            approvalTime: res.approvalTime
          }
        }
      } catch (error) {
        this.isVolunteer = false
      }
    },
    async loadRanking() {
      try {
        const res = await volunteerAPI.getRanking()
        this.ranking = res || []
      } catch (error) {
        console.error('加载排行榜失败', error)
      }
    },
    async handleBecome() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) {
        this.$message.warning('请先登录')
        return
      }
      try {
        await volunteerAPI.becomeVolunteer(user.id)
        this.$message.success('恭喜您，已成为志愿者！')
        this.loadVolunteerStatus()
        this.loadRanking()
      } catch (error) {
        const msg = error.response && error.response.data && error.response.data.message
        this.$message.error(msg || '操作失败')
      }
    },
    async handleQuit() {
      try {
        await this.$confirm('退出志愿者后，所有志愿时长将立即归零，并从排行榜中移除。确定要退出吗？', '退出志愿者', {
          type: 'warning',
          confirmButtonText: '确定退出',
          cancelButtonText: '取消'
        })
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        await volunteerAPI.quitVolunteer(user.id)
        this.$message.success('已退出志愿者，时长已归零')
        this.isVolunteer = false
        this.volunteerStatus = 'quit'
        this.volunteerInfo = {}
        this.loadRanking()
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
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
.ranking-card { margin-top: 20px; }
</style>
