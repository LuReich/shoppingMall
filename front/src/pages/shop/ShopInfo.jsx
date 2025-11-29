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
import { FaIdCard } from "react-icons/fa";
import ProductCard from '../../components/product/ProductCard';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import '../../assets/css/ShopInfo.css';
import Loader from '../../utils/Loaders';
import { FaSearch } from "react-icons/fa";

function ShopInfo() {

    const location = useLocation();
    const navigate = useNavigate();
    
    // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("price,desc");

    const [searchKeyword, setSearchKeyword] = useState(""); // 검색어 입력 상태
    const [submittedKeyword, setSubmittedKeyword] = useState(""); // 제출된 검색어 상태

    const sellerUid = location.state?.sellerUid;

    const { getShopInfo, getPublicSellerProductList } = useSeller();
    const { data: shopInfoCont, isLoading, isError } = getShopInfo(sellerUid);
    const { data: productList } = getPublicSellerProductList(sellerUid, {
        page,
        size: 8,
        sort,
        productName: submittedKeyword,
    });

    if (isLoading) return <Loader/>;
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

    //검색 버튼 클릭시
    const handleSearch = (e) => {
        e.preventDefault();
        setSubmittedKeyword(searchKeyword);
    };

    //휴대폰 포맷
  const formatPhone = (value) => {
    if (!value) return "";
    const raw = value.replace(/\D/g, ""); // 숫자만

    // 02 번호
    if (raw.startsWith("02")) {
      if (raw.length === 9)
        return raw.replace(/(\d{2})(\d{3})(\d{4})/, "$1-$2-$3");
      if (raw.length === 10)
        return raw.replace(/(\d{2})(\d{4})(\d{4})/, "$1-$2-$3");
    }

    // 3자리 지역번호
    if (raw.length === 10)
      return raw.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3");

    if (raw.length === 11)
      return raw.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");

    return value;
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
                    <span>리뷰 평점 {shopInfo.averageRating?.toFixed(1)} ({shopInfo.totalReviews?.toLocaleString()}건)</span>
                </div>
                <div className='popularity-item'>
                    <FaHeart className='popularity-icon heart' />
                    <span>좋아요 {shopInfo.totalLikes?.toLocaleString()}개</span>
                </div>
            </div>

            {/* 소개 문구 */}
            <div className='shop-info-box'>
                <p className='shop-introduce'>"{shopInfo.companyInfo}"</p>
            </div>

            {/* 상세 정보 */}
            <div className='shop-info-detail'>
                <p><IoPerson/> UID: {shopInfo.sellerUid}</p>
                <p><FaPhone /> {formatPhone(shopInfo.phone)}</p>
                <p><IoIosMail /> {shopInfo.sellerEmail}</p>
                <p><IoStorefrontSharp /> {shopInfo.address}, {shopInfo.addressDetail}</p>
                <p><FaIdCard/> 사업자 등록번호: {shopInfo.businessRegistrationNumber.replace(/(\d{3})(\d{2})(\d{5})/, "$1-$2-$3")}</p>
            </div>

            {/* 판매 상품 영역 */}
            <div className='shop-product-box'>
                <div className='shop-product-header'>
                    <h2>{shopInfo.companyName}의 판매 상품</h2>
                  
                </div>
                <div className='shop-porduct-form-box'>
                    <form className='product-search-form-b' onSubmit={handleSearch}>
                        <input type='text' placeholder={`${shopInfo.companyName}의 상품을 검색해보세요`} 
                        value={searchKeyword}
                        className='search-product-post-input'
                        onChange={(e) => setSearchKeyword(e.target.value)}/>
                        <button type='submit' className="product-search-btn"><FaSearch/></button>
                    </form>
                </div>
                  <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg} />
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
