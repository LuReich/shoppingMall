import React, { useEffect, useState } from "react";
import "../../assets/css/Order.css";
import Address from "../../components/address/Address";
import { useOrder } from '../../hooks/useOrder';
import { authStore } from "../../store/authStore";
import { useLocation, useNavigate } from "react-router";
import { useCart } from "../../hooks/useCart";

function Order() {

  const navigate = useNavigate();
  const location = useLocation();
  const {cartIds, productData} = location.state;

  //바로 주문하기와 장바구니에서 주문하기 함께 사용하기 위함
  const productArr = []
  productArr.push(productData);

  if(cartIds){
    console.log("장바구니 넘어온 state", cartIds);
  }

  const {createOrder} = useOrder();
  const {getCartList} = useCart();

  const {mutate: createOrderMutate} = createOrder();
  const {data: cartListCont} = getCartList();

  const user = authStore(state => state.user?.content);

  const allCartItems = cartListCont?.content?.content;

  //장바구니에서 주문하기일 경우 filter
  //바로 주문하기일 경우 productArr
  const cartItems = cartIds
  ? allCartItems?.filter(x => cartIds.includes(x.cartId))
  : productArr;

  console.log("주문할 아이템", cartItems);
  console.log("주문할 아이템2", productArr);

  // 기본 배송지 (API 응답 가정)
  const defaultAddress = {
    recipient: user.nickname,
    phone: user.phone,
    address: user.address,
    detailAddress: user.addressDetail,
  };

  // 배송 관련 상태
  const [deliveryType, setDeliveryType] = useState("default");
  const [deliveryInfo, setDeliveryInfo] = useState({
    recipient: "",
    phone: "",
    address: "",
    detailAddress: "",
  });


  // 배송요청사항 상태
  const [deliveryMsg, setDeliveryMsg] = useState("");
  const [entryType, setEntryType] = useState("비밀번호");
  const [entryPassword, setEntryPassword] = useState("");

  // 결제수단
  const [paymentMethod, setPaymentMethod] = useState("card");

  // 기본 배송지 세팅
  useEffect(() => {
    if (deliveryType === "default" && defaultAddress) {
      setDeliveryInfo(defaultAddress);
    } else if (deliveryType === "new") {
      setDeliveryInfo({
        recipient: "",
        phone: "",
        //zipcode: "",
        address: "",
        detailAddress: "",
      });
    }
  }, [deliveryType]);

  const Kor = {
    "card": "신용카드",
    "kakao": "카카오페이",
    "bank": "계좌이체"
  }


  //총구매가 구하는 함수
  const salePrice = (item) => {
    return cartIds? item.pricePerItem * item.quantity : item.price * item.quantity;
  }

  //총 금액
  const totalPrice = cartIds && cartItems?cartItems.reduce(
    (sum, item) => sum + item.pricePerItem * item.quantity,
    0
  ) : productArr.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );;


  const handleSubmit = (e) => {
    if (!cartItems || cartItems.length === 0) {
      alert("주문할 상품이 없습니다.");
      return;
    }

    if (!deliveryInfo.recipient?.trim()) {
      alert("받는 분 이름을 입력해주세요.");
      return;
    }  

    if (!deliveryInfo.phone?.trim()) {
      alert("연락처를 입력해주세요.");
      return;
    }

    if (!deliveryInfo.address?.trim()) {
      alert("주소를 입력해주세요.");
     return;
    }

    if (!deliveryInfo.detailAddress?.trim()) {
      alert("상세 주소를 입력해주세요.");
      return;
    }

    if (!paymentMethod) {
      alert("결제수단을 선택해주세요.");
      return;
    }
    
    e.preventDefault();

    const orderData = {
      buyerUid: user?.buyerUid || user?.uid,
      totalPrice,
      recipientName: deliveryInfo.recipient,
      recipientAddress: deliveryInfo.address,
      recipientAddressDetail: deliveryInfo.detailAddress,
      orderStatus: "PAID",
      orderDetails: cartItems?.map(item => ({
        productId: item.productId,
        sellerUid: item.sellerUid,
        quantity: item.quantity,
        pricePerItem: cartIds? item.pricePerItem : item.price,
        orderDetailStatus: "PAID",
      })),
    };

    console.log("주문 데이터 전송:", orderData);

    createOrderMutate(orderData);

    alert(`${Kor[paymentMethod]}로 결제가 완료되었습니다!`);
    navigate('/order/complete', {state:{paymentMethod}});
  };

  return (
    <div className="order-page">
      <h1 className="page-title">주문 / 결제</h1>

      <form onSubmit={handleSubmit} className="order-form">
        {/* 배송지 선택 */}
        <h2>배송지 정보</h2>
        <div className="delivery-type">
          <label>
            <input
              type="radio"
              value="default"
              checked={deliveryType === "default"}
              onChange={(e) => setDeliveryType(e.target.value)}
            />
            기본 배송지
          </label>
          <label>
            <input
              type="radio"
              value="new"
              checked={deliveryType === "new"}
              onChange={(e) => setDeliveryType(e.target.value)}
            />
            신규 배송지
          </label>
        </div>
        <Address deliveryInfo={deliveryInfo} setDeliveryInfo={setDeliveryInfo}/>
        
        {/* 배송 요청사항 */}
        <h2>배송 요청사항</h2>
        <table className="delivery-request-table">
          <tbody>
            <tr>
              <th>배송 메시지</th>
              <td>
                <select
                  value={deliveryMsg}
                  onChange={(e) => setDeliveryMsg(e.target.value)}
                >
                  <option value="">배송 메시지를 선택해주세요.</option>
                  <option value="문앞에 두세요">문 앞에 두세요</option>
                  <option value="경비실에 맡겨주세요">
                    경비실에 맡겨주세요
                  </option>
                  <option value="배송 전 연락주세요">배송 전 연락주세요</option>
                </select>
              </td>
            </tr>
            <tr>
              <th>공동현관 출입방법</th>
              <td className="enter-method">
                <label>
                  <input
                    type="radio"
                    value="비밀번호"
                    checked={entryType === "비밀번호"}
                    onChange={(e) => setEntryType(e.target.value)}
                  />
                  비밀번호
                </label>
                <label>
                  <input
                    type="radio"
                    value="경비실 호출"
                    checked={entryType === "경비실 호출"}
                    onChange={(e) => setEntryType(e.target.value)}
                  />
                  경비실 호출
                </label>
                <label>
                  <input
                    type="radio"
                    value="자유출입"
                    checked={entryType === "자유출입"}
                    onChange={(e) => setEntryType(e.target.value)}
                  />
                  자유출입 가능
                </label>
              </td>
            </tr>
            <tr>
              <th>공동현관 비밀번호</th>
              <td>
                <input
                  type="text"
                  className="pwd-input"
                  value={entryPassword}
                  onChange={(e) => setEntryPassword(e.target.value)}
                  placeholder="예: 1234"
                />
              </td>
            </tr>
          </tbody>
        </table>

        {/* 주문상품 */}
        <h2>주문상품</h2>
        <table className="order-table">
          <thead>
            <tr>
              <th>상품정보</th>
              <th>판매가</th>
              <th>수량</th>
              <th>구매가</th>
            </tr>
          </thead>
          <tbody>
            {cartItems?.map((item) => (
              <tr key={item.productId}>
                <td className="item-info">
                  <img src={item.thumbnailUrl} alt={item.productName} />
                  <div>
                    <p className="item-name">{item.productName}</p>
                  </div>
                </td>
                <td>{cartIds? item.pricePerItem?.toLocaleString() : item.price?.toLocaleString()}원</td>
                <td>{item.quantity}</td>
                <td className="sale-price">
                  {salePrice(item)?.toLocaleString()}원
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* 결제 수단 */}
        <h2>결제 수단</h2>
        <div className="payment-methods">
          <label>
            <input
              type="radio"
              name="payment"
              value="card"
              checked={paymentMethod === "card"}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            신용카드
          </label>
          <label>
            <input
              type="radio"
              name="payment"
              value="kakao"
              checked={paymentMethod === "kakao"}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            카카오페이
          </label>
          <label>
            <input
              type="radio"
              name="payment"
              value="bank"
              checked={paymentMethod === "bank"}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            계좌이체
          </label>
        </div>

        {/* 결제 버튼 */}
        <div className="payment-summary">
          <p>
            총 결제 금액: <strong style={{color: 'red'}}>{totalPrice?.toLocaleString()}원</strong>
          </p>
          <button type="submit" className="pay-btn">
            결제하기
          </button>
        </div>
      </form>
    </div>
  );
}

export default Order;
