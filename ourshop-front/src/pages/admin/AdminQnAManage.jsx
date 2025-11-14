import React, { useEffect, useState } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import '../../assets/css/AdminQnAManage.css';
import { useNavigate } from 'react-router';
import Loader from '../../utils/Loaders';

function AdminQnAManage(props) {

    const navigate = useNavigate();
    const [mode, setMode] = useState("buyer");
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createdAt,desc");

    const {getQnAList} = useAdmin();
    // 검색 상태
    const [searchField, setSearchField] = useState("contentKeyword"); 
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    
    // 필터 상태 추가
    const [inquiryStatusFilter, setInquiryStatusFilter] = useState("");
    const [inquiryTypeFilter, setInquiryTypeFilter] = useState("");
    

    //검색 카테고리 변경시 전체 목록 나오도록
    useEffect(()=> {
        setSearchParams({});
        setSearchKeyword("");
    }, [searchField]);

    // 탭 변경 시 모든 필터와 검색어 초기화
    useEffect(() => {
        setPage(0);
        setSort("createdAt,desc");
        setSearchField("contentKeyword"); // mode 변경 시 기본 검색 필드를 'contentKeyword'로 설정
        setSearchKeyword("");
        setSearchParams({});
        setInquiryStatusFilter("");
        setInquiryTypeFilter("");
    }, [mode]);

    const { data: userListData, isLoading, isError, refetch } = getQnAList(mode, {
        page,
        size: 5,
        sort,
        inquiryStatus: inquiryStatusFilter,
        inquiryType: inquiryTypeFilter,
        ...searchParams,
    }) //enable 제어;

    const totalPages = userListData?.content?.totalPages || 0;

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
        // 조건 변경 후 재요청
        refetch();
    };

    if (isLoading) return <Loader/>;
    if (isError) return <p>회원 리스트를 불러올 수 없습니다.</p>;
 
    const userList = userListData?.content?.content;
    console.log("qna", userListData);
    const sortCateg = {
      "createAt": "createdAt",
      "productName": "title",
    }

    const Kor = {
        "PAYMENT": "결제",
        "SHIPPING": "배송",
        "PRODUCT": "상품",
        "VERIFICATION": "판매인증",
        "ETC": "기타",
        "ACCOUNT": "계정",
        "PENDING": "미답변",
        "ANSWERED": "답변"
    }

    return (
        <div className='admin-qna-manage-container'>
            <div className='admin-qna-manage-bar'>
                <h2>문의 관리</h2>
                <div className='admin-qna-manage-search-container'>
                    {/* 회원 유형 선택 필터 */}
                    <div className='admin-qna-manage-mode-filter-box'>
                        <h4>회원유형</h4>
                        <div className='admin-qna-manage-radio-group'>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='userMode' value='buyer' checked={mode === 'buyer'} onChange={() => setMode('buyer')} />
                                <span>구매자</span>
                            </label>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='userMode' value='seller' checked={mode === 'seller'} onChange={() => setMode('seller')} />
                                <span>판매자</span>
                            </label>
                        </div>
                    </div>
                    <div className='admin-qna-manage-filter-box'>
                        <h4>답변상태</h4>
                        <div className='admin-qna-manage-radio-group'>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryStatus' 
                                value='' checked={inquiryStatusFilter === ""} 
                                onChange={(e) => setInquiryStatusFilter(e.target.value)} />
                                <span>전체</span>
                            </label>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryStatus' 
                                value='PENDING' checked={inquiryStatusFilter === "PENDING"} 
                                onChange={(e) => setInquiryStatusFilter(e.target.value)} />
                                <span>미답변</span>
                            </label>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryStatus' 
                                value='ANSWERED' checked={inquiryStatusFilter === "ANSWERED"} 
                                onChange={(e) => setInquiryStatusFilter(e.target.value)} />
                                <span>답변</span>
                            </label>
                        </div>
                    </div>
                    <div className='admin-qna-manage-filter-box'>
                        <h4>카테고리</h4>
                        <div className='admin-qna-manage-radio-group'>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryType'
                                value='' checked={inquiryTypeFilter === ""}
                                onChange={(e) => setInquiryTypeFilter(e.target.value)} />
                                <span>전체</span>
                            </label>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryType'
                                value='ACCOUNT' checked={inquiryTypeFilter === "ACCOUNT"}
                                onChange={(e) => setInquiryTypeFilter(e.target.value)} />
                                <span>계정</span>
                            </label>
                            {mode === "buyer" ?
                            <>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryType'
                                value='PAYMENT' checked={inquiryTypeFilter === "PAYMENT"}
                                onChange={(e) => setInquiryTypeFilter(e.target.value)} />
                                <span>결제</span>
                            </label>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryType'
                                value='SHIPPING' checked={inquiryTypeFilter === "SHIPPING"}
                                onChange={(e) => setInquiryTypeFilter(e.target.value)} />
                                <span>배송</span>
                            </label>
                            </>
                            :
                            <>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryType'
                                value='PRODUCT' checked={inquiryTypeFilter === "PRODUCT"}
                                onChange={(e) => setInquiryTypeFilter(e.target.value)} />
                                <span>상품</span>
                            </label>
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryType'
                                value='VERIFICATION' checked={inquiryTypeFilter === "VERIFICATION"}
                                onChange={(e) => setInquiryTypeFilter(e.target.value)} />
                                <span>판매인증</span>
                            </label>
                            </>
                            }
                            <label className='admin-qna-radio-wrap'>
                                <input type='radio' name='inquiryType'
                                value='ETC' checked={inquiryTypeFilter === "ETC"}
                                onChange={(e) => setInquiryTypeFilter(e.target.value)} />
                                <span>기타</span>
                            </label>
                        </div>  
                    </div>
                    <form className='admin-qna-manage-search-box' onSubmit={handleSearchSubmit}>
                        <h4>결과 내 재검색</h4>
                        <div className='admin-qna-manage-search-form-bar'>
                            <select className='search-admin-qna-select'
                                value={searchField}
                                onChange={(e) => setSearchField(e.target.value)}>
                                <option value="contentKeyword">내용</option>
                                <option value={mode === "buyer" ? "buyerUid" : "sellerUid"}>회원번호</option>
                                <option value="nickname">닉네임</option>
                            </select>
                            <input type="text" placeholder="검색어를 입력하세요" value={searchKeyword} onChange={(e) => setSearchKeyword(e.target.value)} className="search-admin-qna-input" />
                            <button type="submit" className="search-admin-qna-btn">검색</button>
                        </div>
                    </form>
                </div>
                <Sort sort={sort} setPage={setPage} setSort={setSort} sortCateg={sortCateg} />
                <table className='admin-qna-manage-list'>
                    <thead>
                        <tr>
                            <th>번호</th>
                            <th>{mode === 'buyer' ? '닉네임' : '업체명'}</th>
                            <th>답변상태</th>
                            <th>카테고리</th>
                            <th>제목</th>
                            <th>작성일</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            userList?.length > 0 ? userList.map(u => (
                                <tr key={u.inquiryId} onClick={() => navigate(`/admin/${mode}/qna/admin/${u.inquiryId}`)}>
                                    <td>{u.inquiryId}</td>
                                    <td>{u.buyerNickname || u.sellerCompanyName}</td>
                                    <td style={u.inquiryStatus === "PENDING" ? {color: "red"} : {color: "green"}}>{Kor[u.inquiryStatus]}</td>
                                    <td>{Kor[u.inquiryType]}</td>
                                    <td>{u.title}</td>
                                    <td>{new Date(u.createdAt).toLocaleDateString().replace(/\.$/, '')}</td>
                                </tr>
                            ))
                                : <tr><td colSpan="5" className="no-results">검색 결과가 없습니다.</td></tr>
                        }
                    </tbody>
                </table>
                {
                totalPages ? <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)} /> : null 
                }
            </div>
        </div>
    );
}

export default AdminQnAManage;