import api from "../axios/axios";

export const qnaAPI = {
    // [구매자] 본인 문의 리스트 조회
    getList: async(params = {}) => {
        const res = await api.get("/buyer/buyerInquiry/list", {params});
        return res.data;
    
    },

    // [구매자] 상세 문의 조회
    getDetail: async(buyerInquiryId) =>{
        const res = await api.get(`/buyer/buyerInquiry/${buyerInquiryId}`);
        return res.data;
    },

    // [구매자] 문의 등록 (이미지 포함)
    create: async (formData) => {
        const res = await api.post("/buyer/create/buyerInquiry", formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return res.data;
    },

    // [구매자] 문의 수정 (이미지 포함)
    update: async({buyerInquiryId, formData}) => {
        const res = await api.patch(`/buyer/update/buyerInquiry/${buyerInquiryId}`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return res.data;
    },

    // [구매자] 문의 삭제
    delete: async(buyerInquiryId) => {
        const res = await api.delete(`/buyer/delete/buyerInquiry/${buyerInquiryId}`);
        return res.data;
    }

    
}