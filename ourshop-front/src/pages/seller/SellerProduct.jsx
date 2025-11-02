import React, { useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import ProductCard from '../../components/product/ProductCard';
import '../../assets/css/SellerProduct.css';
import { useNavigate } from 'react-router';

function SellerProduct(props) {

    const navigate = useNavigate();

    const [check, setCheck] = useState("");
    const {getSellerProductList} = useSeller();
    const {data: sellerProductList} = getSellerProductList();
    console.log("판매자 상품 리스트",sellerProductList);

    const products = sellerProductList?.content?.content;
    console.log("판매자 상품",products);

    //체크박스 핸들러
    const handleCheck = (productId) => {
        if(check === productId){
            setCheck(""); // 이미 선택된 경우 선택 해제
        }else{
            setCheck(productId); // 새로운 상품 선택
        }
    }

    //수정 버튼
    const updateBtn = () => {
        if(!check){
            alert("수정할 상품을 선택해주세요.");
        }else{
            navigate(`/seller/mypage/products/${check}`);
        }
    }

    return (
        <div className='seller-product-container'>
            <h2>판매자 상품</h2>
            <div className='seller-product'>
                {
                    products?.map(x => (
                        <div key={x.productId} className='seller-product-item'>
                            <input type="checkbox" 
                                className='seller-product-checkbox'
                                value={x.productId} 
                                onChange={() => handleCheck(x.productId)}
                                checked={check === x.productId} />
                            <ProductCard product={x} />
                        </div>
                    ))
                }
                <div className='seller-btn-box'>
                    <button type='button' onClick={updateBtn}>수정하기</button>
                    <button type='button' onClick={()=> navigate(`/seller/mypage/products/upload`)}>등록하기</button>
                </div>
            </div>
        </div>
    );
}

export default SellerProduct;