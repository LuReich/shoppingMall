import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import "../../assets/css/Info.css";
import { useRegister } from "../../hooks/useRegister";
import { authStore } from "../../store/authStore";
import WithdrawModal from "../../components/common/WithdrawModal";

// Yup 스키마
const schema = yup.object().shape({
  password: yup
    .string()
    .transform((v) => (v === "" ? undefined : v)) //공백 undefined로 처리해줘야 입력안할때도 수정됨
    .min(4, "비밀번호는 최소 4자 이상이어야 합니다.")
    .matches(/^[0-9]+$/, "비밀번호는 숫자만 가능합니다.")
    .notRequired(),
  confirmPassword: yup.string().when("password", {
    is: (val) => val && val.length > 0, // 비밀번호를 입력한 경우
    then: (schema) =>
      schema
        .required("비밀번호 확인을 입력해주세요.")
        .oneOf([yup.ref("password")], "비밀번호가 일치하지 않습니다."),
    otherwise: (schema) => schema.notRequired(), // 입력 안 했을 때는 검사 안 함
  }),
  companyName: yup.string().required("업체명을 입력해주세요."),
  sellerEmail: yup
    .string()
    .email("올바른 이메일 형식이 아닙니다.")
    .required("이메일을 입력해주세요."),
  businessRegistrationNumber: yup
    .string()
    .required("사업자등록번호를 입력해주세요.")
    .matches(/^\d{3}-\d{2}-\d{5}$/, "사업자등록번호는 10자리 숫자 형식(XXX-XX-XXXXX)이어야 합니다."),
  phone: yup
    .string()
    .required("전화번호를 입력해주세요.")
    .matches(/^[0-9-]+$/, "숫자만 입력해주세요."),
  address: yup.string().required("주소를 입력해주세요."),
  addressDetail: yup.string(),
  companyDetail: yup.string().required("업체 상세 정보를 입력해주세요."),
});

function SellerInfo() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { user } = authStore();
  const seller = user?.content;
  const sellerUid = seller?.sellerUid;

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
    setIsBusinessNumberChecked,
  } = useRegister("seller");

  // 이메일 도메인 선택 (BuyerInfo 방식)
  const [selectedDomain, setSelectedDomain] = useState("");
  const [customDomain, setCustomDomain] = useState("");

  // 기본값 세팅
  useEffect(() => {
    if (seller) {
      setValue("companyName", seller.companyName);
      setValue("sellerEmail", seller.sellerEmail);
      setValue(
        "businessRegistrationNumber",
        seller.businessRegistrationNumber?.replace(/(\d{3})(\d{2})(\d{5})/, "$1-$2-$3")
      );
      setValue("phone", seller.phone?.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3"));
      setValue("address", seller.address);
      setValue("addressDetail", seller.addressDetail);
      setValue("companyDetail", seller.companyInfo);

      // 이메일 분리
      if (seller.sellerEmail) {
        const [emailId, domain] = seller.sellerEmail.split("@");
        setValue("emailId", emailId);
        setValue("sellerEmail", seller.sellerEmail);
        if (["naver.com", "daum.net", "gmail.com"].includes(domain)) {
          setSelectedDomain(domain);
        } else {
          setSelectedDomain("custom");
          setCustomDomain(domain);
        }
      }
    }
  }, [seller, setValue]);

  // 이메일 합치기
  const handleEmailChange = (field, value) => {
    const emailId = field === "id" ? value : watch("emailId");
    let domain = "";

    if (field === "domain") {
      domain =
        selectedDomain === "custom"
          ? value
          : value === "custom"
          ? customDomain
          : value;
    } else {
      domain = selectedDomain === "custom" ? customDomain : selectedDomain;
    }

    const fullEmail =
      emailId && domain ? `${emailId}@${domain}` : emailId ? `${emailId}@` : "";
    setValue("sellerEmail", fullEmail, { shouldValidate: true });
  };

  // 전화번호 하이픈
  const handlePhoneChange = (e) => {
    const raw = e.target.value.replace(/[^0-9]/g, "");
    let formatted = raw;
    if (raw.startsWith("02")) {
      if (raw.length <= 2) formatted = raw;
      else if (raw.length <= 6) formatted = `${raw.slice(0, 2)}-${raw.slice(2)}`;
      else formatted = `${raw.slice(0, 2)}-${raw.slice(2, 6)}-${raw.slice(6, 10)}`;
    } else {
      if (raw.length > 3 && raw.length <= 7) formatted = `${raw.slice(0, 3)}-${raw.slice(3)}`;
      else if (raw.length > 7)
        formatted = `${raw.slice(0, 3)}-${raw.slice(3, 7)}-${raw.slice(7, 11)}`;
    }
    setValue("phone", formatted, { shouldValidate: true });
  };

  // 사업자등록번호 하이픈
  const handleBusinessNumberChange = (e) => {
    const raw = e.target.value.replace(/[^0-9]/g, "");
    let formatted = raw;
    if (raw.length > 3 && raw.length <= 5) {
      formatted = `${raw.slice(0, 3)}-${raw.slice(3)}`;
    } else if (raw.length > 5) {
      formatted = `${raw.slice(0, 3)}-${raw.slice(3, 5)}-${raw.slice(5, 10)}`;
    }
    setValue("businessRegistrationNumber", formatted, { shouldValidate: true });
  };

  // 이메일 / 사업자번호 자동 체크
  const sellerEmail = watch("sellerEmail");
  const phoneValue = watch("phone");
  const businessValue = watch("businessRegistrationNumber");

  // 이메일 자동체크
  useEffect(() => {
    if (sellerEmail === seller?.sellerEmail) {
      setIsEmailChecked(true);
      setEmailMsg("기존 이메일 그대로입니다.");
    } else {
      setIsEmailChecked(false);
      setEmailMsg("");
    }
  }, [sellerEmail, seller]);

  // 전화번호 자동체크
  useEffect(() => {
    const formatted = seller?.phone?.replace(/-/g, "");
    const current = phoneValue?.replace(/-/g, "");
    if (formatted === current) {
      setIsPhoneChecked(true);
      setPhoneMsg("기존 전화번호 그대로입니다.");
    } else {
      setIsPhoneChecked(false);
      setPhoneMsg("");
    }
  }, [phoneValue, seller]);

  // 사업자등록번호 자동체크
  useEffect(() => {
    const formatted = seller?.businessRegistrationNumber?.replace(/-/g, "");
    const current = businessValue?.replace(/-/g, "");
    if (formatted === current) {
      setIsBusinessNumberChecked(true);
      setBusinessNumberMsg("기존 사업자등록번호 그대로입니다.");
    } else {
      setIsBusinessNumberChecked(false);
      setBusinessNumberMsg("");
    }
  }, [businessValue, seller]);

  // 중복확인 핸들러
  const handleCheckEmail = () => {
    const email = watch("sellerEmail");
    if (!email) return alert("이메일을 입력해주세요.");
    checkEmail.mutate(email);
  };
  const handleCheckBusinessNumber = () => {
    const number = watch("businessRegistrationNumber");
    if (!number) return alert("사업자등록번호를 입력해주세요.");
    checkBusinessNumber.mutate(number.replace(/-/g, ""));
  };

  // 주소 팝업
  const openDaumPostcode = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        setValue("address", data.address, { shouldValidate: true });
      },
    }).open();
  };

  // 수정 제출
  const onSubmit = (data) => {
    if (seller.sellerEmail !== data.sellerEmail && !isEmailChecked)
      return alert("이메일 중복확인을 해주세요.");
    if (seller.phone !== data.phone.replace(/-/g, "") && !isPhoneChecked)
      return alert("전화번호 중복확인을 해주세요.");
    if (
      seller.businessRegistrationNumber !==
        data.businessRegistrationNumber.replace(/-/g, "") &&
      !isBusinessNumberChecked
    )
      return alert("사업자등록번호 중복확인을 해주세요.");

    const updateData = {
      companyName: data.companyName,
      sellerEmail: data.sellerEmail,
      phone: data.phone.replace(/-/g, ""),
      businessRegistrationNumber: data.businessRegistrationNumber.replace(/-/g, ""),
      address: data.address,
      addressDetail: data.addressDetail,
      companyDetail: data.companyDetail,
    };

    // 새 비밀번호가 입력된 경우에만 추가
    if (data.password && data.password.trim() !== "") {
      updateData.password = data.password;
    }

    updateUserInfo.mutate({ buyerUid: sellerUid, data: updateData });
  };

  // 회원 탈퇴
  const handleWithdraw = () => setIsModalOpen(true);
  const handleConfirmWithdraw = (reason) => {
    withdrawUser.mutate(reason || "");
    setIsModalOpen(false);
  };

  return (
    <div className="my-info-container">
      <h2>업체 정보 관리</h2>
      <form onSubmit={handleSubmit(onSubmit)}>
        {/* 비밀번호 */}
        <h3>비밀번호 변경</h3>
        <div className="info-group">
          <label>새 비밀번호</label>
          <input type="password" {...register("password")} placeholder="변경할 경우 입력" />
          <p className="error">{errors.password?.message}</p>
        </div>
        <div className="info-group">
          <label>새 비밀번호 확인</label>
          <input type="password" {...register("confirmPassword")} />
          <p className="error">{errors.confirmPassword?.message}</p>
        </div>

        {/* 업체명 */}
        <h3>회원 정보</h3>
        <div className="info-group">
          <label>업체명</label>
          <input type="text" {...register("companyName")} />
          <p className="error">{errors.companyName?.message}</p>
        </div>

        {/* 이메일 */}
        <div className="info-group">
          <label>이메일</label>
          <div className="email-box">
            <input
              type="text"
              placeholder="이메일 아이디"
              {...register("emailId")}
              onChange={(e) => handleEmailChange("id", e.target.value)}
            />
            <span>@</span>
            <select
              value={selectedDomain}
              onChange={(e) => {
                const val = e.target.value;
                setSelectedDomain(val);
                handleEmailChange("domain", val);
              }}
            >
              <option value="" disabled>
                도메인 선택
              </option>
              <option value="naver.com">naver.com</option>
              <option value="daum.net">daum.net</option>
              <option value="gmail.com">gmail.com</option>
              <option value="custom">직접입력</option>
            </select>
            <button type="button" onClick={handleCheckEmail}>중복확인</button>
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
                handleEmailChange("domain", newDomain);
              }}
            />
          )}

          <p className="error">{errors.sellerEmail?.message}</p>
          {emailMsg ? (
            <p className={`ok ${isEmailChecked ? "active" : ""}`}>{emailMsg}</p>
          ) : (
            !isEmailChecked && <p className="ok">이메일 중복체크 필수</p>
          )}
        </div>

        {/* 사업자등록번호 */}
        <div className="info-group">
          <label>사업자등록번호</label>
          <div className="input-with-button">
            <input
              type="text"
              {...register("businessRegistrationNumber")}
              onChange={handleBusinessNumberChange}
              maxLength="12"
            />
            <button type="button" onClick={handleCheckBusinessNumber}>
              중복확인
            </button>
          </div>
          <p className="error">{errors.businessRegistrationNumber?.message}</p>
          {businessNumberMsg ? (
            <p className={`ok ${isBusinessNumberChecked ? "active" : ""}`}>
              {businessNumberMsg}
            </p>
          ) : (
            !isBusinessNumberChecked && <p className="ok">사업자등록번호 중복체크 필수</p>
          )}
        </div>

        {/* 전화번호 */}
        <div className="info-group">
          <label>전화번호</label>
          <div className="input-with-button">
            <input
              type="text"
              {...register("phone")}
              onChange={handlePhoneChange}
              maxLength="13"
            />
          </div>
          <p className="error">{errors.phone?.message}</p>
          {phoneMsg && <p className={`ok ${isPhoneChecked ? "active" : ""}`}>{phoneMsg}</p>}
        </div>

        {/* 주소 */}
        <div className="info-group">
          <label>주소</label>
          <div className="input-with-button">
            <input type="text" {...register("address")} placeholder="주소" />
            <button type="button" onClick={openDaumPostcode}>
              주소 검색
            </button>
          </div>
          <p className="error">{errors.address?.message}</p>
        </div>

        {/* 상세주소 */}
        <div className="info-group">
          <label>상세 주소</label>
          <input type="text" {...register("addressDetail")} />
        </div>

        {/* 업체 상세정보 */}
        <div className="info-group">
          <label>업체 상세 정보</label>
          <textarea {...register("companyDetail")} style={{height: "100px", textAlign: "start"}}/>
        </div>
        <div className="info-btn-box">
        <button className="update-btn" type="submit">
          정보 수정하기
        </button>
        <button className="withdraw-btn" type="button" onClick={handleWithdraw}>
          회원 탈퇴
        </button>
        </div>
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
