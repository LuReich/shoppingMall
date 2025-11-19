import axios from "axios";
import { authStore } from "../store/authStore";

export const SERVER_URL = "http://3.105.143.5:9090";
//export const SERVER_URL="http://localhost:9090";

// axios 기본 설정
const api = axios.create({
  //baseURL: `/api/v1`,
  baseURL: `${SERVER_URL}/api/v1`,
  withCredentials: false,
});

// 모든 요청에 토큰 자동 첨부
api.interceptors.request.use((config) => {
  const token = authStore.getState().token;
  if (token) {
    // "bearer:" 또는 공백 등 제거하고 순수 토큰만 남기기
    const cleanToken = token.replace(/^bearer[: ]?/i, "").trim();
    config.headers.Authorization = `Bearer ${cleanToken}`;
  }
  return config;
});

export default api;