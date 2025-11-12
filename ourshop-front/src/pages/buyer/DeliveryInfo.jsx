import React from "react";
import { useLocation } from "react-router";
import dayjs from "dayjs";
import "../../assets/css/DeliveryInfo.css";

function DeliveryInfo() {
  const location = useLocation();
  const { order, detail } = location.state;

  const statusKor = {
    PAID: "결제완료",
    SHIPPING: "배송중",
    DELIVERED: "배송완료",
    CANCELED: "취소",
  };

  console.log("주문 정보", order);
  console.log("배송상품 상세 정보", detail);
  const statusOrder = ["PAID", "SHIPPING", "DELIVERED", "CANCELED"];
  const currentIndex = statusOrder.indexOf(detail.orderDetailStatus);

  // 배송중일 때 예상 도착일 (updateAt + 3일)
  const expectedDate =
    detail.orderDetailStatus === "SHIPPING"
      ? dayjs(detail.updateAt).add(3, "day").format("YYYY-MM-DD")
      : null;

  return (
    <div className="del-info-detail-container">
      <h2>배송 조회</h2>

      <div className="del-address-box">
        <h4>배송지 정보</h4>
        <p>{order.recipientName} ({order.buyerPhone.replace(/^(\d{3})(\d{3,4})(\d{4})$/, "$1-$2-$3")})</p>
        <p>{order.recipientAddress} {order.recipientAddressDetail}</p>
      </div>
      <div className="del-card">
        {/* 상품 정보 */}
        <div className="del-product-info">
          <div className="del-product-name">{detail.productName}</div>
          <div className="del-product-company">{detail.companyName}</div>
          {detail.recipientAddress && (
            <div className="del-product-address">
              배송지: {detail.recipientAddress} {detail.recipientAddressDetail}
            </div>
          )}
        </div>

        {/* 진행 바 */}
        <div className="del-progress-bar">
          {statusOrder.slice(0, 3).map((status, index) => (
            <div key={status} className="del-step">
              {/* 연결선 (현재보다 큰 단계는 회색 유지) */}
              {index <= 2 && (
                <div
                  className={`del-bar ${
                    index <= currentIndex &&
                    detail.orderDetailStatus !== "CANCELED"
                      ? "active"
                      : ""
                  }`}
                ></div>
              )}
              {/* 원 */}
              <div
                className={`del-circle ${
                  index <= currentIndex && detail.orderDetailStatus !== "CANCELED"
                    ? "active"
                    : ""
                } ${detail.orderDetailStatus === "CANCELED" ? "canceled" : ""}`}
              >
                {index + 1}
              </div>
              <div className="del-label">{statusKor[status]}</div>
            </div>
          ))}
        </div>

        {/* 상태 표시 */}
        {detail.orderDetailStatus === "CANCELED" ? (
          <p className="del-status canceled">
            주문이 취소되었습니다. ({dayjs(detail.updateAt).format("YYYY-MM-DD")})
          </p>
        ) : (
          <p className="del-status">
            현재 상태: <b>{statusKor[detail.orderDetailStatus]}</b>{" "}
            ({dayjs(detail.updateAt).format("YYYY-MM-DD")})
          </p>
        )}

        {/* 예상 도착일 */}
        {expectedDate && (
          <p className="del-expected-date">📦 예상 도착일: {expectedDate}</p>
        )}
      </div>
    </div>
  );
}

export default DeliveryInfo;
