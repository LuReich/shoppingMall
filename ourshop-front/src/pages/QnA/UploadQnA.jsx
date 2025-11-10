import React, { useState } from 'react';
import '../../assets/css/UploadQnA.css';
import { useQnA } from '../../hooks/useQnA';
import { useNavigate } from 'react-router';

function UploadQnA(props) {
    const navigate = useNavigate();

    const {createQnA} = useQnA();
    const {mutate: createQnAMutate} = createQnA();

    const [inquiryData, setInquiryData] = useState({
        "inquiryType": "",
        "title": "",
        "questionContent": ""
    })

    const [addImages, setAddImages] = useState([]); // { file: File, url: string }

    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        if (files.length === 0) return;

        const newImageObjects = files.map(file => ({
            file: file,
            url: URL.createObjectURL(file)
        }));

        setAddImages(prevImages => [...prevImages, ...newImageObjects]);
    };

    const handleRemoveImage = (indexToRemove) => {
        setAddImages(prevImages => prevImages.filter((_, index) => index !== indexToRemove));
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
        addImages.forEach(imageObj => {
            formData.append("addImages", imageObj.file);
        });
        
        createQnAMutate(formData);
    }

    return (
        <div className='qna-upload-container'>
            <h2>문의하기</h2>
            <form className='qna-upload-form' onSubmit={handleUpload}>
                <div className='input-qna'>
                    <label>카테고리</label>
                    <select name='inquiryType' value={inquiryData.inquiryType} onChange={(e) => setInquiryData({...inquiryData, inquiryType: e.target.value})}>
                        <option value="" disabled>선택</option>
                        <option value="ACCOUNT">계정</option>
                        <option value="PAYMENT">결제</option>
                        <option value="SHIPPING">배송</option>
                        <option value="ETC">기타</option>
                    </select>
                </div>
                <div className='input-qna'>
                    <label>제목</label>
                    <input type='text' placeholder='제목을 입력해주세요' onChange={(e) => setInquiryData({...inquiryData, title: e.target.value})}/>
                </div>
                <div className='input-qna'>
                    <label>내용</label>
                    <textarea placeholder='내용을 입력해주세요' onChange={(e) => setInquiryData({...inquiryData, questionContent: e.target.value})}/>
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
                                <div key={index} className="image-preview-item">
                                    <img src={image.url} alt={`첨부 이미지 ${index + 1}`} />
                                    <button type="button" onClick={() => handleRemoveImage(index)} className="remove-image-btn">×</button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
                <div className='qna-upload-box'>
                    <button type='button' className='qna-delete-button' onClick={() => navigate(-1)}>취소</button>
                    <button type='submit' className='qna-upload-button'>등록</button>
                </div>
            </form>
        </div>
    );
}

export default UploadQnA;