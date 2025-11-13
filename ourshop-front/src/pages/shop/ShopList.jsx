import React, { useEffect, useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import ShopCard from '../../components/shop/ShopCard';
import '../../assets/css/ShopList.css';
import Pagination from '../../components/common/Pagenation';
import { FaSearch } from "react-icons/fa";
import Sort from '../../components/common/Sort';
import { Navigate, useNavigate } from 'react-router';


function ShopList(props) {

    const navigate = useNavigate();
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createAt,desc");

    //검색
    const [searchField, setSearchField] = useState("companyName");
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [verificationFilter, setVerificationFilter] = useState("");

    const {getPublicShopList} = useSeller();
    const {data: PublicShopListData, isLoading, isError} = getPublicShopList({
        page,
        size:8,
        sort,
        isVerified: verificationFilter,
        ...searchParams
    });

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
      
      
    if(isLoading) return <p>업체 리스트를 가져오는 중입니다.</p>
    if(isError) return <p>업체 리스트 가져오기를 실패했습니다.</p>

    const ShopList = PublicShopListData?.content?.content;
    console.log("업체 리스트",ShopList);
    const totalPages = PublicShopListData?.content?.totalPages;

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

    return (
        <div className='shop-list-container'>
            <h2>우리샵에 입점한 업체</h2>
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
                    <label className='product-radio-wrap' onClick={() =>navigate('/shop/recommend')}>나에게 딱 맞는 업체를 추천받고 싶으신가요?</label>
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
            <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
            <div className='shop-list-grid'>
                {
                    ShopList?.map(shop => (
                        <ShopCard shop={shop}/>
                    ))
                }
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

export default ShopList;