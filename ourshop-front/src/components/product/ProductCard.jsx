import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styles from '../../assets/css/ProductCard.module.css'; 
import { authStore } from '../../store/authStore';
import { useProduct } from '../../hooks/useProduct';
import { FaRegHeart } from "react-icons/fa";
import LikesBtn from '../common/LikesBtn';
import { SERVER_URL } from '../../axios/axios';



const ProductCard = ({ product }) => {

    const { isLogin, role } = authStore(state => state);
    const {getProductReview} = useProduct();
    const { data: ProductReview } = getProductReview(product.productId);
    console.log("리뷰 수",ProductReview);
 
    const reviewCount = ProductReview?.content?.content?.length || 0;

    const cutName = (name, limit = 20) => {
        if (!name) return "";
        return name?.length > limit ? name?.slice(0, limit) + "..." : name;
    };

    return (
        <Link to={`/product/${product?.productId}`} className={styles.productCard}>
            <div className={styles.productImageWrapper} >
                 {(role=="BUYER" || !isLogin) && <LikesBtn styleProps={"like-btn-on-card"} product={product}/>}
                 {product?.isDeleted && <div className={styles.productDeleted}>삭제된 상품입니다.</div>}
                <img src={`${SERVER_URL}${product?.thumbnailUrl}`} 
                    alt={product?.productName} 
                    className={styles.productImage}
                />
            </div>
            <div className={styles.productInfo}>
                <h3 className={styles.productName}>{cutName(product?.productName)}</h3>
                {/*<p className={styles.productId}>상품 아이디{product?.productId}</p>*/}
                <p className={styles.productId}>{product?.companyName}</p>
                <div className={styles.productPrice}>
                    {product?.originalPrice && (
                        <span className={styles.originalPrice}>{product?.originalPric?.toLocaleString()}원</span>
                    )}
                    <span className={styles.salePrice}>{product?.price?.toLocaleString()}원</span>
                </div>
                <div className={styles.productRatingLike}>
                    <div>★ {product.averageRating.toFixed(1)} ({reviewCount.toLocaleString()} 건)</div>
                    <div>
                        <FaRegHeart/> ({product.likeCount?.toLocaleString()})
                    </div> 
                </div>
            </div>
        </Link>
    );
};

export default ProductCard;