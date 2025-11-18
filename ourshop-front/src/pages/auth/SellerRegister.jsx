import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import "../../assets/css/Register.css";
import { useRegister } from "../../hooks/useRegister"; // ✅ 추가

// Yup 스키마 정의
const schema = yup.object().shape({
  seller_id: yup
    .string()
    .required("아이디를 입력해주세요.")
    .matches(/^[A-Za-z0-9]+$/, "영문과 숫자만 입력 가능합니다.")
    .min(6, "아이디는 최소 6자 이상이어야 합니다."),
  password: yup
    .string()
    .required("비밀번호를 입력해주세요.")
    .matches(/^[0-9]+$/, "비밀번호는 숫자만 가능합니다."),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref("password"), null], "비밀번호가 일치하지 않습니다."),
  company_name: yup.string()
    .required("업체명을 입력해주세요.")
    .max(16, "업체명은 최대 16자까지 가능합니다."),
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
  company_detail: yup
    .string()
    .required("업체 상세 정보를 입력해주세요.")
    .max(250, "업체 상세 정보는 최대 250자까지 가능합니다.")
});

function SellerRegister() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch,
  } = useForm({
    resolver: yupResolver(schema),
    mode: "onChange",
  });

  // ✅ 중복확인 훅 추가
  const {
    checkId,
    checkEmail,
    checkBusinessNumber,
    registerUser,
    idMsg,
    emailMsg,
    phoneMsg,
    businessNumberMsg,
    isIdChecked,
    isEmailChecked,
    isBusinessNumberChecked,
    setIdMsg,
    setEmailMsg,
    setBusinessNumberMsg,
    setIsIdChecked,
    setIsEmailChecked,
    setIsBusinessNumberChecked,
  } = useRegister("seller");

  const [selectedDomain, setSelectedDomain] = useState("");
  const [customDomain, setCustomDomain] = useState("");

  const onSubmit = (data) => {
    console.log("폼 제출 데이터:", data);

    if (!isIdChecked || !isEmailChecked || /*!isPhoneChecked ||*/ !isBusinessNumberChecked) {
      alert("중복확인을 완료해주세요.");
      return;
    }

    const newData = {
      sellerId: data.seller_id,
      password: data.password,
      companyName: data.company_name,
      sellerEmail: data.seller_email,
      businessRegistrationNumber: data.business_registration_number.replace(/-/g, ""),
      phone: data.phone.replace(/-/g, ""),
      address: data.address,
      addressDetail: data.address_detail,
      companyInfo: data.company_detail,
    };

    registerUser.mutate(newData);
  };

  //// 사업자등록번호 자동 하이픈 (3-2-5)
  const handleBusinessNumberChange = (e) => {
    const rawValue = e.target.value.replace(/[^0-9]/g, ""); // 숫자만 남김
    let formatted = rawValue;

    if (rawValue.length <= 3) {
      formatted = rawValue;
    } else if (rawValue.length <= 5) {
      formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3)}`;
    } else {
      formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3, 5)}-${rawValue.slice(5, 10)}`;
    }

    setValue("business_registration_number", formatted, { shouldValidate: true });
};


  // 전화번호 자동 하이픈
  const handlePhoneChange = (e) => {
    const rawValue = e.target.value.replace(/[^0-9]/g, "");
    let formatted = rawValue;

    if (rawValue.startsWith('02')){
      if(rawValue.length <= 2){
        formatted = rawValue;
      } else if(rawValue.length <= 6){
        formatted = `${rawValue.slice(0, 2)}-${rawValue.slice(2)}`;
      }else if(rawValue.length <= 10){
        formatted = `${rawValue.slice(0, 2)}-${rawValue.slice(2, 6)}-${rawValue.slice(6)}`;
      }
    } else {
      if (rawValue.length > 3 && rawValue.length <= 7){
        formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3)}`;
      } else if (rawValue.length > 7) {
        formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3, 7)}-${rawValue.slice(7, 11)}`;
      }
    }
    setValue("phone", formatted, { shouldValidate: true });
  };

  // 이메일 아이디 + 도메인 합치기
  const handleEmailChange = (field, value) => {
    const emailId = field === "id" ? value : watch("emailId");
    let domain = "";

    // custom domain 즉시 반영
    if (field === "domain") {
        domain =
          selectedDomain === "custom" ? value : value === "custom" ? customDomain : value;
    } else {
        domain = selectedDomain === "custom" ? customDomain : selectedDomain;
    }

    const fullEmail =
        emailId && domain ? `${emailId}@${domain}` : emailId ? `${emailId}@` : "";
        setValue("seller_email", fullEmail, { shouldValidate: true });
  };


  // 각 중복확인 핸들러
  const handleCheckId = () => {
    const id = watch("seller_id");
    if (!id) return alert("아이디를 입력해주세요.");
    checkId.mutate({id: id});
  };

  const handleCheckEmail = () => {
    const email = watch("seller_email");
    if (!email) return alert("이메일을 입력해주세요.");
    console.log("이메일", email);
    checkEmail.mutate(email);
  };

  /*const handleCheckPhone = () => {
    const phone = watch("phone");
    if (!phone) return alert("전화번호를 입력해주세요.");
    checkPhone.mutate(phone.replace(/-/g, ""));
  };*/

  const handleCheckBusinessNumber = () => {
    const businessNumber = watch("business_registration_number");
    if (!businessNumber) return alert("사업자등록번호를 입력해주세요.");
    checkBusinessNumber.mutate(businessNumber.replace(/-/g, ""));
  };

  // 입력 변경 시 중복확인 상태 초기화
  useEffect(() => {
    setIsIdChecked(false);
    setIdMsg("");
  }, [watch("seller_id")]);

  useEffect(() => {
    setIsEmailChecked(false);
    setEmailMsg("");
  }, [watch("seller_email")]);

  /*useEffect(() => {
    setIsPhoneChecked(false);
    setPhoneMsg("");
  }, [watch("phone")]);*/

  useEffect(() => {
    setIsBusinessNumberChecked(false);
    setBusinessNumberMsg("");
  }, [watch("business_registration_number")]);


  // 다음 주소 팝업
  const openDaumPostcode = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        let fullAddress = data.address;
        let extraAddress = "";

        if (data.addressType === "R") {
          if (data.bname !== "") extraAddress += data.bname;
          if (data.buildingName !== "") {
            extraAddress +=
              extraAddress !== "" ? `, ${data.buildingName}` : data.buildingName;
          }
          fullAddress += extraAddress !== "" ? ` (${extraAddress})` : "";
        }

        //setValue("zipcode", data.zonecode);
        setValue("address", fullAddress, { shouldValidate: true });
      },
    }).open();
  };


  return (
    <div className="register-container">
      <h2>판매자 회원가입</h2>
      <form onSubmit={handleSubmit(onSubmit)}>
        {/* 아이디 */}
        <div className="form-group">
          <label>아이디</label>
          <div className="input-with-button">
            <input type="text" {...register("seller_id")} 
            placeholder="아이디"
            />
            <button type="button" onClick={handleCheckId}>아이디 중복확인</button>
          </div>
          <p className="error">{errors.seller_id?.message}</p>
          {idMsg ? <p className={`ok ${isIdChecked ? "active" : ""}`}>{idMsg}</p>
          : !isIdChecked && <p className="ok">아이디 중복체크 필수</p>}
        </div>

        {/* 비밀번호 */}
        <div className="form-group">
          <label>비밀번호</label>
          <input type="password" {...register("password")} 
          placeholder="비밀번호"/>
          <p className="error">{errors.password?.message}</p>
        </div>

        {/* 비밀번호 확인 */}
        <div className="form-group">
          <label>비밀번호 확인</label>
          <input type="password" {...register("confirmPassword")} 
          placeholder="입력한 비밀번호와 동일한 비밀번호로 입력하세요."/>
          <p className="error">{errors.confirmPassword?.message}</p>
        </div>

        {/* 업체명 */}
        <div className="form-group">
          <label>업체명</label>
          <input type="text" {...register("company_name")} 
          placeholder="업체명"/>
          <p className="error">{errors.company_name?.message}</p>
        </div>

        {/* 이메일 */}
        <div className="form-group">
          <label>이메일</label>
          <div className="email-group">
            <input
              type="text"
              placeholder="이메일 아이디"
              {...register("emailId")}
              onChange={(e) => handleEmailChange("id", e.target.value)}
            />
            <span>@</span>
            <select
              onChange={(e) => {
                const val = e.target.value;
                setSelectedDomain(val);
                handleEmailChange("domain", val);
              }}
              defaultValue=""
            >
              <option value="" disabled>
                도메인 선택
              </option>
              <option value="naver.com">naver.com</option>
              <option value="daum.net">daum.net</option>
              <option value="gmail.com">gmail.com</option>
              <option value="custom">직접입력</option>
            </select>
            <button type="button" onClick={handleCheckEmail}>
              이메일 중복확인
            </button>
          </div>
          {selectedDomain === "custom" && (
             <input
              type="text"
              placeholder="도메인을 직접 입력하세요"
              className="custom-domain"
              value={customDomain}
              onChange={(e) => {
                const newDomain = e.target.value;
                setCustomDomain(newDomain);
                // 직접 입력값(newDomain)으로 이메일 즉시 갱신
                handleEmailChange("domain", newDomain);
              }}
            />
          )}
          <p className="error">{errors.seller_email?.message}</p>
          {emailMsg ? <p className={`ok ${isEmailChecked ? "active" : ""}`}>{emailMsg}</p>
          : !isEmailChecked && <p className="ok">이메일 중복체크 필수</p>}
        </div>

        {/* 사업자등록번호 */}
        <div className="form-group">
          <label>사업자등록번호</label>
          <div className="input-with-button">
            <input type="text" 
              {...register("business_registration_number")} 
              onChange={handleBusinessNumberChange}
              placeholder="사업자등록번호 10자리"
            />
            <button type="button" onClick={handleCheckBusinessNumber}>사업자번호 중복확인</button>
          </div>
          <p className="error">{errors.business_registration_number?.message}</p>
          {businessNumberMsg && (
            <p className={`ok ${isBusinessNumberChecked ? "active" : ""}`}>{businessNumberMsg}</p>
          )}
        </div>

        {/* 전화번호 */}
        <div className="form-group">
          <label>전화번호</label>
          <div className="input-with-button">
            <input
              type="text"
              {...register("phone")}
              onChange={handlePhoneChange}
              maxLength="13"
              placeholder="업체 전화번호"
            />
            {/*<button type="button" onClick={handleCheckPhone}>
              중복확인
            </button>*/}
          </div>
          <p className="error">{errors.phone?.message}</p>
          {/*phoneMsg? <p className={`ok ${isPhoneChecked ? "active" : ""}`}>{phoneMsg}</p>
          : !isPhoneChecked && <p className="ok">전화번호 중복체크 필수</p>*/}
        </div>

        {/* 주소 */}
        <div className="form-group">
          <label>주소</label>
          <div className="input-with-button">
            {/*<input type="text" {...register("zipcode")} placeholder="우편번호" readOnly />*/}
            <input type="text" {...register("address")} placeholder="주소" readOnly />
            <button type="button" onClick={openDaumPostcode}>
              주소 검색
            </button>
          </div>
          <p className="error">{errors.address?.message}</p>
        </div>

        {/* 상세 주소 */}
        <div className="form-group">
          <label>상세 주소</label>
          <input type="text" {...register("address_detail")} 
          placeholder="상세 주소"/>
        </div>

        {/* 업체 상세 정보 */}
        <div className="form-group">
          <label>업체 상세 정보</label>
          <textarea type="text" {...register("company_detail")} 
          placeholder="업체와 관련된 정보를 입력해주세요"/>
          <p className="error">{errors.company_detail?.message}</p>
        </div>
        
        <button type="submit" className="register-seller-btn">가입하기</button>
      </form>
    </div>
  );
}

export default SellerRegister;
