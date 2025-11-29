import React from 'react';
import '../../assets/css/SellerVerificationGuide.css';
import { useNavigate } from 'react-router';

function SellerVerificationGuide() {
    const navigate = useNavigate();

    return (
        <div className="verification-guide-container">
            <h2>판매자 인증 안내</h2>
            <div className="policy-content">
                <h3>제1조 (판매자 인증이란?)</h3>
                <p>
                    '판매자 인증'은 우리샵을 이용하는 구매자들이 더욱 안심하고 상품을 구매할 수 있도록, 신뢰할 수 있는 판매자임을 우리샵이 공식적으로 확인해주는 제도입니다. 인증을 받은 판매자는 '인증 마크'를 부여받아 고객들에게 높은 신뢰도를 제공할 수 있습니다.
                </p>

                <h3>제2조 (인증 혜택)</h3>
                <ul>
                    <li><strong>신뢰도 상승:</strong> 프로필에 공식 인증 마크가 표시됩니다.</li>
                    <li><strong>노출 기회 확대:</strong> 베스트 업체 선정, 기획전 등에서 우선적으로 노출될 기회를 얻습니다.</li>
                    <li><strong>고객 신뢰 확보:</strong> 구매자들이 인증된 판매자의 상품을 더 선호하게 되어 매출 증대에 기여할 수 있습니다.</li>
                </ul>

                <h3>제3조 (인증 절차 및 기준)</h3>
                <p>
                    판매자 인증은 다음의 기준을 종합적으로 심사하여 진행됩니다.
                </p>
                <ol>
                    <li><strong>사업장 실사:</strong> 우리샵 담당자가 직접 사업장에 방문하여 실제 운영 여부 및 환경을 확인합니다.</li>
                    <li><strong>누적 거래 수:</strong> 일정 기간 동안의 누적 거래 건수 및 거래액을 기준으로 안정적인 판매 활동을 평가합니다. (예: 최근 6개월간 100건 이상)</li>
                    <li><strong>고객 만족도:</strong> 구매자 리뷰 평점, 긍정적 리뷰 비율, 고객 문의 응대율 등을 통해 고객 만족도를 평가합니다.</li>
                    <li><strong>법규 준수:</strong> 전자상거래법 등 관련 법규를 성실히 준수하는지 여부를 확인합니다.</li>
                </ol>

                <h3>제4조 (인증 유지 조건)</h3>
                <p>
                    인증 자격은 영구적이지 않으며, 구매자 보호를 위해 정기적으로 자격 유지 심사를 진행합니다.
                </p>
                <ul>
                    <li><strong>정기 점검:</strong> 인증을 획득한 판매자는 2개월에 한 번씩 거래 현황, 고객 만족도 등 주요 지표에 대한 정기 점검을 받게 됩니다.</li>
                    <li><strong>자격 박탈:</strong> 점검 결과 인증 기준에 미달하거나, 관련 법규 위반 등 중대한 문제가 발견될 경우 인증 자격이 일시 정지 또는 박탈될 수 있습니다.</li>
                </ul>

                <h3>제5조 (인증 신청 방법)</h3>
                <p>
                    판매자 인증을 희망하시는 경우, 아래 버튼을 통해 고객센터 1:1 문의로 '판매자 인증 신청'이라는 제목으로 문의를 남겨주시면 담당자가 절차에 따라 안내해 드립니다.
                </p>
                <div className="apply-button-container">
                    <button className="apply-btn" onClick={() => navigate('/qna')}>1:1 문의 바로가기</button>
                </div>
            </div>
        </div>
    );
}

export default SellerVerificationGuide;