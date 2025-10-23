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

  // 상품 상세 조회
  const getProductDetail = (productId) => {
    return useQuery({
      queryKey: ["product", productId],
      queryFn: () => productAPI.getDetail(productId),
      enabled: !!productId,
    });
  };

  // 상품 상세 + 리뷰 포함 조회
  const getProductDetailWithReview = (productId) => {
    return useQuery({
      queryKey: ["productDetailWithReview", productId],
      queryFn: () => productAPI.getDetailWithReview(productId),
      enabled: !!productId,
    });
  };

  /* ---------------------------- MUTATIONS ---------------------------- */

  // 상품 등록
  const createProduct = () => {
    return useMutation({
      mutationFn: (productData) => productAPI.create(productData),
      onSuccess: () => {
        qc.invalidateQueries(["productList"]);
        alert("상품이 성공적으로 등록되었습니다.");
      },
      onError: (err) => {
        console.error(err);
        alert("상품 등록 실패.");
      },
    });
  };

  // 상품 삭제
  const deleteProduct = () => {
    return useMutation({
      mutationFn: (productId) => productAPI.delete(productId),
      onSuccess: () => {
        qc.invalidateQueries(["productList"]);
        alert("상품이 삭제되었습니다.");
      },
      onError: (err) => {
        console.error(err);
        alert("상품 삭제 실패.");
      },
    });
  };

  // 상품 상세 정보 등록/수정
  const createOrUpdateDetail = () => {
    return useMutation({
      mutationFn: ({ productId, detailData }) =>
        productAPI.createOrUpdateDetail(productId, detailData),
      onSuccess: (_, variables) => {
        qc.invalidateQueries(["productDetailWithReview", variables.productId]);
        alert("상품 상세 정보가 업데이트되었습니다.");
      },
      onError: (err) => {
        console.error(err);
        alert("상품 상세 정보 등록/수정 실패.");
      },
    });
  };

  return {
    getProductList,
    getProductDetail,
    getProductDetailWithReview,
    createProduct,
    deleteProduct,
    createOrUpdateDetail,
  };
};
