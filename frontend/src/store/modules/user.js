export default {
  namespaced: true,
  state: {
    userInfo: null,
    token: null
  },
  mutations: {
    setUserInfo(state, userInfo) {
      state.userInfo = userInfo
    },
    setToken(state, token) {
      state.token = token
    },
    clearUserInfo(state) {
      state.userInfo = null
      state.token = null
    }
  },
  actions: {
    updateUserInfo({ commit }, userInfo) {
      commit('setUserInfo', userInfo)
    },
    updateToken({ commit }, token) {
      commit('setToken', token)
    },
    logout({ commit }) {
      commit('clearUserInfo')
    }
  },
  getters: {
    userInfo: state => state.userInfo,
    token: state => state.token,
    isLoggedIn: state => !!state.token
  }
}