import React from 'react';

function UserDetailModal({detail, mode}) {
    return (
        <div className='user-detail-modal-container'>
            {
                mode === "buyer"?
                <>
                    <div>{detail.buyerUid}</div>
                    <div>{detail.buyerId}</div>
                    <div>{detail.nickname}</div>
                </>
                :
                <>
                    <div>{detail.sellerUid}</div>
                    <div>{detail.sellerId}</div>
                </>
            }
        
        </div>
    );
}



export default UserDetailModal;