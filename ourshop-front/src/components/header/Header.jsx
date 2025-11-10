import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../../assets/css/Header.css'
import { useCategory } from '../../hooks/useCategory';
import { authStore } from '../../store/authStore';
import styles from '../../assets/css/Button.module.css';
import CategoryDropDown from './CategoryDropDown';
import MyPageDropDown from './MyPageDropDown';
import { RiCustomerService2Fill } from "react-icons/ri";
import CSDropDown from './CSDropDown';


function Header(props) {

    const navigate = useNavigate();
    //카테고리 리스트 가져오기
    const { getCategoryList } = useCategory();
    const { data: categories, isLoading, isError } = getCategoryList(); 

    const {isLogin, setLogout, user, role} = authStore(state => state);
    console.log(isLogin);

    const [isMenuOpen, setIsMenuOpen] = useState(false); //카테고리 메뉴 드롭다운
    const [isCsOpen, setIsCsOpen] = useState(false); //고객센터 드롭다운
    const [isProfileOpen, setIsProfileOpen] = useState(false); //마이페이지 드롭다운
    const [searchInput, setSearchInput] = useState(""); //검색어
    //const [keywords, setKeywords] = useState("")

    if (isLoading) return <p>카테고리를 불러오는 중입니다...</p>;
    if (isError) return <p>카테고리를 불러오는 중 오류가 발생했습니다.</p>;
    if (!categories || categories.length === 0) return <p>등록된 카테고리가 없습니다.</p>;
     
    console.log("카테고리", categories);
    console.log("롤", role);

    //로그아웃
    /*const handleLogout = () => {
        setLogout();
        navigate("/");
    }*/
   
    //카테고리 드롭다운 open
    const handleMenuEnter = () => {
        setIsMenuOpen(true);
        console.log(isMenuOpen); //주석 처리 예정
    }

    //카테고리 드롭다운 close
    const handleMenuLeave = () => {
        setIsMenuOpen(false)
    }

    //고객센터 드롭다운 open
    const handleCsEnter = () => {
        setIsCsOpen(true);
    }

    //고객센터 드롭다운 close
    const handleCsLeave = () => {
        setIsCsOpen(false);
    }

    //마이페이지 드롭다운 open
    const handleProfileEnter = () => {
        setIsProfileOpen(true);
    }

    //마이페이지 드롭다운 close
    const handleProfileLeave = () => {
        setIsProfileOpen(false);
    }


     //상품명 검색
    const searchBtn = (e) => {
        e.preventDefault();
        if (!searchInput.trim()){
            navigate("/products");
        }; // 빈값 방지
        navigate(`/products?productName=${searchInput}`);
        //setSearchInput("");
    };

 

    return (
        <header className="header">
            <div className="header-content">
                {/*isLogin && user && <p className='logout-btn' onClick={handleLogout}>로그아웃</p>*/}
                <div className='nav-total-box'>
                <div className="header-left">
                    <Link to="/" className="logo">우리샵</Link>
                    <nav className="nav">
                        <div
                            className="category-menu-container"
                            onMouseEnter={handleMenuEnter}
                            onMouseLeave={handleMenuLeave}
                        >
                            <Link to="/products" className="category-button">카테고리</Link>
                            {
                                isMenuOpen && <CategoryDropDown categories={categories} />
                            }
                        </div>
                        <Link to="/shop/list">신규 업체</Link>
                        <Link to="/best">베스트</Link>
                    </nav>
                </div>
                <div className="header-right">
                    <form className='input-box' onSubmit={searchBtn}>
                        <input type="text" placeholder="검색" 
                            className="search-bar" 
                            value={searchInput} 
                            onChange={(e) => setSearchInput(e.target.value)}
                        />
                        <button type='submit' className='search-button'>🔍</button>
                    </form>
                    <div className='right-box'>
                        {
                            isLogin && user? 
                                <>
                                <p className='nick-name'>
                                    {role === "BUYER"? 
                                        user.content.nickname 
                                        : 
                                        role=== "SELLER"? 
                                            user.content.companyName : user.content.adminName} 님
                                </p>
                                    {
                                        role !== "ADMIN" &&
                                        <div className='cs-cont'
                                            onMouseEnter={handleCsEnter}
                                            onMouseLeave={handleCsLeave}>
                                        <RiCustomerService2Fill  className="icon-button" 
                                            onMouseEnter={handleCsEnter}
                                        />
                                        {
                                            isCsOpen && <CSDropDown/>
                                        }       
                                        </div>  
                                    }
                                    {role === "ADMIN" ? 
                                        <button type='button' className="icon-button" onClick={()=> navigate("/admin")}>⚙️</button>
                                        :
                                        role === "BUYER" &&
                                            <button type='button' className="icon-button"  onClick={()=>navigate("/cart")}>🛒</button>
                                    }
                                <div className='mypage-cont'
                                    onMouseEnter={handleProfileEnter}
                                    onMouseLeave={handleProfileLeave}>
                                  <button type='button' 
                                    className="icon-button"
                                    onMouseEnter={handleProfileEnter}
                                  >👤</button>
                                {
                                   isProfileOpen && <MyPageDropDown/>
                                }
                                </div>
                                </>
                                :
                                <>
                                    <RiCustomerService2Fill  className="icon-button" onClick={() => navigate("/faq")}/> 
                                    <Link to="/login" className={styles.commonBtn}>로그인</Link>
                                </>
                        }
                    
                    </div>
                </div>
                </div>
            </div>
        </header>
    );
}

export default Header;