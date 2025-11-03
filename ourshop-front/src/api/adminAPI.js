import api from "../axios/axios"

export const adminAPI = {

    //회원 리스트 가져오기 (params에 검색, 정렬, 페이지네이션)
    get: async (mode, params={}) => {
        const res = await api.get(`/admin/${mode}/list`, {params});
        return res.data;
    },

    getDetail: async (mode, uid) => {
        const res = await api.get(`/admin/${mode}/${uid}/detail`);
        return res.data;
    },

}