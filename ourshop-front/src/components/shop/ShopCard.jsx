import React from 'react';
import { useNavigate } from 'react-router';
import '../../assets/css/ShopCard.css';
import { useSeller } from '../../hooks/useSeller';
import { useCategory } from '../../hooks/useCategory';
import { BsShieldFillCheck } from "react-icons/bs";
import { FaRegHeart } from "react-icons/fa";
import { getTimeAgo } from '../../utils/getTimeAgo';

function ShopCard({ shop, rank }) {
  const navigate = useNavigate();
  const { getPublicSellerProductList } = useSeller();
  const { getCategoryList } = useCategory();
  const { data: productListData } = getPublicSellerProductList(shop.sellerUid);
  const { data: categoryListData } = getCategoryList();

  const productList = productListData?.content?.content;
  const categoriesData = categoryListData?.content;
  const categories = productList?.map((product) => product.categoryId);
  const categoryTag = categoriesData
    ?.filter((category) => categories?.includes(category.categoryId))
    .map((category) => category.categoryName);

  return (
    <div
      className="shop-card"
      onClick={() => navigate('/shop', { state: { sellerUid: shop.sellerUid } })}
    >
      {rank && <div className="shop-card-rank">{rank}</div>}

      <div className="shop-name-box">
        <h3>{shop.companyName}</h3>
        {shop.isVerified && (
          <div className="shop-verificaion-box">
            <BsShieldFillCheck />
          </div>
        )}
      </div>

      <div className="shop-info-con">
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

      <div className="shop-info-rating">
        <div>★ {shop.averageRating.toFixed(1)} ({shop.totalReviews.toLocaleString()}건)</div>
        <div>
          <FaRegHeart /> ({shop.totalLikes?.toLocaleString()})
        </div>
      </div>
    </div>
  );
}

export default ShopCard;
