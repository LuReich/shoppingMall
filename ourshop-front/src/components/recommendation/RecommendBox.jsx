import React from 'react';
import { useProduct } from '../../hooks/useProduct';
import { useCategory } from '../../hooks/useCategory';
import ProductCard from "../product/ProductCard";
import '../../assets/css/RecommendBox.css';

function RecommendBox({product}) {

    const {getProductList} = useProduct();

    /*상품이 충분하지 않아서 부모로 조회하나, 충분해지면 단순 부모ID 조회 대신 단순 categoryId로 조회로 수정할 예정 */
    const {getCategoryList} = useCategory();
    const {data: categoryData} = getCategoryList();
    //현재 상품의 카테고리의 부모 카테고리 아이디 찾기
    const parantCategoryId = categoryData?.content.find(cate => cate.id === product.categoryId)?.parentId;
    //같은 부모 카테고리의 상품 리스트 조회
    const {data: productListData} = getProductList({categoryId: parantCategoryId, sort: "likeCount,desc", size: 8});

    const recommendedProducts = productListData?.content?.content.filter(p => p.productId !== product.productId);
     //디버깅
    console.log("카테고리 데이터", categoryData?.content);
    console.log("추천 상품", recommendedProducts);



    return (
        <div className='recommend-box'>
            <h3 className='recommend-title'>추천 상품</h3>
            <div className='recommend-product-list'>
                {
                    recommendedProducts?.length > 0 && recommendedProducts?.map(product => (
                        <div className='recommend-product-card-wrapper' key={product.productId}>
                            <ProductCard product={product}/>
                        </div>
                    ))
                }
            </div>
        </div>
    );
}

export default RecommendBox;