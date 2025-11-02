import React from 'react';
import '../../assets/css/Sort.css'

function Sort({sort, setSort, setPage }) {
    return (
        <div className="sort-section">
                    {/*<label htmlFor="sort">정렬: </label>*/}
                       <select
                         id="sort"
                         value={sort}
                         onChange={(e) => {
                           setSort(e.target.value);
                           setPage(0);
                         }}
                       >
                         <option value="price,desc">가격 높은순</option>
                         <option value="price,asc">가격 낮은순</option>
                         <option value="createAt,asc">오래된순</option>
                         <option value="createAt,desc">최신순</option>
                         <option value="productName,asc">상품명 가나다순</option>
                       </select>
                   </div>
    );
}

export default Sort;