import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import "../../assets/css/Info.css";
import { useRegister } from "../../hooks/useRegister";
import { authStore } from "../../store/authStore";
import WithdrawModal from "../../components/common/WithdrawModal";

// Yup 스키마 정의 (회원가입과 유사하지만, 비밀번호는 선택적으로 변경)
const schema = yup.object().shape({
  password: yup
    .string()
    .min(4, "비밀번호는 최소 4자 이상이어야 합니다.")
    .matches(/^[0-9]+$/, "비밀번호는 숫자만 가능합니다.")
    .notRequired(),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref("password"), null], "비밀번호가 일치하지 않습니다."),
  company_name: yup.string().required("업체명을 입력해주세요."),
  seller_email: yup
    .string()
    .email("올바른 이메일 형식이 아닙니다.")
    .required("이메일을 입력해주세요."),
  business_registration_number: yup
    .string()
    .required("사업자등록번호를 입력해주세요.")
    .matches(/^\d{3}-\d{2}-\d{5}$/, "사업자등록번호는 10자리 숫자 형식(XXX-XX-XXXXX)이어야 합니다."),
  phone: yup
    .string()
    .required("전화번호를 입력해주세요.")
    .matches(/^[0-9-]+$/, "숫자만 입력해주세요."),
  address: yup.string().required("주소를 입력해주세요."),
  address_detail: yup.string(),
  company_detail: yup.string().required("업체 상세 정보를 입력해주세요."),
});

function SellerInfo() {
    

  
  const [isModalOpen, setIsModalOpen] = useState(false);

  const { user } = authStore();
  const sellerUid = user?.content?.sellerUid;

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch,
    reset,
  } = useForm({
    resolver: yupResolver(schema),
    mode: "onChange",
  });

  const {
    checkEmail,
    checkPhone,
    checkBusinessNumber,
    updateUserInfo,
    withdrawUser,
    emailMsg,
    phoneMsg,
    businessNumberMsg,
    isEmailChecked,
    isPhoneChecked,
    isBusinessNumberChecked,
    setEmailMsg,
    setPhoneMsg,
    setBusinessNumberMsg,
    setIsEmailChecked,
    setIsPhoneChecked,
    setIsBusinessNumberChecked
  } = useRegister("seller");

  // 기존 회원 정보로 폼 초기화
  useEffect(() => {
    if (user?.content) {
      const {
        companyName,
        sellerEmail,
        phone,
        businessRegistrationNumber,
        address,
        addressDetail,
        companyInfo,
      } = user.content;
      reset({
        company_name: companyName,
        seller_email: sellerEmail,
        business_registration_number: businessRegistrationNumber,
        phone,
        address,
        address_detail: addressDetail,
        company_detail: companyInfo,
      });
    }
  }, [user, reset]);


  const onSubmit = (data) => {
    // 이메일, 전화번호가 변경되었지만 중복 확인을 하지 않은 경우
    if (user.content.sellerEmail !== data.seller_email && !isEmailChecked) {
      return alert("변경된 이메일의 중복 확인을 해주세요.");
    }
    if (user.content.phone !== data.phone.replace(/-/g, "") && !isPhoneChecked) {
      return alert("변경된 전화번호의 중복 확인을 해주세요.");
    }
    if (user.content.businessRegistrationNumber !== data.business_registration_number.replace(/-/g, "") && !isBusinessNumberChecked) {
      return alert("변경된 사업자등록번호의 중복 확인을 해주세요.");
    }

    const updateData = {
      companyName: data.company_name,
      sellerEmail: data.seller_email,
      phone: data.phone.replace(/-/g, ""),
      businessRegistrationNumber: data.business_registration_number.replace(/-/g, ""),
      address: data.address,
      addressDetail: data.address_detail,
      companyDetail: data.company_detail,
    };

    // 비밀번호가 입력된 경우에만 추가
    if (data.password) {
      updateData.password = data.password;
    }

    updateUserInfo.mutate({ uid: sellerUid, data: updateData });
  };

  // 회원 탈퇴 모달 열기
  const handleWithdraw = () => {
    setIsModalOpen(true);
  };

  // 모달에서 '탈퇴하기' 버튼 클릭 시 실행
  const handleConfirmWithdraw = (reason) => {
    withdrawUser.mutate(reason || ""); // 입력이 없으면 빈 문자열로 전달
    setIsModalOpen(false);
  };
  
  // 전화번호 자동 하이픈
  const handlePhoneChange = (e) => {
    const rawValue = e.target.value.replace(/[^0-9]/g, "");
    let formatted = rawValue;
    if (rawValue.startsWith('02')){
      if(rawValue.length <=2){
        formatted = rawValue;
      }else if(rawValue.length <= 6){
        formatted = `${rawValue.slice(0, 2)}-${rawValue.slice(2)}`;
      }else if(rawValue.length <= 10){
        formatted = `${rawValue.slice(0, 2)}-${rawValue.slice(2, 6)}-${rawValue.slice(6)}`;
      }
    } else {
      if(rawValue.length > 3 && rawValue.length <= 7){
        formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3)}`;
      } else if (rawValue.length > 7) {
        formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3, 7)}-${rawValue.slice(7, 11)}`;
      }
    }
    setValue("phone", formatted, { shouldValidate: true });
  };

  // 사업자등록번호 자동 하이픈
  const handleBusinessNumberChange = (e) => {
    const rawValue = e.target.value.replace(/[^0-9]/g, "");
    const formatted = rawValue.replace(/(\d{3})(\d{2})(\d{5})/, '$1-$2-$3');
    setValue("business_registration_number", formatted.substring(0, 12), { shouldValidate: true });
  };

  // 중복확인 핸들러
  const handleCheckEmail = () => {
    const email = watch("seller_email");
    if (!email) return alert("이메일을 입력해주세요.");
    checkEmail.mutate(email);
  };

  const handleCheckPhone = () => {
    const phone = watch("phone");
    if (!phone) return alert("전화번호를 입력해주세요.");
    checkPhone.mutate(phone.replace(/-/g, ""));
  };

  const handleCheckBusinessNumber = () => {
    const businessNumber = watch("business_registration_number");
    if (!businessNumber) return alert("사업자등록번호를 입력해주세요.");
    checkBusinessNumber.mutate(businessNumber.replace(/-/g, ""));
  };

  // 입력 변경 시 중복확인 상태 초기화
  useEffect(() => {
    if (watch("seller_email") !== user?.content?.sellerEmail) {
      setIsEmailChecked(false);
      setEmailMsg("");
    }
  }, [watch("seller_email"), user]);

  useEffect(() => {
    if (watch("phone") !== user?.content?.phone) {
      setIsPhoneChecked(false);
      setPhoneMsg("");
    }
  }, [watch("phone"), user]);

  useEffect(() => {
    if (watch("business_registration_number") !== user?.content?.businessRegistrationNumber) {
      setIsBusinessNumberChecked(false);
      setBusinessNumberMsg("");
    }
  }, [watch("business_registration_number"), user]);

  // 다음 주소 팝업
  const openDaumPostcode = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        setValue("address", data.address, { shouldValidate: true });
      },
    }).open();
  };

  return (
    <div className="my-info-container">
      <h2>업체 정보 관리</h2>
      <form onSubmit={handleSubmit(onSubmit)}>
        {/* 비밀번호 (선택적 변경) */}
        <div className="info-group">
          <label>새 비밀번호</label>
          <input type="password" {...register("password")} placeholder="변경할 경우에만 입력하세요" />
          <p className="error">{errors.password?.message}</p>
        </div>
        <div className="info-group">
          <label>새 비밀번호 확인</label>
          <input type="password" {...register("confirmPassword")} />
          <p className="error">{errors.confirmPassword?.message}</p>
        </div>

        {/* 업체명 */}
        <div className="info-group">
          <label>업체명</label>
          <input type="text" {...register("company_name")} />
          <p className="error">{errors.company_name?.message}</p>
        </div>

        {/* 이메일 */}
        <div className="info-group">
          <label>이메일</label>
          <div className="input-with-button">
            <input type="text" {...register("seller_email")} />
            <button type="button" onClick={handleCheckEmail}>중복확인</button>
          </div>
          <p className="error">{errors.seller_email?.message}</p>
          {emailMsg && <p className={`ok ${isEmailChecked ? "active" : ""}`}>{emailMsg}</p>}
        </div>

        {/* 사업자등록번호 */}
        <div className="info-group">
          <label>사업자등록번호</label>
          <div className="input-with-button">
            <input type="text"
              {...register("business_registration_number")}
              onChange={handleBusinessNumberChange}
            />
            <button type="button" onClick={handleCheckBusinessNumber}>중복확인</button>
          </div>
          <p className="error">{errors.business_registration_number?.message}</p>
          {businessNumberMsg && <p className={`ok ${isBusinessNumberChecked ? "active" : ""}`}>{businessNumberMsg}</p>}
        </div>

        {/* 전화번호 */}
        <div className="info-group">
          <label>전화번호</label>
          <div className="input-with-button">
            <input type="text" {...register("phone")} onChange={handlePhoneChange} maxLength="13" />
            <button type="button" onClick={handleCheckPhone}>중복확인</button>
          </div>
          <p className="error">{errors.phone?.message}</p>
          {phoneMsg && <p className={`ok ${isPhoneChecked ? "active" : ""}`}>{phoneMsg}</p>}
        </div>

        {/* 주소 */}
        <div className="info-group">
          <label>주소</label>
          <div className="input-with-button">
            <input type="text" {...register("address")} placeholder="주소" />
            <button type="button" onClick={openDaumPostcode}>주소 검색</button>
          </div>
          <p className="error">{errors.address?.message}</p>
        </div>

        {/* 상세 주소 */}
        <div className="info-group">
          <label>상세 주소</label>
          <input type="text" {...register("address_detail")} />
        </div>

        {/* 업체 상세 정보 */}
        <div className="info-group">
          <label>업체 상세 정보</label>
          <input type="text" {...register("company_detail")} />
        </div>

        <button className="update-btn" type="submit">정보 수정하기</button>
        <button className="withdraw-btn" type="button" onClick={handleWithdraw}>회원 탈퇴</button>
      </form>
      <WithdrawModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onConfirm={handleConfirmWithdraw}
      />
    </div>
  );
}

export default SellerInfo;