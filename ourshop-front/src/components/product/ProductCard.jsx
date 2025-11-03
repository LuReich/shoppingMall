import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styles from '../../assets/css/ProductCard.module.css'; 
import { authStore } from '../../store/authStore';
import { useProduct } from '../../hooks/useProduct';
import { FaRegHeart } from "react-icons/fa";
import LikesBtn from '../common/LikesBtn';



const ProductCard = ({ product }) => {

    const { isLogin, role } = authStore(state => state);
    const {getProductReview} = useProduct();
    const { data: ProductReview } = getProductReview();
 
    const reviewCount = ProductReview?.content?.content?.length || 0;

   

    return (
        <Link to={`/product/${product?.productId}`} className={styles.productCard}>
            <div className={styles.productImageWrapper} >
                 {(role=="BUYER" || !isLogin) && <LikesBtn styleProps={"like-btn-on-card"} product={product}/>}
                <img src={`http://localhost:9090${product?.thumbnailUrl}`} 
                    alt={product?.productName} 
                    className={styles.productImage}
                />
            </div>
            <div className={styles.productInfo}>
                <h3 className={styles.productName}>{product?.productName}</h3>
                <div className={styles.productPrice}>
                    {product?.originalPrice && (
                        <span className={styles.originalPrice}>{product?.originalPric?.toLocaleString()}원</span>
                    )}
                    <span className={styles.salePrice}>{product?.price?.toLocaleString()}원</span>
                </div>
                <div className={styles.productRatingLike}>
                    <div>★ {product.averageRating} ({reviewCount.toLocaleString()} 건)</div>
                    <div>
                        <FaRegHeart/> ({product.likeCount?.toLocaleString()})
                    </div> 
                </div>
            </div>
        </Link>
    );
};

export default ProductCard;