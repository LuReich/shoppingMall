import React from 'react';
import SideMenuBar from '../../components/mypage/SideMenuBar';
import { Outlet } from 'react-router';
import '../../assets/css/MyPageLayout.css';

function MyPageLayout(props) {
    return (
        <div className='mypage-layout'>
            <div className='side-menu-bar-wrapper'>
                <SideMenuBar/>
            </div>
            <div className='mypage-content'>
                <Outlet/>
            </div>
        </div>
    );
}

export default MyPageLayout;