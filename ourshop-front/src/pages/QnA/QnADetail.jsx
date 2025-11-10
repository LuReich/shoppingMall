import React from 'react';
import { useParams } from 'react-router';
import { useQnA } from '../../hooks/useQnA';
import '../../assets/css/QnADetail.css';
function QnADetail(props) {
    const {inquiryId} = useParams();
    const {getQnADetail} = useQnA();
    const {data: QnADetailData} = getQnADetail(inquiryId);
    const QnADetail = QnADetailData?.content;
    console.log("문의 번호", inquiryId);
    console.log("문의 상세", QnADetail);

    return (
        <div className="qna-detail-container">
            <div className="qna-detail-header">
                <h2>Q&A 상세 내용</h2>
                <div className="qna-detail-buttons">
                    <button>수정</button>
                    <button>삭제</button>
                </div>
            </div>

            <div className="qna-detail-info">
                <h2 className='qna-detail-h'>문의 상세</h2>
                <p><strong>문의 유형:</strong> {QnADetail?.inquiryType}</p>
                <p><strong>작성자:</strong> {QnADetail?.buyerNickname}</p>
                <p><strong>문의 상태:</strong> {QnADetail?.inquiryStatus}</p>
                <p><strong>작성일:</strong> {new Date(QnADetail?.createdAt).toLocaleDateString()}</p>
            </div>
            <h2 className='qna-detail-h'>문의 내용</h2>
            <div className="qna-detail-content">
                <h3>{QnADetail?.title}</h3>
                <p>{QnADetail?.questionContent}</p>
            </div>

            {QnADetail?.images && QnADetail.images.length > 0 && (
                <div className="qna-detail-images">
                    {QnADetail.images.map((image) => (
                        <img
                            key={image.imageId}
                            src={`http://localhost:9090${image.imagePath}`}
                            alt={image.imageName}
                        />
                    ))}
                </div>
            )}

            {QnADetail?.answerContent && (
                <div className="qna-detail-answer">
                    <h4>답변</h4>
                    <p>{QnADetail.answerContent}</p>
                </div>
            )}
        </div>
    );
}

export default QnADetail;