import React, { Fragment, useState } from 'react';
import { useOrder } from '../../hooks/useOrder';
import "../../assets/css/ShippingDetail.css";
import Pagination from '../../components/common/Pagenation';
import Sort from '../../components/common/Sort';
import { Link, useNavigate } from 'react-router-dom';
import { useReviews } from '../../hooks/useReview';

function BuyerShippingDetail(props) {

    const navigate = useNavigate();

    // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createAt,desc");

    const {getBuyerOrders} = useOrder();
    const {getReviews} = useReviews();
     
    const {data: buyerOrdersCont} = getBuyerOrders({
      page,
      size: 4,
      sort,
    });
    const {data: reviewsCont} = getReviews();

    const reviews = reviewsCont?.content?.content; 

    const buyerOrders = buyerOrdersCont?.content?.content;
    console.log("전체 주문 목록",buyerOrdersCont );
    console.log("주문 목록", buyerOrders);

    const totalPages = buyerOrdersCont?.content?.totalPages || 0;
    //const totalElements = buyerOrdersCont?.content?.content?.length || 0;
    //const totalPages = Math.ceil(totalElements / size);

    //해당 상품에 내가 리뷰썼으면 리뷰 보기 버튼 뜨도록, 쓴 리뷰가 없으면 리뷰쓰기 버튼 뜨도록
    const isReviewExsits = (orderDetailId) => {
      return reviews?.find(x => x.orderDetailId === orderDetailId)? true : false;
    } 

    const sortCateg = {
      "price": "totalPrice",
      "createAt": "createAt",
    }


    //한글 변환
    const statusKor = {
        "PAID": "결제완료",
        "SHIPPING": "배송중",
        "DELIVERED": "배송완료",
        "CANCELED": "취소"
    }

    //주문한 상품 상세 페이지로 이동
    /*const moveToDetail = (detail) => {
      console.log("삭제됨?",detail.isDeleted);
      if(detail.isDeleted){
        alert("삭제된 상품입니다.");
        navigate(`/product/${detail.productId}`);
      }else{
        navigate(`/product/${detail.productId}`);
      }
      
    }*/
   
    
    return (
        <div className='shipping-info-container'>
            <h2>주문/배송 조회</h2>
            {/* 정렬 옵션 */}
           <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
            <table className='shipping-table'>
                <thead>
                    <tr>
                        <th>주문일자</th>
                        <th>주문 아이디</th>
                        <th>주문상세 아이디 </th>
                        <th>상품정보</th>
                        <th>수량</th>
                        <th>주문금액</th>
                        <th>상태</th>
                    </tr>
                </thead>
                <tbody>
                    {buyerOrders?.map((order) => (
                        <Fragment key={order.orderId}>
                        {/* 주문일자 한 번만 표시 */}
                        {order.orderDetails?.map((detail, idx) => (
                            <tr key={detail.orderDetailId}>
                            {
                              idx === 0 ? (
                                <>
                                <td rowSpan={order.orderDetails.length}>
                                  {new Date(order.createAt).toLocaleDateString().replace(/\.$/, '')}
                                </td>
                                <td rowSpan={order.orderDetails.length}>{order.orderId}</td>
                                </>
                              ) : null
                            }
                            <td>{detail.orderDetailId}</td>
                            <td>
                              <div className="detail-info" onClick={() => navigate(`/product/${detail.productId}`)}>
                                <img src={detail.productThumbnailUrl} alt={detail.productName} />
                                <div>
                                  <p>{detail.productName}</p>
                                  <p>{detail.companyName}</p>
                                </div>
                              </div>
                            </td>

                            <td>{detail.quantity}</td>
                            <td>{(detail.quantity * detail.pricePerItem).toLocaleString()} 원</td>

                            <td>
                              <div className="status-box">
                                  <p>{statusKor[detail.orderDetailStatus]}</p>
                                  <div className="status-btn-box">
                                    {(detail.orderDetailStatus === "SHIPPING" ||
                                        detail.orderDetailStatus === "DELIVERED") 
                                      && (
                                           <button type="button" onClick={() => navigate("/buyer/mypage/delivery", {state: {order, detail}})}>배송조회</button>
                                          )
                                    }
                                    {detail.orderDetailStatus === "DELIVERED" 
                                      && ( isReviewExsits(detail.orderDetailId) === false?

                                            <Link to="/buyer/mypage/review/upload" 
                                                  className="review-upload-link"
                                                  state={{ productId: detail.productId, orderDetailId: detail.orderDetailId }}>
                                                  리뷰쓰기
                                            </Link>

                                            :
                                            <Link to="/buyer/mypage/review" 
                                                  className="review-upload-link">
                                                  리뷰보기
                                            </Link>

                                          )
                                    }
                                  </div>
                              </div>
                            </td>
                        </tr>
                      ))}
                  </Fragment>
                ))}
              </tbody>
            </table>
            {totalPages? (
              <Pagination
                page={page}
                totalPages={totalPages}
                onPageChange={(p) => setPage(p)}
              />
            ): null}
        </div>
    );
}

export default BuyerShippingDetail;