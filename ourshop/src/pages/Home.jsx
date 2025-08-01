import React from 'react';
import MainBanner from '../components/home/MainBanner';
import ProductSection from '../components/home/ProductSection';
import productsData from '../data/products.json';

const Home = () => {
    const newProducts = productsData.filter(p => p.isNew).slice(0, 8);
    const recommendedProducts = productsData.filter(p => p.isRecommended).slice(0, 8);

    return (
        <div className="home-container">
            {/* ✨✨✨ 배너 너비 조정을 위한 Wrapper Div 추가 ✨✨✨ */}
            <div className="main-banner-wrapper">
                <MainBanner />
            </div>
            <ProductSection title="최근 입고된 상품" products={newProducts} />
            <ProductSection title="추천 상품" products={recommendedProducts} />
        </div>
    );
};

export default Home;