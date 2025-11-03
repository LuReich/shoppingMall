import api from "../axios/axios";

export const sellerAPI = {
    //판매자 업체 정보 조회
    get: async(sellerUid) => {
        const res = await api.get(`seller/public/${sellerUid}`);
        return res.data;
    },

    //판매자 상품 정보 조회 (판매자 로그인시 본인 상품만)
    //수정 필요
    getProduct: async (params={}) => {
      const res = await api.get("/seller/product/list", { params });
      return res.data;
    },

    //판매자 상품 정보 조회 (공개용, 누구나 조회 가능)
    getPublicProducts: async (sellerUid, params={}) => {
      const res = await api.get(`/seller/public/${sellerUid}/product/list`, { params });
      return res.data;
    }




}