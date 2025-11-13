import React, { use, useState } from 'react';
import { useProduct } from '../../hooks/useProduct';
import ProductCard from '../product/ProductCard';
import '../../assets/css/ProductSection.css';
import { useSeller } from '../../hooks/useSeller';
import ShopCard from '../shop/ShopCard';
import { IoIosArrowForward } from "react-icons/io";
import { useNavigate } from 'react-router';


function ProductSection() {
  const navigate = useNavigate();
  const { getProductList } = useProduct();
  const { getPublicShopList } = useSeller();
  
  const { data, isLoading, isError } = getProductList({ page: 0, size: 8 });
  const { data: PublicShopListData } = getPublicShopList({ size: 8, sort: "createAt,desc" });
  
  const products = data?.content?.content || [];
  const shops = PublicShopListData?.content?.content || [];

  // 페이지 상태 (0 -> 첫 4개, 1 -> 다음 4개)
  const [productPage, setProductPage] = useState(0);
  const [shopPage, setShopPage] = useState(0);

  const itemsPerPage = 4;

  const getPagedData = (data, page) => {
    const start = page * itemsPerPage;
    return data.slice(start, start + itemsPerPage);
  };

  const nextProductPage = () => {
    setProductPage((prev) => (prev + 1) % Math.ceil(products.length / itemsPerPage));
  };
  const prevProductPage = () => {
    setProductPage((prev) =>
      prev === 0 ? Math.ceil(products.length / itemsPerPage) - 1 : prev - 1
    );
  };

  const nextShopPage = () => {
    setShopPage((prev) => (prev + 1) % Math.ceil(shops.length / itemsPerPage));
  };
  const prevShopPage = () => {
    setShopPage((prev) =>
      prev === 0 ? Math.ceil(shops.length / itemsPerPage) - 1 : prev - 1
    );
  };

  if (isLoading) return <p>상품을 불러오는 중입니다...</p>;
  if (isError) return <p>상품 정보를 불러오는 중 오류가 발생했습니다.</p>;
  if (!products.length) return <p>등록된 상품이 없습니다.</p>;

  return (
    <section className="product-section">
      {/* 오늘의 상품 */}
      <h2 className="section-title">오늘의 상품
        <p className="section-sub-title"
        onClick={() => navigate('/products')}
        >우리샵의 모든 상품을 둘러보세요!<IoIosArrowForward style={{marginBottom: "3px"}}/></p>
      </h2>
      <div className="carousel-container">
        <button className="arrow-btn-p left" onClick={prevProductPage}>‹</button>
        <div className="carousel-grid"  id='product-grid' key={productPage}>
          {getPagedData(products, productPage).map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))}
        </div>
        <button className="arrow-btn-p right" onClick={nextProductPage}>›</button>
      </div>

      {/* 신규 업체 */}
      <h2 className="section-title">신규 업체
        <p className="section-sub-title"
        onClick={() => navigate('/shop/list')}
        >우리샵에 입점한 업체가 궁금하신가요?<IoIosArrowForward style={{marginBottom: "3px"}}/></p>
      </h2>
      <div className="carousel-container" key={shopPage}>
        <button className="arrow-btn left" onClick={prevShopPage}>‹</button>
        <div className="carousel-grid">
          {getPagedData(shops, shopPage).map((shop) => (
            <ShopCard key={shop.sellerId} shop={shop} />
          ))}
        </div>
        <button className="arrow-btn right" onClick={nextShopPage}>›</button>
      </div>
    </section>
  );
}

export default ProductSection;
