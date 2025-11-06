import React, { useState } from "react";
import { useLogin } from "../../hooks/useLogin";
import "../../assets/css/Login.css";
import { useNavigate } from "react-router";

function Login() {
  const navigate = useNavigate();
  const [selectedMode, setSelectedMode] = useState("buyer");
  const [form, setForm] = useState({ username: "", password: "" });

  const role = ["buyer", "seller", "admin"];
  const { mutate: loginMutate, isPending } = useLogin();

  const handleModeChange = (mode) => setSelectedMode(mode);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleLogin = (e) => {
    e.preventDefault(); // 폼 제출 시 페이지 새로고침 방지
    if (!form.username || !form.password) {
      alert("아이디와 비밀번호를 입력하세요.");
      return;
    }
    loginMutate({ mode: selectedMode, credentials: form }); 
  };

  // 회원가입 버튼 클릭 시 이동
  const registerBtn = () => {
    if (selectedMode === "buyer") {
      navigate("/register/buyer");
    }
    if (selectedMode === "seller") {
      navigate("/register/seller");
    }
  };

  // 모드별 텍스트 동적 설정
  const getTitle = () => {
    switch (selectedMode) {
      case "buyer":
        return "구매자 로그인";
      case "seller":
        return "판매자 로그인";
      case "admin":
        return "관리자 로그인";
      default:
        return "로그인";
    }
  };

  const getRegisterText = () => {
    switch (selectedMode) {
      case "buyer":
        return "구매자 회원가입";
      case "seller":
        return "판매자 회원가입";
      default:
        return "";
    }
  };

  return (
    <div className="login-container">
      <h1>{getTitle()}</h1>

      {/* 모드 선택 */}
      <div className="mode-selector">
        {role.map((mode) => (
          <div
            key={mode}
            className={`mode-box ${selectedMode === mode ? "active" : ""}`}
            onClick={() => handleModeChange(mode)}
          >
            <div className="icon">
              {mode === "buyer" ? "👤" : mode === "seller" ? "🛒" : "⚙️"}
            </div>
            <p>
              {mode === "buyer"
                ? "구매자 모드"
                : mode === "seller"
                ? "판매자 모드"
                : "관리자 모드"}
            </p>
          </div>
        ))}
      </div>

      {/* 로그인 폼 */}
      <form className="login-form" onSubmit={handleLogin}>
        <input
          type="text"
          name="username"
          placeholder={
            selectedMode === "admin"
              ? "관리자 아이디를 입력하세요"
              : "아이디를 입력하세요"
          }
          value={form.username}
          onChange={handleChange}
        />
        <input
          type="password"
          name="password"
          placeholder="비밀번호를 입력하세요"
          value={form.password}
          onChange={handleChange}
        />

        <div className="login-actions">
          <button
            type="submit"
            className="login-btn"
            disabled={isPending}
          >
            {isPending ? "로그인 중..." : getTitle()}
          </button>

          <div className="auth-btn-box">
            <button className="find-btn">아이디 찾기</button>
            <p>|</p>
            <button className="find-btn">비밀번호 찾기</button>

            {/* 관리자 모드에서는 회원가입 버튼 안보이게 */}
            {selectedMode !== "admin" && (
              <>
                <p>|</p>
                <button className="find-btn" onClick={registerBtn}>
                  {getRegisterText()}
                </button>
              </>
            )}
          </div>
        </div>
      </form>
    </div>
  );
}

export default Login;
