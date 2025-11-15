import React, { Fragment, useEffect, useState } from 'react';
import { useFaq } from '../../hooks/useFaq';
import '../../assets/css/Faq.css';
import Sort from '../../components/common/Sort';
import Pagination from '../../components/common/Pagenation';
import { IoIosArrowUp } from "react-icons/io";
import { IoIosArrowDown } from "react-icons/io";
import { authStore } from '../../store/authStore';
import { useNavigate } from 'react-router';




function Faq(props) {

    const role = authStore(state => state.role);
    const navigate = useNavigate();

    const [sort, setSort] = useState("sortOrder,asc");
    const [page, setPage] = useState(0);
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [userFilter, setUserFilter] = useState(""); // 회원 유형 필터
    const [categoryFilter, setCategoryFilter] = useState(""); // 카테고리 필터
    const [openFaqId, setOpenFaqId] = useState(null); // 열려있는 FAQ의 ID를 저장
    
    const {getFaqList,deleteFaq} = useFaq();

    const {data: faqListData, isLoading, isError} = getFaqList({
        page,
        size: 6,
        sort,
        faqTarget: userFilter,
        faqCategory: categoryFilter,
        ...searchParams
    });
    
    const {mutate: deleteMutate } = deleteFaq();


    // 검색 카테고리 변경시 전체 목록 나오도록
    useEffect(()=> {
        setSearchParams({});
        setSearchKeyword("");
    }, [userFilter, categoryFilter]);

    
    // 검색 버튼 클릭 시 검색조건 업데이트
    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setPage(0);
        // 검색어가 있으면 검색조건 적용, 없으면 초기화
        if (searchKeyword.trim()) {
            setSearchParams({ keyword: searchKeyword });
        } else {
            setSearchParams({});
        }
    };

    const sortCateg = {
        "createAt": "createAt",
        "sortOrder": "sortOrder",
        "productName" : "faqQuestion"
    }

    // FAQ 항목 클릭 핸들러
    const handleToggle = (faqId) => {
        // 이미 열려있는 항목을 다시 클릭하면 닫고, 아니면 새로 열기
        setOpenFaqId(openFaqId === faqId ? null : faqId);
    };
    

    if(isLoading) return <p>FAQ 리스트를 가져오는 중입니다.</p>
    if(isError) return <p>FAQ 리스트 가져오기를 실패했습니다.</p>

    const totalPages = faqListData?.content?.totalPages || 0;
    const faqList = faqListData?.content?.content;

    console.log("FAQ 리스트", faqList);

    const kor = {
        "ACCOUNT": "계정",
        "PAYMENT": "구매",
        "SHIPPING": "배송",
        "PRODUCT": "상품",
        "ETC": "기타",
        "VERIFICATION": "인증",
        "ALL": "공통",
        "BUYER": "구매자",
        "SELLER": "판매자"
    }

    //faq등록
    const handleUpload = () => {
        navigate("/admin/faq/upload");
    }

    //faq수정
    const handleUpdate = (faqId) => {
        navigate(`/admin/faq/upload/${faqId}`);
    }

    //faq삭제
    const handleDelete = (faqId, title) => {
        if(confirm(`[${title}]을 삭제하시겠습니까?`)){
            deleteMutate(faqId);
            setOpenFaqId(null);
            setPage(0);
        }
    }
   

    return (
        <div className='faq-container'>
            <div className='faq-main'>
            <h2>FAQ</h2>
            <div className='faq-create-btn-box'>
                {
                    role === "ADMIN" && (
                        <button type='button' className='faq-create-btn' onClick={handleUpload}>FAQ 등록</button>
                    )
                }   
            </div>
            <div className='faq-search-container'>
                <div className='faq-filter-box'>
                    <h4>회원 유형</h4>
                    <div className='faq-radio-group'>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='userMode' value='' checked={userFilter === ""} onChange={(e) => setUserFilter(e.target.value)} />
                            <span>전체</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='userMode' value='ALL' checked={userFilter === "ALL"} onChange={(e) => setUserFilter(e.target.value)} />
                            <span>공통</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='userMode' value='BUYER' checked={userFilter === "BUYER"} onChange={(e) => setUserFilter(e.target.value)} />
                            <span>구매자</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='userMode' value='SELLER' checked={userFilter === "SELLER"} onChange={(e) => setUserFilter(e.target.value)} />
                            <span>판매자</span>
                        </label>
                    </div>
                </div>
                <div className='faq-filter-box'>
                    <h4>카테고리</h4>
                    <div className='faq-radio-group'>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='categoryMode' value='' checked={categoryFilter === ""} onChange={(e) => setCategoryFilter(e.target.value)} />
                            <span>전체</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='categoryMode' value='ACCOUNT' checked={categoryFilter === "ACCOUNT"} onChange={(e) => setCategoryFilter(e.target.value)} />
                            <span>계정</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='categoryMode' value='PAYMENT' checked={categoryFilter === "PAYMENT"} onChange={(e) => setCategoryFilter(e.target.value)} />
                            <span>구매</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='categoryMode' value='SHIPPING' checked={categoryFilter === "SHIPPING"} onChange={(e) => setCategoryFilter(e.target.value)} />
                            <span>배송</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='categoryMode' value='PRODUCT' checked={categoryFilter === "PRODUCT"} onChange={(e) => setCategoryFilter(e.target.value)} />
                            <span>상품</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='categoryMode' value='ETC' checked={categoryFilter === "ETC"} onChange={(e) => setCategoryFilter(e.target.value)} />
                            <span>기타</span>
                        </label>
                        <label className='faq-radio-wrap'>
                            <input type='radio' name='categoryMode' value='VERIFICATION' checked={categoryFilter === "VERIFICATION"} onChange={(e) => setCategoryFilter(e.target.value)} />
                            <span>인증</span>
                        </label>
                    </div>
                </div>
                <form className='faq-search-box' onSubmit={handleSearchSubmit}>
                    <h4>결과 내 재검색</h4>
                    <div className='faq-search-form-bar'>
                        <input type="text" placeholder="검색어를 입력하세요" value={searchKeyword} onChange={(e) => setSearchKeyword(e.target.value)} className="search-faq-input" />
                        <button type="submit" className="search-faq-btn">검색</button>
                    </div>
                </form>
            </div>
            {/* 정렬 */}
            <Sort sort={sort} setPage={setPage} setSort={setSort} sortCateg={sortCateg} />
            <table className='faq-list'>
                <thead>
                    <tr>
                        <th>번호</th>
                        <th>회원유형</th>
                        <th>카테고리</th>
                        <th>질문</th>
                        {role === "ADMIN" && <th>관리</th>}
                    </tr>
                </thead>
                <tbody>
                {
                    faqList?.length > 0 ? faqList.map(f => (
                        <Fragment key={f.faqId}>  
                            <tr className='faq-question-row' onClick={() => handleToggle(f.faqId)}>
                                <td className='faq-td'>{f.faqId}</td>
                                <td className='faq-td'>{kor[f.faqTarget]}</td>
                                <td className='faq-td'>{kor[f.faqCategory]}</td>
                                <td className='faq-title'>
                                    {f.faqQuestion}
                                    {openFaqId === f.faqId ? <IoIosArrowUp className='faq-arrow' /> : <IoIosArrowDown className='faq-arrow' />}
                                </td>
                                {
                                    role === "ADMIN" && (
                                        <td className='faq-admin-btns'>
                                            <div className='faq-admin-btns-box'>
                                                <button type='button' onClick={() => handleUpdate(f.faqId)}>수정</button>
                                                <button type='button' onClick={() => handleDelete(f.faqId, f.faqQuestion)}>삭제</button>
                                            </div>
                                        </td>
                                    )
                                }
                            </tr>
                            {openFaqId === f.faqId && (
                                <tr className='faq-answer-row'>
                                    <td colSpan={role === "ADMIN" ? "5" : "4"}>
                                        <div className='faq-answer-content'>
                                            {f.faqAnswer}
                                        </div>
                                    </td>
                                </tr>
                            )}
                        </Fragment>
                    ))
                    : <tr><td colSpan="4" className="no-results">등록된 FAQ가 없습니다.</td></tr>
                }
                </tbody>
            </table>
            {totalPages?
                <Pagination
                    page={page}
                    totalPages={totalPages}
                    onPageChange={(p) => setPage(p)}
                /> : null
            }
            </div>
        </div>
    );
}

export default Faq;