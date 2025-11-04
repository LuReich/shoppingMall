import React, { useState, useEffect } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import '../../assets/css/UserDetailModal.css';
import { set } from 'react-hook-form';

function UserDetailModal({uid, mode, setIsModalOpen}) {

    // 1. 모든 훅을 컴포넌트 최상단으로 이동
    const { getUserDetail, updateUser } = useAdmin();
    const { data: detailData, isLoading, isError } = getUserDetail(mode, uid);
    const { mutate: mutateUpdateUser} = updateUser();
    const [buyerDetail, setBuyerDetail] = useState(null);
    const [sellerDetail, setSellerDetail] = useState(null);

    // uid가 변경될 때마다 내부 상태를 초기화하여 이전 사용자 정보가 보이는 것을 방지
    useEffect(() => {
        setBuyerDetail(null);
        setSellerDetail(null);
    }, [uid]);

    // 2. detailData가 변경될 때마다 buyerDetail 상태를 업데이트
    useEffect(() => {
        if (detailData?.content) {
            const detail = detailData.content;
            setBuyerDetail({
                "buyerId": detail.buyerId,
                "nickname": detail.nickname,
                "birth": detail.birth,
                "gender": detail.gender,
                "buyerEmail": detail.buyerEmail,
                "phone": detail.phone,
                "address": detail.address,
                "addressDetail": detail.addressDetail,
                "isActive": detail.isActive,
                "withdrawalStatus": detail.withdrawalStatus,
                "withdrawalReason": detail.withdrawalReason,
            });
        }
    }, [detailData]);

    useEffect(() => {
        if (detailData?.content) {
            const detail = detailData.content;
            setSellerDetail({
                "sellerId": detail.sellerId,
                "companyName": detail.companyName,
                "businessRegistrationNumber": detail.businessRegistrationNumber,
                "sellerEmail": detail.sellerEmail,
                "phone": detail.phone,
                "address": detail.address,
                "addressDetail": detail.addressDetail,
                "companyInfo": detail.companyInfo,
                "isVerified": detail.isVerified,
                "isActive": detail.isActive,
                "withdrawalStatus": detail.withdrawalStatus,
                "withdrawalReason": detail.withdrawalReason,
            });
        }
    }, [detailData]);

    // 계정 활성 상태에 따른 탈퇴 상태 및 탈퇴 사유 변동 처리
    // 모든 훅을 컴포넌트 최상단으로 이동 해야 랜더링 문제 발생안함
    useEffect(() => {
        if (mode === 'buyer' && buyerDetail) {
            if (buyerDetail.isActive === false && buyerDetail.withdrawalStatus == null) {
                setBuyerDetail(prev => ({ ...prev, withdrawalStatus: "FORCED_BY_ADMIN" }));
            } else if (buyerDetail.isActive === true && buyerDetail.withdrawalStatus) {
                setBuyerDetail(prev => ({ ...prev, withdrawalStatus: null, withdrawalReason: null }));
            }
        } else if (mode === 'seller' && sellerDetail) {
            if (sellerDetail.isActive === false && sellerDetail.withdrawalStatus == null) {
                setSellerDetail(prev => ({ ...prev, withdrawalStatus: "FORCED_BY_ADMIN" }));
            } else if (sellerDetail.isActive === true && sellerDetail.withdrawalStatus) {
                setSellerDetail(prev => ({ ...prev, withdrawalStatus: null, withdrawalReason: null }));
            }
        }
    }, [buyerDetail?.isActive, sellerDetail?.isActive, mode]);

    // 훅 호출 이후에 조건부 렌더링 수행
    if (isLoading) return <p>로딩중...</p>;
    if (isError) return <p>회원 상세 정보를 불러올 수 없습니다.</p>;

    const handleUpdate = (e) => {
        e.preventDefault();
        console.log("수정된 회원 정보:", buyerDetail);
        mutateUpdateUser({mode: mode, uid: uid, data: buyerDetail || sellerDetail});
        setIsModalOpen(false);
    }


    return (
         <div className='modal-backdrop'>
           {
            (mode === 'buyer' && buyerDetail) ? (
                <div className='modal-content' onClick={(e) => e.stopPropagation()}>
                    <div className="modal-header">
                        <h2>구매자 상세 정보</h2>
                        <button onClick={() => setIsModalOpen(false)} className="close-btn">&times;</button>
                    </div>
                    <form className='modal-form' onSubmit={handleUpdate}>
                        <div className="form-group"><label>아이디</label><input type='text' value={buyerDetail.buyerId} readOnly /></div>
                        <div className="form-group"><label>닉네임</label><input type='text' value={buyerDetail.nickname} readOnly /></div>
                        <div className="form-group"><label>생년월일</label><input type='text' value={buyerDetail.birth} readOnly /></div>
                        <div className="form-group"><label>성별</label><input type='text' value={buyerDetail.gender} readOnly /></div>
                        <div className="form-group"><label>이메일</label><input type='text' value={buyerDetail.buyerEmail} readOnly /></div>
                        <div className="form-group"><label>휴대폰 번호</label><input type='text' value={buyerDetail.phone} readOnly /></div>
                        <div className="form-group"><label>주소</label><input type='text' value={buyerDetail.address} readOnly /></div>
                        <div className="form-group"><label>상세주소</label><input type='text' value={buyerDetail.addressDetail} readOnly /></div>
                        <div className="form-group">
                            <label>계정 활성 상태</label>
                            <div className="radio-group">
                                <label><input type='radio' name='isActive' checked={buyerDetail.isActive === true} onChange={()=> setBuyerDetail({...buyerDetail, isActive: true})}/> 활성</label>
                                <label><input type='radio' name='isActive' checked={buyerDetail.isActive === false} onChange={()=> setBuyerDetail({...buyerDetail, isActive: false})}/> 비활성</label>
                            </div>
                        </div>
                        <div className="form-group"><label>탈퇴 상태</label><input type='text' value={buyerDetail.withdrawalStatus || '-'} readOnly /></div>
                        <div className="form-group">
                            <label>탈퇴 사유</label>
                            <textarea 
                                value={buyerDetail.withdrawalReason || ''} 
                                onChange={(e) => setBuyerDetail(prev => ({...prev, withdrawalReason: e.target.value}))}
                                readOnly={buyerDetail.isActive === true} 
                            />
                        </div>
                        <div className='modal-footer'>
                            <button type='button' className="cancel-btn" onClick={() => setIsModalOpen(false)}>닫기</button>
                            <button type='submit' className="submit-btn">수정</button>
                        </div>
                    </form>
                </div>

            )
            :(
                mode === 'seller' && sellerDetail && (
                <div className='modal-content' onClick={(e) => e.stopPropagation()}>
                    <div className="modal-header">
                        <h2>판매자 상세 정보</h2>
                        <button onClick={() => setIsModalOpen(false)} className="close-btn">&times;</button>
                    </div>
                    <form className='modal-form' onSubmit={handleUpdate} >
                        <div className="form-group"><label>아이디</label><input type='text' value={sellerDetail.sellerId} readOnly /></div>
                        <div className="form-group"><label>회사명</label><input type='text' value={sellerDetail.companyName} readOnly /></div>
                        <div className="form-group"><label>사업자 등록번호</label><input type='text' value={sellerDetail.businessRegistrationNumber} readOnly /></div>
                        <div className="form-group"><label>이메일</label><input type='text' value={sellerDetail.sellerEmail} readOnly /></div>
                        <div className="form-group"><label>휴대폰 번호</label><input type='text' value={sellerDetail.phone} readOnly /></div>
                        <div className="form-group"><label>주소</label><input type='text' value={sellerDetail.address} readOnly /></div>
                        <div className="form-group"><label>상세주소</label><input type='text' value={sellerDetail.addressDetail} readOnly /></div>
                        <div className="form-group"><label>회사 소개</label><textarea value={sellerDetail.companyInfo} readOnly /></div>
                        <div className="form-group">
                            <label>판매 승인</label>
                            <div className="radio-group">
                                <label><input type='radio' name='isVerified' checked={sellerDetail.isVerified === true} onChange={()=> setSellerDetail({...sellerDetail, isVerified: true})}/> 인증</label>
                                <label><input type='radio' name='isVerified' checked={sellerDetail.isVerified === false} onChange={()=> setSellerDetail({...sellerDetail, isVerified: false})}/> 미인증</label>
                            </div>
                        </div>
                        <div className="form-group">
                            <label>계정 활성 상태</label>
                            <div className="radio-group">
                                <label><input type='radio' name='isActive' checked={sellerDetail.isActive === true} onChange={()=> setSellerDetail({...sellerDetail, isActive: true})}/> 활성</label>
                                <label><input type='radio' name='isActive' checked={sellerDetail.isActive === false} onChange={()=> setSellerDetail({...sellerDetail, isActive: false})}/> 비활성</label>
                            </div>
                        </div>
                        <div className="form-group"><label>탈퇴 상태</label><input type='text' value={sellerDetail.withdrawalStatus || '-'} readOnly /></div>
                        <div className="form-group">
                            <label>탈퇴 사유</label>
                            <textarea 
                                value={sellerDetail.withdrawalReason || ''} 
                                onChange={(e) => setSellerDetail(prev => ({...prev, withdrawalReason: e.target.value}))}
                                readOnly={sellerDetail.isActive === true}
                            />
                        </div>
                        <div className='modal-footer'>
                            <button type='button' className="cancel-btn" onClick={() => setIsModalOpen(false)}>닫기</button>
                            <button type='submit' className="submit-btn">수정</button>
                        </div>
                    </form>
                </div>
                ))
           }

        </div>
    )
}

export default UserDetailModal;