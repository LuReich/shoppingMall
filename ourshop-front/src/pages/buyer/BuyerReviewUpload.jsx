import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useReviews } from '../../hooks/useReview';
import '../../assets/css/BuyerReviewUpload.css';
import { useOrder } from '../../hooks/useOrder';

function BuyerReviewUpload(props) {
    const { reviewId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const isEditMode = !!reviewId;
    const { productId, orderDetailId, productName } = location.state || {};

    // useReview 훅에서 필요한 함수들 가져오기
    const { createReview, updateReview, getReviews } = useReviews();
    const { mutate: createMutate } = createReview();
    const { mutate: updateMutate } = updateReview();
    
    // 리뷰 데이터 상태
    const [rating, setRating] = useState(5);
    const [content, setContent] = useState('');

    // 수정 모드일 때 기존 리뷰 데이터 불러오기
    const { data: reviewData } = getReviews();

    const filteredReview = reviewData?.content?.content?.find(review => review.reviewId === parseInt(reviewId));
    
    const maxLength = 250;
    console.log("기존 리뷰리스트",reviewData);
    console.log("해당 리뷰",filteredReview);

    useEffect(() => {
        if (isEditMode && filteredReview) {
            setRating(filteredReview.rating);
            setContent(filteredReview.content);
        }
    }, [isEditMode, reviewData]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (content.trim() === '') {
            alert('리뷰 내용을 입력해주세요.');
            return;
        }

        if (isEditMode) {
            // 리뷰 수정
            updateMutate({ reviewId, reviewData: { productId, orderDetailId, rating, content } });
        } else {
            // 새 리뷰 등록
            
            if (!productId || !orderDetailId) {
                alert('잘못된 접근입니다. 주문 내역에서 다시 시도해주세요.');
                navigate('/buyer/mypage/shipping');
                return;
            }
            createMutate({ productId, orderDetailId, rating, content });
        }
    };

    return (
        <div className="review-upload-container">
            <h2>{isEditMode ? '리뷰 수정' : '리뷰 작성'}</h2>
            <form onSubmit={handleSubmit} className="review-form">
                <div className="form-group">
                    <label>상품</label>
                        <div style={{marginBottom: "30px"}}>{isEditMode? filteredReview?.productName : productName}</div>
                    <label>별점</label>
                     <div className="star-rating">
                        {[...Array(5)].map((_, index) => {
                            const starValue = index + 1;
                            return (
                                <span
                                    key={starValue}
                                    className={starValue <= rating ? 'star filled' : 'star'}
                                    onClick={() => setRating(starValue)}
                                >
                                ★
                                </span>
                            );
                        })}
                    </div>
                </div>
                <div className="form-group">
                    <label htmlFor="review-content">리뷰 내용</label>
                    <textarea
                        id="review-content"
                        value={content}
                        onChange={(e) => {
                            if (e.target.value.length <= maxLength) {
                                setContent(e.target.value);
                            }
                        }}
                        placeholder="상품에 대한 솔직한 리뷰를 남겨주세요."
                        rows="10"
                    />
                    <div className="char-count">
                        {content.length} / {maxLength}자
                    </div>
                </div>
                <div className="review-form-btns">
                    <button type="button" className="cancel-b" onClick={() => navigate(-1)}>취소</button>
                    <button type="submit" className="submit-btn">
                        {isEditMode ? '수정하기' : '등록하기'}
                    </button>
                </div>
            </form>
        </div>
    );
}

export default BuyerReviewUpload;