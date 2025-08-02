import React, { useState, useEffect, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import ReactPaginate from 'react-paginate';
import productsData from '../data/products.json';
import categoriesData from '../data/categories.json';
import ProductCard from '../components/common/ProductCard';
import '../assets/css/ProductList.css';

//  특정 카테고리와 그 하위의 모든 리프 카테고리 이름(실제 상품 카테고리)을 찾는 재귀 함수 (수정됨) 
const getLeafCategoryNames = (categoryId, categories) => {
    let leafNames = [];

    const findNodeById = (nodes, id) => {
        for (const node of nodes) {
            if (node.id === id) return node;
            if (node.children) {
                const found = findNodeById(node.children, id);
                if (found) return found;
            }
        }
        return null;
    };

    const collectAllLeaves = (node) => {
        if (!node.children || node.children.length === 0) {
            leafNames.push(node.name);
        } else {
            node.children.forEach(collectAllLeaves);
        }
    };

    const startNode = findNodeById(categories, categoryId);
    if (startNode) {
        collectAllLeaves(startNode);
    }

    return leafNames;
};

const getCategoryNameById = (categoryId, categories) => {
    for (const category of categories) {
        if (category.id === categoryId) return category.name;
        if (category.children) {
            const foundName = getCategoryNameById(categoryId, category.children);
            if (foundName) return foundName;
        }
    }
    return null;
};

const ITEMS_PER_PAGE = 8;

const ProductList = () => {
    const { categoryId } = useParams();
    const [itemOffset, setItemOffset] = useState(0);

    const categoryName = useMemo(() => {
        const name = getCategoryNameById(categoryId, categoriesData);
        return name || "전체 상품";
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

    //  카테고리가 변경될 때 itemOffset을 0으로 초기화하는 로직 
    useEffect(() => {
        setItemOffset(0);
    }, [categoryId]);

    useEffect(() => {
        const endOffset = itemOffset + ITEMS_PER_PAGE;
        setCurrentItems(filteredProducts.slice(itemOffset, endOffset));
        setPageCount(Math.ceil(filteredProducts.length / ITEMS_PER_PAGE));
    }, [itemOffset, filteredProducts]);

    const handlePageClick = (event) => {
        const newOffset = (event.selected * ITEMS_PER_PAGE) % filteredProducts.length;
        setItemOffset(newOffset);
        window.scrollTo(0, 0);
    };

    // 카테고리가 바뀔 때 ReactPaginate 컴포넌트 자체를 리셋하기 위한 key
    const paginationKey = useMemo(() => categoryId, [categoryId]);

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
                        key={paginationKey} // 카테고리 변경 시 컴포넌트를 강제로 다시 렌더링하여 페이지를 1로 초기화
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