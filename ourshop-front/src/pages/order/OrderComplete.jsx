import React from 'react';
import { useLocation } from 'react-router';
import { useOrder } from '../../hooks/useOrder';
import '../../assets/css/OrderComplete.css';

function OrderComplete(props) {
   const location = useLocation();
   const paymentMethod = location.state.paymentMethod;



   const {getBuyerOrders} = useOrder();
   const {data: orderItems} = getBuyerOrders();

   if(orderItems){
    console.log("주문완료 페이지", orderItems);
   }

   //const recentOrder = orderItems?.content?.content?.[orderItems.content.length -1];
   const recentOrder = orderItems?.content?.content?.at(-1);
   console.log("최근 주문", recentOrder);
   
    return (
        <div className='order-complete'>
          <h2>주문이 완료되었습니다.</h2>
          {
            recentOrder && 
              <div className='total-payment-info'>
                <div className='payment-into'>
                  <h3>결제 정보</h3>
                  <div>주문번호: {recentOrder.orderId}</div>
                  <div>결제 방식: {paymentMethod}</div>
                  <div>결제 금액: {recentOrder.totalPrice?.toLocaleString()} 원</div>
                </div>
                <div className='order-addr-info'>
                  <h3>주문 배송</h3>
                  <div>받는 사람: {recentOrder.recipientName}</div>
                  <div>연락처: {recentOrder.buyerPhone}</div>
                  <div>주소: {recentOrder.recipientAddress}, {recentOrder.recipientAddressDetail}</div>
                  <div>주문 상품: <strong>{recentOrder.orderDetails[0].productName}</strong>외 <strong>{recentOrder.orderDetails.length}</strong>개</div>
                </div>
              </div>
          }
        </div>
    );
}

export default OrderComplete;