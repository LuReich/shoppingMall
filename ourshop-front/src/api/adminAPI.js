import { get } from "react-hook-form";
import api from "../axios/axios"

export const adminAPI = {

    //회원 리스트 가져오기 (params에 검색, 정렬, 페이지네이션)
    get: async (mode, params={}) => {
        const res = await api.get(`/admin/${mode}/list`, {params});
        return res.data;
    },

    //회원 상세정보 가져오기
    getDetail: async (mode, uid) => {
        const res = await api.get(`/admin/${mode}/${uid}/detail`);
        return res.data;
    },

    //전체 상품 리스트 가져오기
    getProduct: async (params ={}) => {
        const res = await api.get("/admin/product/list", {params});
        return res.data;
    
    },


    //문의 리스트 보기
    getQnaList: async (mode, params = {}) =>{
        const res = await api.get(`/admin/${mode}Inquiry/list`, {params});
        return res.data;
    
    },

    //문의 개별 보기
    getQnaDetail: async (mode, inquiryId) => {
        const res = await api.get(`/admin/${mode}Inquiry/${inquiryId}`);
        return res.data;
    },

    //주문 전체 리스트 가져오기
    getOrderList : async (params= {}) => {
        const res = await api.get("/admin/orders/list", {params});
        return res.data;
    },

    //주문 상세
    getOrderInfo : async (orderId) => {
        const res = await api.get(`admin/orders/${orderId}`);
        return res.data;
    },

    //관리자 권한 - 회원 정보 수정
    update: async (mode, uid, data) => {
        const res = await api.patch(`/admin/update/${mode}/${uid}`, data);
        return res.data;
    },

    //상품 소프트삭제 및 삭제 사유 수정
    deleteProduct: async (productId, data) => {
        const res = await api.patch(`admin/product/change-deletion-status/${productId}`, data);
        return res.data;
    },


    //문의 답변하기
    answerQna: async (mode, inquiryId, data) => {
        const res = await api.patch(`/admin/answer/${mode}Inquiry/${inquiryId}`, data);
        return res.data;

    },

    


 
}