import React, { useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import ShopCard from '../../components/shop/ShopCard';
import '../../assets/css/ShopList.css';
import Pagination from '../../components/common/Pagenation';


function ShopList(props) {

    const [page, setPage] = useState(0);

    const {getPublicShopList} = useSeller();
    const {data: PublicShopListData, isLoading, isError} = getPublicShopList({
        page,
        size:8,
        sort: "createAt,desc"
    });

    if(isLoading) return <p>업체 리스트를 가져오는 중입니다.</p>
    if(isError) return <p>업체 리스트 가져오기를 실패했습니다.</p>

    const ShopList = PublicShopListData?.content?.content;
    console.log("업체 리스트",ShopList);
    const totalPages = PublicShopListData?.content?.totalPages;

    

    return (
        <div className='shop-list-container'>
            <h2>신규 업체</h2>
            <div className='shop-list-grid'>
                {
                    ShopList?.map(shop => (
                        <ShopCard shop={shop}/>
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

export default ShopList;