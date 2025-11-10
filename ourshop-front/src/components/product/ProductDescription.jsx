import React from 'react';
import '../../assets/css/ProductDescription.css';

function ProductDescription({ productDescription }) {
  // description 문자열에서 상대경로를 절대경로로 변환
  const fixedDescription = productDescription?.description
    ?.replaceAll('src="/temp/', 'src="http://localhost:9090/temp/')
    ?.replaceAll('src="/product/', 'src="http://localhost:9090/product/')
    || '';

  return (
    <div className="info-content">
      <h3>상품 상세 정보</h3>
      <div
        className="product-description-html"
        dangerouslySetInnerHTML={{ __html: fixedDescription }}
      />
    </div>
  );
}

export default ProductDescription;
