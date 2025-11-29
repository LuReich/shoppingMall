import React from 'react';
import '../../assets/css/Footer.css';
import { authStore } from '../../store/authStore';
import { useNavigate } from 'react-router';

function Footer(props) {
    const navigate = useNavigate();
    const role = authStore(state => state.role);
    const handleShippingDetail = () => {
       if(role === "BUYER"){
        navigate("/buyer/mypage/shipping" );   
       }else if(role === "SELLER"){
        navigate("/seller/mypage/shipping");
       }else{
        alert("로그인이 필요한 서비스입니다.");
        navigate("/login");
       }
    }

    return (
        <footer className="footer">
            <div className="footer-content">
                <div className="footer-left">
                    <h3 className="footer-logo">우리샵</h3>
                    <p>언제나 신속하고 정확하게 배송해 드립니다.</p>
                </div>
                <div className="footer-right">
                    <div className="footer-links">
                        <div>
                            <h4>고객센터</h4>
                            <ul>
                                <li><a href="/qna">1:1 문의</a></li>
                                <li><a href="/faq">FAQ</a></li>
                                <li><a onClick={handleShippingDetail} >배송 조회</a></li>
                            </ul>
                        </div>
                        <div>
                            <h4>이용안내</h4>
                            <ul>
                                <li><a href="/privacy">개인정보처리방침</a></li>
                                <li><a href="/service">이용약관</a></li>
                                <li><a href="/refund">환불정책</a></li>
                            </ul>
                        </div>
                    </div>
                    <div className="footer-sns">
                        <h4>SNS</h4>
                        <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">📷</a>
                        <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">🇫</a>
                    </div>
                </div>
            </div>
            <div className="footer-bottom">
                <p>© 2025 우리샵. All rights reserved.</p>
            </div>
        </footer>
    );
}

export default Footer;