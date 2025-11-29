import React from 'react';
import { useLocation, useNavigate } from 'react-router';
import { useOrder } from '../../hooks/useOrder';
import '../../assets/css/SellerOrderDetailInfoModal.css';

function SellerOrderDetailInfoModal({item}) {

  const navigate = useNavigate();
  //const location = useLocation();
  //const item = location.state?.item;
  const isAdminMode = item?.orderDetailId ? false : true;


  const cancledItems = item?.orderDetail?.filter((i) => i.orderDetailStatus === "CANCELED");

  console.log("취소된 상품", cancledItems);

   if(item){
    console.log("배송상품 상세 정보", item);
   }
   
    return (
        <div className='seller-order-detail-modal-container'>
          <h2>{isAdminMode? "주문 상세 정보" : "배송 상세 정보"}</h2>
          {
            item && 
              <div className='total-post-info'>
                <div className='post-info'>
                  <h3>배송 상품 정보</h3>
                  <div>주문 아이디: {isAdminMode ? item.orderId: item.orderDetailId }</div>
                  {
                    isAdminMode ?
                    <>
                      <div>구매자 식별번호: {item.buyerUid}</div>
                      <div className='order-prd-info'>
                      {
                        item.orderDetail?.map((i, index) =>(
                          <>
                          <div key={index} 
                          onClick={() => navigate(`/product/${i.productId}`)}
                          >주문 상품 {index+1}: {i.productName} ({i.quantity}개)</div>
                          </>
                        ))
                      }
                      </div>
                    </>
                    :
                    <>
                      <div onClick={() => navigate(`/product/${item.productId}`)}>주문 상품: {item.productName}</div>
                      <div>수량: {item.quantity}</div>
                    </>
                    
                  }
                  <div>결제 금액: {isAdminMode? item.totalPrice?.toLocaleString() : item.pricePerItem?.toLocaleString()} 원</div>
                  <div>주문 일시: {new Date(item.createAt).toLocaleDateString().replace(/\.$/, '')}</div>
                </div>
                <div className='seller-addr-info'>
                  <h3>배송지 정보</h3>
                  <div>수령인: {item.recipientName}</div>
                  <div>연락처: {(() => {
                          const phone = isAdminMode ? item.buyerPhone : item.recipientPhone;
                          return phone ? phone.replace(/^(\d{3})(\d{3,4})(\d{4})$/, "$1-$2-$3") : "-";
                        })()}
                  </div>
                  <div>주소: {item.recipientAddress}, {item.recipientAddressDetail}</div>
                </div>
                {
                  isAdminMode && cancledItems.length >0 && 
                  <div className='seller-addr-info'>
                  <h3 style={{color:"red", borderBottom:"2px solid red"}}>취소 상품</h3>
                  {cancledItems?.map((i,idx) => (
                    <>
                    <div>취소된 상품 {idx+1}: {i.productName}({i.quantity}개)</div>
                    <div style={{paddingBottom:"10px", borderBottom:"1px solid #ccc"}}>취소 사유: {i.orderDetailStatusReason}</div>
                    </>
                  ))}
                </div>
                }
                {
                  (!isAdminMode&& item.orderDetailStatus === "CANCELED") && 
                  <div className='seller-addr-info'>
                    <h3 style={{color:"red", borderBottom:"2px solid red"}}>취소 사유</h3>
                    <div>{item.orderDetailStatusReason}</div>
                </div>
                }
                
              </div>
          }
        </div>
    );
}

export default SellerOrderDetailInfoModal;