import React from 'react';
import { useProduct } from '../../hooks/useProduct';
import ProductCard from '../product/ProductCard';
import '../../assets/css/ProductSection.css';


function ProductSection() {
  const { getProductList } = useProduct();
  const { data, isLoading, isError } = getProductList({page:0, size: 8});

  //추가
  const products = data?.content?.content || [];

  if (isLoading) return <p>상품을 불러오는 중입니다...</p>;
  if (isError) return <p>상품 정보를 불러오는 중 오류가 발생했습니다.</p>;
  if (!products.length) return <p>등록된 상품이 없습니다.</p>; //추가
  //if (!data?.content?.length) return <p>등록된 상품이 없습니다.</p>;
  
  console.log("상품", data);
  
  // 메인페이지 Product Section은 랜덤 상품 8개
  //랜덤으로 상품 8개 뽑기
  /*const getRandomBooks = (books, count) => {
      const shuffled = [...books].sort(() => Math.random() - 0.5);
      return shuffled.slice(0, count);
  };
  const filteredData = getRandomBooks(data.content, 8);
  console.log(data);*/

  const getRandomProducts = (items, count) => {
    const shuffled = [...items].sort(() => Math.random() - 0.5);
    return shuffled.slice(0, count);
  };
  const filteredData = getRandomProducts(products, 8);

  return (
    <section className="product-section">
      <h2 className="section-title">오늘의 상품</h2>
      <div className="product-grid">
        {filteredData.map((product) => (
          <ProductCard key={product.productId} product={product} />
        ))}
      </div>
    </section>
  );
}

export default ProductSection;
