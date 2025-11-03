import React, { useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import { useLocation, useNavigate } from 'react-router';
import { FaPhone } from "react-icons/fa6";
import { IoIosMail } from "react-icons/io";
import { IoStorefrontSharp } from "react-icons/io5";
import ProductCard from '../../components/product/ProductCard';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import '../../assets/css/ShopInfo.css';

function ShopInfo(props) {

    const location = useLocation();
    const navigate = useNavigate();
    
    // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("price,desc");

    const sellerUid = location.state?.sellerUid;
    console.log("판매자 uid",sellerUid);

    const {getShopInfo, getPublicSellerProductList} = useSeller();
    const {data: shopInfoCont, isLoading, isError} = getShopInfo(sellerUid);
    const {data: productList} = getPublicSellerProductList(sellerUid,{
        page,
        size: 8,
        sort,
    })


    if(isLoading) return <p>로딩 중...</p>
    if(isError) return <p>업체 정보를 가져오지 못했습니다.</p>

    const shopInfo = shopInfoCont?.content;

    const products = productList?.content?.content;

    console.log("판매 상품", products);


    const totalPages = productList?.content?.totalPages; 
    //이거 따로 api 요청 해야될 듯 public/{sellerUid}/에 해당 업체가 판매하는 상품 추가 
    // 또는 해당 업체가 판매하는 상품만 조회하는데 public으로 

     
  const sortCateg = {
      "price": "price",
      "createAt": "createAt",
      "productName": "productName",
      "likeCount" : "likeCount",
      "rating": "averageRating"
  }
    return (
        <div className='shop-info-container'>
           <h2>{shopInfo.companyName}</h2>
           <div className='shop-info-box'>
                <p>"{shopInfo.companyInfo}"</p>
            </div> 
            <div className='shop-info-detail'>
                <p><FaPhone/> {shopInfo.phone}</p>
                <p><IoIosMail/> {shopInfo.sellerEmail}</p>
                <p><IoStorefrontSharp /> {shopInfo.address}, {shopInfo.addressDetail}</p>
            </div>
            <div className='shop-product-box'>
                <div className='shop-product-header'>
                    <h2>{shopInfo.companyName}의 판매 상품</h2>
                    <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
                </div>
                <div className='shop-product-grid'>
                    {
                        products?.length > 0 ? products.map(product => (
                            <ProductCard product={product} key={product.productId}/>
                        )) : <p className='no-products'>판매 중인 상품이 없습니다.</p>
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
        </div>
    );
}

export default ShopInfo;