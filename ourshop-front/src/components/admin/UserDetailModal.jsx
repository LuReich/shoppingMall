import React, { useState, useEffect } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import { useRegister } from '../../hooks/useRegister'; 
import '../../assets/css/UserDetailModal.css';

function UserDetailModal({ uid, mode, setIsModalOpen }) {
  const { getUserDetail, updateUser } = useAdmin();
  const { data: detailData, isLoading, isError } = getUserDetail(mode, uid);
  const { mutate: mutateUpdateUser } = updateUser();

  // useRegister 훅 연결
  const {
    checkId,
    idMsg,
    isIdChecked,
    setIdMsg,
    setIsIdChecked
  } = useRegister(mode);

  

  const [buyerDetail, setBuyerDetail] = useState(null);
  const [sellerDetail, setSellerDetail] = useState(null);

  useEffect(() => {
    setBuyerDetail(null);
    setSellerDetail(null);
  }, [uid]);

  // 구매자 상세
  useEffect(() => {
    if (detailData?.content && mode === "buyer") {
      const detail = detailData.content;
      setBuyerDetail({
        buyerId: detail.buyerId,
        nickname: detail.nickname,
        buyerEmail: detail.buyerEmail,
        phone: detail.phone,
        birth: detail.birth, // 추가
        gender: detail.gender, // 추가
        address: detail.address, // 추가
        addressDetail: detail.addressDetail, // 추가
        isActive: detail.isActive,
        withdrawalStatus: detail.withdrawalStatus, // 추가
        withdrawalReason: detail.withdrawalReason, // 추가
      });
    }
  }, [detailData, mode]);

  // 판매자 상세
  useEffect(() => {
    if (detailData?.content && mode === "seller") {
      const detail = detailData.content;
      setSellerDetail({
        sellerId: detail.sellerId,
        companyName: detail.companyName,
        sellerEmail: detail.sellerEmail,
        phone: detail.phone,
        businessRegistrationNumber: detail.businessRegistrationNumber, // 추가
        address: detail.address, // 추가
        addressDetail: detail.addressDetail, // 추가
        companyInfo: detail.companyInfo, // 추가
        isVerified: detail.isVerified,
        isActive: detail.isActive,
        withdrawalStatus: detail.withdrawalStatus, // 추가
        withdrawalReason: detail.withdrawalReason, // 추가
      });
    }
  }, [detailData, mode]);

  // 아이디 값 변경 시 중복확인 초기화 or 자동통과
  useEffect(() => {
    const currentId =
      mode === "buyer" ? buyerDetail?.buyerId : sellerDetail?.sellerId;
    const originalId =
      mode === "buyer"
        ? detailData?.content?.buyerId
        : detailData?.content?.sellerId;

    if (!currentId) return;
    if (currentId === originalId) {
      setIsIdChecked(true);
      setIdMsg("이전과 동일한 아이디입니다.");
    } else {
      setIsIdChecked(false);
      setIdMsg("");
    }
  }, [buyerDetail?.buyerId, sellerDetail?.sellerId]);

  if (isLoading) return <p>로딩중...</p>;
  if (isError) return <p>회원 상세 정보를 불러올 수 없습니다.</p>;

  console.log("회원정보" ,detailData );
  // 아이디 중복확인 핸들러
  const handleCheckId = () => {
    const id = mode === "buyer" ? buyerDetail?.buyerId : sellerDetail?.sellerId;
    if (!id) return alert("아이디를 입력해주세요.");
    checkId.mutate({ id: id, isAdmin: true, uid: uid });
  };

  // 수정 시 중복확인 검증
  const handleUpdate = (e) => {
    e.preventDefault();

    const data = mode === "buyer" ? buyerDetail : sellerDetail;
    const originalId =
      mode === "buyer"
        ? detailData?.content?.buyerId
        : detailData?.content?.sellerId;

    if (data && data.buyerId && data.buyerId !== originalId && !isIdChecked) {
      return alert("아이디 중복확인을 해주세요.");
    }
    if (data && data.sellerId && data.sellerId !== originalId && !isIdChecked) {
      return alert("아이디 중복확인을 해주세요.");
    }

    console.log("최종 업데이트 데이터:", data);
    mutateUpdateUser({ mode, uid, data });
    setIsModalOpen(false);
  };

  return (
    <div className="modal-backdrop" onClick={() => setIsModalOpen(false)}>
      {mode === "buyer" && buyerDetail ? (
        <div className="modal-cont" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h2>구매자 상세 정보</h2>
            <button onClick={() => setIsModalOpen(false)} className="close-btn">X</button>
          </div>
          <form className="modal-form" onSubmit={handleUpdate}>
            <div className="modal-main">
              {/* 왼쪽 컬럼 */}
               <div className='modal-col'>
                <div className="modal-group">
                  <label>아이디</label>
                  <div className="modal-with-button">
                    <input
                      type="text"
                      value={buyerDetail.buyerId || ""}
                      onChange={(e) =>
                        setBuyerDetail({ ...buyerDetail, buyerId: e.target.value })
                      }
                    />
                    <button type="button" onClick={handleCheckId}>
                      중복확인
                    </button>
                  </div>
                  {idMsg && (
                    <p className={`id-ok ${isIdChecked ? "active" : ""}`}>{idMsg}</p>
                  )}
                </div>
                <div className="modal-group">
                  <label>닉네임</label>
                  <input type='text' value={buyerDetail.nickname || ''} readOnly />
                </div>
                <div className="modal-group">
                  <label>생년월일</label>
                  <input type='text' value={buyerDetail.birth || ''} readOnly />
                </div>
                <div className="modal-group">
                  <label>성별</label>
                  <input type='text' value={buyerDetail.gender || ''} readOnly />
                </div>
                <div className="modal-group">
                  <label>이메일</label>
                  <input type='text'
                    value={buyerDetail.buyerEmail || ''}
                    onChange={(e) => setBuyerDetail({ ...buyerDetail, buyerEmail: e.target.value })} />
                </div>
                <div className="modal-group">
                  <label>휴대폰 번호</label>
                  <input type='text' value={buyerDetail.phone || ''} readOnly />
                </div>
              </div>
              {/* 오른쪽 컬럼 */}
              <div className='modal-col'>
                <div className="modal-group">
                  <label>주소</label>
                  <input type='text' value={buyerDetail.address} readOnly />
                </div>
                <div className="modal-group">
                  <label>상세주소</label>
                  <input type='text' value={buyerDetail.addressDetail || ''} readOnly />
                </div>
                <div className="modal-group">
                  <label>계정 활성 상태</label>
                  <div className="radio-group">
                    <label>
                      <input
                        type='radio'
                        name='isActive'
                        checked={buyerDetail.isActive === true}
                        onChange={() => setBuyerDetail(prev => ({
                          ...prev,
                          isActive: true,
                          withdrawalStatus: null,
                          withdrawalReason: null
                        }))}
                      /> 활성
                    </label>
                    <label>
                      <input
                        type='radio'
                        name='isActive'
                        checked={buyerDetail.isActive === false}
                        onChange={() => setBuyerDetail(prev => ({
                          ...prev,
                          isActive: false,
                          withdrawalStatus: prev.withdrawalStatus ?? "FORCED_BY_ADMIN"
                        }))}
                      /> 비활성
                    </label>
                  </div>
                </div>
                <div className="modal-group">
                  <label>탈퇴 상태</label>
                  <input type='text' value={buyerDetail.withdrawalStatus || ''} readOnly />
                </div>
                <div className="modal-group">
                  <label>탈퇴 사유</label>
                  <textarea
                    value={buyerDetail.withdrawalReason || ''}
                    onChange={(e) => setBuyerDetail(prev => ({ ...prev, withdrawalReason: e.target.value }))}
                    readOnly={buyerDetail.isActive === true}
                  />
                </div>
              </div>
            </div>
            <div className='modal-footer'>
              <button type='button' className="cancel-btn" onClick={() => setIsModalOpen(false)}>닫기</button>
              <button type='submit' className="submit-btn">수정</button>
            </div>
          </form>
        </div>
      ) : (
        mode === "seller" &&
        sellerDetail && (
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>판매자 상세 정보</h2>
              <button
                onClick={() => setIsModalOpen(false)}
                className="close-btn"
              >
                X
              </button>
            </div>
            <form className="modal-form" onSubmit={handleUpdate}>
              <div className="modal-main">
                {/* 왼쪽 컬럼 */}
                 <div className='modal-col'>
                  <div className="modal-group">
                    <label>아이디</label>
                    <div className="modal-with-button">
                      <input
                        type="text"
                        value={sellerDetail.sellerId || ""}
                        onChange={(e) =>
                          setSellerDetail({
                            ...sellerDetail,
                            sellerId: e.target.value,
                          })
                        }
                      />
                      <button type="button" onClick={handleCheckId}>
                        중복확인
                      </button>
                    </div>
                    {idMsg && (
                      <p className={`ok ${isIdChecked ? "active" : ""}`}>{idMsg}</p>
                    )}
                  </div>
                  <div className="modal-group">
                    <label>회사명</label>
                    <input type='text' value={sellerDetail.companyName || ''} readOnly />
                  </div>
                  <div className="modal-group">
                    <label>이메일</label>
                    <input type='text'
                      value={sellerDetail.sellerEmail || ''}
                      onChange={(e) => setSellerDetail({ ...sellerDetail, sellerEmail: e.target.value })} />
                  </div>
                  <div className="modal-group">
                    <label>휴대폰 번호</label>
                    <input type='text' value={sellerDetail.phone || ''} readOnly />
                  </div>
                  <div className="modal-group">
                    <label>주소</label>
                    <input type='text' value={sellerDetail.address || ''} readOnly />
                  </div>
                  <div className="modal-group">
                    <label>상세주소</label>
                    <input type='text' value={sellerDetail.addressDetail || ''} readOnly />
                  </div>
                </div>       
                {/* 오른쪽 컬럼 */}
                <div className='modal-col'>
                  <div className="modal-group">
                    <label>사업자 등록번호</label>
                    <input type='text' value={sellerDetail.businessRegistrationNumber} readOnly />
                  </div>
                  <div className="modal-group">
                    <label>회사 소개</label>
                    <textarea value={sellerDetail.companyInfo || ''} readOnly />
                  </div>
                  <div className="modal-group">
                    <label>판매 승인</label>
                    <div className="radio-group">
                      <label>
                        <input
                          type='radio'
                          name='isVerified'
                          checked={sellerDetail.isVerified === true}
                          onChange={() => setSellerDetail({ ...sellerDetail, isVerified: true })}
                        /> 인증
                      </label>
                      <label>
                        <input
                          type='radio'
                          name='isVerified'
                          checked={sellerDetail.isVerified === false}
                          onChange={() => setSellerDetail({ ...sellerDetail, isVerified: false })}
                        /> 미인증
                      </label>
                    </div>
                  </div>
                  <div className="modal-group">
                    <label>계정 활성 상태</label>
                    <div className="radio-group">
                      <label>
                        <input
                          type='radio'
                          name='isActive'
                          checked={sellerDetail.isActive === true}
                          onChange={() => setSellerDetail(prev => ({
                            ...prev,
                            isActive: true,
                            withdrawalStatus: null,
                            withdrawalReason: null
                          }))}
                        /> 활성
                      </label>
                      <label>
                        <input
                          type='radio'
                          name='isActive'
                          checked={sellerDetail.isActive === false}
                          onChange={() => setSellerDetail(prev => ({
                            ...prev,
                            isActive: false,
                            withdrawalStatus: prev.withdrawalStatus ?? "FORCED_BY_ADMIN"
                          }))}
                        /> 비활성
                      </label>
                    </div>
                  </div>
                  <div className="modal-group">
                    <label>탈퇴 상태</label>
                    <input type='text' value={sellerDetail.withdrawalStatus || ''} readOnly />
                  </div>
                  <div className="modal-group">
                    <label>탈퇴 사유</label>
                    <textarea
                      value={sellerDetail.withdrawalReason || ''}
                      onChange={(e) => setSellerDetail(prev => ({ ...prev, withdrawalReason: e.target.value }))}
                      readOnly={sellerDetail.isActive === true}
                    />
                  </div>
                </div>
              </div>
              <div className='modal-footer'>
                <button type='button' className="cancel-btn" onClick={() => setIsModalOpen(false)}>닫기</button>
                <button type='submit' className="submit-btn">수정</button>
              </div>
            </form>
          </div>
        )
      )}
    </div>
  );
}

export default UserDetailModal;
