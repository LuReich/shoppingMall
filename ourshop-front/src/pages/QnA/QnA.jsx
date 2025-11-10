import React, { useState } from 'react';
import { useQnA } from '../../hooks/useQnA';
import Pagenation from '../../components/common/Pagenation';
import '../../assets/css/qna.css';
import { useNavigate } from 'react-router';


function QnA(props) {
    const navigate = useNavigate();
    const [page, setPage] = useState(0);

    const {getQnAList} = useQnA();
    const {data: QnAListData} = getQnAList({
        size: 3,
        page
    })

    const QnAList = QnAListData?.content?.content;                          
    console.log("문의 리스트", QnAListData);

    const totalPages = QnAListData?.content?.totalPages;
    return (
        <div className='product-qna-container'>
            <div className='qna-header'>
                <h2>Q&A</h2>
                <div className='qna-btn-box'>
                    <button type='button' className='qna-write-button' onClick={() => navigate('/upload/qna')}>문의하기</button>
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
                        QnAList?.map((qna) => (
                            <tr key={qna.inquiryId} onClick={() => navigate(`/qna/${qna.inquiryId}`)}>
                                    <td>{qna.inquiryId}</td>
                                    <td>{qna.inquiryStatus}</td>
                                    <td>{qna.inquiryType}</td>
                                    <td>{qna.title}</td>
                                </tr>
                        ))
                    }
                </tbody>
            </table>
            {
                totalPages > 0 ? <Pagenation page={page} setPage={setPage} totalPages={totalPages}/> : null
            }
        </div>
    );
}

export default QnA;