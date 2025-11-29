import React, { useState } from 'react';
import '../../assets/css/WithdrawModal.css';
import { useNavigate } from 'react-router';

function WithdrawModal({ isOpen, onClose, onConfirm }) {
  const [reason, setReason] = useState('');
  const navigate = useNavigate();

  if (!isOpen) {
    return null;
  }

  const handleConfirm = () => {
    onConfirm(reason);
    navigate("/");
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>회원 탈퇴</h2>
        <p>정말 탈퇴하시겠습니까? 탈퇴 사유를 입력해주세요. (선택사항)</p>
        <textarea
          className="reason-textarea"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          placeholder="탈퇴 사유를 입력해주세요."
        />
        <div className="modal-buttons">
          <button onClick={onClose} className="cancel-btn">취소</button>
          <button onClick={handleConfirm} className="confirm-btn">탈퇴하기</button>
        </div>
      </div>
    </div>
  );
}

export default WithdrawModal;