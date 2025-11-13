import React, { useEffect, useState, useMemo, useRef } from 'react';
import { Link, useNavigate, useParams } from 'react-router';
import { useProduct } from '../../hooks/useProduct';
import { useCategory } from '../../hooks/useCategory';
import '../../assets/css/ProductDetail.css';
import ProductDescription from '../../components/product/ProductDescription';
import ProductReviews from '../../components/product/ProductReviews';
import ProductInquiry from '../../components/product/ProductInquiry';
import { authStore } from '../../store/authStore';
import { useCart } from '../../hooks/useCart';
import LikesBtn from '../../components/common/LikesBtn';
import RecommendBox from '../../components/recommendation/RecommendBox';
import { IoIosArrowUp } from "react-icons/io";
import ProductQnA from '../QnA/QnA';


function ProductDetail() {
  const clamp = (n, min, max) => Math.max(min, Math.min(max, n));
  const navigate = useNavigate();
  const { productId } = useParams();

  // 하단 리뷰 탭으로 스크롤하기 위한 ref
  const reviewsRef = useRef(null);
  const moveTop = useRef(null);

  const { isLogin, role } = authStore(state => state);

  // 상품 상세, 설명/리뷰 데이터
  const { getProductDetail, getProductDescription, getProductReview } = useProduct();
  const { getCategoryList } = useCategory();

  const { data: productCont, isLoading, isError } = getProductDetail(productId);
  const { data: productDescriptionCont } = getProductDescription(productId);
  const { data: productReviewCont } = getProductReview(productId);
  const { data: categoryData } = getCategoryList();

  const { addCartItem } = useCart();
  const { mutate: addToCart } = addCartItem();

  const product = productCont?.content || {};
  const productDescription = productDescriptionCont?.content || {};
  const productReviews = productReviewCont?.content?.content || [];

  // ✅ 대표 이미지 상태
  const [activeImage, setActiveImage] = useState(null);

  // ✅ 상품 데이터가 로드된 후 대표 이미지 세팅
  useEffect(() => {
    if (product?.thumbnailUrl) {
      setActiveImage(product.thumbnailUrl);
    }
  }, [product]);

  // 탭, 수량 상태
  const [activeTab, setActiveTab] = useState('info');
  const [quantity, setQuantity] = useState("1");
  const [isDisabled, setIsDisabled] = useState(true);

  useEffect(() => {
    const qNum = Number(quantity || 0);
    setIsDisabled(!(qNum > 1));
  }, [quantity]);

  /* ✅ 카테고리 트리 구조 변환 */
  const categories = useMemo(() => {
    if (!categoryData?.content) return [];
    const flat = categoryData.content;
    const map = {};
    const roots = [];

    flat.forEach(c => (map[c.categoryId] = { ...c, children: [] }));

    flat.forEach(c => {
      if (c.parentId) {
        map[c.parentId]?.children.push(map[c.categoryId]);
      } else {
        roots.push(map[c.categoryId]);
      }
    });

    return roots;
  }, [categoryData]);

  /* ✅ 카테고리 경로 찾기 */
  const findCategoryPath = (categories, targetId, path = []) => {
    for (const cat of categories) {
      const newPath = [...path, cat];
      if (cat.categoryId === targetId) return newPath;
      if (cat.children?.length) {
        const childPath = findCategoryPath(cat.children, targetId, newPath);
        if (childPath) return childPath;
      }
    }
    return null;
  };

  const categoryPath = findCategoryPath(categories, product?.categoryId) || [];

  // 리뷰 길이
  const reviewLength = productReviews?.length || 0;

   useEffect(()=>{
      if(product.isDeleted){
        alert("삭제된 상품입니다.");
      } 
    },[]);


  if (isLoading) return <p>상품 조회중...</p>;
  if (isError) return <p>상품 조회에 실패했습니다.</p>;



  // 리뷰 섹션으로 스크롤하고 탭을 변경하는 함수
  const handleScrollToReviews = () => {
    setActiveTab('reviews');
    reviewsRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  };

  //맨 위로 이동
  const handleMoveTop = () => {
    moveTop?.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  console.log("상품", product);

  // 수량 변경
  const handleQuantityChange = (amount) => {
    let newQuant = Number(quantity) + amount;
    if (newQuant < 1) newQuant = 1;
    if (newQuant > 50) {
      alert("최대 구매 가능 수량은 50개입니다.");
      newQuant = product.stock;
    }
    setQuantity(newQuant);
  };

  const handleQuantityTyping = (e) => {
    const v = e.target.value;
    if (/^\d*$/.test(v)) setQuantity(v);
  };

  const commitQuantity = () => {
    const stockLimit = Number.isFinite(product?.stock) ? product.stock : 50;
    const maxAllowed = clamp(stockLimit, 1, 50);
    let q = Number(quantity);

    if (!quantity || Number.isNaN(q)) {
      alert("유효한 수량을 입력해주세요.");
      q = 1;
    }

    if (q < 1) {
      alert("최소 구매 수량은 1개입니다.");
      q = 1;
    } else if (q > maxAllowed) {
      alert(`최대 구매 가능 수량은 ${maxAllowed}개입니다.`);
      q = maxAllowed;
    }

    setQuantity(String(q));
  };


  const handleQuantityKeyDown = (e) => {
    if (e.key === 'Enter') e.currentTarget.blur();
  };

  // 장바구니 담기
  const cartBtn = () => {
    if (isLogin) {
      const itemData = {
        productId: product.productId,
        quantity: quantity
      };
      addToCart(itemData);
      navigate('/cart');
    } else {
      alert("로그인이 필요한 서비스 입니다.");
      navigate('/login');
    }
  };

  // 바로구매
  const productData = { ...product, quantity: Number(quantity) };

  const orderDirectBtn = () => {
    navigate('/order', { state: { productData } });
  };

  return (
    <div className="product-detail-container" ref={moveTop}>
      {/* 카테고리 경로 */}
      <nav className="breadcrumb">
        {categoryPath.map((cat, index) => (
          <span key={cat.categoryId}>
            <Link
              to={`/products?categoryId=${cat.categoryId}`}
              state={{ categoryName: cat.categoryName }}
            >
              {cat.categoryName}
            </Link>
            {index < categoryPath.length && ' > '}
          </span>
        ))}
        <span> {product.productName}</span>
      </nav>
      <div className="product-detail-main">
        <div className="image-gallery">
          <div className="main-image-container">
            {product.isDeleted && <div className='deleted-product-notice'>삭제된 상품입니다.</div>}
            {activeImage ? (
              <img
                src={
                  activeImage.startsWith('http')
                    ? activeImage
                    : `http://localhost:9090${activeImage}`
                }
                alt={product.productName || "상품 이미지"}
                className="main-image"
                onError={(e) => (e.target.src = '/images/no-image.png')}
              />
            ) : (
              <img
                src="/images/no-image.png"
                alt={product.productName || "상품 이미지"}
                className="main-image"
              />
            )}
          </div>

          <div className="thumbnail-list">
            {/* 대표 이미지도 썸네일 리스트에 포함 */}
            {product.thumbnailUrl && (
              <div
                className={`thumbnail-item ${product.thumbnailUrl === activeImage ? 'active' : ''}`}
                onClick={() => setActiveImage(product.thumbnailUrl)}
              >
                <img
                src={
                      product.thumbnailUrl.startsWith('http')
                      ? product.thumbnailUrl
                      : `http://localhost:9090${product.thumbnailUrl}`
                    } alt="대표 이미지"
                />
              </div>
            )}
            {/* 기존 서브 이미지 목록 */}
           {product.productImages?.map((img, index) => (
              <div
                key={index}
                className={`thumbnail-item ${img.imagePath === activeImage ? 'active' : ''}`}
                onClick={() => setActiveImage(img.imagePath)}
              >
                <img
                  src={
                    img.imagePath?.startsWith('http')
                    ? img.imagePath
                    : `http://localhost:9090${img.imagePath}`}
                    alt={`상품 이미지 ${index + 1}`}
                />
            </div>
            ))}
          </div>
        </div>

        <div className="info-panel">
          <div className='info-top-box'>
            <h1>{product.productName}</h1>
             {(role=="BUYER" || !isLogin) && <LikesBtn styleProps={"like-box"} product={product}/>}
          </div>
          <div className="short-desc">
            <p className='product-n'>상품품번  {product.productId}</p>
            <p className='company-name' onClick={() => navigate('/shop',  { state: { sellerUid: product.sellerUid } })}>{product.companyName}</p>
          </div>

          <div className="review-summary" onClick={handleScrollToReviews} style={{cursor: 'pointer'}}>
            <span className="stars">{'★'.repeat(Math.round(product.averageRating))}</span>
            <span className="rating">{product.averageRating} ({reviewLength}개 리뷰)</span>
          </div>

          <div className="price-section">
            <span className="final-price">{product.price?.toLocaleString()}원</span>
          </div>

          <div className="quantity-selector">
            <span className="label">수량</span>
            <div className="quantity-controls">
              <button onClick={() => handleQuantityChange(-1)} disabled={isDisabled}>-</button>
              <input
                type="number"
                min="1"
                max={Math.min(50, product?.stock ?? 50)}
                value={quantity}
                onChange={handleQuantityTyping}
                onBlur={commitQuantity}
                onKeyDown={handleQuantityKeyDown}
                className="quantity-input"
                inputMode="numeric"
              />
              <button onClick={() => handleQuantityChange(1)}>+</button>
            </div>
            <div className="stock-info">남은 재고: {product.stock}개</div>
          </div>

          <div className="action-buttons">
            {!role || role === 'BUYER' ? (
              <>
                <button type='button' className= {`cart-btn ${product.isDeleted ? 'disabled' : ''}`} disabled={product.isDeleted} onClick={cartBtn}>장바구니 담기</button>
                <button type='button' className={`buy-btn ${product.isDeleted ? 'disabled' : ''}`}  disabled={product.isDeleted} onClick={orderDirectBtn}>바로 구매하기</button>
              </>
            ) : null}
          </div>

          <ul className="shipping-info">
            <li><span>배송 정보</span><span>{productDescription?.shippingInfo}</span></li>
            <li><span>배송일</span><span>내일 도착 예정</span></li>
          </ul>
        </div>
      </div>

      <RecommendBox product={product}/>

      <div className="product-extra-details" ref={reviewsRef}>
        <nav className="tabs-nav">
          <button className={activeTab === 'info' ? 'active' : ''} onClick={() => setActiveTab('info')}>상품 정보</button>
          <button className={activeTab === 'reviews' ? 'active' : ''} onClick={() => setActiveTab('reviews')}>리뷰 ({reviewLength})</button>
          <button className={activeTab === 'shipping' ? 'active' : ''} onClick={() => setActiveTab('shipping')}>배송/반품</button>
        </nav>

        <div className="tab-content">
          {activeTab === 'info' && (
            <ProductDescription productDescription={productDescription} />
          )}
          {activeTab === 'reviews' && (
            <ProductReviews product={product} />
          )}
          {activeTab === 'shipping' && (
            <ProductInquiry />
          )}
        </div>
      </div>
      <button type='button' className='move-top-btn' onClick={handleMoveTop}><IoIosArrowUp/></button>
    </div>
  );
}

export default ProductDetail;
