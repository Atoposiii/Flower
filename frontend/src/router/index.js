import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/common/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/common/Register.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/user/Home.vue')
  },
  {
    path: '/flowers',
    name: 'FlowerList',
    component: () => import('@/views/user/FlowerList.vue')
  },
  {
    path: '/flower/:id',
    name: 'FlowerDetail',
    component: () => import('@/views/user/FlowerDetail.vue')
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/user/Community.vue')
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/user/PostDetail.vue')
  },
  {
    path: '/volunteer',
    name: 'Volunteer',
    component: () => import('@/views/user/Volunteer.vue')
  },
  {
    path: '/activities',
    name: 'Activities',
    component: () => import('@/views/user/Activities.vue')
  },
  {
    path: '/volunteer-service',
    redirect: '/activities'
  },
  {
    path: '/member',
    name: 'Member',
    component: () => import('@/views/user/Member.vue')
  },
  {
    path: '/member/center',
    name: 'MemberCenter',
    component: () => import('@/views/MemberCenter.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue')
  },
  {
    path: '/account',
    name: 'AccountManagement',
    component: () => import('@/views/AccountManagement.vue')
  },
  {
    path: '/consultation',
    name: 'Consultation',
    component: () => import('@/views/user/Consultation.vue')
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: () => import('@/views/user/Feedback.vue')
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'flowers',
        name: 'AdminFlowers',
        component: () => import('@/views/admin/FlowerManage.vue')
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/PostManage.vue')
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('@/views/admin/CommentManage.vue')
      },
      {
        path: 'consultations',
        name: 'AdminConsultations',
        component: () => import('@/views/admin/ConsultationManage.vue')
      },
      {
        path: 'feedbacks',
        name: 'AdminFeedbacks',
        component: () => import('@/views/admin/FeedbackManage.vue')
      },
      {
        path: 'volunteers',
        name: 'AdminVolunteers',
        component: () => import('@/views/admin/VolunteerManage.vue')
      },
      {
        path: 'activities',
        name: 'AdminActivities',
        component: () => import('@/views/admin/ActivityManage.vue')
      },
      {
        path: 'members',
        name: 'AdminMembers',
        component: () => import('@/views/admin/MemberManage.vue')
      },
      {
        path: 'payments',
        name: 'AdminPayments',
        component: () => import('@/views/admin/PaymentManage.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue')
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes
})

const authRequired = ['/profile', '/account']

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (to.path.startsWith('/admin')) {
    if (!user.role || user.role !== 'ADMIN') {
      next('/login')
      return
    }
  }
  if (authRequired.includes(to.path)) {
    if (!user.id) {
      next('/login')
      return
    }
  }
  if (to.path === '/login' || to.path === '/register') {
    if (user.id) {
      next(user.role === 'ADMIN' ? '/admin' : '/home')
      return
    }
  }
  next()
})

export default router
