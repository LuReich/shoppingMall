import React, { useState } from 'react';
import Pagination from '../../components/common/Pagenation';
import Sort from '../../components/common/Sort';
import { Link } from 'react-router-dom';
import { useReviews } from '../../hooks/useReview';
import '../../assets/css/BuyerReview.css';

function BuyerReview(props) {

    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createAt,desc");
    
    const {getReviews, deleteReview} = useReviews();
    const { mutate: deleteReviewMutate } = deleteReview();

    const {data: reviewsCont} = getReviews({
      page,
      size: 5,
      sort,
    });

    const reviews = reviewsCont?.content?.content;
    console.log("전체 리뷰 목록",reviewsCont );
    console.log("리뷰 목록", reviews);

    const totalPages = reviewsCont?.content?.totalPages || 0;
    

    //리뷰 삭제
    const deleteBtn = (reviewId) => {
        if(confirm("정말 리뷰를 삭제하시겠습니까?")){
            deleteReviewMutate(reviewId);
        }
    }

    return (
        <div className='buyer-review-container'>
            <h2>나의 리뷰</h2>
            <Sort sort={sort} setSort={setSort} setPage={setPage}/>
            <div className='review-table'>
                <table >
                    <thead>
                        <tr>
                            <th style={{width: "30%"}}>상품</th>
                            <th style={{width: "70%"}}>리뷰</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                          reviews?.map(review => (
                            <tr key={review.reviewId}>
                                <td className='product-info'>
                                    <div className='product-name'>{review.productName}</div>
                                    <div className='company-name'>{review.companyName}</div>
                                </td>
                                <td className='review-content'>
                                    <div className='revie-cont'>
                                        <div className='review-date'>작성 일자: {new Date(review.createAt).toLocaleDateString()}</div>
                                        <div className='review-rating'>{'★'.repeat(review.rating)}</div>
                                        <div className='review-text'>{review.content}</div>
                                    </div>
                                    <div className='review-actions'> 
                                        <Link to={`/buyer/mypage/review/upload/${review.reviewId}`}>
                                            <button>수정</button> 
                                        </Link>
                                        <button type='button' onClick={() => deleteBtn(review.reviewId)}>삭제</button> 
                                    </div> 
                                </td>
                            </tr>
                          ))  
                        }
                    </tbody>
                </table>
            </div>
            {totalPages ? (
              <Pagination
                page={page}
                totalPages={totalPages}
                onPageChange={(p) => setPage(p)}
              />
            ): null}
        </div>
    );
}

export default BuyerReview;