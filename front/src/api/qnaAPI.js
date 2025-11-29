import api from "../axios/axios";

export const qnaAPI = {
    // 문의 리스트 조회 (mode: buyer | seller)
    getList: async(mode, params = {}) => {
        const res = await api.get(`/${mode}/${mode}Inquiry/list`, {params});
        return res.data;
    
    },

    // 상세 문의 조회 (mode: buyer | seller)
    getDetail: async(mode, inquiryId) =>{
        const res = await api.get(`/${mode}/${mode}Inquiry/${inquiryId}`);
        return res.data;
    },

    // 문의 등록 (mode: buyer | seller)
    create: async (mode, formData) => {
        const res = await api.post(`/${mode}/create/${mode}Inquiry`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return res.data;
    },

    // 문의 수정 (mode: buyer | seller)
    update: async({mode, inquiryId, formData}) => {
        const res = await api.patch(`/${mode}/update/${mode}Inquiry/${inquiryId}`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return res.data;
    },

    // 문의 삭제 (mode: buyer | seller)
    delete: async(mode, inquiryId) => {
        const res = await api.delete(`/${mode}/delete/${mode}Inquiry/${inquiryId}`);
        return res.data;
    }

    
}