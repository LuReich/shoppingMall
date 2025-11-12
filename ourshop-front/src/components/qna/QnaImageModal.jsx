import React from 'react';
import { useNavigate } from 'react-router';
import '../../assets/css/QnaImagesModal.css';

function QnaImageModal({image, setIsModalOpen}) {

    return (
        <div className='qna-image-modal' onClick={() => setIsModalOpen(false)}>
            <div className='qna-image-box'>
                <button type='button' className='qna-close-button' onClick={() => setIsModalOpen(false)}>X</button>
                <img key={image.imageId}
                    src={`http://localhost:9090${image.imagePath}`}
                    alt={image.imageName}
                />
            </div>
        </div>
    );
}

export default QnaImageModal;