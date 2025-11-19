import React, { useEffect, useState } from 'react';
import { useSeller } from '../../hooks/useSeller';
import ProductCard from '../../components/product/ProductCard';
import '../../assets/css/SellerProduct.css';
import { useNavigate } from 'react-router';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import { useProduct } from '../../hooks/useProduct';

function SellerProduct(props) {

    const navigate = useNavigate();

     // 페이지 & 정렬 상태
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("price,desc");

    //검색
    const [searchField, setSearchField] = useState("productName");
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [statusFilter, setStatusFilter] = useState(""); // 배송 상태 필터

    
    //const [check, setCheck] = useState("");
    const {deleteProduct} = useProduct();
    const {getSellerProductList} = useSeller();
    const {data: sellerProductList} = getSellerProductList({
            page,
            size: 8,
            sort,
            isDeleted: statusFilter,
            ...searchParams
        });
    const {mutate: deleteProductMutate} = deleteProduct();

    //필터 변경시 초기화
    useEffect(() => {
        setSearchParams({});
        setSearchKeyword("");
    }, [searchField]);
    
    
    useEffect(() => {
        setPage(0);
        setSearchParams({});
        setSearchKeyword("");
     }, [statusFilter]);

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

    // 검색 버튼 클릭 시 검색조건 업데이트
    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setPage(0);
        // 검색어가 있으면 검색조건 적용, 없으면 초기화
        if (searchKeyword.trim()) {
            setSearchParams({ [searchField]: searchKeyword });
        } else {
            setSearchParams({});
        }

    };

    //수정 버튼
    const updateBtn = (productId) => {
       
        navigate(`/seller/mypage/products/${productId}`);
        
    };

    //상품 삭제 버튼
    const deleteBtn = (productId, productName) => {
        if(confirm("상품을 삭제하시겠습니까?")){
            if(prompt(`"${productName}삭제" 를 동일하게 입력하세요`) ===  `${productName}삭제`){
                 const reason = prompt("상품 삭제 사유를 입력하세요").toString();
                 if (reason) {
                        deleteProductMutate({
                            productId,
                            data: { deletedBySellerReason: reason },
                        })};
            }else{
                alert(`"${productName}삭제"를 정확하게 입력하지 않았습니다.`);
                return;
            }       
        }
    }
    return (
        <div className='seller-product-container'>
            <h2>판매 상품 관리</h2>
            <div className='seller-product-top'>
                <button type='button' className='seller-product-upload' onClick={()=> navigate(`/seller/mypage/products/upload`)}>상품 등록</button>
             </div>
             <div className='seller-product-search-container'>
                <div className='seller-product-deleted-filter-box'>
                    <h4>판매 상태</h4>
                    <div className="deleted-radio-group">
                        <label className='deleted-radio-wrap'>
                            <input type='radio' name='status' value='' checked={statusFilter === ""} onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>전체</span>
                        </label>
                        <label className='  deleted-radio-wrap'>
                            <input type='radio' name='status' value='true' checked={statusFilter === "true"} onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>삭제</span>
                        </label>
                        <label className='deleted-radio-wrap'>
                            <input type='radio' name='status' value='false' checked={statusFilter === "false"} onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>판매중</span>
                        </label>
                    </div>
                </div>
                <form className='seller-product-search-box' onSubmit={handleSearchSubmit}>
                    <h4>결과 내 재검색</h4>
                    <div className='seller-product-search-form-bar'>
                       <select className='search-seller-product-select'
                            value={searchField}
                            onChange={(e)=> setSearchField(e.target.value)}>
                            <option value="productName">상품명</option>
                            <option value="productId">상품아이디</option>
                            <option value="price">가격</option>
                        </select> 
                        <input
                            type="text"
                            placeholder="검색어를 입력하세요"
                            value={searchKeyword}
                            onChange={(e) => setSearchKeyword(e.target.value)}
                            className="search-seller-product-input"
                        />
                        <button type="submit" className="search-seller-product-btn">
                            검색
                        </button>
                    </div>
                </form>
             </div>
             <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg}/>
            <div className='seller-product'>
                {
                    products.length > 0 ? products?.map(x => (
                        <div key={x.productId} className='seller-product-item'>
                            {/*<input type="checkbox" 
                                className='seller-product-checkbox'
                                value={x.productId} 
                                onChange={() => handleCheck(x.productId)}
                                checked={check === x.productId} />*/}
                            <ProductCard product={x} />
                            <div className='product-update-btn-box'>
                                <button type='button' 
                                    className='seller-product-update'
                                    onClick={() => updateBtn(x.productId)}>상품 수정</button>
                                <button type='button' 
                                    className='seller-product-delete'
                                    onClick={()=> deleteBtn(x.productId, x.productName)}
                                >삭제</button>
                            </div>
                            
                        </div>
                    )) : <p className='no-results'>등록한 상품이 없습니다.</p>
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