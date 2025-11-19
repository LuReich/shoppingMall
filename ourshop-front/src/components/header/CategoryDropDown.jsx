import React, { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import "../../assets/css/CategoryDropDown.css";

function CategoryDropDown({ categories, handleMenuLeave }) {
  const navigate = useNavigate();
  const [activeL1, setActiveL1] = useState(null);
  const [activeL2, setActiveL2] = useState(null);
  const [activeCategory, setActiveCategory] = useState(null);

  // flat → tree 변환
  const categoryTree = useMemo(() => {
    // categories가 객체 형태일 수 있으므로 content만 추출
    const list = categories?.content || [];
    if (!Array.isArray(list) || list.length === 0) return [];

    const map = {};
    const roots = [];

    // 각 항목을 map에 등록
    list.forEach((cat) => {
      map[cat.categoryId] = { ...cat, children: [] };
    });

    // 부모-자식 관계 설정
    list.forEach((cat) => {
      if (cat.parentId) {
        map[cat.parentId]?.children.push(map[cat.categoryId]);
      } else {
        roots.push(map[cat.categoryId]);
      }
    });

    return roots;
  }, [categories]);

  const handleL1Enter = (l1) => setActiveL1(l1);
  const handleL2Enter = (l2) => setActiveL2(l2);

  const handleCategoryClick = (categoryId, categoryName) => {
    setActiveCategory(categoryId);
    navigate(`/products?categoryId=${categoryId}`, { state: { categoryName } });
    handleMenuLeave(); // 드롭다운 닫기
  };


  return (
    <div className="mega-menu">
      {/* 1차 메뉴 */}
      <div className="menu-column">
        {categoryTree?.map((l1) => (
          <button
            key={l1.categoryId}
            className={`menu-item ${
              activeCategory === l1.categoryId ? "active" : ""
            }`}
            onMouseEnter={() => handleL1Enter(l1)}
            onClick={() => handleCategoryClick(l1.categoryId, l1.categoryName)}
          >
            {l1.categoryName}
          </button>
        ))}
      </div>

      {/* 2차 메뉴 */}
      {activeL1?.children?.length > 0 && (
        <div className="menu-column">
          {activeL1.children.map((l2) => (
            <button
              key={l2.categoryId}
              className={`menu-item ${
                activeCategory === l2.categoryId ? "active" : ""
              }`}
              onMouseEnter={() => handleL2Enter(l2)}
              onClick={() => handleCategoryClick(l2.categoryId, l2.categoryName)}
            >
              {l2.categoryName}
            </button>
          ))}
        </div>
      )}

      {/* 3차 메뉴 */}
      {activeL2?.children?.length > 0 && (
        <div className="menu-column">
          {activeL2.children.map((l3) => (
            <button
              key={l3.categoryId}
              className={`menu-item ${
                activeCategory === l3.categoryId ? "active" : ""
              }`}
              onClick={() => handleCategoryClick(l3.categoryId, l3.categoryName)}
            >
              {l3.categoryName}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default CategoryDropDown;
