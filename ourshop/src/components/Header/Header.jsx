import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import categoriesData from '../../data/categories.json';
import '../../assets/css/Header.css';

// URL의 categoryId를 기반으로 메뉴에서의 활성 경로(예: ['food', 'farm', 'apple'])를 찾는 함수
const findActivePath = (categoryId, categories) => {
    const search = (id, currentCategories, currentPath) => {
        for (const category of currentCategories) {
            const newPath = [...currentPath, category.id];
            if (category.id === id) return newPath;
            if (category.children) {
                const foundPath = search(id, category.children, newPath);
                if (foundPath) return foundPath;
            }
        }
        return null;
    };
    return search(categoryId, categories, []) || [];
};

const Header = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [activeL1, setActiveL1] = useState(null);
    const [activeL2, setActiveL2] = useState(null);
    const [activePath, setActivePath] = useState([]);

    const location = useLocation();

    useEffect(() => {
        const pathParts = location.pathname.split('/');
        if (pathParts[1] === 'products' && pathParts[2]) {
            const categoryId = pathParts[2];
            setActivePath(findActivePath(categoryId, categoriesData));
        } else {
            setActivePath([]);
        }
    }, [location]);

    const handleMenuEnter = () => setIsMenuOpen(true);
    const handleMenuLeave = () => {
        setIsMenuOpen(false);
        setActiveL1(null);
        setActiveL2(null);
    };

    const handleL1Enter = (l1) => {
        setActiveL1(l1);
        setActiveL2(null); // 1차 메뉴 변경 시 2차는 초기화
    };

    const handleL2Enter = (l2) => {
        setActiveL2(l2);
    };

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
                            {isMenuOpen && (
                                <div className="mega-menu">
                                    {/* 1차 메뉴 */}
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

                                    {/* 2차 메뉴 */}
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

                                    {/* 3차 메뉴 */}
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
                            )}
                        </div>
                        <Link to="/new">신상품</Link>
                        <Link to="/best">베스트</Link>
                    </nav>
                </div>
                <div className="header-right">
                    <input type="text" placeholder="검색" className="search-bar" />
                    <button className="icon-button">🛒</button>
                    <button className="icon-button">👤</button>
                </div>
            </div>
        </header>
    );
};

export default Header;