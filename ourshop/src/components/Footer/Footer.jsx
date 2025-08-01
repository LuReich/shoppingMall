import React from 'react';
import '../../assets/css/Footer.css';

const Footer = () => {
    return (
        <footer className="footer">
            <div className="footer-content">
                <div className="footer-left">
                    <h3 className="footer-logo">우리샵</h3>
                    <p>언제나 집밥 같은 마음으로 배송해 드립니다.</p>
                </div>
                <div className="footer-right">
                    <div className="footer-links">
                        <div>
                            <h4>고객센터</h4>
                            <ul>
                                <li><a href="#/">1:1 문의</a></li>
                                <li><a href="#/">FAQ</a></li>
                                <li><a href="#/">배송 조회</a></li>
                            </ul>
                        </div>
                        <div>
                            <h4>이용안내</h4>
                            <ul>
                                <li><a href="#/">개인정보처리방침</a></li>
                                <li><a href="#/">이용약관</a></li>
                                <li><a href="#/">환불정책</a></li>
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
};

export default Footer;