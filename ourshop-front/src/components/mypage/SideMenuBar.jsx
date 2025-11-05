import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { authStore } from '../../store/authStore';
import '../../assets/css/SideMenuBar.css';

function SideMenuBar(props) {

    const navigate = useNavigate();

    const {setLogout, role} = authStore(state => state);

    //로그아웃
    const handleLogout = () => {
        setLogout();
        alert("로그아웃 되었습니다.");
        navigate("/");
    }


    return (
        <>
            <h2 className='side-menu-title'>마이페이지</h2>
            <div className='side-menu-separator'>
            {
                role === "BUYER"?
                <>
                    <NavLink className='side-link-btn' to='/buyer/mypage/info'>회원 정보 관리</NavLink>
                    <NavLink className='side-link-btn' to='/buyer/mypage/shipping'>주문/배송 조회</NavLink>
                    <NavLink className='side-link-btn' to='/buyer/mypage/review'>리뷰 관리</NavLink>
                    <NavLink className='side-link-btn' to='/buyer/mypage/likes'>좋아요한 상품</NavLink>
                    <p className='side-logout-btn' onClick={handleLogout}>로그아웃</p>
                </>
                :
                role === "SELLER"?
                    <>
                        <NavLink className='side-link-btn' to='/seller/mypage/info'>업체 정보 관리</NavLink>
                        <NavLink className='side-link-btn' to='/seller/mypage/products'>업체 상품 관리</NavLink>
                        <NavLink className='side-link-btn' to='/seller/mypage/shipping'>판매 내역 관리</NavLink>
                        <p className='side-logout-btn' onClick={handleLogout}>로그아웃</p>
                    </>
                    :
                    <>
                        <Link className='side-link-btn' to='#'>관리자 정보 관리</Link>
                        <p className='side-logout-btn' onClick={handleLogout}>로그아웃</p>
                    </>
            } 
            </div> 
        </>
    );
}

export default SideMenuBar;