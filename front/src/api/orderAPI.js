import api from "../axios/axios";

// 주문 관련 API
export const orderAPI = {
  // 주문 생성
  createOrder: async (orderData) => {
    const res = await api.post("/orders", orderData);
    return res.data;
  },

  // 주문 목록 조회 (구매자 기준)
  getBuyerOrders: async (params={}) => {
    const res = await api.get("/buyer/orders", {params});
    return res.data;
  },

  // 주문 상세 조회
  getOrderDetail: async (orderId) => {
    const res = await api.get(`/orders/${orderId}`);
    return res.data;
  },
};
