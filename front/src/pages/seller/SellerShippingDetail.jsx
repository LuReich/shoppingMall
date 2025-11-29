import React, { Fragment, useEffect, useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import "../../assets/css/ShippingDetail.css";
import Pagination from '../../components/common/Pagenation';
import Sort from '../../components/common/Sort';
import { useNavigate } from 'react-router';
import SellerOrderDetailInfoModal from '../../components/seller/SellerOrderDetailInfoModal';
import { authStore } from '../../store/authStore';
import Loader from '../../utils/Loaders';

function SellerShippingDetail(props) {

    const isLogin = authStore(state => state.isLogin);
    
    
    if(!isLogin){
        alert("로그인이 필요한 서비스입니다.");
        navigate("/login");
    }

    const navigate = useNavigate();
    const [sort, setSort] = useState("order.createAt,desc");
    const [page, setPage] = useState(0);
    const [selectedItem, setSelectedItem] = useState(null);
    const [searchField, setSearchField] = useState("productName");
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [statusFilter, setStatusFilter] = useState(""); // 배송 상태 필터

    //  필터가 변경되면 세팅 초기화 
    useEffect(() => {
        setSearchParams({});
        setSearchKeyword("");
    }, [searchField]);


    useEffect(() => {
        setPage(0);
        setSearchParams({});
        setSearchKeyword("");
    }, [statusFilter]);


    const { getDeliverySellerProductList, updateDeliveryStatus } = useSeller();
    const { data: deliverySellerProductsCont, isLoading, isError, refetch }
        = getDeliverySellerProductList({ sort, size: 5, page , orderDetailStatus: statusFilter, ...searchParams});

    const { mutate: updateStatusMutate } = updateDeliveryStatus();

    // 검색 버튼 클릭 시 검색조건 업데이트
    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setPage(0);
        // 검색어가 있으면 검색조건 적용, 없으면 초기화
        if (searchKeyword.trim()) {
            setSearchParams({ [searchField]: searchKeyword });
        } else {
            setSearchParams({});
        }

    };

    if (isLoading) return <Loader/>;
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

    //글자 수 길면 ...처리
    const cutText = (text, maxLen = 15) => {
      if (!text) return "";
      return text?.length > maxLen ? text.slice(0, maxLen) + "..." : text;
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
            <div className='search-seller-post-bar'>
            <div className='seller-filter-box'>
                <h4 className='filter-label'>배송 상태</h4>
                <div className="seller-radio-group">
                        <label className='seller-radio-wrap'>
                            <input type='radio' name='status' value='' 
                            checked={statusFilter === ""} 
                            onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>전체</span>
                        </label>
                        <label className='seller-radio-wrap'>
                            <input type='radio' name='status' value='PAID' 
                            checked={statusFilter === "PAID"} 
                            onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>결제완료</span>
                        </label>
                        <label className='seller-radio-wrap'>
                            <input type='radio' name='status' value='SHIPPING' 
                            checked={statusFilter === "SHIPPING"} 
                            onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>배송중</span>
                        </label>
                        <label className='seller-radio-wrap'>
                            <input type='radio' name='status' value='DELIVERED' 
                            checked={statusFilter === "DELIVERED"} 
                            onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>배송완료</span>
                        </label>
                        <label className='seller-radio-wrap'>
                            <input type='radio' name='status' value='CANCELED' 
                            checked={statusFilter === "CANCELED"} 
                            onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>취소</span>
                        </label>
                </div>
            </div>
            <form  className='seller-form-box' onSubmit={handleSearchSubmit}>
                <h4>결과 내 재검색</h4>
                <div className='seller-search-form-b'>
                 <select className='search-seller-post-select'
                    value={searchField}
                    onChange={(e)=> setSearchField(e.target.value)}>
                    <option value="productName">상품명</option>
                    <option value="productId">상품아이디</option>
                    <option value="recipientName">수령인 이름</option>
                    <option value="recipientPhone">수령인 전화번호</option>
                    <option value="recipientAddress">배송 주소</option>
                    <option value="order.orderId">주문 아이디</option>
                    <option value="orderDetailId">주문상세 아이디</option>
                 </select>
                 <input
                    type="text"
                    placeholder="검색어를 입력하세요"
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                    className="search-seller-post-input"
                 />
                 <button type="submit" className="search-seller-post-btn">
                    검색
                 </button>
                </div>
            </form>
            </div>
            <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg} />
            <table className='shipping-table'>
                <thead>
                    <tr>
                        <th>주문상세 아이디</th>
                        <th>주문일자</th>
                        <th>수령인</th>
                        <th>상품정보</th>
                        <th>수량</th>
                        <th>주문금액</th>
                        <th>배송상태</th>
                    </tr>
                </thead>
                <tbody>
                    {deliverySellerProducts.length > 0 ? deliverySellerProducts.map((item) => (
                        <tr key={item.orderDetailId} className='shipping-detail-row'>
                            <td className='order-detail-id'
                            onClick={()=> setSelectedItem(item)}
                            >{item.orderDetailId}</td>
                            <td>{new Date(item.createAt).toLocaleDateString().replace(/\.$/, '')}</td>
                            <td>{item.recipientName}</td>
                            <td>
                                <div className="detail-info" 
                                onClick={() => navigate(`/product/${item.productId}`)}>
                                    <p>{cutText(item.productName)}</p>
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
                        <tr><td colSpan={7} className="n-results">판매 내역이 없습니다.</td></tr>
                    )}
                </tbody>
            </table>
            {totalPages > 0 ? (
                <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)} />
            ): null}

            {selectedItem && (
                <div className='order-detail-modal' onClick={() => setSelectedItem(null)}>
                    <div className='seller-order-detail-modal-content' onClick={(e) => e.stopPropagation()}>
                        <button className="modal-close-btn" onClick={() => setSelectedItem(null)}>×</button>
                        <SellerOrderDetailInfoModal item={selectedItem}/>
                    </div>
                </div>
            )}
        </div> 
       
    );
}

export default SellerShippingDetail;