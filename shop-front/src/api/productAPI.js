import api from "../axios/axios";

export const productAPI = {

    // 상품 목록 조회 (categoryId, page, size 등 params 가능)
    getList: async (params = {}) => {
        const res = await api.get("/product/list", { params });
        return res.data;
    },

    // 상품 상세 조회
    getDetail: async (productId) => {
        const res = await api.get(`/product/${productId}`);
        return res.data;
    },

    // 상품 등록
    create: async (productData) => {
        const res = await api.post("/product", productData);
        return res.data;
    },

    // 상품 삭제
    delete: async (productId) => {
        const res = await api.delete(`/product/${productId}`);
        return res.data;
    },

    // 상품 상세 정보(설명, 배송정보, 리뷰 포함)
    getDetailWithReview: async (productId) => {
        const res = await api.get(`/product/${productId}/detail`);
        return res.data;
    },

    // 상품 상세 등록 or 수정
    createOrUpdateDetail: async (productId, detailData) => {
        const res = await api.post(`/product/${productId}/detail`, detailData);
        return res.data;
    },
};
