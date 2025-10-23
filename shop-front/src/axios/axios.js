import axios from "axios";
import { authStore } from "../store/authStore";

//axios 기본 설정
const api = axios.create({
  baseURL: "http://localhost:9090/api", //스프링부트 엔드포인트
  headers: {},
  withCredentials: false,
});

// 모든 요청에 토큰 자동 첨부
api.interceptors.request.use((config) => {
  const token = authStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
