import api from "../axios/axios";

export const reviewAPI = {
  // 내가 쓴 리뷰 목록 조회 (페이지네이션, 상품명 검색 포함)
  getMyReviews: async (params={}) => {
    const res = await api.get("/buyer/reviews", { params });
    return res.data;
  },

  //리뷰 등록
  create: async (reviewData) => {
    const res = await api.post("/review/write", reviewData);
    return res.data;
  },

  //리뷰 수정
  update: async({reviewId,reviewData}) => {
    const res = await api.patch(`/review/${reviewId}`, reviewData);
    return res.data;
  },

  //리뷰 삭제
  delete : async (reviewId) => {
    const res = await api.delete(`/review/${reviewId}`);
    return res.data;   
  }
};

