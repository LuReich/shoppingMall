import { useMutation, useQueryClient } from "@tanstack/react-query";
import { registerAPI } from "../api/RegisterAPI";
import { authStore } from "../store/authStore";
import { useNavigate } from "react-router";
import { useState } from "react";

export const useRegister = (mode) => {
  const qc = useQueryClient();
  const { updateUser, setLogout } = authStore();
  const navigate = useNavigate();

  // 메시지 상태 
  const [idMsg, setIdMsg] = useState("");
  const [emailMsg, setEmailMsg] = useState("");
  const [phoneMsg, setPhoneMsg] = useState("");
  const [businessNumberMsg, setBusinessNumberMsg] = useState("");

  const [isIdChecked, setIsIdChecked] = useState(false);
  const [isEmailChecked, setIsEmailChecked] = useState(false);
  const [isPhoneChecked, setIsPhoneChecked] = useState(false);
  const [isBusinessNumberChecked, setIsBusinessNumberChecked] = useState(false);
// 아이디 중복확인
const checkId = useMutation({
  // ✅ mutationFn에서 전달받는 변수들을 객체로 구조분해
  mutationFn: ({ id, isAdmin = false, uid = null }) =>
    registerAPI.checkId(mode, id, isAdmin, uid),

  onSuccess: (res) => {
    const msg = res?.content;
    console.log("아이디 중복확인 성공:", res);
    setIdMsg(msg);
    setIsIdChecked(true);
  },

  onError: (err) => {
    const msg = err.response?.data?.content;
    console.error("아이디 중복확인 실패:", err);
    setIdMsg(msg);
    setIsIdChecked(false);
  },
});


  // 이메일 중복확인
  const checkEmail = useMutation({
    mutationFn: (email) => registerAPI.checkEmail(mode, email),
    onSuccess: (res) => {
      const msg = res.content;
      setEmailMsg(msg);
      setIsEmailChecked(true);
    },
    onError: (err) => {
      const msg = err.response?.data?.content;
      console.error("이메일 중복확인 실패:", err);
      setEmailMsg(msg);
      setIsEmailChecked(false);
    },
  });

  // 전화번호 중복확인
  const checkPhone = useMutation({
    mutationFn: (phone) => registerAPI.checkPhone(mode, phone),
    onSuccess: (res) => {
      const msg = res.content;
      setPhoneMsg(msg);
      setIsPhoneChecked(true);
    },
    onError: (err) => {
      const msg =  err.response?.data?.content;
      console.error("전화번호 중복확인 실패:", err);
      setPhoneMsg(msg);
      setIsPhoneChecked(false); 
    },
  });

  const checkBusinessNumber = useMutation({
    mutationFn: (businessNumber) => registerAPI.checkBusinessNumber(mode, businessNumber),
    onSuccess: (res) => {
      const msg = res.content;
      setBusinessNumberMsg(msg);
      setIsBusinessNumberChecked(true);
    },
    onError: (err) => {
      const msg = err.response?.data?.content;
      console.error("사업자등록번호 중복확인 실패:", err);
      setBusinessNumberMsg(msg);
      setIsBusinessNumberChecked(false);
    },
  });

  // 회원가입
  const registerUser = useMutation({
    mutationFn: (formData) => registerAPI.register(mode, formData),
    onSuccess: (res) => {
      setEmailMsg("");
      setPhoneMsg("");
      console.log("회원가입 성공:", res);
      alert("회원가입이 완료되었습니다!");
      navigate("/");
    },
    onError: (err) => {
      console.error("회원가입 실패:", err);
      alert(err.response.data.content||"회원가입 중 오류가 발생했습니다.");
    },
  });

  // 회원정보 수정
  const updateUserInfo = useMutation({
    mutationFn: ({ buyerUid, data }) => registerAPI.update(mode, buyerUid, data),
    onSuccess: (res, variables) => {
      qc.invalidateQueries(["myInfo"]);
      console.log(res);
      updateUser(variables.data);
      alert(res.message || "회원정보가 수정되었습니다!");
      navigate('/');
    },
    onError: (err) => {
      console.error("회원정보 수정 실패:", err);
      alert(err.response.data.content||"회원정보 수정 중 오류가 발생했습니다.");
    },
  });

  //회원 탈퇴
  const withdrawUser = useMutation({
    mutationFn: (withdrawalReason) =>
      registerAPI.withdraw(mode, withdrawalReason),
    onSuccess: (res) => {
      alert(res.content || "회원 탈퇴가 완료되었습니다.");
      setLogout(); // 로그인 정보 초기화 (Zustand store)
      qc.clear();  // React Query 캐시 초기화
      navigate("/"); // 메인 페이지로 이동
    },

    onError: (err) => {
      console.error("회원탈퇴 실패:", err);
      alert(err.response?.data?.content || "회원탈퇴 중 오류가 발생했습니다.");
    },
  });

  // 메시지와 상태를 함께 반환
  return {
    checkId,
    checkEmail,
    checkPhone,
    checkBusinessNumber,
    registerUser,
    updateUserInfo,
    withdrawUser,
    idMsg,
    emailMsg,
    phoneMsg,
    businessNumberMsg,
    isIdChecked,
    isEmailChecked,
    isPhoneChecked,
    isBusinessNumberChecked,
    setIdMsg,
    setEmailMsg,
    setPhoneMsg,
    setBusinessNumberMsg,
    setIsIdChecked,
    setIsEmailChecked,
    setIsPhoneChecked,
    setIsBusinessNumberChecked
  };
};
