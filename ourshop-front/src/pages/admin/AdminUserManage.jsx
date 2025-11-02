import React, { useState } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import Sort from '../../components/common/Sort';
import '../../assets/css/AdminUserManage.css';
import Pagination from '../../components/common/Pagenation';

function AdminUserManage() {
    const [mode, setMode] = useState("buyer");
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState("createAt,desc");

    // 검색 상태
    const [searchField, setSearchField] = useState("buyerId"); // 기본값: 아이디
    const [searchKeyword, setSearchKeyword] = useState("");
    const [searchParams, setSearchParams] = useState({});

    const { getUserList } = useAdmin();


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

    console.log("사용자 정보", userList);

    return (
        <div className='admin-user-manage'>
            <h2>회원관리</h2>
            <div className='mode-select-box'>
                <button type='button' className={`mode-select-btn ${mode === "buyer" ? "active" : ""}`} onClick={()=>setMode("buyer")}>구매자 회원관리</button>
                <button type='button' className={`mode-select-btn ${mode === "seller" ? "active" : ""}`} onClick={()=>setMode("seller")}>판매자 회원관리</button>
            </div>
            {/* ✅ 검색 영역 */}
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
                </select>
                {
                    searchField === "withdrawalStatus" ?
                    <>
                    <label class="radio-wrap">
                        <input type="radio" name="status" value="VOLUNTARY" onChange={(e) => setSearchKeyword(e.target.value)} />
                        <span>자발</span>
                    </label>
                    <label class="radio-wrap">
                        <input type="radio" name="status" value="FORCED_BY_ADMI" onChange={(e) => setSearchKeyword(e.target.value)} />
                        <span>강제</span>
                    </label>
                    </>
                    :
                    searchField === "isActive" ?
                    <>
                    <label class="radio-wrap">
                        <input type="radio" name="status" value={1} onChange={(e) => setSearchKeyword(e.target.value)}/>
                        <span>활성</span>
                    </label>
                    <label class="radio-wrap">
                        <input type="radio" name="status" value={0} onChange={(e) => setSearchKeyword(e.target.value)}/>
                        <span>비활성</span>
                    </label>
                    </>
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
            <Sort sort={sort} setPage={setPage} setSort={setSort} />

            {/* 회원 리스트 (임시 출력) */}
                <div className="user-list">
                    {userList?.content?.content?.length > 0 ? (
                        userList.content.content.map((user) => (
                            mode === "buyer" ? (
                                <div key={user.buyerUid} className="user-item">
                                    <p><strong>{user.nickname}</strong> ({user.buyerId})</p>
                                    <p>{user.buyerEmail} | {user.phone}</p>
                                    <p>상태: {user.isActive ? "활성" : "비활성"}</p>
                                    {user.withdrawalStatus&& <p>탈퇴 상태: {user.withdrawalStatus}</p>}
                                    {user.withdrawalReason&& <p>탈퇴 사유: {user.withdrawalReason}</p>}
                                </div>
                            ) : (
                                <div key={user.sellerUid} className="user-item">
                                    <p><strong>{user.companyName}</strong> ({user.sellerUid})</p>
                                    <p>{user.sellerEmail}</p>
                                    <p>상태: {user.isActive ? "활성" : "비활성"}</p>
                                    {user.withdrawalStatus&& <p>탈퇴 상태: {user.withdrawalStatus}</p>}
                                    {user.withdrawalReason&& <p>탈퇴 사유: {user.withdrawalReason}</p>}
                                </div>
                                )
                            ))
                        ) : (
                        <p>검색 결과가 없습니다.</p>
                    )}
                </div>
                {totalPages && (
                    <Pagination
                        page={page}
                        totalPages={totalPages}
                        onPageChange={(p) => setPage(p)}
                    />
                )}
        </div>
    );
}

export default AdminUserManage;
