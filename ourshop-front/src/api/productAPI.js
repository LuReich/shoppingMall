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

    //상품 리뷰 조회
    getReviews: async (productId, params = { page: 0, size: 10 }) => {
        const res = await api.get(`/product/${productId}/review`, { params });
        return res.data; 
    },


    // 상품 상세 정보(설명, 배송정보, 리뷰 포함)
    getDetailWithReview: async (productId) => {
        const res = await api.get(`/product/${productId}/detail`);
        return res.data;
    },

     // 상품 수정용 데이터 조회
    getProductForUpdate: async (productId) => {
        const res = await api.get(`/product/${productId}/edit`);
        return res.data;
    },


    //좋아요한 상품 조회 (구매자-로그인 필요)
    getLikedProducts: async (params = {}) => {
        const res = await api.get("/buyer/like/product/list", { params });
        return res.data;
    },


    // ReactQuill 에디터 이미지 임시 업로드
    uploadTempDescriptionImage: async (imageFile) => {
        const formData = new FormData();
        formData.append("image", imageFile);
        const res = await api.post("/product/description/temp", formData, {
        headers: { "Content-Type": "multipart/form-data" },
        });

        return res.data; // {content: {imageUrl: "/temp/xxxx.png"}}
    },

    // 상품 등록 (main + sub + productData)
    createProduct: async (formData) => {
        const res = await api.post("/product/create", formData, {
         headers: { "Content-Type": "multipart/form-data" },
        });
        return res.data;
    },

    // 상품 수정
    updateProduct: async (productId, formData) => {
        const res = await api.patch(`/product/${productId}`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return res.data;
    },

    //상품 좋아요
    likeProduct: async (productId) => {
        const res = await api.post(`/buyer/like/${productId}`);
        return res.data;
    },

    //판매자 상품 삭제 및 이유 등록
    deleteBySeller: async(productId, data) => {
        const res = await api.delete(`/seller/delete/product/${productId}`,data);
        return res.data;
    },
   
    

};
