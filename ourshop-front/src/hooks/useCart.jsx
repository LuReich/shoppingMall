import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { cartAPI } from "../api/cartAPI";
import { useNavigate } from "react-router";

export const useCart = () => {
  const qc = useQueryClient();
  const navigate = useNavigate();

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

        if(confirm("장바구니로 이동하시겠습니까?")){
          navigate('/cart');
        }else{
          navigate(-1);
        }
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
         console.log("수량 변경 완료");
      },
      onError: () => alert("수량 변경 실패"),
    });
  };

  // 개별 삭제
  const deleteCartItem = () => {
    return useMutation({
      mutationFn: (cartId) => cartAPI.deleteItem(cartId),
      onSuccess: (res) => {
        qc.invalidateQueries(["cartList"]);
        console.log("삭제 성공",res);
      },
      onError: (err) => {
        console.log("삭제 실패", err)
      },
    });
  };

  // 선택 삭제
  const deleteSelectedCartItems = () => {
    return useMutation({
      mutationFn: (cartIds) => cartAPI.deleteSelectedItems(cartIds),
      onSuccess: () => {
        qc.invalidateQueries(["cartList"]);
        console.log("삭제 성공",res);
      },
      onError: (err) => console.log("삭제 실패", err)
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
