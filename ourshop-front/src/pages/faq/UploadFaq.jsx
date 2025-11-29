import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { useFaq } from '../../hooks/useFaq';
import '../../assets/css/UploadFaq.css';

function UploadFaq(props) {
    const navigate = useNavigate();
    const {faqId} = useParams();
    console.log(faqId);

    const {getFaqDetail, updateFaq, createFaq} = useFaq();
    // faqId가 있을 때만 API를 호출하도록 enabled 옵션을 추가합니다.
    const {data: faqDetailData} = getFaqDetail(faqId, { enabled: !!faqId });
    const {mutate: updateMutate} = updateFaq();
    const {mutate: createMutate} = createFaq();

    const [faqData, setFaqData] = useState({
        sortOrder:"",
        faqTarget: "",
        faqCategory: "",
        faqQuestion: "",
        faqAnswer: ""
    })

    console.log("faq상세 정보",faqDetailData);

    useEffect(() => {
        if (faqDetailData?.content) {
            setFaqData(faqDetailData.content);
        }
    }, [faqDetailData]);


    //등록 버튼
    const handleUpload = () => {
        const { sortOrder, faqTarget, faqCategory, faqQuestion, faqAnswer } = faqData;
        if (sortOrder === "" 
            || !faqTarget 
            || !faqCategory 
            || !faqQuestion.trim() 
            || !faqAnswer.trim()) {
            alert("모든 필드를 입력해주세요.");
            return;
        }


        if(confirm("해당 FAQ를 등록하시겠습니까?")){
            createMutate(faqData);
            navigate("/admin/faq");
            
        }
 
    }

    //수정 버튼
    const handleUpdate = () => {
        const { sortOrder, faqTarget, faqCategory, faqQuestion, faqAnswer } = faqData;
        if (sortOrder === "" || !faqTarget || !faqCategory || !faqQuestion.trim() || !faqAnswer.trim()) {
            alert("모든 필드를 입력해주세요.");
            return;
        }


        if(confirm("해당 FAQ를 수정하시겠습니까?")){
            updateMutate({faqId, data: faqData});
            navigate("/admin/faq");
        }
    }
    
    return (
        <form className='upload-faq-form'>
            <h2>{faqId ? "FAQ 수정" : "FAQ 등록"}</h2>
            <h3>필터</h3>
            <div className='upload-faq-input-box'>
                <label>순서</label>
                <input type='number' 
                placeholder='정렬 숫자를 입력하세요'
                onChange={(e) => setFaqData({...faqData, sortOrder: Number(e.target.value)})}
                value={faqData.sortOrder}/>
            </div>
            <div className='upload-faq-input-box'>
                <label>회원 유형</label>
                <select className={`upload-faq-select ${!faqData.faqTarget ? 'placeholder-selected' : ''}`} 
                value={faqData.faqTarget} 
                onChange={(e) => setFaqData({...faqData, faqTarget: e.target.value})}>
                    <option value='' disabled>선택</option>
                    <option value='ALL'>공통</option>
                    <option value='BUYER' >구매자</option>
                    <option value='SELLER'>판매자</option>
                </select>
            </div>
            <div className='upload-faq-input-box'>
                <label>카테고리</label>
                <select className={`upload-faq-select ${!faqData.faqCategory ? 'placeholder-selected' : ''}`} 
                value={faqData.faqCategory} 
                onChange={(e) => setFaqData({...faqData, faqCategory: e.target.value})}>
                    <option value='' disabled>선택</option>
                    <option value='ACCOUNT'>계정</option>
                    <option value='PAYMENT'>구매</option>
                    <option value='SHIPPING'>배송</option>
                    <option value='PRODUCT'>상품</option>
                    <option value='VERIFICATION'>인증</option>
                    <option value='ETC'>기타</option>
                </select>
            </div>  
            <h3>내용</h3>
            <div className='upload-faq-input-box'>
                <label>제목</label>
                <input type='text' 
                placeholder='질문형식의 제목을 작성해주세요.'
                onChange={(e) => setFaqData({...faqData, faqQuestion: e.target.value})}
                value={faqData.faqQuestion}/>
            </div>
            <div className='upload-faq-input-box'>
                <label>답변</label>
                <textarea
                placeholder='답변을 작성해주세요.'
                onChange={(e) => setFaqData({...faqData, faqAnswer: e.target.value})}
                value={faqData.faqAnswer}
                />
            </div>
            <div className='faq-create-b-box'>
                <button type='button' className='faq-back-b' onClick={() => navigate(-1)}>취소</button>
                {
                    faqId ? 
                    <button type='button' className='faq-create-b' onClick={handleUpdate}>수정</button>
                    :
                    <button type='button' className='faq-create-b' onClick={handleUpload}>등록</button>
            
                }
            </div>
        </form>
    );
}

export default UploadFaq;