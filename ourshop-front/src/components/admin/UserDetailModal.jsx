import React, { useState, useEffect } from 'react';
import { useAdmin } from '../../hooks/useAdmin';
import { useRegister } from '../../hooks/useRegister'; 
import '../../assets/css/UserDetailModal.css';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';

// =========================
// 📌 비밀번호 변경 스키마 (입력 시에만 검사)
// =========================
const passwordSchema = yup.object().shape({
  password: yup
    .string()
    .transform((v) => (v === "" ? undefined : v)) // "" → undefined 처리
    .notRequired()
    .matches(/^[A-Za-z0-9]+$/, "비밀번호는 영문 또는 숫자만 가능합니다.")
    .min(4, "비밀번호는 최소 4자 이상이어야 합니다."),
  passwordConfirm: yup.string().when("password", {
    is: (val) => val && val.length > 0, // 비밀번호를 입력한 경우만
    then: (schema) =>
      schema
        .required("비밀번호 확인을 입력해주세요.")
        .oneOf([yup.ref("password")], "비밀번호가 일치하지 않습니다."),
    otherwise: (schema) => schema.notRequired(),
  }),
});

function UserDetailModal({ uid, mode, setIsModalOpen }) {
  const { getUserDetail, updateUser } = useAdmin();
  const { data: detailData, isLoading, isError } = getUserDetail(mode, uid);
  const { mutate: mutateUpdateUser } = updateUser();

  // useRegister 훅
  const {
    checkId,
    idMsg,
    isIdChecked,
    setIdMsg,
    setIsIdChecked,
  } = useRegister(mode);

  // react-hook-form
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    reset: resetPwForm,
  } = useForm({
    resolver: yupResolver(passwordSchema),
    mode: "onChange",
    defaultValues: {
      password: "",
      passwordConfirm: "",
    }
  });

  const password = watch("password");

  const [buyerDetail, setBuyerDetail] = useState(null);
  const [sellerDetail, setSellerDetail] = useState(null);

  // 휴대폰 포맷
  const formatPhone = (value) => {
    if (!value) return "";
    const raw = value.replace(/\D/g, "");

    if (raw.startsWith("02")) {
      if (raw.length === 9)
        return raw.replace(/(\d{2})(\d{3})(\d{4})/, "$1-$2-$3");
      if (raw.length === 10)
        return raw.replace(/(\d{2})(\d{4})(\d{4})/, "$1-$2-$3");
    }
    if (raw.length === 10)
      return raw.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3");
    if (raw.length === 11)
      return raw.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");

    return value;
  };

  // uid 바뀔 때마다 초기화
  useEffect(() => {
    setBuyerDetail(null);
    setSellerDetail(null);
    resetPwForm();
  }, [uid, resetPwForm]);

  // 구매자 정보 세팅
  useEffect(() => {
    if (detailData?.content && mode === "buyer") {
      const d = detailData.content;

      setBuyerDetail({
        buyerId: d.buyerId,
        nickname: d.nickname,
        buyerEmail: d.buyerEmail,
        phone: d.phone,
        birth: d.birth,
        gender: d.gender,
        address: d.address,
        addressDetail: d.addressDetail,
        isActive: d.isActive,
        withdrawalStatus: d.withdrawalStatus,
        withdrawalReason: d.withdrawalReason,
      });
    }
  }, [detailData, mode]);

  // 판매자 정보 세팅
  useEffect(() => {
    if (detailData?.content && mode === "seller") {
      const d = detailData.content;

      setSellerDetail({
        sellerId: d.sellerId,
        companyName: d.companyName,
        sellerEmail: d.sellerEmail,
        phone: d.phone,
        businessRegistrationNumber: d.businessRegistrationNumber,
        address: d.address,
        addressDetail: d.addressDetail,
        companyInfo: d.companyInfo,
        isVerified: d.isVerified,
        isActive: d.isActive,
        withdrawalStatus: d.withdrawalStatus,
        withdrawalReason: d.withdrawalReason,
      });
    }
  }, [detailData, mode]);

  // 아이디 중복확인 초기화 처리
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

  // 아이디 중복확인
  const handleCheckId = () => {
    const id = mode === "buyer" ? buyerDetail?.buyerId : sellerDetail?.sellerId;
    if (!id) return alert("아이디를 입력해주세요.");

    checkId.mutate({ id, isAdmin: true, uid });
  };

  // =========================
  // 📌 최종 업데이트 (핵심)
  // =========================
  const handleUpdate = (formData) => {
    const { password } = formData;

    const original = detailData?.content;
    let data = mode === "buyer" ? { ...buyerDetail } : { ...sellerDetail };

    // 아이디 변경 시 중복확인 필수
    const originalId = mode === "buyer" ? original?.buyerId : original?.sellerId;
    const newId = mode === "buyer" ? buyerDetail.buyerId : sellerDetail.sellerId;

    if (newId !== originalId && !isIdChecked) {
      return alert("아이디 중복확인을 해주세요.");
    }

    // 🚀 password 입력한 경우에만 적용
    if (password && password.trim() !== "") {
      data.password = password;
    }

    mutateUpdateUser({ mode, uid, data });
    setIsModalOpen(false);
  };

  return (
    <div className="modal-backdrop" onClick={() => setIsModalOpen(false)}>

      {/* ====================== */}
      {/*     구매자 모달         */}
      {/* ====================== */}
      {mode === "buyer" && buyerDetail && (
        <div className="modal-cont" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h2>구매자 상세 정보</h2>
            <button onClick={() => setIsModalOpen(false)} className="close-btn">
              X
            </button>
          </div>

          <form className="modal-form" onSubmit={handleSubmit(handleUpdate)}>
            <div className="modal-main">

              {/* 왼쪽 */}
              <div className="modal-col">
                {/* 아이디 */}
                <div className="modal-group">
                  <label>구매자 UID</label>
                  <input type="text" readOnly value={uid} />
                </div>
                <div className="modal-group">
                  <label>아이디</label>
                  <div className="modal-with-button">
                    <input
                      type="text"
                      value={buyerDetail.buyerId}
                      onChange={(e) =>
                        setBuyerDetail((prev) => ({
                          ...prev,
                          buyerId: e.target.value,
                        }))
                      }
                    />
                    <button type="button" onClick={handleCheckId}>중복확인</button>
                  </div>
                  {idMsg && (
                    <p className={`id-ok ${isIdChecked ? "active" : ""}`}>
                      {idMsg}
                    </p>
                  )}
                </div>

                {/* 비밀번호 */}
                <div className="modal-group">
                  <label>새 비밀번호</label>
                  <input
                    type="text"
                    {...register("password")}
                    placeholder="영문+숫자 (변경 시에만 입력)"
                  />
                  {errors.password && <p className="error">{errors.password.message}</p>}
                </div>

                {/* 비밀번호 확인 */}
                <div className="modal-group">
                  <label>비밀번호 확인</label>
                  <input
                    type="text"
                    {...register("passwordConfirm")}
                    placeholder="비밀번호 확인"
                  />
                  {errors.passwordConfirm && (
                    <p className="error">{errors.passwordConfirm.message}</p>
                  )}
                </div>

                <div className="modal-group">
                  <label>닉네임</label>
                  <input type="text" readOnly value={buyerDetail.nickname} />
                </div>
                <div className="modal-group">
                  <label>생년월일</label>
                  <input type="text" readOnly value={buyerDetail.birth} />
                </div>
                <div className="modal-group">
                  <label>성별</label>
                  <input type="text" readOnly value={buyerDetail.gender} />
                </div>
              </div>

              {/* 오른쪽 */}
              <div className="modal-col">
                <div className="modal-group">
                  <label>이메일</label>
                  <input type="text" readOnly value={buyerDetail.buyerEmail} />
                </div>

                <div className="modal-group">
                  <label>휴대폰</label>
                  <input type="text" readOnly value={formatPhone(buyerDetail.phone)} />
                </div>
             
                <div className="modal-group">
                  <label>주소</label>
                  <input type="text" readOnly value={buyerDetail.address} />
                </div>

                <div className="modal-group">
                  <label>상세주소</label>
                  <input
                    type="text"
                    readOnly
                    value={buyerDetail.addressDetail}
                  />
                </div>

                <div className="modal-group">
                  <label>계정 활성 상태</label>
                  <div className="radio-group">
                    <label>
                      <input
                        type="radio"
                        checked={buyerDetail.isActive === true}
                        onChange={() =>
                          setBuyerDetail((prev) => ({
                            ...prev,
                            isActive: true,
                            withdrawalStatus: null,
                            withdrawalReason: null,
                          }))
                        }
                      />{" "}
                      활성
                    </label>

                    <label>
                      <input
                        type="radio"
                        checked={buyerDetail.isActive === false}
                        onChange={() =>
                          setBuyerDetail((prev) => ({
                            ...prev,
                            isActive: false,
                            withdrawalStatus: prev.withdrawalStatus ?? "FORCED_BY_ADMIN",
                          }))
                        }
                      />{" "}
                      비활성
                    </label>
                  </div>
                </div>

                <div className="modal-group">
                  <label>탈퇴 상태</label>
                  <input type="text" readOnly value={buyerDetail.withdrawalStatus || ""} />
                </div>

                <div className="modal-group">
                  <label>탈퇴 사유</label>
                  <textarea
                    readOnly={buyerDetail.isActive === true}
                    value={buyerDetail.withdrawalReason || ""}
                    onChange={(e) =>
                      setBuyerDetail((prev) => ({
                        ...prev,
                        withdrawalReason: e.target.value,
                      }))
                    }
                  />
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button type="button" className="cancel-btn" onClick={() => setIsModalOpen(false)}>
                닫기
              </button>
              <button type="submit" className="submit-btn">수정</button>
            </div>
          </form>
        </div>
      )}

      {/* ====================== */}
      {/*    판매자 모달          */}
      {/* ====================== */}
      {mode === "seller" && sellerDetail && (
        <div className="modal-cont" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h2>판매자 상세 정보</h2>
            <button onClick={() => setIsModalOpen(false)} className="close-btn">
              X
            </button>
          </div>

          <form className="modal-form" onSubmit={handleSubmit(handleUpdate)}>
            <div className="modal-main">

              {/* 왼쪽 */}
              <div className="modal-col">
                 <div className="modal-group">
                  <label>판매자 UID</label>
                  <input type="text" readOnly value={uid} />
                </div>
                <div className="modal-group">
                  <label>아이디</label>
                  <div className="modal-with-button">
                    <input
                      type="text"
                      value={sellerDetail.sellerId}
                      onChange={(e) =>
                        setSellerDetail((prev) => ({
                          ...prev,
                          sellerId: e.target.value,
                        }))
                      }
                    />
                    <button type="button" onClick={handleCheckId}>중복확인</button>
                  </div>
                  {idMsg && (
                    <p className={`id-ok ${isIdChecked ? "active" : ""}`}>
                      {idMsg}
                    </p>
                  )}
                </div>

                {/* 비밀번호 */}
                <div className="modal-group">
                  <label>새 비밀번호</label>
                  <input
                    type="text"
                    {...register("password")}
                    placeholder="영문+숫자 (변경 시에만 입력)"
                  />
                  {errors.password && <p className="error">{errors.password.message}</p>}
                </div>

                {/* 비밀번호 확인 */}
                <div className="modal-group">
                  <label>비밀번호 확인</label>
                  <input
                    type="text"
                    {...register("passwordConfirm")}
                    placeholder="비밀번호 확인"
                  />
                  {errors.passwordConfirm && (
                    <p className="error">{errors.passwordConfirm.message}</p>
                  )}
                </div>

                <div className="modal-group">
                  <label>회사명</label>
                  <input type="text" readOnly value={sellerDetail.companyName} />
                </div>

                <div className="modal-group">
                  <label>이메일</label>
                  <input type="text" readOnly value={sellerDetail.sellerEmail} />
                </div>

                <div className="modal-group">
                  <label>휴대폰 번호</label>
                  <input type="text" readOnly value={formatPhone(sellerDetail.phone)} />
                </div>
                <div className="modal-group">
                  <label>사업자 등록번호</label>
                  <input
                    type="text"
                    readOnly
                    value={sellerDetail.businessRegistrationNumber.replace(
                      /(\d{3})(\d{2})(\d{5})/,
                      "$1-$2-$3"
                    )}
                  />
                </div> 
              </div>

              {/* 오른쪽 */}
              <div className="modal-col">              
                <div className="modal-group">
                  <label>주소</label>
                  <input type="text" readOnly value={sellerDetail.address} />
                </div>
                <div className="modal-group">
                  <label>상세주소</label>
                  <input type="text" readOnly value={sellerDetail.addressDetail} />
                </div>
                
                <div className="modal-group">
                  <label>회사 소개</label>
                  <textarea readOnly value={sellerDetail.companyInfo} />
                </div>

                <div className="modal-group">
                  <label>판매 인증</label>
                  <div className="radio-group">
                    <label>
                      <input
                        type="radio"
                        checked={sellerDetail.isVerified === true}
                        onChange={() =>
                          setSellerDetail((prev) => ({
                            ...prev,
                            isVerified: true,
                          }))
                        }
                      />{" "}
                      인증
                    </label>

                    <label>
                      <input
                        type="radio"
                        checked={sellerDetail.isVerified === false}
                        onChange={() =>
                          setSellerDetail((prev) => ({
                            ...prev,
                            isVerified: false,
                          }))
                        }
                      />{" "}
                      미인증
                    </label>
                  </div>
                </div>

                <div className="modal-group">
                  <label>계정 활성 상태</label>
                  <div className="radio-group">
                    <label>
                      <input
                        type="radio"
                        checked={sellerDetail.isActive === true}
                        onChange={() =>
                          setSellerDetail((prev) => ({
                            ...prev,
                            isActive: true,
                            withdrawalStatus: null,
                            withdrawalReason: null,
                          }))
                        }
                      />{" "}
                      활성
                    </label>

                    <label>
                      <input
                        type="radio"
                        checked={sellerDetail.isActive === false}
                        onChange={() =>
                          setSellerDetail((prev) => ({
                            ...prev,
                            isActive: false,
                            withdrawalStatus: prev.withdrawalStatus ?? "FORCED_BY_ADMIN",
                          }))
                        }
                      />{" "}
                      비활성
                    </label>
                  </div>
                </div>

                <div className="modal-group">
                  <label>탈퇴 상태</label>
                  <input type="text" readOnly value={sellerDetail.withdrawalStatus || ""} />
                </div>

                <div className="modal-group">
                  <label>탈퇴 사유</label>
                  <textarea
                    readOnly={sellerDetail.isActive === true}
                    value={sellerDetail.withdrawalReason || ""}
                    onChange={(e) =>
                      setSellerDetail((prev) => ({
                        ...prev,
                        withdrawalReason: e.target.value,
                      }))
                    }
                  />
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button type="button" className="cancel-btn" onClick={() => setIsModalOpen(false)}>
                닫기
              </button>
              <button type="submit" className="submit-btn">수정</button>
            </div>
          </form>
        </div>
      )}

    </div>
  );
}

export default UserDetailModal;
