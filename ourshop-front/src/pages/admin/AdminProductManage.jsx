import React, { useEffect, useState } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import { useNavigate } from 'react-router-dom';
import Sort from '../../components/common/Sort';
import '../../assets/css/AdminProductManage.css'; // 상품 관리 페이지 전용 스타일
import Pagination from '../../components/common/Pagenation';
import Loader from '../../utils/Loaders';

function AdminProductManage(props) {

    const [sort, setSort] = useState("createAt,desc");
    const [page, setPage] = useState(0);
    const [searchField, setSearchField] = useState("productId"); // 기본값: 상품번호
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [statusFilter, setStatusFilter] = useState(""); // 판매 상태 필터

    const {getProductList, deleteProduct} = useAdmin();
    const navigate = useNavigate();
    const {data: productList, isLoading, isError} = getProductList({
        page,
        size: 6,
        sort,
        isDeleted: statusFilter,
        ...searchParams
    });

    const {mutate: deleteProductMutate} = deleteProduct();

    //검색 카테고리 변경시 전체 목록 나오도록
    useEffect(()=> {
         setSearchParams({});
         setSearchKeyword("");
    }, [searchField]);

    // 판매 상태 필터 변경 시 페이지 및 검색 조건 초기화
    useEffect(() => {
        setPage(0);
        setSearchParams({});
        setSearchKeyword("");
    }, [statusFilter]);

    if(isLoading) return <Loader/>
    if(isError) return <p>상품 리스트 가져오기를 실패했습니다.</p>

    const totalPages = productList?.content?.totalPages || 0;
    const products = productList?.content?.content;

    console.log("관리자 상품 리스트", productList);
    console.log("관리자 상품", products);

    const sortCateg = {
        "createAt": "createAt",
        "productName": "productName",
        "companyName": "companyName",
    }

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



    //삭제 버튼
    const handleDelete = (productId, isDeleted) => {
        const shouldDelete = isDeleted === 'true';
        if(shouldDelete){
            if(confirm("상품을 삭제하시겠습니까")){
                const reason = prompt("삭제 사유를 입력하세요");
                if (reason !== null) deleteProductMutate({productId, data: {isDeleted: shouldDelete, deletedByAdminReason: reason}});
            }
        }else{
            if(confirm("삭제된 상품을 판매중으로 변경하시겠습니까?")){
                const reason = prompt("복수 사유를 입력하세요");
                if (reason !== null) deleteProductMutate({productId, data: {isDeleted: shouldDelete, deletedByAdminReason:reason}});
            }
        }
        
    }   

    return (
        <div className='admin-product-manage-container'>
            <div className='admin-product-bar'>
            <h2>상품 관리</h2>
            <div className='admin-product-search-container'>
                <div className='admin-product-filter-box'>
                    <h4>판매 상태</h4>
                    <div className="admin-product-radio-group">
                        <label className='admin-product-radio-wrap'>
                            <input type='radio' name='status' value='' checked={statusFilter === ""} onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>전체</span>
                        </label>
                        <label className='admin-product-radio-wrap'>
                            <input type='radio' name='status' value='true' checked={statusFilter === "true"} onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>삭제</span>
                        </label>
                        <label className='admin-product-radio-wrap'>
                            <input type='radio' name='status' value='false' checked={statusFilter === "false"} onChange={(e) => setStatusFilter(e.target.value)} />
                            <span>판매중</span>
                        </label>
                    </div>
                </div>
                <form className='admin-product-search-box' onSubmit={handleSearchSubmit}>
                    <h4>결과 내 재검색</h4>
                    <div className='admin-product-search-form-bar'>
                        <select className='search-admin-product-select'
                            value={searchField}
                            onChange={(e) => setSearchField(e.target.value)}>
                            <option value="productId">상품 아이디</option>
                            <option value="productName">상품명</option>
                            <option value="companyName">업체명</option>
                        </select>
                        <input type="text"
                            placeholder="검색어를 입력하세요"
                            value={searchKeyword}
                            onChange={(e) => setSearchKeyword(e.target.value)}
                            className="search-admin-product-input" />
                        <button type="submit" className="search-admin-product-btn">검색</button>
                    </div>
                </form>
            </div>
            <Sort sort={sort} setSort={setSort} setPage={setPage} sortCateg={sortCateg} />
            <table>
                <thead>
                    <tr>
                        <th>상품 아이디</th>
                        <th>등록일</th>
                        <th>상품</th>
                        <th>업체명</th>
                        <th>상태</th>
                        <th>처리 사유</th>
                        <th>삭제 사유(판매자)</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        products?.map((p) => (
                            <tr key={p.productId}>
                                <td>{p.productId}</td>
                                <td>{new Date(p.createAt).toLocaleDateString().replace(/\.$/, '')}</td>
                                <td>
                                    <div className="detail-info" onClick={() => navigate(`/product/${p.productId}`)}>
                                        <img src={`http://localhost:9090${p.thumbnailUrl}`} alt={p.productName} style={{width: '60px', height: '60px', objectFit: 'cover', marginRight: '10px'}} />
                                        <div>
                                            <p>{p.productName}</p>
                                        </div>
                                    </div>
                                </td>
                                <td>{p.companyName}</td>
                                <td>
                                    <select value={p.isDeleted ? "true" : "false"} 
                                    className='select-admin-isDeleted'
                                    onChange={(e) => handleDelete(p.productId, e.target.value)}>
                                        <option value="true">삭제됨</option>
                                        <option value="false">판매중</option>   
                                    </select>
                                </td>
                                <td>{p.deletedByAdminReason || "-"} </td>
                                <td>{p.deletedBySellerReason|| "-"}</td>
                            </tr>
                        ))
                    }
                </tbody>
            </table>
            {totalPages? <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)}/> : null } 
        </div>
        </div>
    );
}

export default AdminProductManage;