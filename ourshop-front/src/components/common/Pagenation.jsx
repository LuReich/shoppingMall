import React from "react";
import { IoIosArrowBack, IoIosArrowForward} from "react-icons/io";
import { MdKeyboardDoubleArrowLeft, MdOutlineKeyboardDoubleArrowRight } from "react-icons/md";

import "../../assets/css/Pagination.css";

function Pagination({ page, totalPages, onPageChange }) {
  const visibleCount = 5;

  const getVisiblePages = () => {
    const half = Math.floor(visibleCount / 2);
    let start = page - half;
    let end = page + half + 1;

    // 맨 앞쪽(예: 1~5페이지일 때) 예외 처리
    if (start < 0) {
      start = 0;
      end = Math.min(visibleCount, totalPages);
    }

    // 맨 뒤쪽(예: 마지막 5페이지일 때) 예외 처리
    if (end > totalPages) {
      end = totalPages;
      start = Math.max(totalPages - visibleCount, 0);
    }

    return Array.from({ length: end - start }, (_, i) => start + i);
  };

  const visiblePages = getVisiblePages();

  return (
    <div className="pagination">
      {/* 맨 앞으로 */}
      <button
        onClick={() => onPageChange(0)}
        disabled={page === 0}
        className="edge-btn"
      >
        <MdKeyboardDoubleArrowLeft/>
      </button>

      {/* 이전 */}
      <button
        onClick={() => onPageChange(Math.max(page - 1, 0))}
        disabled={page === 0}
      >
        <IoIosArrowBack />
      </button>

      {/* 페이지 번호 */}
      {visiblePages.map((p) => (
        <button
          key={p}
          className={page === p ? "active" : ""}
          onClick={() => onPageChange(p)}
        >
          {p + 1}
        </button>
      ))}

      {/* 다음 */}
      <button
        onClick={() => onPageChange(Math.min(page + 1, totalPages - 1))}
        disabled={page + 1 >= totalPages}
      >
        <IoIosArrowForward />
      </button>

      {/* 맨 뒤로 */}
      <button
        onClick={() => onPageChange(totalPages - 1)}
        disabled={page + 1 >= totalPages}
        className="edge-btn"
      >
        <MdOutlineKeyboardDoubleArrowRight/>
      </button>
    </div>
  );
}

export default Pagination;
