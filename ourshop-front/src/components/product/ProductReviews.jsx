import React, { useState } from 'react';
import '../../assets/css/ProductReview.css';
import { IoIosArrowDown } from "react-icons/io";

function ProductReviews({productReviews}) {

    const [size , setSize] = useState(3);
    const [needMore, setNeedMore] = useState(true);
    const PrdReveiws =  productReviews || null;
    let count = 3;
    const moreReviewsBtn = () => {
        let c = size+count
        setSize(c);

        if(c > PrdReveiws.length){
            setNeedMore(false);
        }
    }
    const filteredReviews = PrdReveiws?.slice(0, size);


    return (
        <div className="customer-reviews-section">
            <div className="section-header">
                <h2>고객 리뷰</h2>
                <button className="write-review-btn">리뷰 작성하기</button>
            </div>
            <div className="review-container">
                {
                    PrdReveiws?.length !==0?
                        filteredReviews.map((x, idx) => (
                            <div className='review-box' key={idx}>
                                <div className='nickname-box'>
                                    <p>{x.buyerNickname}</p>
                                </div>
                                <div className='review-info-box'>
                                    <p>{'★'.repeat(Math.round(x.rating))}</p>
                                    <div className='product-info-box'>
                                        <p>[{x.productName}]</p>
                                        <p>{x.sellerCompanyName}</p>
                                    </div>
                                    <p>{x.content}</p>
                                    <p className='date-box'>{new Date(x.createAt).toLocaleString()}</p>
                                </div>
                            </div>
                        ))
                    :
                        <p className='review-placeholder'>해당 상품에 등록된 리뷰가 없습니다.</p>
                }
            </div>
            {
                !needMore? 
                 <div className="more-reviews" onClick={moreReviewsBtn}>
                    <p>더 많은 리뷰 보기 </p>
                    <IoIosArrowDown />
                 </div>
                 :
                 null
            }
           
            </div>
    );
}

export default ProductReviews;
