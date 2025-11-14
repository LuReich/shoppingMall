import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import "../../assets/css/Info.css";
import { authStore } from "../../store/authStore";
import { useRegister } from "../../hooks/useRegister";
import { useNavigate } from "react-router";
import WithdrawModal from "../../components/common/WithdrawModal";


const schema = yup.object().shape({
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
  birth: yup.string().required("생년월일을 입력해주세요."),
  gender: yup.string().required("성별을 선택해주세요."),
});

function BuyerInfo() {

  const navigate = useNavigate();
  const user = authStore(state => state.user?.content);
  const [isModalOpen, setIsModalOpen] = useState(false);
 

  console.log("사용자 기본 정보", user);

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
        updateUserInfo,
        withdrawUser,
        emailMsg,
        phoneMsg,
        isEmailChecked,
        isPhoneChecked,
        setEmailMsg,
        setPhoneMsg,
        setIsEmailChecked,
        setIsPhoneChecked,
        
    } = useRegister("buyer");
  

  const [selectedDomain, setSelectedDomain] = useState("");
  const [customDomain, setCustomDomain] = useState("");
  const [reason, setReason] = useState(""); //탈퇴 사유

  // 내 정보 로딩 시 기본값 세팅
  useEffect(() => {
    if (user) {
        setValue("nickname", user.nickname);

        if (user.buyerEmail) {
            const [emailId, domain] = user.buyerEmail.split("@");
            setValue("emailId", emailId);
            setValue("buyerEmail", user.buyerEmail);

        if (["naver.com", "daum.net", "gmail.com"].includes(domain)) {
            setSelectedDomain(domain);
        } else {
            setSelectedDomain("custom");
            setCustomDomain(domain);
            }
        }

        setValue("phone", user.phone?.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3"));
        setValue("address", user.address);
        setValue("addressDetail", user.addressDetail);
        setValue("birth", user.birth);
        setValue("gender", user.gender);
    }
  }, [user, setValue]);


  // 전화번호 자동 하이픈
  const handlePhoneChange = (e) => {
    const rawValue = e.target.value?.replace(/[^0-9]/g, "");
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

   // 이메일을 수정하는 순간, 다시 중복체크가 필요하도록 초기화
  const buyerEmail = watch("buyerEmail");

  useEffect(() => {
    // 기존 이메일과 동일하면 자동 통과
    if (buyerEmail === user?.buyerEmail) {
      setIsEmailChecked(true);
      setEmailMsg("기존 이메일 그대로입니다.");
    } else {
      setIsEmailChecked(false);
      setEmailMsg("");
    }
}, [buyerEmail, user]);


  // 전화번호를 수정하는 순간, 다시 중복체크가 필요하도록 초기화
  const phoneValue = watch("phone");

  useEffect(() => {
    const formattedUserPhone = user?.phone?.replace(/-/g, "");
    const currentInput = phoneValue?.replace(/-/g, "");

    // 기존 번호와 동일하면 자동 통과
    if (formattedUserPhone === currentInput) {
      setIsPhoneChecked(true);
      setPhoneMsg("기존 전화번호 그대로입니다.");
    } else {
      setIsPhoneChecked(false);
      setPhoneMsg("");
    }
  }, [phoneValue, user]);


  // 이메일 중복확인
  const handleCheckEmail = () => {
    const email = watch("buyerEmail");
    if (!email) return alert("이메일을 입력해주세요.");
    checkEmail.mutate(email);
  };

  // 전화번호 중복확인
  const handleCheckPhone = () => {
    const phone = watch("phone");
    if (!phone) return alert("전화번호를 입력해주세요.");
    checkPhone.mutate(phone?.replace(/-/g, ""));
  };



  // 주소 검색
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

        setValue("address", fullAddress, { shouldValidate: true });
      },
    }).open();
  };

  // 수정 제출
  const onSubmit = (data) => {
    if(!isEmailChecked || !isPhoneChecked){
       alert("이메일과 전화번호 중복체크를 진행하세요.")
       return;
    };
    const buyerUid = user?.buyerUid;
    const newData = {
      nickname: data.nickname,
      buyerEmail: data.buyerEmail,
      phone: data.phone?.replace(/-/g, ""),
      address: data.address,
      addressDetail: data.addressDetail,
      birth: data.birth,
      gender: data.gender.toUpperCase(),
    };
    console.log("수정 데이터:", newData);
    updateUserInfo.mutate({ buyerUid, data: newData });
  };

  //회원 탈퇴
  // 회원 탈퇴 모달 열기
  const handleWithdraw = () => {
    setIsModalOpen(true);
  };
  // 모달에서 '탈퇴하기' 버튼 클릭 시 실행
  const handleConfirmWithdraw = (reason) => {
    withdrawUser.mutate(reason); // 입력이 없으면 빈 문자열로 전달
    setIsModalOpen(false);
  };

useEffect(() => {
  console.log("전번 중복",isPhoneChecked);
}, [isPhoneChecked]);

    


  return (
    <div className="my-info-container">
      <h2>회원 정보 수정</h2>
      <form onSubmit={handleSubmit(onSubmit)}>
        <h3>회원 정보</h3>
        <div className="info-group">
          <label>아이디</label>
          <input type="text" placeholder="아이디" value={user?.buyerId} readOnly disabled/>
        </div>
          <div className="info-group">
          <label>UID</label>
          <input type="text" placeholder="아이디" value={user?.buyerUid} readOnly disabled/>
        </div>
        {/* 닉네임 */}
        <div className="info-group">
          <label>닉네임</label>
          <input type="text" {...register("nickname")} placeholder="닉네임" />
          <p className="error">{errors.nickname?.message}</p>
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
        <h3 className="addr-b">배송지</h3>
        <div className="form-group">
          <label>주소</label>
          <div className="input-with-button">
            <input type="text" {...register("address")} placeholder="주소" readOnly/>
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

        <div className="info-btn-box">
          <button className="update-btn" type="submit">정보 수정하기</button>
          <button className="withdraw-btn" type="button" onClick={handleWithdraw}>회원 탈퇴</button>
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

export default BuyerInfo;
