import React, { useEffect, useState } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import { get, set } from 'react-hook-form';
import Sort from '../../components/common/Sort';
import { useNavigate } from 'react-router';
import dayjs from 'dayjs';
import Pagination from '../../components/common/Pagenation';
import '../../assets/css/AdminOrderManage.css';
import { date } from 'yup';
import SellerOrderDetailInfoModal from '../../components/seller/SellerOrderDetailInfoModal';
import Loader from '../../utils/Loaders';

function AdminOrderManage(props) {
    
    const navigate = useNavigate();
    const [sort, setSort] = useState("createAt,desc");
    const [page, setPage] = useState(0);
    const [selectedItem, setSelectedItem] = useState(null);
    const [searchField, setSearchField] = useState("orderId");
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [statusFilter, setStatusFilter] = useState(""); // 배송 상태 필터
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [activeDateButton, setActiveDateButton] = useState(0); // 날짜 버튼 활성화 상태
    const [dateFilter, setDateFilter] = useState({
        sDate: "",
        eDate: ""
    }); //

    // 배송 상태 필터가 변경되면 세팅 초기화 
    useEffect(() => {
        setSearchParams({});
        setSearchKeyword("");
    }, [searchField]);


    useEffect(() => {
        setPage(0);
        setSearchParams({});
        setSearchKeyword("");
    }, [statusFilter]);

    const {getOrderList} = useAdmin();
    const {data: orderListData, isLoading, isError} = getOrderList({
        sort, 
        size: 5, 
        page, 
        orderStatus: statusFilter, 
        startDate: dateFilter.sDate, 
        endDate: dateFilter.eDate,
        ...searchParams});

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

    //날짜 조회 dayjs 사용
    //오늘 날짜
    const today = dayjs().format("YYYY-MM-DD");

    //개월 버튼 클릭시
    const hanldeMonthClick = (m) => {
        setActiveDateButton(m);
        const start = dayjs().subtract(m , "month").format("YYYY-MM-DD");

        if(m === 0){
            setDateFilter({
                sDate: "",
                eDate: ""
            })
            setStartDate("");
            setEndDate("");
        }else{
            setDateFilter({
                sDate: start,
                eDate: today
            })
            setStartDate(start);
            setEndDate(today);
        }  
    };

    //날짜 조회 버튼 클릭시
    const handleDateSubmit = (e) => {
        e.preventDefault();
        if(!startDate.trim() || !endDate.trim()){
            alert("날짜를 선택해주세요.");
            return;
        };
        if (dayjs(endDate).isAfter(dayjs(), "day") || dayjs(startDate).isAfter(dayjs(), "day")) {
            alert("오늘 날짜를 초과한 일정은 조회 할 수 없습니다.");
            return;
        };
        setDateFilter({
            sDate: startDate,
            eDate: endDate
        })
        setActiveDateButton(null); // 직접 조회 시 버튼 활성화 해제
        
    };


    if (isLoading) return <Loader/>;
    if (isError) return <p>판매내역 조회에 실패했습니다.</p>;

    const orderList = orderListData?.content?.content || [];
    const totalPages = orderListData?.content?.totalPages || 0;

    console.log("판매내역", orderList);



    const sortCateg = {
        "createAt": "createAt",
        "productName": "recipientName",
        "orderId" : "orderId"
    };

    const statusKor = {
        "PAID": "결제완료",
        "SHIPPING": "배송중",
        "DELIVERED": "배송완료",
        "CANCELED": "취소"
    };


    return (
        <div className='admin-order-manage-container'>
           <div className='admin-order-manage-bar'>
                <h2>주문 조회</h2>
                <div className='admin-order-manage-search-container'>
                    <div className='admin-order-manage-date-box'>
                        <h4 className='order-label'>주문일자</h4>
                            <div className='admin-order-manage-date-filter-box'>
                                <button type='button' className={activeDateButton === 0 ? 'active' : ''} onClick={() => hanldeMonthClick(0)}>전체</button>
                                <button type='button' className={activeDateButton === 1 ? 'active' : ''} onClick={() => hanldeMonthClick(1)}>1개월</button>
                                <button type='button' className={activeDateButton === 3 ? 'active' : ''} onClick={() => hanldeMonthClick(3)}>3개월</button>
                                <button type='button' className={activeDateButton === 6 ? 'active' : ''} onClick={() => hanldeMonthClick(6)}>6개월</button>
                                <form onSubmit={handleDateSubmit} className='admin-date-form'>
                                    <input type='date' value={startDate} onChange={(e) => setStartDate(e.target.value)}/>
                                    -
                                    <input type='date' value={endDate} onChange={(e) => setEndDate(e.target.value)}/>
                                    <button type='submit' className='search-admin-order-btn'>조회</button>
                                </form>
                            </div>
                    </div>
                    <div className='admin-order-filter-box'>
                        <h4 className='order-label'>배송 상태</h4>
                        <div className="admin-order-radio-group">
                            <label className='admin-order-radio-wrap'>
                                <input type='radio' name='status' value='' 
                                checked={statusFilter === ""} 
                                onChange={(e) => setStatusFilter(e.target.value)} />
                                <span>전체</span>
                            </label>
                            <label className='admin-order-radio-wrap'>
                                <input type='radio' name='status' value='PAID' 
                                checked={statusFilter === "PAID"} 
                                onChange={(e) => setStatusFilter(e.target.value)} />
                                <span>결제완료</span>
                            </label>
                            <label className='admin-order-radio-wrap'>
                                <input type='radio' name='status' value='SHIPPING' 
                                checked={statusFilter === "SHIPPING"} 
                                onChange={(e) => setStatusFilter(e.target.value)} />
                                <span>배송중</span>
                            </label>
                            <label className='admin-order-radio-wrap'>
                                <input type='radio' name='status' value='DELIVERED' 
                                checked={statusFilter === "DELIVERED"} 
                                onChange={(e) => setStatusFilter(e.target.value)} />
                                <span>배송완료</span>
                            </label>
                            <label className='admin-order-radio-wrap'>
                                <input type='radio' name='status' value='CANCELED' 
                                checked={statusFilter === "CANCELED"} 
                                onChange={(e) => setStatusFilter(e.target.value)} />
                                <span>취소</span>
                            </label>
                        </div>
                    </div>
                    <form className='admin-order-manage-search-box' onSubmit={handleSearchSubmit}>
                        <h4>결과 내 재검색</h4>
                        <div className='admin-order-manage-search-form-bar'>
                            <select className='search-admin-order-select' 
                                value={searchField}
                                onChange={(e)=> setSearchField(e.target.value)}>
                                <option value="orderId">주문아이디</option>
                                <option value="buyerUid">구매자 식별번호</option>
                                <option value="recipientName">수령인</option>
                                <option value="recipientAddress">배송주소</option>
                            </select>
                            <input type='text' placeholder='검색어를 입력하세요'
                                value={searchKeyword}
                                onChange={(e) => setSearchKeyword(e.target.value)}
                                className='search-admin-order-input'
                            />
                            <button type='submit' className='search-admin-order-btn'>
                                검색
                            </button>
                        </div>
                    </form>
                </div>
                <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg} />
                <table className='admin-order-manage-table'>
                    <thead>
                        <tr>
                            <th>주문일자</th>
                            <th>주문 아이디</th>
                            <th>구매자 식별번호</th>
                            <th>수령인</th>
                            <th>수령주소</th>
                            <th>주문금액</th>
                            <th>주문상세 아이디</th>
                            <th>상품정보</th>
                            <th>수량</th>
                            <th>상품주문금액</th>
                            <th>배송상태</th>
                        </tr>
                    </thead>
                     {orderList.map((order) => (
                        <tbody key={order.orderId} className='admin-order-manage-tbody'
                         onClick={() => setSelectedItem(order)}>
                        {order.orderDetail.map((detail, index) => (
                            <tr key={detail.orderDetailId}>
                            {index === 0 && (
                            <>
                                <td rowSpan={order.orderDetail.length}>{new Date(order.createAt).toLocaleDateString().replace(/\.$/, '')}</td>
                                <td rowSpan={order.orderDetail.length}>{order.orderId}</td>
                                <td rowSpan={order.orderDetail.length}>{order.buyerUid}</td>
                                <td rowSpan={order.orderDetail.length}>{order.recipientName}</td>
                                <td rowSpan={order.orderDetail.length}>
                                    {order.recipientAddress} {order.recipientAddressDetail}
                                </td>
                                <td rowSpan={order.orderDetail.length}>{order.totalPrice.toLocaleString()}원</td>
                            </>
                        )}
                        <td>{detail.orderDetailId}</td>
                        <td>{detail.productName}</td>
                        <td>{detail.quantity}</td>
                        <td>{(detail.pricePerItem * detail.quantity).toLocaleString()}원</td>
                        <td>{statusKor[detail.orderDetailStatus]}</td>
                     </tr>
                    ))}
                  </tbody>
                ))}
                </table>
                {
                    totalPages > 0 ? (
                        <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)} />
                    ): null     
                          
                }
                {selectedItem && (
                <div className='order-detail-modal' onClick={() => setSelectedItem(null)}>
                    <div className='seller-order-detail-modal-content' onClick={(e) => e.stopPropagation()}>
                        <button className="modal-close-btn" onClick={() => setSelectedItem(null)}>×</button>
                        <SellerOrderDetailInfoModal item={selectedItem}/>
                    </div>
                </div>
                )}
            </div> 
        </div>
    );
}

export default AdminOrderManage;