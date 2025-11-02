import api from "../axios/axios";

export const registerAPI = {

  // 아이디 중복 확인  (판매자, 구매자)
  checkId: async (mode, id) => {
    console.log("[API] 아이디 중복확인 요청:", { id });
    const response = await api.post(`/${mode}/check-${mode}Id`, { [`${mode}Id`]: id });
    return response.data; // { success, message, data }
  
  },
  // 이메일 중복 확인 (판매자, 구매자)
  checkEmail: async (mode, email) => {
    console.log("[API] 이메일 중복확인 요청:", { email });
    const response = await api.post(`/${mode}/check-email`, { [`${mode}Email`]: email });
    return response.data; // { success, message, data }
  },

  // 전화번호 중복 확인 (판매자, 구매자)
  checkPhone: async (mode, phone) => {
    console.log("[API] 전화번호 중복확인 요청:", { phone });
    const response = await api.post(`/${mode}/check-phone`, { phone });
    return response.data;
  },

  //사업자 등록번호 중복 확인 (판매자)
  checkBusinessNumber: async(mode, businessNumber) => {
    console.log("[API] 사업자 등록번호 중복확인 요청:", { businessNumber });
    const response = await api.post(`/${mode}/check-businessRegistrationNumber`, { businessRegistrationNumber: businessNumber });
    return response.data;

  },

  // 회원가입 (판매자/ 구매자)
  register: async (mode, data) => {
    console.log("[API] 회원가입 요청:", data);
    const response = await api.post(`/${mode}/register`, data);
    return response.data;
  },

  // 판매자 회원정보 수정
  update: async (mode, uid, data) => {
    const response = await api.patch(`/${mode}/${uid}`, data);
    return response.data;
  },

  // 회원 탈퇴 (구매자/판매자)
  withdraw: async (mode, withdrawalReason = "") => {
    console.log("[API] 회원탈퇴 요청:", { withdrawalReason });
    const response = await api.patch(`/${mode}/withdraw`, { withdrawalReason });
    return response.data; // { success, message, content }
  },
};
