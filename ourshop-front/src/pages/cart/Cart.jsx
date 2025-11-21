import { useCart } from '../../hooks/useCart';
import '../../assets/css/Cart.css';
import styles from '../../assets/css/Button.module.css';
import { useNavigate } from 'react-router';
import { useEffect, useState } from 'react';
import ProductSection from '../../components/home/ProductSection';
import { useProduct } from '../../hooks/useProduct';
import ProductCard from '../../components/product/ProductCard';
import Loader from '../../utils/Loaders';


function Cart(props) {
    
    const navigate = useNavigate();

    const { getProductList } = useProduct(); //장바구니 비어있을시 추천
    //해당 로그인 유저의 장바구니 내역 가져오기
    const { getCartList, updateCartQuantity, deleteCartItem, deleteSelectedCartItems} = useCart();
    

    const {data: productListData} = getProductList(); //장바구니 비어있을시 추천
    const { data: cartItemsCont, isLoading } = getCartList();
    const {mutate: updateQuant} = updateCartQuantity();
    const {mutate: deleteItem} = deleteCartItem();
    const {mutate: deleteSelectedItme} = deleteSelectedCartItems();
    

    const productList = productListData?.content?.content || []; //장바구니 비어있을시 추천
    //랜덤으로 4개
    const filteredProduct = [...productList].sort(() => Math.random() - 0.5).slice(0, 4);
    
    const cartItems = Array.isArray(cartItemsCont?.content?.content) ? cartItemsCont.content.content : [];
    const cartIds = cartItems.map(x => x.cartId);

    console.log("cartItemsCont:", cartItemsCont);

    //장바구니 리스트 배열
    const [cartArr, setCartArr] = useState([]);
    const [isDisabled, setIsDisabled] = useState(false);
    

    const [quantities, setQuantities] = useState({});

    // 장바구니 불러오면 로컬 입력값 초기화
    useEffect(() => {
      if (cartItems.length > 0) {
        setQuantities(Object.fromEntries(cartItems.map(it => [it.cartId, String(it.quantity)])));
      } else {
        setQuantities({});
      }
    }, [cartItems]);  

    // 데이터 로드 후 전체 선택 상태로 세팅
    useEffect(() => {
      if (cartItems.length > 0) {
        setCartArr(cartItems.map((item) => item.cartId));
      }
    }, [cartItems]);
  

  

    if (isLoading) return <Loader/>;
   
    


    //수량 변경 시
    const handleQuantityChange = (cartId, quantity) => {
      if(quantity < 1){
        setIsDisabled(true);
        updateQuant({cartId, quantity: 1});
      }

      if(quantity > 50){
        alert("최대 주문 수량은 50개 입니다.");
        updateQuant({cartId, quantity: 50});
      }
      updateQuant({cartId, quantity});
    }

    //수랑 타이핑 시
    const handleQuantityTyping = (cartId, e) => {
      const v = e.target.value;
      // 숫자만 허용(빈 문자열은 입력 중 허용)
      if (/^\d*$/.test(v)) {
        setQuantities(prev => ({ ...prev, [cartId]: v }));
      }
    };


    const clamp = (n, min, max) => Math.max(min, Math.min(max, n));

    // 포커스 아웃에만 업데이트
    const commitQuantity = (cartId) => {
      let raw = quantities[cartId];
      // 빈 입력이면 1로
      let q = Number(raw || 1);

      // alert 조건 추가
      if (q < 1) {
        alert("최소 주문 수량은 1개입니다.");
      } else if (q > 50) {
        alert("최대 주문 수량은 50개입니다.");
      }

      q = clamp(q, 1, 50); // 1~50 제한

      // UI 값 보정
      setQuantities(prev => ({ ...prev, [cartId]: String(q) }));

      // 현재 서버 값과 다를 때만 호출
      const serverCurrent = cartItems.find(x => x.cartId === cartId)?.quantity;
      if (serverCurrent !== q) {
          updateQuant({ cartId, quantity: q });
      }
    };

    // 엔터로도 커밋
    const handleKeyDown = (cartId, e) => {
        if (e.key === 'Enter') {
        e.currentTarget.blur(); // 엔터치면 blur 유도 -> commit
      }
    };


    //체크박스 선택시
      const handleCartArr = (cartId) => {
        setCartArr((prev) => {
          if (prev.includes(cartId)) {
            // 이미 선택되어 있으면 제거
            return prev.filter((id) => id !== cartId);
          } else {
            // 선택되어 있지 않으면 추가
            return [...prev, cartId];
          }
      });
    };

    const orderItems = cartItems.filter((item) => cartArr.includes(item.cartId));
    console.log("장바구니 배열",cartArr);
    console.log("장바구니에서 주문할 아이템",orderItems);

    //선택 상품 삭제
    const deleteSelectBtn = () => {
      deleteSelectedItme(cartArr);
    }

    //전체 상품 삭제
    const deleteAllBtn = () => {
      if (!cartIds?.length) return;
      // 상태에 의존하지 말고, 바로 최신 값으로 호출
      setCartArr(cartIds); 
      deleteSelectedItme(cartIds);  
    };

    //전체 주문 가격
    const totalPrice = cartItems.reduce(
        (sum, item) => sum + item.pricePerItem * item.quantity,
        0
    );

    //주문하기 버튼 클릭시
    const orderBtn = () => {
        if (!cartIds?.length) return;
        setCartArr(cartIds); 
        navigate('/order',{state: {cartIds}});
    }

    //선택 주문하기 버튼 클릭시 
    const orderSelectBtn = () => {
        navigate('/order',{state: { cartIds: cartArr } });
    }
    return (
    <div className='cart-container'>
      <h2>장바구니</h2>
      {
        !cartItems || cartItems.length === 0 ?
        <>
          <p className='cart-empty-text' style={{textAlign: 'center', fontSize: '18px'}}>장바구니가 비어있어요. 장바구니에 담을 상품을 추가하세요!</p>
          <div className='cart-empty-recom-box'>
            <p onClick={() => navigate('/products')}>이 상품 어때요?</p>
             <div className='cart-product-recom'>
              {
                filteredProduct?.map((item) => (
                  <ProductCard key={item.productId} product={item}/>
                ))
              }
             </div>
          </div>
        </>
        :
        <div className='cart-list-box'>
      <div className='table-scroll-box'>
      <table>
        <thead>
          <tr>
            <th>선택</th>
            <th>상품정보</th>
            <th>판매가</th>
            <th>수량</th>
            <th>구매가</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          {cartItems.map((item) => (
            <tr key={item.cartId}>
              <td>
                <input type='checkbox' onChange={()=> handleCartArr(item.cartId)} checked={cartArr.includes(item.cartId)}/>
              </td>
              <td onClick={() => navigate(`/product/${item.productId}`)} className='cart-product-name'>{item.productName}</td>
              <td>{item.pricePerItem.toLocaleString()}원</td>
              <td className='quant-update-box'>
                <button className='quat-btn' onClick={() => handleQuantityChange(item.cartId, item.quantity - 1)} disabled={item.quantity <=1}>-</button>
                <input
                  type="number"
                  min="1"
                  max="50"
                  value={quantities[item.cartId] ?? ""}
                  onChange={(e) => handleQuantityTyping(item.cartId, e)}   // 입력 중엔 로컬만
                  onBlur={() => commitQuantity(item.cartId)}               // 포커스 아웃에만 업데이트
                  onKeyDown={(e) => handleKeyDown(item.cartId, e)}         // 엔터로도 커밋
                  className="quantity-input"
                />
                <button className='quat-btn' onClick={() => handleQuantityChange(item.cartId, item.quantity + 1)}>+</button>
              </td>
              <td>{(item.pricePerItem * item.quantity).toLocaleString()}원</td>
              <td>
                <button type='button' className='quat-btn' onClick={()=> deleteItem(item.cartId)}>X</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
       <div className='delete-box'>
        <button type='button' className="order-delete-cart-btn" onClick={deleteSelectBtn}>선택 상품 삭제</button>
        {/*<button type='button' className='order-delete-cart-btn' onClick={deleteAllBtn}>전체 상품 삭제</button>*/}
      </div>
      </div>
     
      <div className='total-info-container'>
        <div className='total-cart-info'>
          <h4>주문 예정 상품</h4>
          {
            orderItems.length > 0 ?
             orderItems?.map((item) => (
              <p className='cart-item-info'>{item.productName} X {item.quantity}</p>
              ))
             :
             <p>선택 상품이 없습니다.</p>
          }
        </div>
        <div className='total-cart-info'>
          <h4>총 결제 예상 금액</h4>
          <strong>{totalPrice.toLocaleString()}원</strong>
        </div>
        <div className='order-btn-box'>
          {/*
          <button type='button' className={styles.commonBtn} onClick={orderSelectBtn}>선택 주문하기</button>
          <button type='button' className={styles.commonBtn} onClick={orderBtn}>주문하기</button>
          */}
          <button type='button' className='order-cart-btn' onClick={orderSelectBtn}>주문하기</button>
        </div>
      </div>
     </div>
      }
      
    </div>

    );
}

export default Cart;