import React from 'react';
import { useSeller } from '../../hooks/useSeller';
import { useLocation } from 'react-router';
import { FaPhone } from "react-icons/fa6";
import { IoIosMail } from "react-icons/io";

function ShopInfo(props) {

    const location = useLocation();

    const sellerUid = location.state?.sellerUid;
    console.log("판매자 uid",sellerUid);

    const {getShopInfo} = useSeller();
    const {data: shopInfo, isLoading, isError} = getShopInfo(sellerUid)

    if(isLoading) return <p>로딩 중...</p>
    if(isError) return <p>업체 정보를 가져오지 못했습니다.</p>

    return (
        <div className='shop-info-container'>
           <h2>{shopInfo.companyName}</h2>
           <div className='shop-info-box'>
                <p>{shopInfo.companyInfo}</p>
            </div> 
            <div className='shop-info-detail'>
                <p><FaPhone/> {shopInfo.phone}</p>
                <p><IoIosMail/> {shopInfo.sellerEmail}</p>
            </div>
        </div>
    );
}

export default ShopInfo;