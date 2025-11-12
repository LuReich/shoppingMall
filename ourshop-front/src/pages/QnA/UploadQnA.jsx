import React, { useState } from 'react';
import '../../assets/css/UploadQnA.css';
import { useQnA } from '../../hooks/useQnA';
import { useLocation, useNavigate, useParams } from 'react-router';

function UploadQnA(props) {
    const navigate = useNavigate();
    const location = useLocation();
    const { QnADetail } = location.state || {}; // QnADetail이 state에 직접 담겨있다고 가정
    const {mode, inquiryId} = useParams();
    
    const {createQnA, updateQnA} = useQnA();
    const {mutate: createQnAMutate} = createQnA();
    const {mutate: updateQnAMutate} = updateQnA();

    const [inquiryData, setInquiryData] = useState({
        "inquiryType": inquiryId ? QnADetail?.inquiryType : "",
        "title": inquiryId ? QnADetail?.title : "",
        "questionContent": inquiryId ? QnADetail?.questionContent : "", 
    })

    // 수정 모드일 경우 기존 이미지를 addImages 상태에 포함
    const [addImages, setAddImages] = useState(inquiryId && QnADetail?.images ? QnADetail.images.map(img => ({ ...img, url: `http://localhost:9090${img.imagePath}` })) : []); // { file: File, url: string } or { imageId: number, imagePath: string, url: string }
    const [deletedImageIds, setDeletedImageIds] = useState([]); // 삭제될 이미지 ID 목록

    console.log("수정 모드 데이터", QnADetail);
    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        if (files.length === 0) return;

        const newImageObjects = files.map(file => ({
            file: file,
            url: URL.createObjectURL(file),
            isNew: true // 새로 추가된 이미지임을 표시
        }));

        setAddImages(prevImages => [...prevImages, ...newImageObjects]);
    };

    const handleRemoveImage = (indexToRemove) => {
        setAddImages(prevImages => {
            const removedImage = prevImages[indexToRemove];
            if (removedImage && removedImage.imageId) { // 기존 이미지인 경우
                setDeletedImageIds(prevIds => [...prevIds, removedImage.imageId]);
            }
            return prevImages.filter((_, index) => index !== indexToRemove);
        });
    };

    //등록 버튼
    const handleUpload = (e) => {
        // 백엔드 API 스펙에 맞게 FormData 구성
        e.preventDefault();
        const formData = new FormData();
        
        // 1. inquiryData를 JSON 문자열로 변환 후 Blob으로 감싸서 추가
        const inquiryDataBlob = new Blob([JSON.stringify(inquiryData)], { type: "application/json" });
        formData.append("inquiryData", inquiryDataBlob);

        // 2. 이미지 파일들을 'addImages' 키로 각각 추가
        addImages.filter(img => img.isNew).forEach(imageObj => { // 새로 추가된 파일만 FormData에 추가
            formData.append("addImages", imageObj.file); 
        });
        
        createQnAMutate({mode, formData});
        navigate(-1);
    }

    //수정 버튼
    const handleUpdate = (e) => {
        e.preventDefault();
        const formData = new FormData();

        // 수정 시에는 inquiryData에 deletedImageIds를 포함
        const finalInquiryData = {
            ...inquiryData,
            deletedImageIds: deletedImageIds
        };

        const inquiryDataBlob = new Blob([JSON.stringify(finalInquiryData)], { type: "application/json" });
        formData.append("inquiryData", inquiryDataBlob);

        // 새로 추가된 이미지 파일들
        addImages.filter(img => img.isNew).forEach(imageObj => {
            formData.append("addImages", imageObj.file);
        })

        updateQnAMutate({ mode, inquiryId, formData });
        navigate(-1);
    };

    return (
        <div className='qna-upload-container'>
            <h2>{inquiryId ? "문의 수정" : "문의하기"}</h2>
            <form className='qna-upload-form' onSubmit={inquiryId ? handleUpdate : handleUpload}>
                <div className='input-qna'>
                    <label>카테고리</label>
                    <select name='inquiryType' value={inquiryData?.inquiryType} onChange={(e) => setInquiryData({...inquiryData, inquiryType: e.target.value})}>
                        <option value="" disabled>선택</option>
                        <option value="ACCOUNT">계정</option>
                        {
                            mode==="buyer"?
                            <>
                              <option value="PAYMENT">결제</option>
                              <option value="SHIPPING">배송</option>
                            </>
                            :
                            <>
                              <option value="PRODUCT">상품</option>
                              <option value="VERIFICATION">판매인증</option>
                            </>
                        }
                        <option value="ETC">기타</option>
                    </select>
                </div>
                <div className='input-qna'>
                    <label>제목</label>
                    <input type='text' placeholder='제목을 입력해주세요' value={inquiryData.title} onChange={(e) => setInquiryData({...inquiryData, title: e.target.value})}/>
                </div>
                <div className='input-qna'>
                    <label>내용</label>
                    <textarea placeholder='내용을 입력해주세요' value={inquiryData?.questionContent} onChange={(e) => setInquiryData({...inquiryData, questionContent: e.target.value})}/>
                </div>
                <div className='input-qna'>
                    <label>이미지 첨부 (선택)</label>
                    <label htmlFor="qna-image-input" className="file-input-label">
                        파일 선택
                    </label>
                    <input
                        id="qna-image-input"
                        type="file"
                        accept="image/*"
                        multiple
                        onChange={handleImageChange}
                        style={{ display: 'none' }}
                    />
                    {addImages.length > 0 && (
                        <div className="image-preview-container">
                            {addImages.map((image, index) => (
                                <div key={image.imageId || index} className="image-preview-item"> {/* 기존 이미지는 imageId, 새 이미지는 index 사용 */}
                                    <img src={image.url} alt={`첨부 이미지 ${index + 1}`} />
                                    <button type="button" onClick={() => handleRemoveImage(index)} className="remove-image-btn">×</button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
                <div className='qna-upload-box'>
                    <button type='button' className='qna-delete-button' onClick={() => navigate(-1)}>취소</button>
                    <button type='submit' className='qna-upload-button'>{inquiryId ? "수정" : "등록"}</button>
                </div>
            </form>
        </div>
    );
}

export default UploadQnA;