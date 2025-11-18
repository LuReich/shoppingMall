import React, { useEffect, useState } from 'react';
import { useProduct } from '../../hooks/useProduct';
import { useSeller } from '../../hooks/useSeller';
import ShopCard from '../../components/shop/ShopCard';
import '../../assets/css/RecommendShop.css';
import { authStore } from '../../store/authStore';
import { useNavigate } from 'react-router';
import { FaSearch } from "react-icons/fa";
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import Loader from '../../utils/Loaders';


function RecommendShop() {
    const navigate = useNavigate();
    const { getLikedProducts, getProductList } = useProduct();
    const { getPublicShopList } = useSeller();
    const {isLogin, role, user} = authStore(state => state);

    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createAt,desc");
    
    //검색
    const [searchField, setSearchField] = useState("companyName");
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [verificationFilter, setVerificationFilter] = useState("");
    const [recommendedShops, setRecommendedShops] = useState([]);

    // 1. 사용자가 좋아요한 상품 목록 가져오기
    const { data: likedProductsData, isLoading: isLoadingLikes } = getLikedProducts();

    // 2. 모든 상품 및 업체 목록 가져오기
    const { data: allProductsData, isLoading: isLoadingProducts } = getProductList({ size: 1000 }); // 충분히 많은 상품을 가져옴
    const { data: allShopsData, isLoading: isLoadingShops } = getPublicShopList({
        page,
        size:1000,
        sort,
        isVerified: verificationFilter,
        ...searchParams
    });

    console.log("allProductsData:", allProductsData);
    console.log("allShopsData:", allShopsData);
    console.log("likedProductsData:", likedProductsData);

     // 필터가 변경되면 세팅 초기화 
    useEffect(() => {
        setSearchParams({});
        setSearchKeyword("");
    }, [searchField]);
    
    useEffect(() => {
        setPage(0);
        setSearchParams({});
        setSearchKeyword("");
    }, [verificationFilter]);
          
    useEffect(() => {
        if (likedProductsData && allProductsData && allShopsData) {
            // 3. 좋아요한 상품들의 카테고리 ID 추출 (중복 제거)
            const likedCategoryIds = [...new Set(likedProductsData?.content?.content.map(item => item.categoryId))];

            if (likedCategoryIds.length === 0) {
                setRecommendedShops([]);
                return;
            }

            // 4. 전체 상품에서 추천 카테고리에 해당하는 상품들의 sellerUid 추출
            
            const allProducts = allProductsData.content.content;
            const relevantSellerUids = [...new Set(
                allProducts
                    .filter(product => likedCategoryIds.includes(product.categoryId))
                    .map(product => product.sellerUid)
            )];

            // 5. 추출된 sellerUid를 기반으로 최종 업체 목록 필터링
            const allShops = allShopsData.content.content;
            const filteredShops = allShops.filter(shop =>
                relevantSellerUids.includes(shop.sellerUid)
            );

            setRecommendedShops(filteredShops);
        }
    }, [likedProductsData, allProductsData, allShopsData]);

    useEffect(()=>{
        if(!isLogin || role !== "BUYER"){
            alert("구매자 로그인이 필요한 서비스입니다.");
            navigate("/login");
        }
    },[]);

    // 검색 버튼 클릭 시 검색조건 업데이트
    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setPage(0);
        // 검색어가 있으면 검색조건 적용, 없으면 초기화
        if (searchKeyword.trim()) {
            setSearchParams({ [searchField]: searchKeyword });
        } else {
            setSearchParams({});
        }

    };
    
    const sortCateg = {
      "createAt": "createAt",
      "productName": "companyName",
      "likeCount" : "totalLikes",
      "rating": "averageRating",
      "reviewCount": "totalReviews"
    }

    const totalPages = allShopsData?.content?.totalPages;

    
    if (isLoadingLikes || isLoadingProducts || isLoadingShops) return <Loader/>;

    return (
        <div className='shop-list-container'>
            <h2>{user?.content?.buyerId}님을 위한 추천 업체</h2>
            <div className='product-filter-box'>
                             <div className="product-radio-group">
                                <label className='product-radio-wrap'>
                                  <input type='radio' name='status' value='' 
                                    checked={verificationFilter === ""} 
                                    onChange={(e) => setVerificationFilter(e.target.value)} />
                                  <span>전체</span>
                                </label>
                                <label className='product-radio-wrap'>
                                  <input type='radio' name='status' value={true} 
                                    checked={verificationFilter === "true"} 
                                    onChange={(e) => setVerificationFilter(e.target.value)} />
                                  <span>판매인증</span>
                                </label>
                                <label className='product-radio-wrap'>
                                  <input type='radio' name='status' value={false} 
                                    checked={verificationFilter === "false"} 
                                    onChange={(e) => setVerificationFilter(e.target.value)} />
                                  <span>판매미인증</span>
                                </label>
                             </div>
                            <form  className='product-form-box' onSubmit={handleSearchSubmit}>
                                <div className='product-search-form-b'>
                                    <select className='search-product-select'
                                        value={searchField}
                                        onChange={(e)=> setSearchField(e.target.value)}>
                                        <option value="companyName">업체명</option>
                                    </select>
                                    <input
                                        type="text"
                                        placeholder="검색어를 입력하세요"
                                        value={searchKeyword}
                                        onChange={(e) => setSearchKeyword(e.target.value)}
                                        className="search-product-post-input"
                                    />
                                    <button type="submit" className="product-search-btn">
                                     <FaSearch/>
                                    </button>
                                </div>
                            </form>
                        </div>
            <p className='shop-list-subtitle'>회원님께서 좋아하신 상품들을 기반으로 맞춤 업체를 추천해 드립니다.</p>
            <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
            <div className='rec-shop-list-grid'>
                {recommendedShops.length > 0 ? (
                    recommendedShops.map(shop => <ShopCard key={shop.sellerUid} shop={shop} />)
                ) : (
                    <div className='no-results'>
                        <img src='/heart.png' alt='heart' className='heart-icon' onClick={() => navigate('/products')}/>
                        <p>추천할 업체가 없습니다. 마음에 드는 상품에 '좋아요'를 눌러보세요!</p>
                    </div>
                )}
            </div>

            {
                totalPages? (
                    <Pagination
                        page={page}
                        totalPages={totalPages}
                        onPageChange={(p) => setPage(p)}
                    />
                ) : null
            }
        </div>
    );
}

export default RecommendShop;