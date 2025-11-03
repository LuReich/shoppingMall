import React, { useState } from 'react';
import { useProduct } from '../../hooks/useProduct';
import ProductCard from '../../components/product/ProductCard';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import '../../assets/css/BuyerProductLiked.css';

function BuyerProductLiked(props) {

    // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("product.price,desc");

    const {getLikedProducts} = useProduct();
    const {data: LikedProducts} = getLikedProducts({
        sort,
        size:8,
        page,
    });

    const {isLoading, isError} = getLikedProducts();
    if(isLoading) return <div>로딩중...</div>;
    if(isError) return <div>좋아요한 상품정보 조회에 오류가 발생하였습니다.</div>;

    const totalPages = LikedProducts?.content?.totalPages || 0;
    const products = LikedProducts?.content?.content || [];

    const sortCateg = {
        "productName": "product.productName",
        "price": "product.price",
        "createAt": "product.createAt",
        "rating": "product.averageRating",
        "likeCount" : "product.likeCount",
    };


    return (
        <div className='buyer-product-liked-container'>
            <h2>좋아요한 상품 목록</h2>
            <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
            <div className='liked-products-grid'>
            {
                products.length > 0 ? products.map( product => (
                    <ProductCard key={product.productId} product={product}/>
                ))
                :
                <p>좋아요한 상품이 없습니다.</p>
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
    );
}

export default BuyerProductLiked;