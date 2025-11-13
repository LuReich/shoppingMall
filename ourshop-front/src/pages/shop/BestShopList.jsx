import React, { useState } from 'react';
import ShopCard from '../../components/shop/ShopCard';
import { useSeller } from '../../hooks/useSeller';
import '../../assets/css/BestShopList.css';
import Pagination from '../../components/common/Pagenation';

function BestShopList(props) {

     const [page, setPage] = useState(0);
    
    const {getPublicShopList} = useSeller();
    const {data: PublicShopListData, isLoading, isError} = getPublicShopList({ // 오타 수정: getPublicShopList
        page,
        size:8,
        sort: "averageRating,desc" // 평점 높은 순으로 변경
    });

    if(isLoading) return <p>업체 리스트를 가져오는 중입니다.</p>
    if(isError) return <p>업체 리스트 가져오기를 실패했습니다.</p>

    const ShopList = PublicShopListData?.content?.content;
    console.log("업체 리스트",ShopList);
    const totalPages = PublicShopListData?.content?.totalPages;

    
    return (
        <div className='best-shop-list-container'>
            <h2>베스트 업체</h2>
             <div className="best-radio-group">
                <label className='best-radio-wrap' onClick={() =>navigate('/shop/recommend')}>나에게 딱 맞는 업체를 추천받고 싶으신가요?</label>
            </div>
            <div className='best-shop-list-grid'>
                {
                    ShopList?.map((shop, idx) => (
                        <ShopCard key={shop.sellerUid} shop={shop} rank={idx + 1 + (page * 8)} />
                    ))
                }
            </div>
            {
                totalPages&& (
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

export default BestShopList;