import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router";
import { useProduct } from "../../hooks/useProduct";
import ProductCard from "../../components/product/ProductCard";
import "../../assets/css/ProductList.css";
import Pagination from "../../components/common/Pagenation";
import Sort from "../../components/common/Sort";
import { useCategory } from "../../hooks/useCategory";
import { FaSearch } from "react-icons/fa";
import Loader from "../../utils/Loaders";


function ProductList() {
  const navigate = useNavigate();
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const categoryId = params.get("categoryId");
  const keywords = params.get("productName"); //헤더 검색바 검색어 가져오기
  const categoryName = location.state?.categoryName;


  // 페이지 & 정렬 상태
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("averageRating,desc");

  //검색
  const [searchField, setSearchField] = useState("productName");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [searchParams, setSearchParams] = useState({});
  const [categoryFilter, setCategoryFilter] = useState(""); // 카테고리 필터

  const {getCategoryList} = useCategory();
  const {data: categoryData} = getCategoryList();
  const category = categoryData?.content;

  console.log("상품리스트 카테고리", category);

  const parentCategories = category?.filter(x => x.parentId === null); //부모 카테고리
  const isParent = parentCategories?.find(x => x.categoryId === Number(categoryId)); //부모카테고리에 해당할 경우 true
  const childCategories = category?.filter(x => x.parentId === Number(categoryId)); //해당 부모의 자식 카테고리

  
  // 필터가 변경되면 세팅 초기화 
  useEffect(() => {
    setSearchParams({});
    setSearchKeyword("");
  }, [searchField]);
  
  
 useEffect(() => {
    setPage(0);
    setSearchParams({});
    setSearchKeyword("");
  }, [categoryFilter]);
  

  // categoryId 변경 시 첫 페이지로 초기화
  useEffect(() => {
    setPage(0);
  }, [categoryId]);

  //헤더의 검색바 내용 변경되면 상품리스트페이지 검색바 초기화
  useEffect(() => {
    setPage(0);
    setSearchParams({});
    setSearchKeyword("");
  },[keywords])

  // 상품 리스트 가져오기
  const { getProductList } = useProduct();
  const { data: products, isLoading, isError } = getProductList({
    page,
    size: 8,
    sort,
    categoryId: categoryFilter? categoryFilter : categoryId ? Number(categoryId) : null,
    productName: keywords,
    ...searchParams,
  });



  if (isLoading) return <Loader/>;
  if (isError) return <p>상품리스트 가져오기 실패</p>;

  // 상품 데이터
  const productList = products?.content?.content || [];
  const totalPages = products?.content?.totalPages || 0;

  console.log("검색어", keywords);
  console.log("상품 리스트", productList);
   

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
      "price": "price",
      "createAt": "createAt",
      "productName": "productName",
      "likeCount" : "likeCount",
      "rating": "averageRating"
  }

  return (
    <div className="product-list-container">
      <h2>{categoryName ? `${categoryName} 상품 목록` : keywords? `[${keywords}]에 대한 검색결과`: "전체 상품 목록"}</h2>
      {/*검색바*/}
      <div className='product-filter-box'>
            <div className="product-radio-group">
                  {
                    (isParent && childCategories.length > 0 || categoryId === null) && 
                    <label className='product-radio-wrap'>
                      <input type='radio' name='status' value='' 
                        checked={categoryFilter === ""} 
                        onChange={(e) => setCategoryFilter(e.target.value)} />
                      <span>전체</span>
                    </label>
                  }
                  {
                    (isParent && childCategories.length > 0) &&
                    childCategories.map(c => (
                      <label className='product-radio-wrap'>
                        <input type='radio' name='status' value={c.categoryId} 
                          checked={Number(categoryFilter) === c.categoryId}
                          onChange={(e) => setCategoryFilter(Number(e.target.value))} />
                        <span>{c.categoryName}</span>
                      </label>
                    )) 
                    
                  }
                  {
                    categoryId === null&&
                    parentCategories.map(c => (
                      <label className='product-radio-wrap'>
                        <input type='radio' name='status' value={c.categoryId} 
                          checked={Number(categoryFilter) === c.categoryId}
                          onChange={(e) => setCategoryFilter(Number(e.target.value))} />
                        <span>{c.categoryName}</span>
                      </label>
                    ))   
                  }
                  {
                    (categoryId !== null && !isParent) &&
                    <>
                      <label className='product-radio-wrap' 
                      onClick={() => navigate("/shop/list")}>신뢰 가는 업체를 찾고 싶으신가요?</label>
                      <label className='product-radio-wrap'
                      onClick={() => navigate("/shop/recommend")}
                      >나에게 딱 맞는 업체를 추천받고 싶으신가요?</label>
                    </>
                  }
                  
              </div>
          <form  className='product-form-box' onSubmit={handleSearchSubmit}>
              <div className='product-search-form-b'>
                <select className='search-product-select'
                  value={searchField}
                  onChange={(e)=> setSearchField(e.target.value)}>
                  <option value="productName">상품명</option>
                  <option value="companyName">업체명</option>
                  <option value="productId">상품 아이디</option>
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
      {/* 정렬 옵션 */}
        <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
      {/* 상품 목록 */}
      <div className="product-box">
        {productList.length > 0 ? (
          productList.map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))
        ) : (
          <p>{categoryName? "해당 카테고리" : keywords? `[${keywords}]와 관련된 검색 결과` : "해당 페이지"}에 상품이 없습니다.</p>
        )}
      </div>
      {/* 페이지네이션 컴포넌트 */}
      {totalPages ? (
        <Pagination
          page={page}
          totalPages={totalPages}
          onPageChange={(p) => setPage(p)}
        />
      ) : null}
    </div>
  );
}

export default ProductList;
