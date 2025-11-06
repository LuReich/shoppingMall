import React, { Fragment, useEffect, useState } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import Sort from '../../components/common/Sort';
import '../../assets/css/AdminUserManage.css';
import Pagination from '../../components/common/Pagenation';
import UserDetailModal from '../../components/admin/UserDetailModal';
import { set } from 'react-hook-form';

function AdminUserManage() {
    const [mode, setMode] = useState("buyer");
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createAt,desc");

    // 검색 상태
    const [searchField, setSearchField] = useState("buyerId"); // 기본값: 아이디
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});
    const [uid, setUid] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const { getUserList } = useAdmin();

    //검색 카테고리 변경시 전체 목록 나오도록
    useEffect(()=> {
         setSearchParams({});
    }, [searchField]);


    const { data: userList, isLoading, isError, refetch } = getUserList(mode, {
        page,
        size: 10,
        sort,
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

    if (isLoading) return <p>로딩중...</p>;
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
            <h2>회원 관리</h2>
            <div className='mode-select-box'>
                <button type='button' className={`mode-select-btn ${mode === "buyer" ? "active" : ""}`} onClick={()=>setMode("buyer")}>구매자 회원관리</button>
                <button type='button' className={`mode-select-btn ${mode === "seller" ? "active" : ""}`} onClick={()=>setMode("seller")}>판매자 회원관리</button>
            </div>
            {/* 검색 영역 */}
            <form className="search-user-bar" onSubmit={handleSearchSubmit}>
                <select
                    value={searchField}
                    onChange={(e) => setSearchField(e.target.value)}
                    className="search-user-select"
                >
                    <option value="buyerId">아이디</option>
                    <option value="nickname">닉네임</option>
                    <option value="buyerEmail">이메일</option>
                    <option value="phone">전화번호</option>
                    <option value="isActive">회원 활성 상태</option>
                    <option value="withdrawalStatus">회원 탈퇴 상태</option>
                    {mode === "seller" && <option value="isVerified">판매 승인 상태</option>}
                </select>
                {
                    searchField === "withdrawalStatus" ?
                    <div class="radio-group">
                    <label class="radio-wrap">
                        <input type="radio" name="status" value="VOLUNTARY" onChange={(e) => setSearchKeyword(e.target.value)} />
                        <span>자발</span>
                    </label>
                    <label class="radio-wrap">
                        <input type="radio" name="status" value="FORCED_BY_ADMI" onChange={(e) => setSearchKeyword(e.target.value)} />
                        <span>강제</span>
                    </label>
                    </div>
                    :
                    searchField === "isActive" ?
                    <div class="radio-group">
                    <label class="radio-wrap">
                        <input type="radio" name="status" value={1} onChange={(e) => setSearchKeyword(e.target.value)}/>
                        <span>활성</span>
                    </label>
                    <label class="radio-wrap">
                        <input type="radio" name="status" value={0} onChange={(e) => setSearchKeyword(e.target.value)}/>
                        <span>비활성</span>
                    </label>
                    </div>
                    :
                    searchField === "isVerified"?
                    <div class="radio-group">
                    <label className='radio-wrap'>
                        <input type="radio" name="status" value={1} onChange={(e) => setSearchKeyword(e.target.value)}/>
                        <span>인증</span>
                    </label>
                    <label className='radio-wrap'>
                        <input type="radio" name="status" value={0} onChange={(e) => setSearchKeyword(e.target.value)}/>
                        <span>미인증</span>
                    </label>
                    </div>
                    :
                    <input
                        type="text"
                        placeholder="검색어를 입력하세요"
                        value={searchKeyword}
                        onChange={(e) => setSearchKeyword(e.target.value)}
                        className="search-user-input"
                    />
                }
                <button type="submit" className="search-user-btn">
                    검색
                </button>
            </form>

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
                                        <td>{new Date(user.createAt).toLocaleDateString()}</td>
                                    </tr>
                                ) : (
                                    <tr key={user.sellerUid} onClick={() => onpenDetailBtn(user.sellerUid)}>
                                        <td>{user.sellerUid}</td>
                                        <td>{user.sellerId}</td>
                                        <td>{user.companyName}</td>
                                        <td>{user.sellerEmail}</td>
                                        <td>{user.isActive ? "활성" : "비활성"}</td>
                                        <td>{user.withdrawalStatus ? user.withdrawalStatus === "VOLUNTARY" ? "자발" : "강제" :'-'}</td>
                                        <td>{new Date(user.createAt).toLocaleDateString()}</td>
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
    ); 
}

export default AdminUserManage;
