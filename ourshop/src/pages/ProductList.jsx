import React, { useState, useEffect, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import ReactPaginate from 'react-paginate';
import productsData from '../data/products.json';
import categoriesData from '../data/categories.json';
import ProductCard from '../components/common/ProductCard';
import '../assets/css/ProductList.css';

// ... (getLeafCategoryNames, getCategoryNameById 헬퍼 함수는 이전과 동일)
const getLeafCategoryNames = (categoryId, categories) => {
    let leafNames = [];
    const findLeaves = (items) => {
        for (const item of items) {
            let found = false;
            if (item.id === categoryId) { found = true; }
            if (found) {
                const collectAllLeaves = (node) => {
                    if (!node.children || node.children.length === 0) { leafNames.push(node.name); }
                    else { node.children.forEach(collectAllLeaves); }
                };
                collectAllLeaves(item);
                return;
            }
            if (item.children) { findLeaves(item.children); }
        }
    };
    findLeaves(categories);
    return leafNames;
};
const getCategoryNameById = (categoryId, categories) => {
    for (const category of categories) {
        if (category.id === categoryId) { return category.name; }
        if (category.children) {
            const foundName = getCategoryNameById(categoryId, category.children);
            if (foundName) { return foundName; }
        }
    }
    return null;
}


const ITEMS_PER_PAGE = 8; // 한 페이지에 보여줄 상품 수

const ProductList = () => {
    const { categoryId } = useParams();

    const categoryName = useMemo(() => {
        const name = getCategoryNameById(categoryId, categoriesData);
        return name || "카테고리";
    }, [categoryId]);

    const filteredProducts = useMemo(() => {
        if (!categoryId) return productsData;
        const leafCategories = getLeafCategoryNames(categoryId, categoriesData);
        return productsData.filter(product =>
            leafCategories.includes(product.category)
        );
    }, [categoryId]);

    // --- 페이지네이션 상태 관리 ---
    const [currentItems, setCurrentItems] = useState([]);
    const [pageCount, setPageCount] = useState(0);
    const [itemOffset, setItemOffset] = useState(0);

    useEffect(() => {
        const endOffset = itemOffset + ITEMS_PER_PAGE;
        setCurrentItems(filteredProducts.slice(itemOffset, endOffset));
        setPageCount(Math.ceil(filteredProducts.length / ITEMS_PER_PAGE));
    }, [itemOffset, filteredProducts]);

    const handlePageClick = (event) => {
        const newOffset = (event.selected * ITEMS_PER_PAGE) % filteredProducts.length;
        setItemOffset(newOffset);
        window.scrollTo(0, 0); // 페이지 이동 시 맨 위로 스크롤
    };

    return (
        <div className="product-list-container">
            <h2 className="list-title">{categoryName} 상품 목록</h2>
            {currentItems.length > 0 ? (
                <>
                    <div className="product-grid">
                        {currentItems.map(product => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>
                    <ReactPaginate
                        breakLabel="..."
                        nextLabel=">"
                        onPageChange={handlePageClick}
                        pageRangeDisplayed={5}
                        pageCount={pageCount}
                        previousLabel="<"
                        renderOnZeroPageCount={null}
                        containerClassName="pagination"
                        pageLinkClassName="page-num"
                        previousLinkClassName="page-num"
                        nextLinkClassName="page-num"
                        activeLinkClassName="active"
                    />
                </>
            ) : (
                <p className="no-products">해당 카테고리에 상품이 없습니다.</p>
            )}
        </div>
    );
};

export default ProductList;