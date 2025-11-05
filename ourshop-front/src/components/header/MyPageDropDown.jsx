import React from 'react';
import "../../assets/css/MyPageDropDown.css"
import { Link, useNavigate } from 'react-router';
import { authStore } from '../../store/authStore';
function MyPageDropDown(props) {
    
    const navigate = useNavigate();

    const {setLogout, role} = authStore(state => state);

    //로그아웃
    const handleLogout = () => {
        setLogout();
        navigate("/");
    }

    return (
        <div className='mypage-drop-down'>
            {
                role === "BUYER"?
                <>
                    <Link className='link-btn' to='/buyer/mypage/info'>회원 정보 관리</Link>
                    <Link className='link-btn' to='/buyer/mypage/shipping'>주문/배송 조회</Link>
                    <Link className='link-btn' to='/buyer/mypage/review'>리뷰 관리</Link>
                    <Link className='link-btn' to='/buyer/mypage/likes'>좋아요한 상품</Link>
                    <p className='link-btn' onClick={handleLogout}>로그아웃</p>
                </>
                :
                role === "SELLER"?
                    <>
                        <Link className='link-btn' to='/seller/mypage/info'>업체 정보 관리</Link>
                        <Link className='link-btn' to='/seller/mypage/products'>업체 상품 관리</Link>
                        <Link className='link-btn' to='/seller/mypage/shipping'>판매 내역 관리</Link>
                        <p className='link-btn' onClick={handleLogout}>로그아웃</p>
                    </>
                    :
                    <>
                        <Link className='link-btn' to='#'>관리자 정보 관리</Link>
                        <p className='link-btn' onClick={handleLogout}>로그아웃</p>
                    </>
            }  
        </div>
    );
}

export default MyPageDropDown;