import api from "../axios/axios"; 

export const loginAPI = {
  // 로그인 요청
  postLogin: async (mode, credentials) => {
    try {
      const res = await api.post(`/${mode}/login`, credentials);
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  // 유저 정보 요청
  getUserInfo: async (mode) => {
    try {
      const res = await api.get(`/${mode}/me`);
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },
};
