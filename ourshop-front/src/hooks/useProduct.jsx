import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { productAPI } from "../api/productAPI"

export const useProduct = () => {
  const qc = useQueryClient();

  /* ------------------------------ QUERY ------------------------------ */

  // 상품 목록 조회
  const getProductList = (params = {}) => {
    return useQuery({
      queryKey: ["productList", params],
      queryFn: () => productAPI.getList(params),
    });
  };

  // 상품 상세 정보
  const getProductDetail = (productId) => {
    return useQuery({
      queryKey: ["product", productId],
      queryFn: () => productAPI.getDetail(productId),
      enabled: !!productId,
    });
  };

  // 상품 설명 + 배송 정보 조회
  const getProductDescription = (productId) => {
    return useQuery({
      queryKey: ["productDescription", productId],
      queryFn: () => productAPI.getDetailWithReview(productId),
      enabled: !!productId,
    });
  };

  //상품 리뷰 조회
  const getProductReview = (productId, params = { page: 0, size: 10 }) => {
    return useQuery({
      queryKey: ["productReview", productId, params],
      queryFn: () => productAPI.getReviews(productId, params),
      enabled: !!productId,
    });
  };
  
  /* ---------------------------- MUTATIONS ---------------------------- */

  // ✅ React Quill 이미지 임시 업로드
  const uploadTempDescriptionImage = () => {
    return useMutation({
      mutationFn: (imageFile) => productAPI.uploadTempDescriptionImage(imageFile),
      onError: (err) => {
        console.error(err);
        alert("에디터 이미지 업로드 실패");
      },
    });
  };

  // ✅ 상품 등록
  const createProduct = () => {
    return useMutation({
      mutationFn: (formData) => productAPI.createProduct(formData),
      onSuccess: () => {
        qc.invalidateQueries(["productList"]);
        alert( "상품이 성공적으로 등록되었습니다.");
      },
      onError: (err) => {
        console.error(err);
        alert(err.response?.data?.content || "상품 등록 실패.");
      },
    });
  };
   
        

  return {
    getProductList,
    getProductDetail,
    getProductDescription,
    getProductReview,
    uploadTempDescriptionImage,
    createProduct
  };
};
