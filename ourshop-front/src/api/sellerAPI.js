import api from "../axios/axios";

export const sellerAPI = {
    //판매자 업체 정보 조회
    get: async(sellerUid) => {
        const res = await api.get(`seller/public/${sellerUid}`);
        return res.data;
    },

    //판매자 상품 정보 조회
    getProduct: async (params={}) => {
    const res = await api.get("/seller/products", { params });
    return res.data;
  },


}