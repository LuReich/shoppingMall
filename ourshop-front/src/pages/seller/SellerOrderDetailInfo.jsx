import React from 'react';
import { useLocation, useNavigate } from 'react-router';
import { useOrder } from '../../hooks/useOrder';
import '../../assets/css/SellerOrderDetailInfo.css';

function SellerOrderDetailInfo({item}) {

  const navigate = useNavigate();
  //const location = useLocation();
  //const item = location.state?.item;

   if(item){
    console.log("배송상품 상세 정보", item);
   }
   
    return (
        <div className='seller-order-detail-modal-container'>
          <h2>배송 상세 정보</h2>
          {
            item && 
              <div className='total-post-info'>
                <div className='post-info'>
                  <h3>배송 상품 정보</h3>
                  <div>주문번호: {item.orderDetailId}</div>
                  <div onClick={() => navigate(`/product/${item.productId}`)}>주문 상품: {item.productName}</div>
                  <div>수량: {item.quantity}</div>
                  <div>결제 금액: {item.pricePerItem?.toLocaleString()} 원</div>
                  <div>주문 일시: {item.createAt}</div>
                </div>
                <div className='seller-addr-info'>
                  <h3>배송지 정보</h3>
                  <div>받는 사람: {item.recipientName}</div>
                  <div>연락처: {item.recipientPhone}</div>
                  <div>주소: {item.recipientAddress}, {item.recipientAddressDetail}</div>
                </div>
              </div>
          }
        </div>
    );
}

export default SellerOrderDetailInfo;