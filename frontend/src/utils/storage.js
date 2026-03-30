// 本地存储工具类

// 设置本地存储
export const setStorage = (key, value) => {
  try {
    const jsonValue = JSON.stringify(value)
    localStorage.setItem(key, jsonValue)
  } catch (error) {
    console.error('设置本地存储失败:', error)
  }
}

// 获取本地存储
export const getStorage = (key, defaultValue = null) => {
  try {
    const jsonValue = localStorage.getItem(key)
    return jsonValue != null ? JSON.parse(jsonValue) : defaultValue
  } catch (error) {
    console.error('获取本地存储失败:', error)
    return defaultValue
  }
}

// 删除本地存储
export const removeStorage = (key) => {
  try {
    localStorage.removeItem(key)
  } catch (error) {
    console.error('删除本地存储失败:', error)
  }
}

// 清空本地存储
export const clearStorage = () => {
  try {
    localStorage.clear()
  } catch (error) {
    console.error('清空本地存储失败:', error)
  }
}

// 存储Token
export const setToken = (token) => {
  setStorage('token', token)
}

// 获取Token
export const getToken = () => {
  return getStorage('token', '')
}

// 删除Token
export const removeToken = () => {
  removeStorage('token')
}

// 存储用户信息
export const setUserInfo = (userInfo) => {
  setStorage('userInfo', userInfo)
}

// 获取用户信息
export const getUserInfo = () => {
  return getStorage('userInfo', null)
}

// 删除用户信息
export const removeUserInfo = () => {
  removeStorage('userInfo')
}
