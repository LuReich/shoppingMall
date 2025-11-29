import React from 'react';
import { useNavigate } from 'react-router';
import '../../assets/css/QnaImagesModal.css';
import { SERVER_URL } from '../../axios/axios';

function QnaImageModal({image, setIsModalOpen}) {

    return (
        <div className='qna-image-modal' onClick={() => setIsModalOpen(false)}>
            <div className='qna-image-box'>
                <button type='button' className='qna-close-button' onClick={() => setIsModalOpen(false)}>X</button>
                <img key={image.imageId}
                    src={`${SERVER_URL}${image.imagePath}`}
                    alt={image.imageName}
                />
            </div>
        </div>
    );
}

export default QnaImageModal;