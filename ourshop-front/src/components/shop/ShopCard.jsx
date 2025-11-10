import React from 'react';
import { useNavigate } from 'react-router';
import '../../assets/css/ShopCard.css';
import { useSeller } from '../../hooks/useSeller';
import { useCategory } from '../../hooks/useCategory';
import { BsShieldFillCheck } from "react-icons/bs";


function ShopCard({shop}) {
    const navigate = useNavigate();
    const {getPublicSellerProductList} = useSeller();
    const {getCategoryList} = useCategory();
    const {data: productListData} = getPublicSellerProductList(shop.sellerUid);
    const {data: categoryListData} = getCategoryList();

    const productList = productListData?.content?.content;
    const categoriesData = categoryListData?.content;
    const categories = productList?.map(product => product.categoryId);

    const categoryTag = categoriesData?.filter(category => categories?.includes(category.categoryId)).map(category => category.categoryName);


    //디버깅
    console.log("판매 상품", productList);
    console.log("카테고리 리스트", categoriesData);
    console.log("판매 상품 카테고리", categories);
    console.log("판매 상품 태그", categoryTag);


    return (
        <div className='shop-card' onClick={() => navigate('/shop', {state: {sellerUid: shop.sellerUid} })}>
            <div className='shop-name-box'>
                <h3>{shop.companyName}</h3>
            </div>
            <div className='shop-verificaion-box'>{shop.isVerified ? <BsShieldFillCheck /> : null}</div>
            <div className='shop-info-con'>
                <p>"{shop.companyInfo}"</p>
                <div className='category-tag-box'>
                {
                    categoryTag.length > 0?
                    categoryTag.map(x => (
                        <span>#{x}</span>
                    ))
                    :
                    <span>상품 등록 중</span>
                }
                </div>
                <p>{new Date(shop.createAt).toLocaleDateString()}</p>
            </div>
        </div>
    );
}

export default ShopCard;