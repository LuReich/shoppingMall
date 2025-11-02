import React from "react";
import styles from "../../assets/css/Address.module.css";

function Address({ deliveryInfo, setDeliveryInfo }) {
  const handleChange = (e) => {
    const { name, value } = e.target;
    setDeliveryInfo((prev) => ({ ...prev, [name]: value }));
  };

  // 다음 주소 팝업 (설치 X, script 태그 이용)
  const openDaumPostcode = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        let fullAddress = data.address;
        let extraAddress = "";

        if (data.addressType === "R") {
          if (data.bname !== "") extraAddress += data.bname;
          if (data.buildingName !== "") {
            extraAddress +=
              extraAddress !== "" ? `, ${data.buildingName}` : data.buildingName;
          }
          fullAddress += extraAddress !== "" ? ` (${extraAddress})` : "";
        }

        setDeliveryInfo((prev) => ({
          ...prev,
          //zipcode: data.zonecode,
          address: fullAddress,
        }));

        document.getElementById("detailAddress").focus();
      },
    }).open();
  };

  return (
    <table className={styles.table}>
      <tbody>
        <tr>
          <th>받는분</th>
          <td>
            <input
              type="text"
              name="recipient"
              value={deliveryInfo.recipient}
              onChange={handleChange}
              placeholder="이름을 입력하세요"
              required
              className={styles.input}
            />
          </td>
        </tr>
        <tr>
          <th>연락처</th>
          <td>
            <input
              type="text"
              name="phone"
              value={deliveryInfo.phone}
              onChange={handleChange}
              placeholder="010-1234-5678"
              required
              className={styles.input}
            />
          </td>
        </tr>
        <tr>
          <th>주소</th>
          <td className={styles.addressArea}>
            {/*<div className={styles.zipcodeRow}>
              <input
                className={styles.zipcodeInput}
                name="zipcode"
                value={deliveryInfo.zipcode}
                placeholder="우편번호"
                readOnly
              />
            </div>*/}

            <div className={styles.addrRow}>
            <input
              className={styles.addressInput}
              name="address"
              value={deliveryInfo.address}
              placeholder="주소"
              readOnly
            />
            <button
                type="button"
                className={styles.searchBtn}
                onClick={openDaumPostcode}
              >
                주소 검색
              </button>
            </div>
            <input
              id="detailAddress"
              className={styles.detailAddrInput}
              name="detailAddress"
              value={deliveryInfo.detailAddress}
              onChange={handleChange}
              placeholder="상세주소 입력"
            />
          </td>
        </tr>
      </tbody>
    </table>
  );
}

export default Address;
