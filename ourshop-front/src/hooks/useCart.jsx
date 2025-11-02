import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { cartAPI } from "../api/cartAPI";

export const useCart = () => {
  const qc = useQueryClient();

  /* ------------------------------ QUERY ------------------------------ */
  // 장바구니 목록 조회
  const getCartList = () => {
    return useQuery({
      queryKey: ["cartList"],
      queryFn: () => cartAPI.getList(),
    });
  };

  /* ---------------------------- MUTATIONS ---------------------------- */
  // 상품 추가
  const addCartItem = () => {
    return useMutation({
      mutationFn: cartAPI.addItem,
      onSuccess: () => {
        qc.invalidateQueries(["cartList"]);
        alert("상품이 장바구니에 추가되었습니다.");
      },
      onError: () => alert("장바구니 추가 실패"),
    });
  };

  // 수량 변경
  const updateCartQuantity = () => {
    return useMutation({
      mutationFn: ({ cartId, quantity }) => cartAPI.updateQuantity(cartId, quantity),
      onSuccess: () => {
        qc.invalidateQueries(["cartList"]);
         alert("수량 변경 완료");
      },
      onError: () => alert("수량 변경 실패"),
    });
  };

  // 개별 삭제
  const deleteCartItem = () => {
    return useMutation({
      mutationFn: (cartId) => cartAPI.deleteItem(cartId),
      onSuccess: () => {
        qc.invalidateQueries(["cartList"]);
        alert("상품이 장바구니에서 삭제되었습니다.");
      },
      onError: () => alert("삭제 실패"),
    });
  };

  // 선택 삭제
  const deleteSelectedCartItems = () => {
    return useMutation({
      mutationFn: (cartIds) => cartAPI.deleteSelectedItems(cartIds),
      onSuccess: () => {
        qc.invalidateQueries(["cartList"]);
        alert("상품이 삭제되었습니다.");
      },
      onError: () => alert("선택 삭제 실패"),
    });
  };

  return {
    getCartList,
    addCartItem,
    updateCartQuantity,
    deleteCartItem,
    deleteSelectedCartItems,
  };
};
