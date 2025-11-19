import React from 'react';
import { useNavigate } from 'react-router';
import '../../assets/css/ShopCard.css';
import { useSeller } from '../../hooks/useSeller';
import { useCategory } from '../../hooks/useCategory';
import { BsShieldFillCheck } from "react-icons/bs";
import { FaHeart } from "react-icons/fa";
import { getTimeAgo } from '../../utils/getTimeAgo';

function ShopCard({ shop, rank }) {
  const navigate = useNavigate();
  const { getPublicSellerProductList } = useSeller();
  const { getCategoryList } = useCategory();
  const { data: productListData } = getPublicSellerProductList(shop.sellerUid);
  const { data: categoryListData } = getCategoryList();

  const productList = productListData?.content?.content;
  const categoriesData = categoryListData?.content;

  // 카테고리별 상품 수 계산 및 정렬
  const categoryTag = React.useMemo(() => {
    if (!productList || !categoriesData) return [];

    const categoryCounts = productList.reduce((acc, product) => {
      acc[product.categoryId] = (acc[product.categoryId] || 0) + 1;
      return acc;
    }, {});

    return Object.entries(categoryCounts)
      .sort(([, countA], [, countB]) => countB - countA)
      .map(([categoryId]) => categoriesData.find(cat => cat.categoryId === Number(categoryId))?.categoryName)
      .filter(Boolean); // categoryName이 없는 경우 제외
  }, [productList, categoriesData]);
  

   //카테고리별 아이콘
  const CategIcons = {
    1 : "phone.png",
    5 : "apple.png",
    9 : "t-shirt.png",
    13: "books.png"
  };

  //부모 카테고리 아이콘 가져오기
  const parentIcon = React.useMemo(() => {
    if (!productList || !categoriesData || productList.length === 0) return null;

    // 부모 카테고리별로 상품 수를 합산.
    const parentCategoryCounts = productList.reduce((acc, product) => {
      const category = categoriesData.find(cat => cat.categoryId === product.categoryId);
      if (category) {
        // 부모 ID가 없으면 자기 자신을 부모로 간주.
        const parentId = category.parentId ?? category.categoryId;
        acc[parentId] = (acc[parentId] || 0) + 1;
      }
      return acc;
    }, {});

    // 가장 많은 상품 수를 가진 부모 카테고리 ID를 찾기.
    const topParentId = Object.keys(parentCategoryCounts).length > 0
      ? Number(Object.keys(parentCategoryCounts).reduce((a, b) => parentCategoryCounts[a] > parentCategoryCounts[b] ? a : b))
      : null;

    if (!topParentId) return null;

    return CategIcons[topParentId];
  }, [productList, categoriesData]);

  console.log("categoryTag", categoryTag)

  return (
    <div
      className="shop-card"
      onClick={() => navigate('/shop', { state: { sellerUid: shop.sellerUid } })}
    >
      <div className="shop-card-inner">
        {rank && <div className="shop-card-rank">{rank}</div>}
  
        <div className="shop-info-con">
          <div className="shop-name-box">
            <h3>
              {parentIcon && (
                <img src={`/${parentIcon}`} alt="category-icon" className="category-icon" />
              )}
              <p style={{marginTop: "5px"}}>{shop.companyName}</p>
            </h3>
          </div>
          
          <div className="shop-info-rating">
            <div><p style={{color: "orange", fontSize: "15px"}}>★</p> {shop.averageRating?.toFixed(1)} ({shop.totalReviews?.toLocaleString()}건)</div>
            <div>
              <FaHeart style={{marginBottom: "3px"}}/> <p>({shop.totalLikes?.toLocaleString()})</p>
            </div>
          </div>
          {shop.isVerified && (
            <div className="shop-verificaion-box">
              <BsShieldFillCheck />인증 업체
            </div>
          )}
          <p>"{shop.companyInfo}"</p>
          <div className="category-tag-box">
            {categoryTag?.length > 0 ? (
              categoryTag.slice(0,3).map((x) => <span key={x}>#{x}</span>)
            ) : (
              <span>상품 등록 중</span>
            )}
          </div>
          <p>{getTimeAgo(shop.createAt)} 가입</p>
        </div>
      </div>
    </div>
  );
}

export default ShopCard;
