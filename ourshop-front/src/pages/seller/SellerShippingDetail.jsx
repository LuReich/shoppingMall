import React, { Fragment, useEffect, useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import "../../assets/css/ShippingDetail.css";
import Pagination from '../../components/common/Pagenation';
import Sort from '../../components/common/Sort';
import { useNavigate } from 'react-router';
import SellerOrderDetailInfo from './SellerOrderDetailInfo';

function SellerShippingDetail(props) {

    const navigate = useNavigate();
    const [sort, setSort] = useState("product.productName,desc");
    const [page, setPage] = useState(0);
    const [selectedItem, setSelectedItem] = useState(null);



    const { getDeliverySellerProductList, updateDeliveryStatus } = useSeller();
    const { data: deliverySellerProductsCont, isLoading, isError }
        = getDeliverySellerProductList({ sort, size: 5, page });

    const { mutate: updateStatusMutate } = updateDeliveryStatus();

    if (isLoading) return <p>판매내역을 조회중입니다.</p>;
    if (isError) return <p>판매내역 조회에 실패했습니다.</p>;

    const deliverySellerProducts = deliverySellerProductsCont?.content?.content || [];
    const totalPages = deliverySellerProductsCont?.content?.totalPages || 0;

    console.log("판매내역", deliverySellerProducts);

    const sortCateg = {
        "createAt": "order.createAt",
        "productName": "product.productName",
    };

    const statusKor = {
        "PAID": "결제완료",
        "SHIPPING": "배송중",
        "DELIVERED": "배송완료",
        "CANCELED": "취소"
    };

    // 배송 상태 변경 핸들러
    const handleStatusChange = (orderDetailId, newStatus) => {

        if (!window.confirm(`${statusKor[newStatus]} 상태로 변경하시겠습니까?`)) return;
        if(newStatus === "CANCELED"){
            const reason = prompt("취소 사유를 입력해주세요.");
            updateStatusMutate({orderDetailId, data: {orderDetailStatus: newStatus, orderDetailStatusReason: reason}});
            
        }else{
            updateStatusMutate({ orderDetailId, data: { orderDetailStatus: newStatus, orderDetailStatusReason: null }});
            
        }
    };




    return (
        <div className='shipping-info-container'>
            <h2>판매/배송 관리</h2>
            <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg} />
            <table className='shipping-table'>
                <thead>
                    <tr>
                        <th style={{width: "13%", paddingLeft:30}}>주문번호</th>
                        <th>주문일자</th>
                        <th>상품정보</th>
                        <th style={{width:"13%"}}>수량</th>
                        <th>주문금액</th>
                        <th>배송상태</th>
                    </tr>
                </thead>
                <tbody>
                    {deliverySellerProducts.length > 0 ? deliverySellerProducts.map((item) => (
                        <tr key={item.orderDetailId} className='shipping-detail-row' 
                            onClick={()=> setSelectedItem(item)}>
                            <td style={{width: "10%", paddingLeft:10}}>{item.orderDetailId}</td>
                            <td>{new Date(item.createAt).toLocaleDateString()}</td>
                            <td>
                                <div className="detail-info" 
                                onClick={() => navigate(`/product/${item.productId}`)}>
                                    <p>{item.productName}</p>
                                </div>
                            </td>
                            <td>{item.quantity}</td>
                            <td>{(item.pricePerItem * item.quantity).toLocaleString()} 원</td>
                            <td>
                                <div className="status-box">
                                    <select value={item.orderDetailStatus}
                                        onChange={(e) => handleStatusChange(item.orderDetailId, e.target.value)}>
                                        {Object.keys(statusKor).map(status => (
                                            <option key={status} value={status}>{statusKor[status]}</option>
                                        ))}
                                    </select>
                                    {/*<button onClick={() => handleUpdateStatus(item.orderDetailId)}>수정</button>*/}
                                </div>
                            </td>
                        </tr>
                    )) : (
                        <tr><td colSpan="6" className="no-results">판매 내역이 없습니다.</td></tr>
                    )}
                </tbody>
            </table>
            {totalPages > 0 ? (
                <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
            ): null}

            {selectedItem && (
                <div className='order-detail-modal' onClick={() => setSelectedItem(null)}>
                    <div className='seller-order-detail-modal-content' onClick={(e) => e.stopPropagation()}>
                        <button className="modal-close-btn" onClick={() => setSelectedItem(null)}>×</button>
                        <SellerOrderDetailInfo item={selectedItem}/>
                    </div>
                </div>
            )}
        </div> 
       
    );
}

export default SellerShippingDetail;