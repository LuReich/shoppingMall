import React, { useEffect, useState } from "react";
import { useLocation } from "react-router";
import { useProduct } from "../../hooks/useProduct";
import ProductCard from "../../components/product/ProductCard";
import "../../assets/css/ProductList.css";
import Pagination from "../../components/common/Pagenation";
import Sort from "../../components/common/Sort";

function ProductList() {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const categoryId = params.get("categoryId");
  const keywords = params.get("productName");
  const categoryName = location.state?.categoryName;

  // 페이지 & 정렬 상태
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("price,desc");

  // categoryId 변경 시 첫 페이지로 초기화
  useEffect(() => {
    setPage(0);
  }, [categoryId]);

  // 상품 리스트 가져오기
  const { getProductList } = useProduct();
  const { data: products, isLoading, isError } = getProductList({
    page,
    size: 8,
    sort,
    categoryId: categoryId ? Number(categoryId) : undefined,
    productName: keywords || undefined,
  });



  if (isLoading) return <p>상품리스트 가져오는 중...</p>;
  if (isError) return <p>상품리스트 가져오기 실패</p>;

  // 상품 데이터
  const productList = products?.content?.content || [];
  const totalPages = products?.content?.totalPages || 0;

  console.log("검색어", keywords);
  console.log("상품 리스트", productList);
   
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
