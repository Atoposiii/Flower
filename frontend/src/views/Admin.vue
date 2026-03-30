<template>
  <div class="admin-dashboard">
    <div class="sidebar">
      <div class="logo">
        <div class="logo-icon">🌻</div>
        <h2>花卉科普管理<br/>后台</h2>
      </div>
      <el-menu :default-active="activeMenu" class="sidebar-menu" @select="handleMenuSelect">
        <el-menu-item index="welcome">
          <i class="el-icon-house"></i>
          <span slot="title">欢迎首页</span>
        </el-menu-item>
        <el-menu-item index="flower">
          <i class="el-icon-s-claim"></i>
          <span slot="title">花卉百科管理</span>
        </el-menu-item>
        <el-menu-item index="community">
          <i class="el-icon-chat-dot-round"></i>
          <span slot="title">互动社区管理</span>
        </el-menu-item>
        <el-submenu index="volunteer">
          <template slot="title">
            <i class="el-icon-s-custom"></i>
            <span>志愿服务管理</span>
          </template>
          <el-menu-item index="volunteer-records">志愿者记录</el-menu-item>
          <el-menu-item index="volunteer-activities">志愿活动</el-menu-item>
          <el-menu-item index="volunteer-ranking">志愿者排行</el-menu-item>
        </el-submenu>
        <el-menu-item index="member">
          <i class="el-icon-s-finance"></i>
          <span slot="title">会员中心管理</span>
        </el-menu-item>
        <el-menu-item index="customer">
          <i class="el-icon-service"></i>
          <span slot="title">人工客服</span>
        </el-menu-item>
        <el-menu-item index="user">
          <i class="el-icon-user"></i>
          <span slot="title">用户管理</span>
        </el-menu-item>
      </el-menu>
    </div>
    
    <div class="main-content">
      <div class="header">
        <div class="header-left">
          <h1>{{ currentPageTitle }}</h1>
        </div>
        <div class="header-right">
          <span>{{ currentTime }}</span>
          <el-button type="danger" size="small" @click="logout">退出登录</el-button>
        </div>
      </div>
      
      <div class="content">
        <div v-if="activeMenu === 'welcome'" class="welcome-page">
          <div class="welcome-card">
            <h1>🎉 欢迎进入花卉科普网站后台</h1>
            <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
          </div>
          
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">👥</div>
              <div class="stat-info">
                <h3>{{ userCount }}</h3>
                <p>注册用户</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🌸</div>
              <div class="stat-info">
                <h3>{{ flowerCount }}</h3>
                <p>花卉百科</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">💬</div>
              <div class="stat-info">
                <h3>{{ postCount }}</h3>
                <p>社区帖子</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🤝</div>
              <div class="stat-info">
                <h3>{{ volunteerCount }}</h3>
                <p>志愿者</p>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="activeMenu === 'flower'" class="flower-management">
          <div class="action-bar">
            <el-button type="primary" @click="showAddFlowerDialog = true">
              <i class="el-icon-plus"></i> 添加花卉
            </el-button>
            <el-input v-model="flowerSearch" placeholder="搜索花卉名称" style="width: 300px;" clearable></el-input>
          </div>
          
          <el-table :data="filteredFlowers" style="width: 100%" stripe>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="name" label="花卉名称" width="150"></el-table-column>
            <el-table-column prop="scientificName" label="学名" width="180"></el-table-column>
            <el-table-column prop="origin" label="原产地"></el-table-column>
            <el-table-column prop="floweringPeriod" label="花期"></el-table-column>
            <el-table-column prop="isRecommended" label="推荐" width="80">
              <template slot-scope="scope">
                <el-tag :type="scope.row.isRecommended ? 'success' : 'info'">
                  {{ scope.row.isRecommended ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template slot-scope="scope">
                <el-button type="primary" size="small" @click="editFlower(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="deleteFlower(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div v-if="activeMenu === 'community'" class="community-management">
          <div class="action-bar">
            <el-select v-model="postStatusFilter" placeholder="帖子状态" style="width: 150px;">
              <el-option label="全部" value=""></el-option>
              <el-option label="正常" value="active"></el-option>
              <el-option label="已删除" value="deleted"></el-option>
            </el-select>
          </div>
          
          <el-table :data="filteredPosts" style="width: 100%" stripe>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="title" label="标题" width="250"></el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
            <el-table-column prop="viewCount" label="浏览" width="80"></el-table-column>
            <el-table-column prop="likeCount" label="点赞" width="80"></el-table-column>
            <el-table-column prop="commentCount" label="评论" width="80"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
                  {{ scope.row.status === 'active' ? '正常' : '已删除' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template slot-scope="scope">
                <el-button type="danger" size="small" @click="deletePost(scope.row.id)" v-if="scope.row.status === 'active'">
                  删除
                </el-button>
                <el-button type="success" size="small" @click="restorePost(scope.row.id)" v-else>
                  恢复
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div v-if="activeMenu === 'volunteer-records'" class="volunteer-records">
          <el-table :data="volunteerRecords" style="width: 100%" stripe>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column label="用户信息" width="150">
              <template slot-scope="scope">
                <div v-if="scope.row.user">
                  <div>{{ scope.row.user.username }}</div>
                  <div style="font-size: 12px; color: #999">{{ scope.row.realName }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="applicationTime" label="申请时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.applicationTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.status === 'approved'" type="success">已批准</el-tag>
                <el-tag v-else-if="scope.row.status === 'pending'" type="warning">待审批</el-tag>
                <el-tag v-else-if="scope.row.status === 'rejected'" type="danger">已拒绝</el-tag>
                <el-tag v-else-if="scope.row.status === 'quit'" type="info">已退出</el-tag>
                <el-tag v-else>{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="quitTime" label="退出时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.quitTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="serviceHours" label="服务时长" width="100">
              <template slot-scope="scope">
                {{ scope.row.serviceHours || 0 }}小时
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button type="primary" size="small" @click="openUpdateHoursDialog(scope.row)" v-if="scope.row.status === 'approved'">更新时长</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div v-if="activeMenu === 'volunteer-activities'" class="volunteer-activities">
          <div class="action-bar">
            <el-button type="primary" @click="showAddActivityDialog = true">
              <i class="el-icon-plus"></i> 发布活动
            </el-button>
          </div>
          
          <el-table :data="volunteerActivities" style="width: 100%" stripe>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="title" label="活动标题" width="200"></el-table-column>
            <el-table-column prop="description" label="活动描述" show-overflow-tooltip></el-table-column>
            <el-table-column prop="location" label="活动地点" width="150"></el-table-column>
            <el-table-column prop="activityTime" label="活动时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.activityTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="maxParticipants" label="人数上限" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'">
                  {{ scope.row.status === 'active' ? '进行中' : '已结束' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right">
              <template slot-scope="scope">
                <el-button type="info" size="small" @click="viewActivitySignups(scope.row)">
                  报名记录
                </el-button>
                <el-button type="primary" size="small" @click="editActivity(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="deleteActivity(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div v-if="activeMenu === 'volunteer-ranking'" class="volunteer-ranking">
          <div class="action-bar">
            <el-button type="primary" @click="loadVolunteerRanking">刷新排行</el-button>
          </div>
          
          <el-table :data="volunteerRanking" style="width: 100%" stripe>
            <el-table-column type="index" label="排名" width="80">
              <template slot-scope="scope">
                <span v-if="scope.$index === 0" style="color: #FFD700; font-weight: bold;">🥇 1</span>
                <span v-else-if="scope.$index === 1" style="color: #C0C0C0; font-weight: bold;">🥈 2</span>
                <span v-else-if="scope.$index === 2" style="color: #CD7F32; font-weight: bold;">🥉 3</span>
                <span v-else>{{ scope.$index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="用户信息" width="150">
              <template slot-scope="scope">
                <div v-if="scope.row.user">
                  <div>{{ scope.row.user.username }}</div>
                  <div style="font-size: 12px; color: #999">{{ scope.row.realName }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="serviceHours" label="服务时长" width="120">
              <template slot-scope="scope">
                <el-tag type="warning" size="medium">{{ scope.row.serviceHours || 0 }}小时</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastServiceTime" label="最后服务时间">
              <template slot-scope="scope">
                {{ formatDate(scope.row.lastServiceTime) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div v-if="activeMenu === 'member'" class="member-management">
          <el-tabs v-model="memberTab">
            <el-tab-pane label="会员开通记录" name="records">
              <el-table :data="memberRecords" style="width: 100%" stripe>
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column label="用户" width="120">
                  <template slot-scope="scope">
                    <div v-if="scope.row.user">
                      {{ scope.row.user.username }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="amount" label="金额" width="100">
                  <template slot-scope="scope">
                    ¥{{ scope.row.amount || 0 }}
                  </template>
                </el-table-column>
                <el-table-column prop="months" label="时长" width="100">
                  <template slot-scope="scope">
                    {{ scope.row.months || 0 }}个月
                  </template>
                </el-table-column>
                <el-table-column prop="paymentMethod" label="支付方式" width="120"></el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template slot-scope="scope">
                    <el-tag type="success">已到账</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="开通时间">
                  <template slot-scope="scope">
                    {{ formatDate(scope.row.createTime) }}
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
        
        <div v-if="activeMenu === 'customer'" class="customer-service">
          <div class="chat-container">
            <div class="chat-list">
              <div class="chat-list-header">
                <h3>咨询列表 ({{ chatList.length }})</h3>
                <el-button type="text" @click="loadConsultations">刷新</el-button>
              </div>
              <div class="chat-list-item" v-for="chat in chatList" :key="chat.id"
                   :class="{ active: currentChatId === chat.id, unread: chat.unread }"
                   @click="selectChat(chat)">
                <div class="chat-user">
                  {{ chat.username }}
                  <el-tag :type="chat.type === 'vip' ? 'warning' : 'info'" size="mini">
                    {{ chat.type === 'vip' ? '💎会员' : '🌸非会员' }}
                  </el-tag>
                </div>
                <div class="chat-preview">{{ chat.lastMessage }}</div>
                <div class="chat-meta">
                  <span class="chat-time">{{ formatDate(chat.lastTime) }}</span>
                  <el-tag v-if="chat.flowerName" size="mini" type="success">{{ chat.flowerName }}</el-tag>
                </div>
                <el-tag v-if="chat.unread" type="danger" size="mini" class="unread-tag">待回复</el-tag>
                <el-tag v-else type="success" size="mini" class="unread-tag">已回复</el-tag>
              </div>
              <div v-if="chatList.length === 0" class="empty-tip">
                <p>暂无咨询记录</p>
              </div>
            </div>

            <div class="chat-detail">
              <div class="chat-detail-header" v-if="currentChat">
                <div>
                  <h3>{{ currentChat.username }}</h3>
                  <el-tag :type="currentChat.type === 'vip' ? 'warning' : 'info'">
                    {{ currentChat.type === 'vip' ? '💎会员频道' : '🌸非会员频道' }}
                  </el-tag>
                  <el-tag v-if="currentChat.flowerName" type="success">{{ currentChat.flowerName }}</el-tag>
                </div>
                <el-tag :type="currentChat.status === 'replied' ? 'success' : 'danger'">
                  {{ currentChat.status === 'replied' ? '已回复' : '待回复' }}
                </el-tag>
              </div>

              <div class="chat-messages" v-if="currentChat">
                <div v-for="msg in currentChat.messages" :key="msg.id"
                     :class="['message', msg.fromAdmin ? 'admin' : 'user']">
                  <div class="message-content">{{ msg.content }}</div>
                  <div class="message-time">{{ formatDate(msg.time) }}</div>
                </div>
              </div>

              <div class="chat-input" v-if="currentChat">
                <el-input type="textarea" :rows="3" v-model="replyMessage" placeholder="输入回复内容..."></el-input>
                <el-button type="primary" @click="sendReply" :disabled="!replyMessage.trim()">
                  发送回复
                </el-button>
              </div>

              <div v-if="!currentChat" class="no-chat-selected">
                <p>请从左侧选择一条咨询进行回复</p>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="activeMenu === 'user'" class="user-management">
          <div class="action-bar">
            <el-input v-model="userSearch" placeholder="搜索用户名或邮箱" style="width: 300px;" clearable></el-input>
          </div>
          
          <el-table :data="filteredUsers" style="width: 100%" stripe>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="username" label="用户名" width="120"></el-table-column>
            <el-table-column prop="nickname" label="昵称" width="120"></el-table-column>
            <el-table-column prop="email" label="邮箱"></el-table-column>
            <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
            <el-table-column prop="role" label="角色" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.role === 'admin' ? 'danger' : scope.row.role === 'volunteer' ? 'warning' : 'primary'">
                  {{ scope.row.role === 'admin' ? '管理员' : scope.row.role === 'volunteer' ? '志愿者' : '用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="isMember" label="会员" width="80">
              <template slot-scope="scope">
                <el-tag :type="scope.row.isMember ? 'success' : 'info'">
                  {{ scope.row.isMember ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
                  {{ scope.row.status === 'active' ? '活跃' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template slot-scope="scope">
                <el-button type="primary" size="small" @click="editUser(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    
    <el-dialog title="添加/编辑花卉" :visible.sync="showAddFlowerDialog" width="80%" top="5vh">
      <el-form :model="flowerForm" label-width="130px" class="flower-form">
        <!-- 第一部分：基础档案 -->
        <el-divider content-position="left">📋 一、基础档案</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="花卉名称">
              <el-input v-model="flowerForm.name" placeholder="请输入花卉正式名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拉丁学名">
              <el-input v-model="flowerForm.scientificName" placeholder="例如：Rosa rugosa"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="别名俗称">
          <el-input v-model="flowerForm.aliases" placeholder="例如：月季、刺玫花、徘徊花（多个用顿号分隔）"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科属">
              <el-input v-model="flowerForm.family" placeholder="例如：蔷薇科蔷薇属"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="植株类型">
              <el-select v-model="flowerForm.plantType" placeholder="请选择" style="width: 100%">
                <el-option label="一年生草本" value="一年生草本"></el-option>
                <el-option label="多年生草本" value="多年生草本"></el-option>
                <el-option label="灌木" value="灌木"></el-option>
                <el-option label="乔木" value="乔木"></el-option>
                <el-option label="藤本" value="藤本"></el-option>
                <el-option label="球根花卉" value="球根花卉"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原产地">
              <el-input v-model="flowerForm.origin" placeholder="例如：中国、日本、地中海地区"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生长环境">
              <el-input v-model="flowerForm.growthEnvironment" placeholder="例如：温带山地、湿地、沙漠边缘"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二部分：形态特征 -->
        <el-divider content-position="left">🌿 二、形态特征</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="株高">
              <el-input v-model="flowerForm.plantHeight" placeholder="例如：30-60厘米、1-2米"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="香气类型">
              <el-input v-model="flowerForm.fragranceType" placeholder="例如：浓郁芳香、淡淡清香、无香味、果香"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="茎干特征">
          <el-input type="textarea" :rows="2" v-model="flowerForm.stemFeatures" placeholder="例如：直立生长，表面有细毛，基部木质化"></el-input>
        </el-form-item>
        <el-form-item label="叶片特征">
          <el-input type="textarea" :rows="2" v-model="flowerForm.leafFeatures" placeholder="例如：羽状复叶，小叶5-9片，边缘有锯齿，深绿色"></el-input>
        </el-form-item>
        <el-form-item label="花朵特征">
          <el-input type="textarea" :rows="2" v-model="flowerForm.flowerFeatures" placeholder="例如：单生或簇生，花径5-8厘米，花瓣20-30片，重瓣"></el-input>
        </el-form-item>
        <el-form-item label="花蕊花萼">
          <el-input type="textarea" :rows="2" v-model="flowerForm.flowerStamenFeatures" placeholder="例如：雄蕊多数，金黄色；花萼5裂，绿色"></el-input>
        </el-form-item>
        <el-form-item label="果实种子">
          <el-input type="textarea" :rows="2" v-model="flowerForm.fruitSeedFeatures" placeholder="例如：蔷薇果，球形，红色，内含多数小种子"></el-input>
        </el-form-item>
        <el-form-item label="观赏特点">
          <el-input type="textarea" :rows="2" v-model="flowerForm.ornamentalFeatures" placeholder="例如：花大色艳，花期长，香气浓郁，适合庭院观赏"></el-input>
        </el-form-item>

        <!-- 第三部分：生长与花期 -->
        <el-divider content-position="left">🌱 三、生长与花期</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="花期">
              <el-input v-model="flowerForm.floweringPeriod" placeholder="例如：5-6月、春季、全年"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开花频率">
              <el-input v-model="flowerForm.floweringFrequency" placeholder="例如：一年一次、一年多次、四季开花"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="适宜温度">
              <el-input v-model="flowerForm.suitableTemperature" placeholder="例如：15-25℃，耐寒-10℃"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="光照需求">
              <el-select v-model="flowerForm.lightRequirements" placeholder="请选择" style="width: 100%">
                <el-option label="全日照" value="全日照"></el-option>
                <el-option label="半日照" value="半日照"></el-option>
                <el-option label="耐阴" value="耐阴"></el-option>
                <el-option label="喜阴" value="喜阴"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="水分需求">
              <el-select v-model="flowerForm.waterRequirements" placeholder="请选择" style="width: 100%">
                <el-option label="喜湿润" value="喜湿润"></el-option>
                <el-option label="耐旱" value="耐旱"></el-option>
                <el-option label="怕积水" value="怕积水"></el-option>
                <el-option label="适中" value="适中"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="土壤偏好">
              <el-input v-model="flowerForm.soilPreference" placeholder="例如：疏松肥沃、排水良好、微酸性"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生长速度">
              <el-select v-model="flowerForm.growthSpeed" placeholder="请选择" style="width: 100%">
                <el-option label="快" value="快"></el-option>
                <el-option label="中等" value="中等"></el-option>
                <el-option label="慢" value="慢"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适宜季节">
              <el-input v-model="flowerForm.suitableSeason" placeholder="例如：春季、秋季"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="种植方式">
          <el-radio-group v-model="flowerForm.plantingMethod">
            <el-radio label="地栽">地栽</el-radio>
            <el-radio label="盆栽">盆栽</el-radio>
            <el-radio label="均可">均可</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生长习性">
          <el-input type="textarea" :rows="2" v-model="flowerForm.growthHabit" placeholder="例如：喜阳光充足，耐寒性较强，适合温暖湿润环境，怕积水"></el-input>
        </el-form-item>

        <!-- 第四部分：花语与文化寓意 -->
        <el-divider content-position="left">🌸 四、花语与文化寓意</el-divider>
        <el-form-item label="核心花语">
          <el-input type="textarea" :rows="2" v-model="flowerForm.flowerLanguage" placeholder="例如：爱情、纯洁、友谊、坚强、富贵、思念"></el-input>
        </el-form-item>
        <el-form-item label="颜色含义">
          <el-input type="textarea" :rows="2" v-model="flowerForm.colorMeanings" placeholder="例如：红玫瑰-热烈爱情，白玫瑰-纯洁尊敬，粉玫瑰-初恋"></el-input>
        </el-form-item>
        <el-form-item label="赠送对象">
          <el-input type="textarea" :rows="2" v-model="flowerForm.suitableRecipients" placeholder="例如：恋人、家人、老师、病人、朋友、长辈"></el-input>
        </el-form-item>
        <el-form-item label="适用场合">
          <el-input type="textarea" :rows="2" v-model="flowerForm.suitableOccasions" placeholder="例如：生日、婚礼、探病、道歉、祝福、表白"></el-input>
        </el-form-item>
        <el-form-item label="市花/国花">
          <el-input v-model="flowerForm.isCityFlower" placeholder="例如：中国国花、北京市花、美国国花"></el-input>
        </el-form-item>
        <el-form-item label="传说故事">
          <el-input type="textarea" :rows="3" v-model="flowerForm.legend" placeholder="相关的传说故事、神话或历史典故"></el-input>
        </el-form-item>

        <!-- 第五部分：用途价值 -->
        <el-divider content-position="left">🎯 五、用途价值</el-divider>
        <el-form-item label="观赏用途">
          <el-input type="textarea" :rows="2" v-model="flowerForm.uses" placeholder="例如：庭院观赏、盆栽、切花、花艺搭配、花坛"></el-input>
        </el-form-item>
        <el-form-item label="食用价值">
          <el-input type="textarea" :rows="2" v-model="flowerForm.isEdible" placeholder="例如：可食用，泡茶、入菜、做甜点等；不可食用"></el-input>
        </el-form-item>
        <el-form-item label="药用价值">
          <el-input type="textarea" :rows="2" v-model="flowerForm.medicinalValue" placeholder="例如：活血化瘀、清热解毒、理气解郁等功效"></el-input>
        </el-form-item>

        <!-- 第六部分：趣味小知识 -->
        <el-divider content-position="left">💡 六、趣味小知识</el-divider>
        <el-form-item label="有趣特点">
          <el-input type="textarea" :rows="3" v-model="flowerForm.funFacts" placeholder="例如：会睡觉（昼开夜合）、会变色、夜间开花、世界之最、名人典故、诗词歌赋、冷知识等"></el-input>
        </el-form-item>

        <!-- 第七部分：搭配与禁忌 -->
        <el-divider content-position="left">⚠️ 七、搭配与禁忌</el-divider>
        <el-form-item label="适合搭配">
          <el-input type="textarea" :rows="2" v-model="flowerForm.goodCompanions" placeholder="例如：满天星、尤加利叶、康乃馨"></el-input>
        </el-form-item>
        <el-form-item label="赠送禁忌">
          <el-input type="textarea" :rows="2" v-model="flowerForm.taboos" placeholder="例如：不宜送给病人、不宜在葬礼使用、数量禁忌等"></el-input>
        </el-form-item>
        <el-form-item label="安全性">
          <el-input v-model="flowerForm.isToxic" placeholder="例如：无毒，适合家庭种植；有毒，不适合有宠物/小孩家庭；汁液有毒，避免接触"></el-input>
        </el-form-item>

        <!-- 第八部分：养护管理 -->
        <el-divider content-position="left">🌱 八、养护管理</el-divider>
        <el-form-item label="浇水方法">
          <el-input type="textarea" :rows="2" v-model="flowerForm.wateringTips" placeholder="例如：见干见湿，每周浇水2-3次，夏季早晚浇，冬季中午浇"></el-input>
        </el-form-item>
        <el-form-item label="施肥方法">
          <el-input type="textarea" :rows="2" v-model="flowerForm.fertilizationTips" placeholder="例如：生长期每月施肥1次，花期前增施磷钾肥，冬季停止施肥"></el-input>
        </el-form-item>
        <el-form-item label="修剪方法">
          <el-input type="textarea" :rows="2" v-model="flowerForm.pruningTips" placeholder="例如：花后及时修剪残花，春季整形修剪，剪除病弱枝"></el-input>
        </el-form-item>
        <el-form-item label="繁殖方式">
          <el-input type="textarea" :rows="2" v-model="flowerForm.propagation" placeholder="例如：扦插繁殖（春秋季）、播种繁殖（春季）、分株繁殖（早春）"></el-input>
        </el-form-item>
        <el-form-item label="病虫害防治">
          <el-input type="textarea" :rows="2" v-model="flowerForm.pestControl" placeholder="例如：蚜虫-用吡虫啉喷洒；白粉病-用多菌灵防治"></el-input>
        </el-form-item>
        <el-form-item label="过冬注意">
          <el-input type="textarea" :rows="2" v-model="flowerForm.winterCare" placeholder="例如：移入室内，减少浇水，停止施肥，保持5℃以上"></el-input>
        </el-form-item>
        <el-form-item label="度夏注意">
          <el-input type="textarea" :rows="2" v-model="flowerForm.summerCare" placeholder="例如：适当遮阴，增加浇水频率，注意通风降温"></el-input>
        </el-form-item>
        <el-form-item label="常见问题">
          <el-input type="textarea" :rows="2" v-model="flowerForm.commonProblems" placeholder="例如：黄叶-可能是浇水过多或缺铁；不开花-可能是光照不足"></el-input>
        </el-form-item>

        <!-- 其他信息 -->
        <el-divider content-position="left">📷 其他信息</el-divider>
        <el-form-item label="图片URL">
          <el-input v-model="flowerForm.imageUrl" placeholder="请输入图片链接地址"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="花色">
              <el-input v-model="flowerForm.color" placeholder="例如：红色、粉色、白色"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主要特点">
          <el-input type="textarea" :rows="2" v-model="flowerForm.description" placeholder="花卉的主要特点简介"></el-input>
        </el-form-item>
        <el-form-item label="推荐">
          <el-switch v-model="flowerForm.isRecommended" active-text="是" inactive-text="否"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showAddFlowerDialog = false">取消</el-button>
        <el-button type="primary" @click="saveFlower">保存</el-button>
      </span>
    </el-dialog>
    
    <el-dialog title="更新服务时长" :visible.sync="showUpdateHoursDialog" width="400px">
      <el-form label-width="100px">
        <el-form-item label="志愿者">
          <span>{{ currentVolunteer?.realName || '-' }}</span>
        </el-form-item>
        <el-form-item label="当前时长">
          <span>{{ currentVolunteer?.serviceHours || 0 }}小时</span>
        </el-form-item>
        <el-form-item label="新增时长">
          <el-input-number v-model="additionalHours" :min="1" :max="1000"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showUpdateHoursDialog = false">取消</el-button>
        <el-button type="primary" @click="updateServiceHours">确认</el-button>
      </span>
    </el-dialog>
    
    <el-dialog title="添加志愿活动" :visible.sync="showAddActivityDialog" width="60%">
      <el-form :model="activityForm" label-width="100px">
        <el-form-item label="活动标题">
          <el-input v-model="activityForm.title"></el-input>
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input type="textarea" :rows="4" v-model="activityForm.description"></el-input>
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="activityForm.location"></el-input>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker v-model="activityForm.activityTime" type="datetime" placeholder="选择时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="activityForm.maxParticipants" :min="1" :max="1000"></el-input-number>
        </el-form-item>
        <el-form-item label="服务时长">
          <el-input-number v-model="activityForm.serviceHours" :min="1" :max="24"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showAddActivityDialog = false">取消</el-button>
        <el-button type="primary" @click="saveActivity">保存</el-button>
      </span>
    </el-dialog>
    
    <el-dialog :title="editingUser ? '编辑用户' : '添加用户'" :visible.sync="showUserDialog" width="50%">
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" :disabled="!!editingUser"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!editingUser">
          <el-input type="password" v-model="userForm.password"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="userForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role">
            <el-option label="用户" value="user"></el-option>
            <el-option label="志愿者" value="volunteer"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员状态">
          <el-switch v-model="userForm.isMember"></el-switch>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="userForm.status">
            <el-option label="活跃" value="active"></el-option>
            <el-option label="禁用" value="inactive"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showUserDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import userApi from '../api/modules/user'
import volunteerApi from '../api/modules/volunteer'
import flowerApi from '../api/modules/flower'
import postApi from '../api/modules/post'
import volunteerActivityApi from '../api/modules/volunteerActivity'

export default {
  data() {
    return {
      activeMenu: 'welcome',
      memberTab: 'records',
      currentTime: '',
      timer: null,
      
      userCount: 0,
      flowerCount: 0,
      postCount: 0,
      volunteerCount: 0,
      
      users: [],
      userSearch: '',
      showUserDialog: false,
      editingUser: null,
      userForm: {
        username: '',
        password: '',
        nickname: '',
        email: '',
        phone: '',
        role: 'user',
        isMember: false,
        status: 'active'
      },
      
      flowers: [],
      flowerSearch: '',
      showAddFlowerDialog: false,
      editingFlower: null,
      flowerForm: {
        // 基础档案
        name: '',
        scientificName: '',
        aliases: '',
        family: '',
        plantType: '',
        origin: '',
        growthEnvironment: '',
        // 形态特征
        plantHeight: '',
        stemFeatures: '',
        leafFeatures: '',
        flowerFeatures: '',
        flowerStamenFeatures: '',
        fragranceType: '',
        fruitSeedFeatures: '',
        ornamentalFeatures: '',
        // 生长与花期
        floweringPeriod: '',
        floweringFrequency: '',
        suitableTemperature: '',
        lightRequirements: '',
        waterRequirements: '',
        soilPreference: '',
        growthSpeed: '',
        suitableSeason: '',
        plantingMethod: '',
        growthHabit: '',
        // 花语与文化
        flowerLanguage: '',
        colorMeanings: '',
        suitableRecipients: '',
        suitableOccasions: '',
        isCityFlower: '',
        legend: '',
        // 用途价值
        uses: '',
        isEdible: '',
        medicinalValue: '',
        // 趣味小知识
        funFacts: '',
        // 搭配与禁忌
        goodCompanions: '',
        taboos: '',
        isToxic: '',
        // 养护管理
        wateringTips: '',
        fertilizationTips: '',
        pruningTips: '',
        propagation: '',
        pestControl: '',
        winterCare: '',
        summerCare: '',
        commonProblems: '',
        // 其他信息
        color: '',
        description: '',
        imageUrl: '',
        isRecommended: false
      },
      
      posts: [],
      postStatusFilter: '',
      
      volunteerRecords: [],
      showUpdateHoursDialog: false,
      currentVolunteer: null,
      additionalHours: 1,
      
      volunteerActivities: [],
      showAddActivityDialog: false,
      editingActivity: null,
      activityForm: {
        title: '',
        description: '',
        location: '',
        activityTime: null,
        maxParticipants: 20,
        serviceHours: 2
      },
      
      volunteerRanking: [],
      
      memberRecords: [],
      
      chatList: [],
      currentChatId: null,
      currentChat: null,
      replyMessage: ''
    }
  },
  computed: {
    currentPageTitle() {
      const titles = {
        'welcome': '欢迎首页',
        'flower': '花卉百科管理',
        'community': '互动社区管理',
        'volunteer-records': '志愿者记录',
        'volunteer-activities': '志愿活动管理',
        'volunteer-ranking': '志愿者排行',
        'member': '会员中心管理',
        'customer': '人工客服',
        'user': '用户管理'
      }
      return titles[this.activeMenu] || '管理后台'
    },
    currentDate() {
      return new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
    },
    filteredUsers() {
      if (!this.userSearch) return this.users
      return this.users.filter(u => 
        u.username.includes(this.userSearch) || 
        (u.email && u.email.includes(this.userSearch))
      )
    },
    filteredFlowers() {
      if (!this.flowerSearch) return this.flowers
      return this.flowers.filter(f => f.name.includes(this.flowerSearch))
    },
    filteredPosts() {
      if (!this.postStatusFilter) return this.posts
      return this.posts.filter(p => p.status === this.postStatusFilter)
    }
  },
  mounted() {
    const savedUserInfo = localStorage.getItem('userInfo')
    const isLoggedIn = savedUserInfo && JSON.parse(savedUserInfo) && Object.keys(JSON.parse(savedUserInfo)).length > 0
    if (!isLoggedIn) {
      this.$router.push('/login')
      return
    }
    this.updateTime()
    this.timer = setInterval(this.updateTime, 1000)
    this.loadStats()
    this.loadUsers()
    this.loadFlowers()
    this.loadPosts()
    this.loadVolunteers()
    this.loadActivities()
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    updateTime() {
      this.currentTime = new Date().toLocaleString('zh-CN')
    },
    handleMenuSelect(index) {
      this.activeMenu = index
      if (index === 'volunteer-ranking') {
        this.loadVolunteerRanking()
      }
      if (index === 'customer') {
        this.loadConsultations()
      }
    },
    logout() {
      this.$store.dispatch('user/logout')
      this.$router.push('/login')
    },
    async loadStats() {
      try {
        const userRes = await userApi.getAllUsers()
        this.userCount = Array.isArray(userRes.data) ? userRes.data.length : 0

        const flowerRes = await flowerApi.getAllFlowers()
        this.flowerCount = Array.isArray(flowerRes.data) ? flowerRes.data.length : 0

        const postRes = await postApi.getAllPosts()
        this.postCount = Array.isArray(postRes.data) ? postRes.data.length : 0

        const volunteerRes = await volunteerApi.getApprovedVolunteers()
        this.volunteerCount = Array.isArray(volunteerRes.data) ? volunteerRes.data.length : 0
      } catch (e) {
        console.error(e)
      }
    },
    async loadFlowers() {
      try {
        const res = await flowerApi.getAllFlowers()
        this.flowers = Array.isArray(res.data) ? res.data : []
        this.flowerCount = this.flowers.length
      } catch (e) {
        console.error(e)
      }
    },
    async loadPosts() {
      try {
        const res = await postApi.getAllPosts()
        const postsArray = Array.isArray(res.data) ? res.data : []
        this.posts = postsArray.map(p => ({ ...p, status: 'active' }))
        this.postCount = this.posts.length
      } catch (e) {
        console.error(e)
      }
    },
    async loadActivities() {
      try {
        const res = await volunteerActivityApi.getAllActivities()
        this.volunteerActivities = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error(e)
        this.$message.error('加载志愿活动失败')
      }
    },
    async loadUsers() {
      try {
        const response = await userApi.getAllUsers()
        this.users = Array.isArray(response.data) ? response.data : []
      } catch (error) {
        console.error('加载用户失败:', error)
      }
    },
    editUser(user) {
      this.editingUser = user
      this.userForm = { ...user, password: '' }
      this.showUserDialog = true
    },
    async saveUser() {
      try {
        if (this.editingUser) {
          await userApi.updateUser(this.editingUser.id, this.userForm)
          this.$message.success('用户更新成功')
        } else {
          await userApi.createUser(this.userForm)
          this.$message.success('用户添加成功')
        }
        this.showUserDialog = false
        this.loadUsers()
        this.editingUser = null
        this.userForm = { username: '', password: '', nickname: '', email: '', phone: '', role: 'user', isMember: false, status: 'active' }
      } catch (error) {
        this.$message.error('保存失败')
      }
    },
    async deleteUser(id) {
      try {
        await this.$confirm('确定删除该用户吗？')
        await userApi.deleteUser(id)
        this.$message.success('删除成功')
        this.loadUsers()
      } catch (e) {
        if (e !== 'cancel') this.$message.error('删除失败')
      }
    },
    editFlower(flower) {
      this.editingFlower = flower
      this.flowerForm = { ...flower }
      this.showAddFlowerDialog = true
    },
    async saveFlower() {
      try {
        if (this.editingFlower) {
          await flowerApi.updateFlower(this.editingFlower.id, this.flowerForm)
        } else {
          await flowerApi.addFlower(this.flowerForm)
        }
        this.$message.success('保存成功')
        this.showAddFlowerDialog = false
        this.editingFlower = null
        this.flowerForm = {
          name: '',
          scientificName: '',
          family: '',
          origin: '',
          floweringPeriod: '',
          color: '',
          description: '',
          growthHabit: '',
          flowerLanguage: '',
          wateringTips: '',
          fertilizationTips: '',
          propagation: '',
          commonProblems: '',
          isCityFlower: '',
          legend: '',
          isEdible: '',
          medicinalValue: '',
          funFacts: '',
          uses: '',
          imageUrl: '',
          isRecommended: false
        }
        this.loadFlowers()
      } catch (e) {
        console.error(e)
        this.$message.error('保存失败')
      }
    },
    async deleteFlower(id) {
      try {
        await this.$confirm('确定删除该花卉吗？')
        await flowerApi.deleteFlower(id)
        this.$message.success('删除成功')
        this.loadFlowers()
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    deletePost(id) {
      this.$confirm('确定删除该帖子吗？').then(() => {
        const post = this.posts.find(p => p.id === id)
        if (post) {
          post.status = 'deleted'
        }
        this.$message.success('删除成功')
      }).catch(() => {})
    },
    restorePost(id) {
      const post = this.posts.find(p => p.id === id)
      if (post) {
        post.status = 'active'
      }
      this.$message.success('恢复成功')
    },
    async loadVolunteers() {
      try {
        const res = await volunteerApi.getAllVolunteers()
        this.volunteerRecords = Array.isArray(res.data) ? res.data : []
        this.volunteerCount = this.volunteerRecords.filter(v => v.status === 'approved').length
      } catch (e) {
        console.error(e)
      }
    },
    openUpdateHoursDialog(volunteer) {
      this.currentVolunteer = volunteer
      this.additionalHours = 1
      this.showUpdateHoursDialog = true
    },
    async updateServiceHours() {
      try {
        const total = (this.currentVolunteer.serviceHours || 0) + this.additionalHours
        await volunteerApi.updateServiceHours(this.currentVolunteer.id, total)
        this.$message.success('更新成功')
        this.showUpdateHoursDialog = false
        this.loadVolunteers()
      } catch (e) {
        this.$message.error('更新失败')
      }
    },
    async loadVolunteerRanking() {
      try {
        const res = await volunteerApi.getVolunteersByServiceHours()
        this.volunteerRanking = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error(e)
      }
    },
    editActivity(activity) {
      this.editingActivity = activity
      this.activityForm = { ...activity }
      this.showAddActivityDialog = true
    },
    async saveActivity() {
      try {
        if (this.editingActivity) {
          await volunteerActivityApi.updateActivity(this.editingActivity.id, this.activityForm)
          this.$message.success('更新成功')
        } else {
          await volunteerActivityApi.createActivity(this.activityForm)
          this.$message.success('添加成功')
        }
        this.showAddActivityDialog = false
        this.editingActivity = null
        this.activityForm = { title: '', description: '', location: '', activityTime: null, maxParticipants: 20, serviceHours: 2 }
        this.loadActivities()
      } catch (e) {
        console.error(e)
        this.$message.error('保存失败')
      }
    },
    async deleteActivity(id) {
      try {
        await this.$confirm('确定删除该活动吗？')
        await volunteerActivityApi.deleteActivity(id)
        this.$message.success('删除成功')
        this.loadActivities()
      } catch (e) {
        if (e !== 'cancel') {
          console.error(e)
          this.$message.error('删除失败')
        }
      }
    },
    viewActivitySignups(activity) {
      this.$message.info(`活动"${activity.title}"的报名记录功能开发中...`)
    },
    async loadConsultations() {
      try {
        const response = await this.$http.get('/consultation/list/all')
        this.chatList = (response.data || []).map(item => ({
          id: item.id,
          username: item.userId ? `用户${item.userId}` : '游客',
          lastMessage: item.message,
          lastTime: item.createTime,
          unread: item.status === 'pending',
          type: item.type,
          flowerName: item.flowerName,
          originalMessage: item.message,
          reply: item.reply,
          status: item.status
        }))
      } catch (error) {
        console.error('加载咨询列表失败:', error)
      }
    },
    selectChat(chat) {
      this.currentChatId = chat.id
      this.currentChat = {
        ...chat,
        messages: [
          { id: 1, content: chat.originalMessage || chat.lastMessage, fromAdmin: false, time: chat.lastTime }
        ]
      }
      if (chat.reply) {
        this.currentChat.messages.push({
          id: 2,
          content: chat.reply,
          fromAdmin: true,
          time: chat.updateTime || chat.lastTime
        })
      }
      chat.unread = false
    },
    async sendReply() {
      if (!this.replyMessage.trim() || !this.currentChat) return
      try {
        await this.$http.post('/consultation/reply', {
          id: this.currentChat.id,
          reply: this.replyMessage
        })
        this.currentChat.messages.push({
          id: this.currentChat.messages.length + 1,
          content: this.replyMessage,
          fromAdmin: true,
          time: new Date()
        })
        this.currentChat.lastMessage = this.replyMessage
        this.currentChat.lastTime = new Date()
        this.currentChat.status = 'replied'
        this.currentChat.reply = this.replyMessage
        this.replyMessage = ''
        this.$message.success('回复成功')
        this.loadConsultations()
      } catch (error) {
        this.$message.error('回复失败')
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc;
  font-size: 17px;
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
  color: #333;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e2e8f0;
}

.logo {
  padding: 24px 16px;
  text-align: center;
  border-bottom: 1px solid #e2e8f0;
}

.logo-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.logo h2 {
  font-size: 22px;
  margin: 0;
  color: #2d3748;
  font-weight: 700;
  line-height: 1.4;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

.sidebar-menu >>> .el-menu-item,
.sidebar-menu >>> .el-submenu__title {
  color: #4a5568;
  font-size: 17px;
  height: 60px;
  line-height: 60px;
  border-bottom: 1px solid #f1f5f9;
}

.sidebar-menu >>> .el-menu-item:hover,
.sidebar-menu >>> .el-submenu__title:hover {
  background-color: #e0f2fe;
  color: #0369a1;
}

.sidebar-menu >>> .el-menu-item.is-active {
  background-color: #0ea5e9;
  color: #fff;
}

.sidebar-menu >>> .el-menu-item.is-active i,
.sidebar-menu >>> .el-menu-item.is-active span {
  color: #fff;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: #ffffff;
  padding: 20px 36px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border-bottom: 1px solid #e2e8f0;
}

.header h1 {
  font-size: 26px;
  margin: 0;
  color: #1e293b;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
  color: #64748b;
  font-size: 17px;
}

.content {
  flex: 1;
  padding: 24px 36px;
  overflow-y: auto;
}

.welcome-page {
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-card {
  background: linear-gradient(135deg, #0ea5e9 0%, #38bdf8 100%);
  color: #fff;
  padding: 48px;
  border-radius: 16px;
  text-align: center;
  margin-bottom: 36px;
  box-shadow: 0 4px 20px rgba(14,165,233,0.2);
}

.welcome-card h1 {
  font-size: 36px;
  margin-bottom: 16px;
  font-weight: 700;
}

.welcome-card p {
  font-size: 18px;
  opacity: 0.95;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.stat-card {
  background: #ffffff;
  padding: 32px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #f1f5f9;
}

.stat-icon {
  font-size: 56px;
}

.stat-info h3 {
  font-size: 36px;
  margin: 0 0 6px 0;
  color: #1e293b;
  font-weight: 700;
}

.stat-info p {
  margin: 0;
  color: #64748b;
  font-size: 16px;
}

.action-bar {
  margin-bottom: 24px;
  display: flex;
  gap: 16px;
}

.chat-container {
  display: flex;
  height: 650px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #f1f5f9;
}

.chat-list {
  width: 320px;
  border-right: 1px solid #e2e8f0;
}

.chat-list-header {
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
}

.chat-list-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.chat-list-item {
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  position: relative;
}

.chat-list-item:hover {
  background: #f1f5f9;
}

.chat-list-item.active {
  background: #e0f2fe;
  border-left: 4px solid #0ea5e9;
}

.chat-user {
  font-weight: 600;
  margin-bottom: 6px;
  font-size: 16px;
  color: #1e293b;
}

.chat-preview {
  font-size: 14px;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-time {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 6px;
}

.unread-tag {
  position: absolute;
  top: 20px;
  right: 20px;
}

.chat-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.empty-tip {
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
}

.no-chat-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 16px;
}

.chat-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-detail-header {
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-detail-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.message {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
}

.message.user {
  align-items: flex-start;
}

.message.admin {
  align-items: flex-end;
}

.message-content {
  padding: 14px 20px;
  border-radius: 12px;
  max-width: 70%;
  font-size: 16px;
}

.message.user .message-content {
  background: #f1f5f9;
  color: #1e293b;
}

.message.admin .message-content {
  background: #0ea5e9;
  color: #fff;
}

.message-time {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 6px;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e2e8f0;
}

.chat-input .el-textarea {
  margin-bottom: 12px;
}

/* 表格样式优化 */
.admin-dashboard >>> .el-table {
  font-size: 15px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #f1f5f9;
}

.admin-dashboard >>> .el-table th {
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
  font-size: 15px;
}

.admin-dashboard >>> .el-table td {
  padding: 16px 0;
}

.admin-dashboard >>> .el-button {
  font-size: 14px;
  padding: 10px 20px;
  border-radius: 8px;
}

.admin-dashboard >>> .el-input__inner {
  font-size: 15px;
  height: 42px;
  border-radius: 8px;
}

.admin-dashboard >>> .el-dialog {
  border-radius: 16px;
}

.admin-dashboard >>> .el-dialog__header {
  padding: 24px 24px 16px;
}

.admin-dashboard >>> .el-dialog__title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.admin-dashboard >>> .el-dialog__body {
  padding: 16px 24px 24px;
}

.admin-dashboard >>> .el-form-item__label {
  font-size: 15px;
}

.admin-dashboard >>> .el-tag {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 6px;
}
</style>
