
import api from "../axios/axios";

export const cartAPI = {
  // 장바구니 목록 조회
  getList: async () => {
    const res = await api.get("/cart/list");
    return res.data;
  },

  // 상품 추가
  addItem: async (itemData) => {
    // itemData = { productId, quantity }
    const res = await api.post("/cart/add", itemData);
    return res.data;
  },

  // 수량 변경
  updateQuantity: async (cartId, quantity) => {
    const res = await api.patch(`/cart/${cartId}`, { quantity });
    return res.data;
  },

  // 개별 상품 삭제
  deleteItem: async (cartId) => {
    const res = await api.delete(`/cart/${cartId}`);
    return res.data;
  },

  // 선택 상품 삭제
  deleteSelectedItems: async (cartIds) => {
    const res = await api.delete(`/cart/selected`, { data: cartIds });
    return res.data;
  },
};
