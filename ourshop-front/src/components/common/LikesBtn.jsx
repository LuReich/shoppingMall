import React, { useEffect, useState } from 'react';
import { FaRegHeart, FaHeart } from "react-icons/fa";
import { useNavigate } from 'react-router';
import { useProduct } from '../../hooks/useProduct';
import { authStore } from '../../store/authStore';
import '../../assets/css/LikesBtn.css';

function LikesBtn({ product, styleProps }) {
  const { likeProduct, getLikedProducts } = useProduct();
  const { data: LikedProducts } = getLikedProducts();
  const { mutate: likeProductMutate } = likeProduct();

  const { isLogin } = authStore(state => state);
  const navigate = useNavigate();

  const [isLiked, setIsLiked] = useState(false);

  const products = LikedProducts?.content?.content || [];

  // 로그인 후, 좋아요 목록 기준으로 현재 상품 좋아요 여부 판단
  useEffect(() => {
    if (!products.length || !product?.productId) return;
    const liked = products.some(p => p.productId === product.productId);
    setIsLiked(liked);
  }, [products, product]);

  // 좋아요 클릭 시
  const handleLike = (e) => {
    e.preventDefault(); // 링크 안에서 눌렀을 때 페이지 이동 방지
    if (!isLogin) {
      alert("로그인이 필요한 서비스입니다.");
      navigate('/login');
      return;
    }
    likeProductMutate(product.productId);
    setIsLiked(prev => !prev); // 즉시 토글 반영
  };

    return (
      <div className={styleProps} onClick={handleLike}>
      {isLiked ? (
        <FaHeart className='like-heart filled' style={{ color: 'red'}}/>
      ) : (
        <FaRegHeart className='like-heart empty' />
      )}
    </div>
    );
}

export default LikesBtn;