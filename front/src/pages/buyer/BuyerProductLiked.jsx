import React, { useState } from 'react';
import { useProduct } from '../../hooks/useProduct';
import ProductCard from '../../components/product/ProductCard';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import '../../assets/css/BuyerProductLiked.css';
import Loader from '../../utils/Loaders';
import { useNavigate } from 'react-router';

function BuyerProductLiked(props) {

    const navigate = useNavigate();

    // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("product.price,desc");

    const {getLikedProducts} = useProduct();
    const {data: LikedProducts} = getLikedProducts({
        sort,
        size:6,
        page,
    });

    const {isLoading, isError} = getLikedProducts();
    if(isLoading) return <Loader/>;
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
                <div className='no-results'>
                    <img src='/heart.png' alt='heart' className='heart-icon' onClick={() => navigate('/products')} />
                    <p>'좋아요'한 상품이 없습니다. 마음에 드는 상품에 '좋아요'를 눌러보세요!</p>
                </div>
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