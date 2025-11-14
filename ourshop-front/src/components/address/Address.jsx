import React, { useEffect, useState } from "react";
import styles from "../../assets/css/Address.module.css";

function Address({ deliveryInfo, setDeliveryInfo }) {

   const [formattedPhone, setFormattedPhone] = useState("");

  // 전화번호 포맷 함수
  const formatPhone = (num) => {
    if (!num) return "";
    const digits = num.replace(/\D/g, "");
    if (digits.length < 4) return digits;
    if (digits.length < 7) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
    if (digits.length < 11)
      return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(6)}`;
    return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7, 11)}`;
  };

  // deliveryInfo.phone이 바뀔 때마다 표시용 포맷 갱신
  useEffect(() => {
    setFormattedPhone(formatPhone(deliveryInfo.phone || ""));
  }, [deliveryInfo.phone]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "phone") {
      const onlyNums = value.replace(/\D/g, "");
      setDeliveryInfo((prev) => ({ ...prev, phone: onlyNums }));
    } else {
      setDeliveryInfo((prev) => ({ ...prev, [name]: value }));
    }
  };

  
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
              value={deliveryInfo.recipient || ""}
              onChange={handleChange}
              placeholder="이름을 입력하세요"
              className={styles.input}
            />
          </td>
        </tr>
        <tr>
          <th>구매자 연락처</th>
          <td>
            <input
              type="text"
              name="phone"
              value={formattedPhone}
              onChange={handleChange}
              placeholder="010-1234-5678"
              className={styles.input}
              maxLength={13}
              readOnly
              disabled
            />
          </td>
        </tr>
        <tr>
          <th>주소</th>
          <td className={styles.addressArea}>
            <div className={styles.addrRow}>
              <input
                className={styles.addressInput}
                name="address"
                value={deliveryInfo.address || ""}
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
              value={deliveryInfo.detailAddress || ""}
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
