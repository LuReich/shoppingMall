import React from 'react';
import '../../assets/css/Sort.css'

function Sort({sort, setSort, setPage, sortCateg }) {

    
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
                         {sortCateg?.price && 
                         <>
                          <option value={`${sortCateg?.price},desc`}>가격 높은순</option>
                          <option value={`${sortCateg?.price},asc`}>가격 낮은순</option>
                         </>
                        }
                        {
                          sortCateg?.likeCount && 
                          <>
                            <option value={`${sortCateg?.likeCount},desc`}>좋아요순</option>
                          </>
                        }
                        {
                          sortCateg?.createAt && 
                          <>
                            <option value={`${sortCateg?.createAt},asc`}>오래된순</option>
                            <option value={`${sortCateg?.createAt},desc`}>최신순</option>
                          </>
                        }
                        {
                          sortCateg?.productName && 
                          <>
                            <option value={`${sortCateg?.productName},asc`}>가나다순</option>
                          </>  
                        }
                        {
                          sortCateg?.rating && 
                          <>
                            <option value={`${sortCateg?.rating},desc`}>평점 높은순</option>
                            <option value={`${sortCateg?.rating},asc`}>평점 낮은순</option>
                          </>  
                        }
                        {
                          sortCateg?.sortOrder &&
                          <>
                            <option value={`${sortCateg?.sortOrder},asc`}>기본순서</option>
                          </>
                        }
                        {
                          sortCateg?.orderId &&
                          <>
                            <option value={`${sortCateg?.orderId},asc`}>주문아이디 낮은순</option>
                            <option value={`${sortCateg?.orderId},desc`}>주문아이디 높은순</option>
                          </>
                        }
                       </select>
                   </div>
    );
}

export default Sort;