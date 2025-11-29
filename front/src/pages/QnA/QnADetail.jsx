import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { useQnA } from '../../hooks/useQnA';
import '../../assets/css/QnADetail.css';
import QnaImageModal from '../../components/qna/qnaImageModal';
import { authStore } from '../../store/authStore';
import { useAdmin } from '../../hooks/useAdmin';
import Loader from '../../utils/Loaders';
import { SERVER_URL } from '../../axios/axios';
function QnADetail(props) {
    const navigate = useNavigate();
    const role = authStore(state => state.role);
    const isAdmin = role === "ADMIN";

    const {mode, inquiryId} = useParams();
    const {getQnADetail, deleteQnA} = useQnA();
    const {getAdminQnADetail, answerQnA} = useAdmin();

    const {data: QnADetailData} = getQnADetail(mode, inquiryId);
    const {data: adminQnADetailData, isLoading} = getAdminQnADetail(mode, inquiryId);

    const {mutate: deleteQnAMutate} = deleteQnA();
    const {mutate: answerQnAMutate} = answerQnA();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalImage, setModalImage] = useState(null);


    if(isLoading) return <Loader/>


    const QnADetail = isAdmin ? adminQnADetailData?.content : QnADetailData?.content;
    console.log("문의 번호", inquiryId);
    console.log("문의 상세", QnADetail);

    //문의 삭제
    const handleDelete = () => {
        confirm("해당 문의 내역을 삭제하시겠습니까?") && deleteQnAMutate({mode, inquiryId});
        navigate(-1);
    }

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

    return (
        <div className="qna-detail-container">
            <div className="qna-detail-header">
                <h2>Q&A 상세 내용</h2>
                {
                    isAdmin ? null 
                    : 
                    <div className="qna-detail-buttons">
                        {
                            QnADetail?.inquiryStatus === "ANSWERED" ? null : <button type='button' onClick={() => navigate(`/${mode}/qna/update/${inquiryId}`, {state: {QnADetail}})}>수정</button>
                        }
                        <button type='button' onClick={handleDelete}>삭제</button>
                    </div>
                }
            </div>

            <div className="qna-detail-info">
                <h2 className='qna-detail-h'>문의 상세</h2>
                <p><strong>문의 카테고리:</strong> {Kor[QnADetail?.inquiryType]}</p>
                <p><strong>작성자:</strong> {QnADetail?.buyerNickname || '알 수 없음'}</p>
                <p><strong>문의 상태:</strong> {Kor[QnADetail?.inquiryStatus]}</p>
                <p><strong>작성일:</strong> {new Date(QnADetail?.createdAt).toLocaleDateString()}</p>
            </div>
            <h2 className='qna-detail-h'>문의 내용</h2>
            <div className="qna-detail-content">
                <h3>{QnADetail?.title}</h3>
                <p>{QnADetail?.questionContent}</p>
            </div>

            {QnADetail?.images && QnADetail.images.length > 0 && (
                <div className="qna-detail-images">
                    {QnADetail.images?.map((image) => ( // Optional chaining for safety
                        <img
                            key={image.imageId}
                            src={`${SERVER_URL}${image.imagePath}`}
                            alt={image.imageName}
                            onClick={() => {
                                setModalImage(image);
                                setIsModalOpen(true)}
                            }
                        />
                    ))}
                </div>
            )}

            <div className="qna-detail-answer">
                <h4>답변</h4>
                {QnADetail?.answerContent !== null ?
                     // null이 아닌 경우에만 렌더링
                    <div className='qna-detail-answer-box'>
                    <p>{QnADetail?.answerContent}</p>
                    <div className='qna-admin-info-box'>
                        <p> {QnADetail?.adminName}</p>
                        <p>{new Date(QnADetail?.answerAt).toLocaleDateString().replace(/\.$/, '')}</p>
                    </div>
                    </div>
                     :
                    isAdmin?
                        <div className='admin-qna-answer-box'>
                            <textarea className='admin-qna-answer' 
                                placeholder='답변을 작성하세요'
                                value={QnADetail?.answerContent}
                                onChange={(e) => QnADetail.answerContent = e.target.value}
                            />
                            <button type='button' onClick={() => answerQnAMutate({mode, inquiryId, data: {answerContent: QnADetail.answerContent}})}>답변업로드</button>
                        </div>
                        : 
                        <p>등록된 답변이 없습니다.</p>        
                }
            </div>
            {
                isModalOpen && modalImage && (
                    <QnaImageModal 
                    image={modalImage} 
                    setIsModalOpen={setIsModalOpen}
                    />
                )
            }
        </div>
    );
}

export default QnADetail;