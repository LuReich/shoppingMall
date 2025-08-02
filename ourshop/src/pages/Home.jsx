import React from 'react';
import MainBanner from '../components/home/MainBanner';
import ProductSection from '../components/home/ProductSection';
import productsData from '../data/products.json';

const Home = () => {
    //  '최근 입고된 상품' 로직 수정 
    // 1. 원본 배열을 훼손하지 않기 위해 얕은 복사본을 만듭니다.
    // 2. uploadedAt을 Date 객체로 변환하여 최신순(내림차순)으로 정렬합니다.
    const newProducts = [...productsData]
        .sort((a, b) => new Date(b.uploadedAt) - new Date(a.uploadedAt))
        .slice(0, 8); // 정렬된 목록에서 상위 8개만 잘라냅니다.

    // '추천 상품' 로직은 isRecommended 플래그를 그대로 사용합니다.
    const recommendedProducts = productsData.filter(p => p.isRecommended).slice(0, 8);

    return (
        <div className="home-container">
            <div className="main-banner-wrapper">
                <MainBanner />
            </div>
            <ProductSection title="최근 입고된 상품" products={newProducts} />
            <ProductSection title="추천 상품" products={recommendedProducts} />
        </div>
    );
};

export default Home;