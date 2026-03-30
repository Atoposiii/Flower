// 表单验证工具类

// 验证邮箱
export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 验证手机号
export const validatePhone = (phone) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

// 验证密码强度
export const validatePassword = (password) => {
  // 密码长度至少8位
  if (password.length < 8) {
    return { valid: false, message: '密码长度至少8位' }
  }
  // 密码包含字母和数字
  if (!/[a-zA-Z]/.test(password) || !/\d/.test(password)) {
    return { valid: false, message: '密码必须包含字母和数字' }
  }
  return { valid: true, message: '' }
}

// 验证用户名
export const validateUsername = (username) => {
  // 用户名长度3-20位
  if (username.length < 3 || username.length > 20) {
    return { valid: false, message: '用户名长度3-20位' }
  }
  // 用户名只能包含字母、数字、下划线
  if (!/^[a-zA-Z0-9_]+$/.test(username)) {
    return { valid: false, message: '用户名只能包含字母、数字、下划线' }
  }
  return { valid: true, message: '' }
}

// 验证是否为空
export const validateRequired = (value, fieldName) => {
  if (!value || value.trim() === '') {
    return { valid: false, message: `${fieldName}不能为空` }
  }
  return { valid: true, message: '' }
}

// 验证长度范围
export const validateLength = (value, min, max, fieldName) => {
  if (!value) {
    return { valid: true, message: '' }
  }
  if (value.length < min || value.length > max) {
    return { valid: false, message: `${fieldName}长度${min}-${max}位` }
  }
  return { valid: true, message: '' }
}
