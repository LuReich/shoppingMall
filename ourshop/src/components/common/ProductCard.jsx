import React from 'react';
import { Link } from 'react-router-dom';
import '../../assets/css/ProductCard.css';

// 내용은 이전과 동일
const ProductCard = ({ product }) => {
    return (
        <Link to={`/product/${product.id}`} className="product-card">
            <div className="product-image-wrapper">
                <img src={product.imageUrl} alt={product.name} className="product-image" />
            </div>
            <div className="product-info">
                <h3 className="product-name">{product.name}</h3>
                <div className="product-price">
                    {product.originalPrice && (
                        <span className="original-price">{product.originalPrice.toLocaleString()}원</span>
                    )}
                    <span className="sale-price">{product.price.toLocaleString()}원</span>
                </div>
            </div>
        </Link>
    );
};

export default ProductCard;