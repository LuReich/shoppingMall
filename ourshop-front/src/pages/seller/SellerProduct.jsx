import React, { useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import ProductCard from '../../components/product/ProductCard';
import '../../assets/css/SellerProduct.css';
import { useNavigate } from 'react-router';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';

function SellerProduct(props) {

    const navigate = useNavigate();

     // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("price,desc");

    //const [check, setCheck] = useState("");
    const {getSellerProductList} = useSeller();
    const {data: sellerProductList} = getSellerProductList({
            page,
            size: 8,
            sort,
        });

    console.log("판매자 상품 리스트",sellerProductList);

    const products = sellerProductList?.content?.content;
    console.log("판매자 상품",products);

    const totalPages = sellerProductList?.content?.totalPages;

    const sortCateg = {
      "price": "price",
      "createAt": "createAt",
      "productName": "productName"
  }

    //체크박스 핸들러
    /*const handleCheck = (productId) => {
        if(check === productId){
            setCheck(""); // 이미 선택된 경우 선택 해제
        }else{
            setCheck(productId); // 새로운 상품 선택
        }
    }*/

    //수정 버튼
    const updateBtn = (productId) => {
       
        navigate(`/seller/mypage/products/${productId}`);
        
    }

    return (
        <div className='seller-product-container'>
            <h2>판매 상품 관리</h2>
            <div className='seller-product-top'>
                <div className='seller-btn-box'>
                    {/*<button type='button' onClick={updateBtn}>상품 수정</button>*/}
                    <button type='button' onClick={()=> navigate(`/seller/mypage/products/upload`)}>상품 등록</button>
                </div>
                <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
             </div>
            <div className='seller-product'>
                {
                    products?.map(x => (
                        <div key={x.productId} className='seller-product-item'>
                            {/*<input type="checkbox" 
                                className='seller-product-checkbox'
                                value={x.productId} 
                                onChange={() => handleCheck(x.productId)}
                                checked={check === x.productId} />*/}
                                <button type='button' 
                                className='seller-product-update'
                                onClick={() => updateBtn(x.productId)}>상품 수정</button>
                            <ProductCard product={x} />
                        </div>
                    ))
                }
               
            </div>
            {totalPages ? (
            <Pagination
                page={page}
                totalPages={totalPages}
                onPageChange={(p) => setPage(p)}
            />
      ) : null}
        </div>
    );
}

export default SellerProduct;