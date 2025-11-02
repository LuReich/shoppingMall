import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import "../../assets/css/Register.css";
import { useRegister } from "../../hooks/useRegister";

const schema = yup.object().shape({
  buyerId: yup
    .string()
    .required("아이디를 입력해주세요.")
    .matches(/^[A-Za-z0-9]+$/, "영문과 숫자만 입력 가능합니다.")
    .min(6, "아이디는 6자리 이상이어야 합니다."),
  password: yup
    .string()
    .required("비밀번호를 입력해주세요.")
    .matches(/^[A-Za-z0-9]+$/, "비밀번호는 영문과 숫자만 가능합니다."),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref("password"), null], "비밀번호가 일치하지 않습니다."),
  nickname: yup.string().required("닉네임을 입력해주세요."),
  buyerEmail: yup
    .string()
    .email("올바른 이메일 형식이 아닙니다.")
    .required("이메일을 입력해주세요."),
  phone: yup
    .string()
    .required("전화번호를 입력해주세요.")
    .matches(/^[0-9-]+$/, "숫자만 입력해주세요."),
  address: yup.string().required("주소를 입력해주세요."),
  addressDetail: yup.string(),
  //zipcode: yup.string().required("우편번호를 입력해주세요."),
  birth: yup.string().required("생년월일을 입력해주세요."),
  gender: yup.string().required("성별을 선택해주세요."),
});

function BuyerRegister() {
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
    checkId,
    checkEmail,
    checkPhone,
    registerUser,
    idMsg,
    emailMsg,
    phoneMsg,
    isIdChecked,
    isEmailChecked,
    isPhoneChecked,
    setIdMsg,
    setEmailMsg,
    setPhoneMsg,
    setIsIdChecked,
    setIsEmailChecked,
    setIsPhoneChecked,
  } = useRegister("buyer");

  const [selectedDomain, setSelectedDomain] = useState("");
  const [customDomain, setCustomDomain] = useState("");
 

  // 전화번호 자동 하이픈
  const handlePhoneChange = (e) => {
    const rawValue = e.target.value.replace(/[^0-9]/g, "");
    let formatted = rawValue;
    if (rawValue.length > 3 && rawValue.length <= 7) {
      formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3)}`;
    } else if (rawValue.length > 7) {
      formatted = `${rawValue.slice(0, 3)}-${rawValue.slice(3, 7)}-${rawValue.slice(7, 11)}`;
    }
    setValue("phone", formatted, { shouldValidate: true });
  };

  // 이메일 합치기
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
        setValue("buyerEmail", fullEmail, { shouldValidate: true });
  };

  //아이디 중복확인
  const handleCheckId = () => {
    const buyerId = watch("buyerId");
    if (!buyerId) {
      alert("아이디를 입력해주세요.");
      return;
    }
    console.log("아이디", buyerId);
    checkId.mutate(buyerId);
  };

  // 이메일 중복확인
  const handleCheckEmail = () => {
    const email = watch("buyerEmail");
    if (!email) {
      alert("이메일을 입력해주세요.");
      return;
    }
    console.log("이메일", email);
    checkEmail.mutate(email);
  };

  // 전화번호 중복확인
  const handleCheckPhone = () => {
    const phone = watch("phone");
    if (!phone) {
      alert("전화번호를 입력해주세요.");
      return;
    }
  
    const newPhone = phone.replace(/-/g, "");
    console.log("전화번호", newPhone);
    checkPhone.mutate(newPhone);
  };

  //아이디 수정하는 순간, 다시 중복체크 필요
    const id = watch("buyerId");
    useEffect(() => {
      setIsIdChecked(false);
      setIdMsg("");
    }, [id]);

  // 이메일을 수정하는 순간, 다시 중복체크가 필요하도록 초기화
    const buyerEmail = watch("buyerEmail");
    useEffect(() => {
      setIsEmailChecked(false);
      setEmailMsg("");
    }, [buyerEmail]);
  
    // 전화번호를 수정하는 순간, 다시 중복체크가 필요하도록 초기화
    const phoneValue = watch("phone");
    useEffect(() => {
      setIsPhoneChecked(false);
      setPhoneMsg("");
    }, [phoneValue]);

  // 회원가입 제출
  const onSubmit = (data) => {
    console.log("폼 원본 데이터:", data);

    // 백엔드 DTO 맞게 변환
    const newData = {
      buyerId: data.buyerId,
      password: data.password,
      nickname: data.nickname,
      buyerEmail: data.buyerEmail,
      phone: data.phone.replace(/-/g, ""), // 하이픈 제거
      address: data.address,
      addressDetail: data.addressDetail,
      birth: data.birth,
      gender: data.gender.toUpperCase(),
    };

    console.log("백엔드 전송 데이터:", newData);

    if(!isEmailChecked || !isPhoneChecked){
      alert("이메일과 전화번호 중복체크를 진행하세요.");
      return;
    }
    registerUser.mutate(newData);
  };

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
      <h2>구매자 회원가입</h2>
      <form onSubmit={handleSubmit(onSubmit)}>
        {/* 아이디 */}
        <div className="form-group">
          <label>아이디</label>
          <div className="input-with-button">
          <input type="text" {...register("buyerId")} placeholder="아이디" />
            <button type="button" onClick={handleCheckId}>
              아이디 중복확인
            </button>
          </div>
          <p className="error">{errors.buyerId?.message}</p>
          {idMsg ? (
                <p className={`ok ${isIdChecked ? "active" : ""}`}>{idMsg}</p>
              ) : (
                !isIdChecked && <p className="ok">이메일 중복체크 필수</p>
              )
          }
        </div>

        {/* 비밀번호 */}
        <div className="form-group">
          <label>비밀번호</label>
          <input type="password" {...register("password")} placeholder="비밀번호" />
          <p className="error">{errors.password?.message}</p>
        </div>

        {/* 비밀번호 확인 */}
        <div className="form-group">
          <label>비밀번호 확인</label>
          <input
            type="password"
            {...register("confirmPassword")}
            placeholder="입력한 비밀번호와 동일한 비밀번호로 입력하세요."
          />
          <p className="error">{errors.confirmPassword?.message}</p>
        </div>

        {/* 닉네임 */}
        <div className="form-group">
          <label>닉네임</label>
          <input type="text" {...register("nickname")} placeholder="닉네임" />
          <p className="error">{errors.nickname?.message}</p>
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
          <p className="error">{errors.buyerEmail?.message}</p>
          {emailMsg ? (
                <p className={`ok ${isEmailChecked ? "active" : ""}`}>{emailMsg}</p>
              ) : (
                !isEmailChecked && <p className="ok">이메일 중복체크 필수</p>
              )
          }
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
              placeholder="휴대폰 번호"
            />
            <button type="button" onClick={handleCheckPhone}>
              전화번호 중복확인
            </button>
          </div>
          <p className="error">{errors.phone?.message}</p>
          {phoneMsg ? (
                <p className={`ok ${isPhoneChecked ? "active" : ""}`}>{phoneMsg}</p>
              ) : (
                !isPhoneChecked && <p className="ok">전화번호 중복체크 필수</p>
              )
          }
        </div>

        {/* 생년월일 */}
        <div className="form-group">
          <label>생년월일</label>
          <input type="date" {...register("birth")} />
          <p className="error">{errors.birth?.message}</p>
        </div>

        {/* 성별 */}
        <div className="form-group">
          <label>성별</label>
          <select {...register("gender")}>
            <option value="">성별 선택</option>
            <option value="MALE">남성</option>
            <option value="FEMALE">여성</option>
          </select>
          <p className="error">{errors.gender?.message}</p>
        </div>

        {/* 주소 */}
        <div className="form-group">
          <label>주소</label>
          <div className="input-with-button">
            {/*<input type="text" {...register("zipcode")} placeholder="우편번호" readOnly />*/}
            <input type="text" {...register("address")} placeholder="주소" />
            <button type="button" onClick={openDaumPostcode}>
              주소 검색
            </button>
          </div>
          <p className="error">{errors.address?.message}</p>
        </div>

        {/* 상세 주소 */}
        <div className="form-group">
          <label>상세 주소</label>
          <input type="text" {...register("addressDetail")} placeholder="상세 주소" />
        </div>

        <button type="submit" className="register-btn">가입하기</button>
      </form>
    </div>
  );
}

export default BuyerRegister;
