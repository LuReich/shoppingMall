import React, { useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import { useLocation, useNavigate } from 'react-router';
import { FaPhone } from "react-icons/fa6";
import { IoIosMail } from "react-icons/io";
import { IoStorefrontSharp } from "react-icons/io5";
import { IoPerson } from "react-icons/io5";
import { FaHeart } from "react-icons/fa";
import { FaStar } from "react-icons/fa";
import { BsShieldFillCheck } from "react-icons/bs";
import ProductCard from '../../components/product/ProductCard';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import '../../assets/css/ShopInfo.css';

function ShopInfo() {

    const location = useLocation();
    const navigate = useNavigate();
    
    // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("price,desc");

    const sellerUid = location.state?.sellerUid;

    const { getShopInfo, getPublicSellerProductList } = useSeller();
    const { data: shopInfoCont, isLoading, isError } = getShopInfo(sellerUid);
    const { data: productList } = getPublicSellerProductList(sellerUid, {
        page,
        size: 8,
        sort,
    });

    if (isLoading) return <p>로딩 중...</p>;
    if (isError) return <p>업체 정보를 가져오지 못했습니다.</p>;

    

    const shopInfo = shopInfoCont?.content;
    const products = productList?.content?.content;
    const totalPages = productList?.content?.totalPages;

    console.log("업체 정보", shopInfo);

    const sortCateg = {
        "price": "price",
        "createAt": "createAt",
        "productName": "productName",
        "likeCount": "likeCount",
        "rating": "averageRating"
    };

    return (
        <div className='shop-info-container'>
            <h2>{shopInfo.companyName}</h2>

            {/* 인기도 표시 박스 */}
            <div className='shop-popularity-box'>
                {
                    shopInfo.isVerified && 
                    <div className='popularity-item'>
                        <BsShieldFillCheck className='popularity-icon verification' />
                        <span>인증업체</span>
                    </div>
                }
                
                <div className='popularity-item'>
                    <FaStar className='popularity-icon star' />
                    <span>리뷰 평점 {shopInfo.averageRating} ({shopInfo.totalReviews?.toLocaleString()}건)</span>
                </div>
                <div className='popularity-item'>
                    <FaHeart className='popularity-icon heart' />
                    <span>좋아요 {shopInfo.totalLikes?.toLocaleString()}개</span>
                </div>
            </div>

            {/* 소개 문구 */}
            <div className='shop-info-box'>
                <p>"{shopInfo.companyInfo}"</p>
            </div>

            {/* 상세 정보 */}
            <div className='shop-info-detail'>
                <p><IoPerson/> UID: {shopInfo.sellerUid}</p>
                <p><FaPhone /> {shopInfo.phone}</p>
                <p><IoIosMail /> {shopInfo.sellerEmail}</p>
                <p><IoStorefrontSharp /> {shopInfo.address}, {shopInfo.addressDetail}</p>
            </div>

            {/* 판매 상품 영역 */}
            <div className='shop-product-box'>
                <div className='shop-product-header'>
                    <h2>{shopInfo.companyName}의 판매 상품</h2>
                    <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg} />
                </div>
                <div className='shop-product-grid'>
                    {
                        products?.length > 0 ? (
                            products.map(product => (
                                <ProductCard product={product} key={product.productId} />
                            ))
                        ) : (
                            <p className='no-products'>판매 중인 상품이 없습니다.</p>
                        )
                    }
                </div>
                {
                    totalPages > 0 && (
                        <Pagination
                            page={page}
                            totalPages={totalPages}
                            onPageChange={(p) => setPage(p)}
                        />
                    )
                }
            </div>
        </div>
    );
}

export default ShopInfo;
