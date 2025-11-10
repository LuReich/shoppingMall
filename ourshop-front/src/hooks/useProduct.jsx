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
  const getProductReview = (productId, params = {}) => {
    return useQuery({
      queryKey: ["productReview", productId, params],
      queryFn: () => productAPI.getReviews(productId, params),
      enabled: !!productId,
    });
  };

  // 상품 수정용 데이터 조회
  const getProductForUpdate = (productId) => {
    return useQuery({
      queryKey: ["productForUpdate", productId],
      queryFn: () => productAPI.getProductForUpdate(productId),
      enabled: !!productId,
    });
  };

  //좋아요 누른 상품 조회
  const getLikedProducts = (params = {}) => {
    
    return useQuery({
      queryKey: ["likedProducts", params],
      queryFn: () => productAPI.getLikedProducts(params),
    });
  }
  
  /* ---------------------------- MUTATIONS ---------------------------- */

  // React Quill 이미지 임시 업로드
  const uploadTempDescriptionImage = () => {
    return useMutation({
      mutationFn: (imageFile) => productAPI.uploadTempDescriptionImage(imageFile),
      onError: (err) => {
        console.error(err);
        alert("에디터 이미지 업로드 실패");
      },
    });
  };

  // 상품 등록
  const createProduct = () => {
    return useMutation({
      mutationFn: (formData) => productAPI.createProduct(formData),
      onSuccess: () => {
        qc.invalidateQueries(["productList"]);
      },
      onError: (err) => {
        console.error(err);
        alert(err.response?.data?.content || "상품 등록 실패.");
      },
    });
  };

  // 상품 수정
  const updateProduct = () => {
    return useMutation({
      mutationFn: ({ productId, formData }) => productAPI.updateProduct(productId, formData),
      onSuccess: () => {
        qc.invalidateQueries(["productList"]);
        qc.invalidateQueries(["product"]);
        qc.invalidateQueries(["productDescription"]);
      },
      onError: (err) => {
        console.error(err);
        alert(err.response?.data?.content || "상품 수정 실패.");
      },
    });
  };

  //상품 좋아요
  const likeProduct = () => {
    return useMutation({
      mutationFn: (productId) => productAPI.likeProduct(productId),
      onSuccess: (res) => {
        qc.invalidateQueries(["product"]);
        console.log("좋아요", res.content);
      
      },
      onError: (err) => {
        console.error(err);
        
      },
    });  

  };

  //판매자 상품 삭제 및 이유 
  const deleteProduct = () => {
    return useMutation({
      mutationFn: ({productId, data}) => productAPI.deleteBySeller(productId, data),
      onSuccess: (res) => {
        qc.invalidateQueries(["productList"]);
        console.log("상품 삭제", res.content);
        alert("상품이 성공적으로 삭제되었습니다.")
      
      },
      onError: (err) => {
        console.error(err);
        alert(err.response?.data?.content || "상품삭제 실패.");
      },
    });




  }
   
        

  return {
    getProductList,
    getProductDetail,
    getProductDescription,
    getProductReview,
    getLikedProducts,
    uploadTempDescriptionImage,
    createProduct,
    updateProduct,
    getProductForUpdate,
    likeProduct,
    deleteProduct
  };
}
