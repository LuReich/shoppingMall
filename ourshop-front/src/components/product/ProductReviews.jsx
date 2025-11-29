import React, { useState } from 'react';
import '../../assets/css/ProductReview.css';
import { IoIosArrowDown } from "react-icons/io";
import { useProduct } from '../../hooks/useProduct';
import Sort from '../common/Sort';
import Pagination from '../common/Pagenation';

function ProductReviews({product}) {

    //const [size , setSize] = useState(1);
    //const [needMore, setNeedMore] = useState(true);
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("rating,desc");
    const productId = product?.productId;
    const { getProductReview } = useProduct();
    const { data: productReviewCont } = getProductReview(productId, {page, size: 5, sort});


    const productReviews = productReviewCont?.content?.content || [];

    const sortCateg = {
        "rating": "rating",
        "createAt": "createAt",
    }

    const totalPages = productReviewCont?.content?.totalPages || 0;
    
    console.log("리뷰 데이터", productReviews);
    /*let count = 1;

    const moreReviewsBtn = () => {
        let c = size+count
        setSize(c);

        if(c > productReviews.length){
            setNeedMore(false);
        }
    }
    const filteredReviews = productReviews?.slice(0, size);*/


    return (
        <div className="customer-reviews-section">
            <div className="section-header">
                <div className='review-summary-box'>
                    <p><a style={{color: '#fcc419'}}>★</a> {product?.averageRating.toFixed(1)} 점</p>
                    <p>총 {productReviews?.length}개의 리뷰</p>
                </div>
                <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
            </div>
            <div className="review-container">
                {
                    productReviews?.length !==0?
                        productReviews.map((x, idx) => (
                            <div className='review-box' key={idx}>
                                <div className='nickname-box'>
                                    <p>{x.buyerNickname}</p>
                                    <p className='date-box'>{new Date(x.createAt).toLocaleString()}</p>
                                </div>
                                <div className='review-info-box'>
                                    <div className='review-star-and-info'>
                                    <p className='star-box'>{'★'.repeat(Math.round(x.rating))}</p>
                                    <div className='product-info-box'>
                                        <p>[{x.productName}]</p>
                                        <p>{x.sellerCompanyName}</p>
                                    </div>
                                    <p style={{marginTop: '5px'}} 
                                    className='product-review-content-b'>{x.content}</p>
                                    </div>
                                    {/*<p className='date-box'>{new Date(x.createAt).toLocaleString()}</p>*/}
                                </div>
                            </div>
                        ))
                    :
                        <p className='review-placeholder'>해당 상품에 등록된 리뷰가 없습니다.</p>
                }
            </div>
            {/*
                !needMore? 
                 <div className="more-reviews" onClick={moreReviewsBtn}>
                    <p>더 많은 리뷰 보기 </p>
                    <IoIosArrowDown />
                 </div>
                 :
                 null
            */}
            {
                totalPages ? <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)}/> : null
            }
           
            </div>
    );
}

export default ProductReviews;
