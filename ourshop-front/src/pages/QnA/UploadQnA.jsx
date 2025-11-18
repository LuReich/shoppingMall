import React, { useEffect, useState } from 'react';
import '../../assets/css/UploadQnA.css';
import { useQnA } from '../../hooks/useQnA';
import { useParams } from 'react-router';
import { SERVER_URL } from '../../axios/axios';

function UploadQnA() {
    
    const { mode, inquiryId } = useParams();

    const { getQnADetail, createQnA, updateQnA } = useQnA();
    const { mutate: createQnAMutate } = createQnA();
    const { mutate: updateQnAMutate } = updateQnA();

    // 🔥 수정 모드일 때만 상세 조회
    const { data: QnaDetailData } = inquiryId
        ? getQnADetail(mode, inquiryId)
        : { data: null };

    const [inquiryData, setInquiryData] = useState({
        inquiryType: "",
        title: "",
        questionContent: "",
    });

    const [addImages, setAddImages] = useState([]);
    const [deletedImageIds, setDeletedImageIds] = useState([]);

    console.log("기존 qna 상세",QnaDetailData)
    const QnADetail  = QnaDetailData?.content;
    // 🔥 상세 데이터 들어오면 state 초기화
    useEffect(() => {
        if (!QnADetail) return;

        setInquiryData({
            inquiryType: QnADetail.inquiryType,
            title: QnADetail.title,
            questionContent: QnADetail.questionContent,
        });

        setAddImages(
            QnADetail.images?.map(img => ({
                ...img,
                url: `${SERVER_URL}${img.imagePath}`,
            })) || []
        );
    }, [QnaDetailData?.content]);
    
    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        if (files.length === 0) return;

        const newImageObjects = files.map(file => ({
            file,
            url: URL.createObjectURL(file),
            isNew: true
        }));

        setAddImages(prevImages => [...prevImages, ...newImageObjects]);
    };

    const handleRemoveImage = (indexToRemove) => {
        setAddImages(prevImages => {
            const removedImage = prevImages[indexToRemove];
            if (removedImage?.imageId) {
                setDeletedImageIds(prev => [...prev, removedImage.imageId]);
            }
            return prevImages.filter((_, idx) => idx !== indexToRemove);
        });
    };

    // 등록
    const handleUpload = (e) => {
        e.preventDefault();

        const formData = new FormData();

        const inquiryDataBlob = new Blob(
            [JSON.stringify(inquiryData)],
            { type: "application/json" }
        );
        formData.append("inquiryData", inquiryDataBlob);

        addImages
            .filter(img => img.isNew)
            .forEach(img => formData.append("addImages", img.file));

        createQnAMutate({ mode, formData });
    
    };

    // 수정
    const handleUpdate = (e) => {
        e.preventDefault();

        const formData = new FormData();

        const finalInquiryData = {
            ...inquiryData,
            deletedImageIds: deletedImageIds,
        };

        const inquiryDataBlob = new Blob(
            [JSON.stringify(finalInquiryData)],
            { type: "application/json" }
        );
        formData.append("inquiryData", inquiryDataBlob);

        addImages
            .filter(img => img.isNew)
            .forEach(img => formData.append("addImages", img.file));

        updateQnAMutate({ mode, inquiryId, formData });
        navigate(-1);
    };

    return (
        <div className='qna-upload-container'>
            <h2>{inquiryId ? "문의 수정" : "문의하기"}</h2>

            <form className='qna-upload-form' onSubmit={inquiryId ? handleUpdate : handleUpload}>
                <div className='input-qna'>
                    <label>카테고리</label>
                    <select
                        name='inquiryType'
                        value={inquiryData.inquiryType}
                        onChange={(e) =>
                            setInquiryData({ ...inquiryData, inquiryType: e.target.value })
                        }
                    >
                        <option value="" disabled>선택</option>
                        <option value="ACCOUNT">계정</option>
                        {mode === "buyer" ? (
                            <>
                                <option value="PAYMENT">결제</option>
                                <option value="SHIPPING">배송</option>
                            </>
                        ) : (
                            <>
                                <option value="PRODUCT">상품</option>
                                <option value="VERIFICATION">판매인증</option>
                            </>
                        )}
                        <option value="ETC">기타</option>
                    </select>
                </div>

                <div className='input-qna'>
                    <label>제목</label>
                    <input
                        type='text'
                        placeholder='제목을 입력해주세요'
                        value={inquiryData.title}
                        onChange={(e) =>
                            setInquiryData({ ...inquiryData, title: e.target.value })
                        }
                    />
                </div>

                <div className='input-qna'>
                    <label>내용</label>
                    <textarea
                        placeholder='내용을 입력해주세요'
                        value={inquiryData.questionContent}
                        onChange={(e) =>
                            setInquiryData({ ...inquiryData, questionContent: e.target.value })
                        }
                    />
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
                                <div key={image.imageId || index} className="image-preview-item">
                                    <img src={image.url} alt="첨부 이미지" />
                                    <button
                                        type="button"
                                        onClick={() => handleRemoveImage(index)}
                                        className="remove-image-btn"
                                    >
                                        ×
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className='qna-upload-box'>
                    <button
                        type='button'
                        className='qna-delete-button'
                        onClick={() => navigate(-1)}
                    >
                        취소
                    </button>
                    <button type='submit' className='qna-upload-button'>
                        {inquiryId ? "수정" : "등록"}
                    </button>
                </div>
            </form>
        </div>
    );
}

export default UploadQnA;
