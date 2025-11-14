import React, { Fragment, useEffect, useState } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import Sort from '../../components/common/Sort';
import '../../assets/css/AdminUserManage.css';
import Pagination from '../../components/common/Pagenation';
import UserDetailModal from '../../components/admin/UserDetailModal';
import Loader from '../../utils/Loaders';

function AdminUserManage() {
    const [mode, setMode] = useState("buyer");
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createAt,desc");

    // 검색 상태
    const [searchField, setSearchField] = useState("buyerId"); // 기본값: 아이디
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [uid, setUid] = useState(null);
    // 필터 상태 추가
    const [isActiveFilter, setIsActiveFilter] = useState("");
    const [withdrawalStatusFilter, setWithdrawalStatusFilter] = useState("");
    const [isVerifiedFilter, setIsVerifiedFilter] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);

    const { getUserList } = useAdmin();

    //검색 카테고리 변경시 전체 목록 나오도록
    useEffect(()=> {
         setSearchParams({});
         setSearchKeyword("");
    }, [searchField]);

    // 탭 변경 시 모든 필터와 검색어 초기화
    useEffect(() => {
        setPage(0);
        setSort("createAt,desc");
        setSearchField(mode === "buyer" ? "buyerId" : "sellerId");
        setSearchKeyword("");
        setSearchParams({});
        setIsActiveFilter("");
        setWithdrawalStatusFilter("");
        setIsVerifiedFilter("");
    }, [mode]);

    const { data: userList, isLoading, isError, refetch } = getUserList(mode, {
        page,
        size: 10,
        sort,
        isActive: isActiveFilter,
        withdrawalStatus: withdrawalStatusFilter,
        isVerified: isVerifiedFilter,
        ...searchParams,
    }, true) //enable 제어;

 
    const totalPages = userList?.content?.totalPages || 0;

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
 

    //디버깅
    console.log("사용자 정보", userList);
    console.log("모드", mode);
    console.log("uid", uid);
     
   

    const sortCateg = {
      "createAt": "createAt",
      "productName": mode=== "buyer"? "nickname" : "companyName",
    }

    // 회원 상세 모달 열기
    const onpenDetailBtn = (uid) => {
        setUid(uid);
        setIsModalOpen(true);
    }

    //디버깅
    console.log("uid", uid);
    console.log("isModalOpen", isModalOpen);
    console.log("mode", mode);

    
   
    return (
        <div className='admin-user-manage'>
            <div className='admin-user-bar'>
            <h2>회원 관리</h2>
            <div className='admin-user-search-container'>
                {/* 회원 유형 선택 필터 */}
                <div className='admin-user-mode-filter-box'>
                    <h4>회원 유형</h4>
                    <div className='admin-user-radio-group'>
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='userMode' value='buyer' checked={mode === 'buyer'} onChange={() => setMode('buyer')} />
                            <span>구매자</span>
                        </label>
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='userMode' value='seller' checked={mode === 'seller'} onChange={() => setMode('seller')} />
                            <span>판매자</span>
                        </label>
                    </div>
                </div>

                {/* 기존 회원 활성 상태 필터 */}
                <div className='admin-user-filter-box'>
                    <h4>회원 활성 상태</h4>
                    <div className="admin-user-radio-group">
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='isActive' value='' checked={isActiveFilter === ""} onChange={(e) => setIsActiveFilter(e.target.value)} />
                            <span>전체</span>
                        </label>
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='isActive' value='true' checked={isActiveFilter === "true"} onChange={(e) => setIsActiveFilter(e.target.value)} />
                            <span>활성</span>
                        </label>
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='isActive' value='false' checked={isActiveFilter === "false"} onChange={(e) => setIsActiveFilter(e.target.value)} />
                            <span>비활성</span>
                        </label>
                    </div>
                </div>
                {/* 기존 회원 탈퇴 상태 필터 */}
                <div className='admin-user-filter-box'>
                    <h4>회원 탈퇴 상태</h4>
                    <div className="admin-user-radio-group">
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='withdrawalStatus' value='' checked={withdrawalStatusFilter === ""} onChange={(e) => setWithdrawalStatusFilter(e.target.value)} />
                            <span>전체</span>
                        </label>
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='withdrawalStatus' value='VOLUNTARY' checked={withdrawalStatusFilter === "VOLUNTARY"} onChange={(e) => setWithdrawalStatusFilter(e.target.value)} />
                            <span>탈퇴</span>
                        </label>
                        <label className='admin-user-radio-wrap'>
                            <input type='radio' name='withdrawalStatus' value='FORCED_BY_ADMIN' checked={withdrawalStatusFilter === "FORCED_BY_ADMIN"} onChange={(e) => setWithdrawalStatusFilter(e.target.value)} />
                            <span>추방</span>
                        </label>
                    </div>
                </div>
                {/* 기존 판매 승인 상태 필터 (판매자 모드에서만) */}
                {mode === "seller" && (
                    <div className='admin-user-filter-box'>
                        <h4>판매 승인 상태</h4>
                        <div className="admin-user-radio-group">
                            <label className='admin-user-radio-wrap'>
                                <input type='radio' name='isVerified' value='' checked={isVerifiedFilter === ""} onChange={(e) => setIsVerifiedFilter(e.target.value)} />
                                <span>전체</span>
                            </label>
                            <label className='admin-user-radio-wrap'>
                                <input type='radio' name='isVerified' value='true' checked={isVerifiedFilter === "true"} onChange={(e) => setIsVerifiedFilter(e.target.value)} />
                                <span>인증</span>
                            </label>
                            <label className='admin-user-radio-wrap'>
                                <input type='radio' name='isVerified' value='false' checked={isVerifiedFilter === "false"} onChange={(e) => setIsVerifiedFilter(e.target.value)} />
                                <span>미인증</span>
                            </label>
                        </div>
                    </div>
                )}
                {/* 기존 결과 내 재검색 폼 */}
                <form className='admin-user-search-box' onSubmit={handleSearchSubmit}>
                    <h4>결과 내 재검색</h4>
                    <div className='admin-user-search-form-bar'>
                        <select className='search-admin-user-select' value={searchField} onChange={(e) => setSearchField(e.target.value)}>
                            <option value={mode === "buyer" ? "buyerId" : "sellerId"}>아이디</option>
                            <option value={mode === "buyer" ? "nickname" : "companyName"}>{mode === "buyer" ? "닉네임" : "업체명"}</option>
                            <option value={mode === "buyer" ? "buyerUid" : "sellerUid"}>UID</option>
                            <option value={mode === "buyer" ? "buyerEmail" : "sellerEmail"}>이메일</option>
                            <option value="phone">전화번호</option>
                        </select>
                        <input type="text" placeholder="검색어를 입력하세요" value={searchKeyword} onChange={(e) => setSearchKeyword(e.target.value)} className="search-admin-user-input" />
                        <button type="submit" className="search-admin-user-btn">검색</button>
                    </div>
                </form>
            </div>

            {/* 정렬 */}
            <Sort sort={sort} setPage={setPage} setSort={setSort} sortCateg={sortCateg} />

            {/* 회원 리스트 */}
            <div className="user-list">
                <table>
                    <thead>
                        <tr>
                            <th>UID</th>
                            <th>아이디</th>
                            <th>{mode === 'buyer' ? '닉네임' : '업체명'}</th>
                            <th>이메일</th>
                            {/*<th>전화번호</th>*/}
                            <th>활성 상태</th>
                            <th>탈퇴 상태</th>
                            <th>가입일</th>
                            {mode==="seller" && <th>판매 승인 상태</th>}
                        </tr>
                    </thead>
                    <tbody>
                        {userList?.content?.content?.length > 0 ? (
                            userList.content.content.map((user) => (
                                mode === "buyer" ? (
                                    <tr key={user.buyerUid} onClick={() => onpenDetailBtn(user.buyerUid)}>
                                        <td>{user.buyerUid}</td>
                                        <td>{user.buyerId}</td>
                                        <td>{user.nickname}</td>
                                        <td>{user.buyerEmail}</td>
                                        <td>{user.isActive ? "활성" : "비활성"}</td>
                                        <td>{user.withdrawalStatus || '-'}</td>
                                        <td>{new Date(user.createAt).toLocaleDateString().replace(/\.$/, '')}</td>
                                    </tr>
                                ) : (
                                    <tr key={user.sellerUid} onClick={() => onpenDetailBtn(user.sellerUid)}>
                                        <td>{user.sellerUid}</td>
                                        <td>{user.sellerId}</td>
                                        <td>{user.companyName}</td>
                                        <td>{user.sellerEmail}</td>
                                        <td>{user.isActive ? "활성" : "비활성"}</td>
                                        <td>{user.withdrawalStatus ? user.withdrawalStatus === "VOLUNTARY" ? "자발" : "강제" :'-'}</td>
                                        <td>{new Date(user.createAt).toLocaleDateString().replace(/\.$/, '')}</td>
                                        <td>{user.isVerified ? "인증" : "미인증"}</td>
                                    </tr>
                                )
                            ))
                        ) : (
                            <tr><td colSpan="8" className="no-results">검색 결과가 없습니다.</td></tr>
                        )}
                    </tbody>
                </table>
                {isModalOpen && (
                    <UserDetailModal uid={uid} mode={mode} setIsModalOpen={setIsModalOpen} />
                )}
            </div> 
                {totalPages ? (
                    <Pagination
                        page={page}
                        totalPages={totalPages}
                        onPageChange={(p) => setPage(p)}
                    />) : null
                }
        </div>
        </div>
    ); 
}

export default AdminUserManage;
