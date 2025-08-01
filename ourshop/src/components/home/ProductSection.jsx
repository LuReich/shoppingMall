import React from 'react';
import ProductCard from '../common/ProductCard';
import '../../assets/css/ProductSection.css';

const ProductSection = ({ title, products }) => {
    return (
        <section className="product-section">
            <h2 className="section-title">{title}</h2>
            <div className="product-grid">
                {products.map(product => (
                    <ProductCard key={product.id} product={product} />
                ))}
            </div>
        </section>
    );
};

export default ProductSection;