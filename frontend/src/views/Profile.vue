<template>
  <div class="profile">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">个人中心</h1>
        <el-button type="default" @click="goBack">返回</el-button>
      </div>
      
      <div class="profile-section">
        <h2 class="section-title">🌼个人信息🌼</h2>
        <el-form :model="userForm" :rules="rules" ref="userForm" label-width="120px" class="profile-form" hide-required-asterisk>
          
          <el-form-item label="姓名" prop="name" hide-required-asterisk>
            <el-input v-model="userForm.name" placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="userForm.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
              <el-radio label="其他">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="年龄" prop="age">
            <el-input v-model.number="userForm.age" type="number" placeholder="请输入年龄"></el-input>
          </el-form-item>
          <el-form-item label="喜欢的花类" prop="favoriteFlower">
            <el-input v-model="userForm.favoriteFlower" placeholder="请输入喜欢的花类"></el-input>
          </el-form-item>
          <el-form-item label="个性签名" prop="bio">
            <el-input v-model="userForm.bio" type="textarea" placeholder="请输入个性签名" :rows="2" style="height: 80px;"></el-input>
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role" placeholder="请选择角色" disabled>
              <el-option label="普通用户" value="USER"></el-option>
              <el-option label="会员" value="MEMBER"></el-option>
              <el-option label="志愿者" value="VOLUNTEER"></el-option>
              <el-option label="管理员" value="ADMIN"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="会员状态" prop="isMember">
            <el-switch v-model="userForm.isMember" disabled></el-switch>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('userForm')">保存修改</el-button>
            <el-button @click="resetForm('userForm')">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userForm: {
        name: '',
        gender: '',
        age: null,
        favoriteFlower: '',
        bio: '',
        role: '',
        isMember: false
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback()
              } else if (this.$store.state.user.usedNames.includes(value) && value !== this.userInfo?.name) {
                callback(new Error('该姓名已被使用，请更换一个'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        age: [
          {
            validator: (rule, value, callback) => {
              if (value === null || value === '') {
                callback()
              } else if (isNaN(value)) {
                callback(new Error('请输入有效的年龄'))
              } else {
                const numValue = Number(value)
                if (numValue < 1 || numValue > 150) {
                  callback(new Error('年龄在 1 到 150 之间'))
                } else {
                  callback()
                }
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.user.userInfo || {}
    },
    isLoggedIn() {
      return this.$store.state.user.isLoggedIn
    }
  },
  mounted() {
    this.loadUserInfo()
    // 如果未登录，跳转到登录页面
    if (!this.isLoggedIn) {
      this.$router.push('/login')
    }
  },
  methods: {
    loadUserInfo() {
      if (this.userInfo) {
        this.userForm = {
          name: this.userInfo.name || '',
          gender: this.userInfo.gender || '',
          age: this.userInfo.age || null,
          favoriteFlower: this.userInfo.favoriteFlower || '',
          bio: this.userInfo.bio || '',
          role: this.userInfo.role || 'USER',
          isMember: this.userInfo.isMember || false
        }
      } else {
        this.userForm = {
          name: '',
          gender: '',
          age: null,
          favoriteFlower: '',
          bio: '',
          role: 'USER',
          isMember: false
        }
      }
    },
    submitForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$store.dispatch('user/updateUserInfo', this.userForm)
            this.$message.success('个人信息更新成功！')
            this.loadUserInfo()
          } else {
            return false
          }
        })
      }
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields()
        this.$message.success('表单已重置！')
      }
    },
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
/* 全局强制黑体+抗锯齿，提升清晰度 */
* {
  font-family: "SimHei", "Microsoft YaHei", "黑体", sans-serif !important;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 确保所有Element UI组件字体统一 */
.el-form-item__label,
.el-input__inner,
.el-textarea__inner,
.el-button,
.el-radio__label,
.el-select__input,
.el-select-dropdown__item,
.el-switch__label {
  font-family: "SimHei", "Microsoft YaHei", "黑体", sans-serif !important;
}

/* 确保占位符字体统一 */
.el-input__inner::placeholder,
.el-textarea__inner::placeholder {
  font-family: "SimHei", "Microsoft YaHei", "黑体", sans-serif !important;
}

/* 统一所有输入框字体设置 */
.profile-form .el-input__inner,
.profile-form .el-textarea__inner {
  font-family: "SimHei", "Microsoft YaHei", "黑体", sans-serif !important;
  font-size: 14px !important;
  line-height: 1.4 !important;
  padding: 8px 10px !important;
}

/* 个性签名输入框特殊设置 */
.profile-form .el-textarea {
  width: 100%;
}

.profile-form .el-textarea__inner {
  height: 80px !important;
  resize: none !important;
}

.profile {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('/个人中心背景.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  padding: 20px 0;
}

.container {
  max-width: 700px;
  width: 100%;
  padding: 30px 25px;
  background-color: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.page-title {
  font-size: 28px;
  color: #000 !important;
  font-weight: 600;
  margin: 0;
}

.page-header .el-button {
  margin-left: 10px;
  padding: 6px 12px;
  background-color: rgba(255, 255, 255, 0.3);
  color: #000 !important;
  border-color: rgba(255, 255, 255, 0.5);
}

.profile-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  margin-bottom: 20px;
  color: #000 !important;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 8px;
  font-weight: 500;
}

.profile-form {
  background-color: transparent;
  backdrop-filter: none;
  padding: 0;
  border-radius: 0;
  border: none;
  box-shadow: none;
}

/* 表单标签强制纯黑，清晰可见 */
.profile-form .el-form-item__label {
  color: #000 !important;
  font-weight: 500;
  font-size: 14px;
  padding: 0;
}

.profile-form .el-form-item {
  margin-bottom: 18px;
}

/* 输入框文字纯黑，占位符也纯黑（降低透明度但不发灰） */
.profile-form .el-input__inner,
.profile-form .el-textarea__inner {
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border-color: rgba(255, 255, 255, 0.5);
  color: #000 !important;
  font-size: 14px;
  padding: 8px 10px;
  border-radius: 6px;
}

.profile-form .el-input__inner::placeholder,
.profile-form .el-textarea__inner::placeholder {
  color: #000 !important;
  opacity: 0.7 !important;
}

/* 单选框文字纯黑 */
.profile-form .el-radio__label {
  color: #000 !important;
  font-size: 14px;
}

/* 下拉框文字纯黑 */
.profile-form .el-select {
  .el-select__input {
    background-color: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(8px);
    color: #000 !important;
    font-size: 14px;
    padding: 8px 10px;
  }
  .el-select__caret {
    color: #000 !important;
  }
}

/* 按钮文字纯黑 */
.profile-form .el-button {
  margin-right: 8px;
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  color: #000 !important;
  border-color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  border-radius: 6px;
}

.profile-form .el-button--primary {
  background-color: rgba(64, 158, 255, 0.2);
  backdrop-filter: blur(8px);
  border-color: rgba(64, 158, 255, 0.5);
  color: #000 !important;
}

/* 开关文字纯黑 */
.profile-form .el-switch {
  .el-switch__core {
    background-color: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(8px);
    border-color: rgba(255, 255, 255, 0.5);
  }
  .el-switch__label {
    color: #000 !important;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 20px 15px;
    margin: 0 10px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .section-title {
    font-size: 18px;
  }
  
  .profile-form .el-form-item {
    margin-bottom: 15px;
  }
}
</style>