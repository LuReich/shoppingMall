import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../../assets/css/Header.css'
import { useCategory } from '../../hooks/useCategory';

function Header(props) {

    //카테고리 리스트 가져오기
     const { getCategoryList } = useCategory();
    const { data: category, isLoading, isError } = getCategoryList(); 

    const [isMenuOpen, setIsMenuOpen] = useState(false);


    console.log("카테고리", category);

    //if (isLoading) return <p>카테고리를 불러오는 중입니다...</p>;
    //if (isError) return <p>카테고리를 불러오는 중 오류가 발생했습니다.</p>;
    //if (!category || category.length === 0) return <p>등록된 카테고리가 없습니다.</p>;

    
    
        //카테고리 드롭다운 open
    const handleMenuEnter = () => {
      
        
        setIsMenuOpen(true);
        console.log(isMenuOpen); //주석 처리 예정
    }

    //카테고리 드롭다운 close
    const handleMenuLeave = () => {
        setIsMenuOpen(false)
    }
    return (
        <header className="header">
            <div className="header-content">
                <div className="header-left">
                    <Link to="/" className="logo">우리샵</Link>
                    <nav className="nav">
                        <div
                            className="category-menu-container"
                            onMouseEnter={handleMenuEnter}
                            onMouseLeave={handleMenuLeave}
                        >
                            <button className="category-button">카테고리</button>
                            {/*{isMenuOpen && (
                                <div className="mega-menu">
                                   1차 메뉴
                                    <div className="menu-column">
                                        {categoriesData.map(l1 => (
                                            <Link
                                                to={`/products/${l1.id}`}
                                                key={l1.id}
                                                className={`menu-item l1 ${activePath.includes(l1.id) ? 'active' : ''}`}
                                                onMouseEnter={() => handleL1Enter(l1)}
                                                onClick={handleMenuLeave}
                                            >
                                                {l1.name}
                                            </Link>
                                        ))}
                                    </div>

                                    2차 메뉴
                                    {activeL1?.children.length > 0 && (
                                        <div className="menu-column">
                                            {activeL1.children.map(l2 => (
                                                <Link
                                                    to={`/products/${l2.id}`}
                                                    key={l2.id}
                                                    className={`menu-item l2 ${activePath.includes(l2.id) ? 'active' : ''}`}
                                                    onMouseEnter={() => handleL2Enter(l2)}
                                                    onClick={handleMenuLeave}
                                                >
                                                    {l2.name}
                                                </Link>
                                            ))}
                                        </div>
                                    )}

                                    3차 메뉴
                                    {activeL2?.children.length > 0 && (
                                        <div className="menu-column">
                                            {activeL2.children.map(l3 => (
                                                <Link
                                                    to={`/products/${l3.id}`}
                                                    key={l3.id}
                                                    className={`menu-item l3 ${activePath.includes(l3.id) ? 'active' : ''}`}
                                                    onClick={handleMenuLeave}
                                                >
                                                    {l3.name}
                                                </Link>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            )}*/}
                        </div>
                        <Link to="/new">신규 업체</Link>
                        <Link to="/best">베스트</Link>
                    </nav>
                </div>
                <div className="header-right">
                    <div className='input-box'>
                        <input type="text" placeholder="검색" className="search-bar"/>
                        <button type='button' className='search-button'>🔍</button>
                    </div>
                    <button className="icon-button">🛒</button>
                    <button className="icon-button">👤</button>
                </div>
            </div>
        </header>
    );
}

export default Header;