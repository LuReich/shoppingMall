
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { orderAPI } from "../api/orderAPI";

export const useOrder = () => {
  const qc = useQueryClient();

    // 구매자 주문 목록 조회
  const getBuyerOrders = (params = {}) =>
    useQuery({
      queryKey: ["buyerOrders", params],
      queryFn: ()=> orderAPI.getBuyerOrders(params),
    });

  // 주문 상세 조회
  const getOrderDetail = (orderId) =>
    useQuery({
      queryKey: ["orderDetail", orderId],
      queryFn: () => orderAPI.getOrderDetail(orderId),
      enabled: !!orderId,
    });
    
  // 주문 생성 (POST)
  const createOrder = () =>
    useMutation({
      mutationFn: (orderData) => orderAPI.createOrder(orderData),
      onSuccess: (data) => {
        console.log("주문 생성 완료:", data);
        qc.invalidateQueries(["buyerOrders"]);
        qc.invalidateQueries(["cartList"]);
        alert("결제가 완료되었습니다.")
      },
      onError: (err) => {
        alert("결제가 실패하였습니다.")
        console.error("주문 생성 실패:", err);
      },
    });



  return { createOrder, getBuyerOrders, getOrderDetail };
};
