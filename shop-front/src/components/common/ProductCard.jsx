import React from 'react';
import { Link } from 'react-router-dom';
import styles from '../../assets/css/ProductCard.module.css'; 



const ProductCard = ({ product }) => {
    return (
        <Link to={`/product/${product.productId}`} className={styles.productCard}>
            <div className={styles.productImageWrapper} >
                <img src={product.thumbnailUrl} 
                    alt={product.productName} 
                    className={styles.productImage}
                />
            </div>
            <div className={styles.productInfo}>
                <h3 className={styles.productName}>{product.productName}</h3>
                <div className={styles.productPrice}>
                    {product.originalPrice && (
                        <span className={styles.originalPrice}>{product.originalPrice.toLocaleString()}원</span>
                    )}
                    <span className={styles.salePrice}>{product.price.toLocaleString()}원</span>
                </div>
            </div>
        </Link>
    );
};

export default ProductCard;