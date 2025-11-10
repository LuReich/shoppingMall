import api from "../axios/axios";

export const faqAPI = {
    
    //FAQ 리스트 조회
    getList: async(params={}) => {
        const res = await api.get("/faq/list", {params});
        return res.data;
    },

    //단일 FAQ 조회
    getDetail: async(faqId)=> {
        const res = await api.get(`/faq/${faqId}`);
        return res.data;
    },

    //FAQ 등록
    create: async (data) => {
        const res = await api.post("/faq/create", data);
        return res.data;
    
    },

    //FAQ 수정
    update: async (faqId, data) => {
        const res = await api.patch(`/faq/update/${faqId}`, data);
        return res.data;
    },

    //FAQ 삭제 
    delete: async (faqId) => {
        const res = await api.delete(`/faq/delete/${faqId}`);
        return res.data;
    }

}