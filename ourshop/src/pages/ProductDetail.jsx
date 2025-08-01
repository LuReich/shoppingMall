import React, { useState, useEffect, useMemo } from 'react';
import { useParams, Link } from 'react-router-dom';
import productsData from '../data/products.json';
import categoriesData from '../data/categories.json';
import '../assets/css/ProductDetail.css';

// ... (findCategoryPath 헬퍼 함수는 이전과 동일하여 여기에선 생략)
const findCategoryPath = (categoryId, categories) => {
    const search = (id, currentCategories, path) => {
        for (const category of currentCategories) {
            const newPath = [...path, category];
            if (category.id === id) return newPath;
            if (category.children) {
                const found = search(id, category.children, newPath);
                if (found) return found;
            }
        }
        return null;
    };
    const findLeafCategory = (name, cats) => {
        for (const cat of cats) {
            if (cat.name === name) return cat;
            if (cat.children) {
                const found = findLeafCategory(name, cat.children);
                if (found) return found;
            }
        }
        return null;
    }
    const leafCategory = findLeafCategory(categoryId, categories);
    if (!leafCategory) return [{ id: 'home', name: '홈' }];

    return search(leafCategory.id, categories, [{ id: 'home', name: '홈' }]) || [{ id: 'home', name: '홈' }];
};

const ProductDetail = () => {
    const { id } = useParams();
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [activeImage, setActiveImage] = useState('');
    const [activeTab, setActiveTab] = useState('info');
    const [selectedSubscription, setSelectedSubscription] = useState('instant');

    const subscriptionTypes = [
        { key: 'instant', label: '일시' },
        { key: 'daily', label: '매일' },
        { key: 'weekly', label: '매주' },
        { key: 'monthly', label: '매달' }
    ];

    useEffect(() => {
        const foundProduct = productsData.find(p => p.id === parseInt(id));
        setProduct(foundProduct);
        if (foundProduct) {
            setActiveImage(foundProduct.galleryImages[0] || foundProduct.imageUrl);
            // 기본 선택 옵션을 활성화된 첫번째 옵션으로 설정
            const firstAvailable = subscriptionTypes.find(type => foundProduct.subscriptionOptions?.[type.key]);
            if (firstAvailable) {
                setSelectedSubscription(firstAvailable.key);
            }
        }
        window.scrollTo(0, 0);
    }, [id]);

    const categoryPath = useMemo(() => {
        if (!product) return [];
        return findCategoryPath(product.category, categoriesData);
    }, [product]);

    if (!product) {
        return <div className="loading">상품 정보를 불러오는 중입니다...</div>;
    }

    const handleQuantityChange = (amount) => {
        setQuantity(prev => Math.max(1, prev + amount));
    };

    const discountRate = product.originalPrice ? Math.round(((product.originalPrice - product.price) / product.originalPrice) * 100) : 0;
    const origin = product.details?.split('\n')[0].split(':')[1]?.trim() || '정보 없음';

    return (
        <div className="product-detail-container">
            <nav className="breadcrumb">
                {categoryPath.map((cat, index) => (
                    <span key={cat.id}>
                        <Link to={cat.id === 'home' ? '/' : `/products/${cat.id}`}>{cat.name}</Link>
                        {index < categoryPath.length - 1 && ' > '}
                    </span>
                ))}
                <span>{product.name}</span>
            </nav>

            <div className="product-detail-main">
                <div className="image-gallery">
                    <div className="main-image-container">
                        <img src={activeImage} alt={product.name} className="main-image" />
                    </div>
                    <div className="thumbnail-list">
                        {product.galleryImages?.map((imgUrl, index) => (
                            <div
                                key={index}
                                className={`thumbnail-item ${imgUrl === activeImage ? 'active' : ''}`}
                                onClick={() => setActiveImage(imgUrl)}
                            >
                                <img src={imgUrl} alt={`상품 이미지 ${index + 1}`} />
                            </div>
                        ))}
                    </div>
                </div>

                <div className="info-panel">
                    <h1>{product.name}</h1>
                    <p className="short-desc">{product.shortDescription}</p>
                    <div className="review-summary">
                        <span className="stars">★★★★★</span>
                        <span className="rating">{product.rating} ({product.reviewCount} 리뷰)</span>
                    </div>

                    <div className="price-section">
                        {discountRate > 0 && <span className="discount-badge">{discountRate}% 할인</span>}
                        <span className="final-price">{product.price.toLocaleString()}원</span>
                        {product.originalPrice && <span className="original-price">{product.originalPrice.toLocaleString()}원</span>}
                    </div>

                    {/* ✨✨✨ 정기배송 옵션 섹션으로 변경 ✨✨✨ */}
                    <div className="subscription-selector">
                        <span className="label">정기배송</span>
                        <div className="subscription-options">
                            {subscriptionTypes.map((type) => {
                                const isEnabled = product.subscriptionOptions?.[type.key] === true;
                                return (
                                    <label
                                        key={type.key}
                                        className={`sub-option ${!isEnabled ? 'disabled' : ''} ${selectedSubscription === type.key ? 'selected' : ''}`}
                                    >
                                        <input
                                            type="radio"
                                            name="subscription"
                                            value={type.key}
                                            checked={selectedSubscription === type.key}
                                            disabled={!isEnabled}
                                            onChange={() => setSelectedSubscription(type.key)}
                                        />
                                        {type.label}
                                    </label>
                                );
                            })}
                        </div>
                    </div>

                    <div className="quantity-selector">
                        <span className="label">수량</span>
                        <div className="quantity-controls">
                            <button onClick={() => handleQuantityChange(-1)}>-</button>
                            <span>{quantity}</span>
                            <button onClick={() => handleQuantityChange(1)}>+</button>
                        </div>
                    </div>

                    <div className="action-buttons">
                        <button className="cart-btn">장바구니 담기</button>
                        <button className="buy-btn">바로 구매하기</button>
                    </div>

                    <ul className="shipping-info">
                        <li><span>배송비</span><span>무료배송</span></li>
                        <li><span>배송일</span><span>내일 도착 예정</span></li>
                        <li><span>원산지</span><span>{origin}</span></li>
                    </ul>
                </div>
            </div>

            <div className="product-extra-details">
                <nav className="tabs-nav">
                    <button className={activeTab === 'info' ? 'active' : ''} onClick={() => setActiveTab('info')}>상품 정보</button>
                    <button className={activeTab === 'reviews' ? 'active' : ''} onClick={() => setActiveTab('reviews')}>리뷰 ({product.reviewCount})</button>
                    <button className={activeTab === 'shipping' ? 'active' : ''} onClick={() => setActiveTab('shipping')}>배송/반품</button>
                </nav>

                <div className="tab-content">
                    {activeTab === 'info' && (
                        <div className="info-content">
                            <h3>상품 상세 정보</h3>
                            <p className="product-catchphrase">{product.description}</p>
                            {product.details && (
                                <div className="details-text-area">
                                    {product.details.split('\n').map((line, i) => (
                                        <p key={i} style={{ whiteSpace: 'pre-wrap', margin: '0.5em 0' }}>{line}</p>
                                    ))}
                                </div>
                            )}
                        </div>
                    )}

                    {activeTab === 'reviews' && (
                        <div className="customer-reviews-section">
                            <div className="section-header">
                                <h2>고객 리뷰</h2>
                                <button className="write-review-btn">리뷰 작성하기</button>
                            </div>
                            <div className="review-placeholder">
                                <p>아직 작성된 리뷰가 없습니다.</p>
                            </div>
                            <div className="more-reviews">
                                더 많은 리뷰 보기 V
                            </div>
                        </div>
                    )}

                    {activeTab === 'shipping' && (
                        <div className="shipping-content">
                            <h3>배송 및 반품 안내</h3>
                            <p>배송 안내: 오후 2시 이전 주문 시 당일 출고되며, 일반적으로 1~2일 내에 도착합니다. (주말/공휴일 제외)</p>
                            <p>반품 안내: 신선식품의 경우, 단순 변심으로 인한 교환/반품이 불가합니다. 상품에 문제가 있을 경우 수령 후 24시간 이내에 고객센터로 연락주세요.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProductDetail;