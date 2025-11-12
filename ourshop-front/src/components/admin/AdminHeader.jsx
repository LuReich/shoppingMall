import React from 'react';
import { Link } from 'react-router';
import '../../assets/css/AdminHeader.css';

function AdminHeader(props) {
    return (
        <div className='admin-header-container'>
            <div className='admin-page-logo'>
                <h2><p style={{color: "#2193b0"}}>우리샵</p> 관리자 페이지</h2>
            </div>
            <nav className='admin-nav'>
                <Link className='link' to='/admin'>회원 관리</Link>
                <Link className='link' to='/admin/products'>상품 관리</Link>
                <Link className='link' to='/admin/faq'>FAQ 관리</Link>
                <Link className='link' to='/admin/qna'>문의 관리</Link>
            </nav>
            <Link className='link-go-back' to='/'>나가기</Link>
        </div>
    );
}

export default AdminHeader;