import React, { useState } from 'react';
import '../../assets/css/Login.css'

function Login(props) {
    const [selectedMode, setSelectedMode] = useState("seller"); // 기본값: 판매자

    const handleModeChange = (mode) => {
        setSelectedMode(mode);
    };

    return (
        <div className="login-container">
            <h1>로그인</h1>

            {/* 모드 선택 */}
            <div className="mode-selector">
                <div
                    className={`mode-box ${selectedMode === "buyer" ? "active" : ""}`}
                    onClick={() => handleModeChange("buyer")}
                >
                    <div className="icon">👤</div>
                    <p>구매자 모드</p>
                </div>
                <div
                    className={`mode-box ${selectedMode === "seller" ? "active" : ""}`}
                    onClick={() => handleModeChange("seller")}
                >
                    <div className="icon">🛒</div>
                    <p>판매자 모드</p>
                </div>
        <div
          className={`mode-box ${selectedMode === "admin" ? "active" : ""}`}
          onClick={() => handleModeChange("admin")}
        >
          <div className="icon">⚙️</div>
          <p>관리자 모드</p>
        </div>
      </div>

      {/* 로그인 폼 */}
      <div className="login-form">
        <input type="text" placeholder="아이디를 입력하세요" />
        <input type="password" placeholder="비밀번호를 입력하세요" />
        <div className="login-actions">
          {/*<button className="find-btn">아이디/비밀번호 찾기</button>*/}
          <button className="login-btn">로그인</button>
          <div className='auth-btn-box'>
                <button className="find-btn">아이디 찾기</button>
                <p>|</p>
                <button className="find-btn">비밀번호 찾기</button>
                <p>|</p>
                <button className="find-btn">회원 가입</button>
          </div>
        </div>
      </div>
    </div>
    );
}

export default Login;