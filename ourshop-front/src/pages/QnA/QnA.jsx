import React, { useState } from 'react';
import { useQnA } from '../../hooks/useQnA';
import Pagenation from '../../components/common/Pagenation';
import '../../assets/css/qna.css';
import { useNavigate, useParams } from 'react-router-dom';
import { authStore } from '../../store/authStore';


function QnA(props) {
    const navigate = useNavigate();
    const uid = authStore(state => state.user)?.content?.buyerUid || authStore(state => state.user)?.content?.sellerUid;
    const isLogin = authStore(state => state.isLogin);
    const role = authStore(state => state.role);
    const isAdmin = role === "ADMIN";
    const mode = authStore(state => state.role) === "BUYER" ? "buyer" : "seller";
    const [page, setPage] = useState(0);

    const {getQnAList} = useQnA();
    const {data: QnAListData} = getQnAList(mode, {
        size: 5,
        page
    }, uid);

    const QnAList = QnAListData?.content?.content;                          
    console.log("문의 리스트", QnAListData);

    const totalPages = QnAListData?.content?.totalPages;

    const Kor = {
        "PAYMENT": "결제",
        "SHIPPING": "배송",
        "PRODUCT": "상품",
        "VERIFICATION": "판매인증",
        "ETC": "기타",
        "ACCOUNT": "계정",
        "PENDING": "미답변",
        "ANSWERED": "답변"
    }

    if(!isLogin){
        alert("로그인이 필요한 서비스입니다.");
        navigate("/login");
    }
    return (
        <div className='product-qna-container'>
            <div className='qna-header'>
                <h2>Q&A</h2>
                <div className='qna-btn-box'>
                    <button type='button' className='qna-write-button' onClick={() => navigate(`/${mode}/qna/upload`)}>문의하기</button>
                </div>
            </div>
            <table className='qna-table'>
                <thead>
                    <tr>
                        <th>문의번호</th>
                        <th>답변상태</th>
                        <th>카테고리</th>
                        <th>제목</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        QnAList?.length > 0? QnAList.map((qna) => (
                            <tr key={qna.inquiryId} onClick={() => navigate(`/${mode}/qna/${qna.inquiryId}`)}>
                                    <td>{qna.inquiryId}</td>
                                <td style={qna.inquiryStatus === "PENDING" ? {color: "red"} : {color: "green"}}>{Kor[qna.inquiryStatus]}</td>
                                    <td>{Kor[qna.inquiryType]}</td>
                                    <td>{qna.title}</td>
                                </tr>
                        )) : (
                             <tr>
                                <td colSpan="4" style={{ textAlign: "center" }}>
                                    등록한 문의가 없습니다.
                                </td>
                            </tr>
                        )
                    }
                </tbody>
            </table>
            {
                totalPages > 0 ? <Pagenation page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)}/> : null
            }
        </div>
    );
}

export default QnA;