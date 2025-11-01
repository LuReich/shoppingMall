# React Quill 이미지 처리 가이드 (data-image-id 방식)

## 개요
- **방식**: Blob URL + data-image-id 속성을 이용한 이미지 매칭
- **장점**: temp 폴더 불필요, 등록 버튼 클릭 시 한 번에 처리, 순서 보장
- **핵심**: imageMapping 배열로 이미지 순서 전달

---

## 백엔드 구현 완료 사항

### 1. ProductCreateDTO에 imageMapping 필드 추가
```java
// imageMapping: React Quill에서 data-image-id 순서대로 이미지 ID 배열
private java.util.List<String> imageMapping;
```

### 2. replaceImageIdsWithUrls() 메서드 구현
- `data-image-id` 속성을 가진 `<img>` 태그를 찾음
- imageMapping에서 해당 ID의 인덱스를 찾음
- 해당 인덱스의 실제 저장된 URL로 교체
- `data-image-id` 속성 제거

---

## 프론트엔드 구현 가이드

### 1. 이미지 삽입 시 (React Quill imageHandler)

```javascript
const imageHandler = () => {
  const input = document.createElement('input');
  input.setAttribute('type', 'file');
  input.setAttribute('accept', 'image/*');
  input.click();

  input.onchange = async () => {
    const file = input.files[0];
    if (!file) return;

    // 1. 고유 ID 생성 (UUID 권장)
    const imageId = generateUniqueId(); // 예: 'img_1234567890' 또는 UUID

    // 2. Blob URL 생성 (미리보기용)
    const blobUrl = URL.createObjectURL(file);

    // 3. imageMapping 배열에 ID 추가 (순서 보장)
    setImageMapping(prev => [...prev, imageId]);

    // 4. descriptionImages 배열에 File 추가 (같은 순서로)
    setDescriptionImages(prev => [...prev, file]);

    // 5. React Quill에 이미지 삽입 (Blob URL + data-image-id 속성)
    const quill = quillRef.current.getEditor();
    const range = quill.getSelection();
    quill.insertEmbed(range.index, 'image', blobUrl);
    
    // data-image-id 속성 추가
    const delta = quill.getContents();
    const imageIndex = range.index;
    const blot = quill.getLeaf(imageIndex)[0];
    if (blot && blot.domNode && blot.domNode.tagName === 'IMG') {
      blot.domNode.setAttribute('data-image-id', imageId);
    }
  };
};

// UUID 생성 함수 (예시)
const generateUniqueId = () => {
  return 'img_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
};
```

### 2. 이미지 삭제 시

```javascript
// Quill의 text-change 이벤트 감지
const handleTextChange = (content, delta, source, editor) => {
  if (source === 'user') {
    const currentHtml = editor.getHTML();
    
    // 현재 HTML에서 data-image-id 목록 추출
    const parser = new DOMParser();
    const doc = parser.parseFromString(currentHtml, 'text/html');
    const images = doc.querySelectorAll('img[data-image-id]');
    const remainingIds = Array.from(images).map(img => img.getAttribute('data-image-id'));

    // imageMapping과 비교하여 삭제된 이미지 찾기
    const deletedIds = imageMapping.filter(id => !remainingIds.includes(id));

    if (deletedIds.length > 0) {
      // imageMapping에서 삭제된 ID 제거
      const newMapping = imageMapping.filter(id => !deletedIds.includes(id));
      
      // descriptionImages에서도 같은 인덱스 제거
      const newImages = descriptionImages.filter((file, index) => 
        !deletedIds.includes(imageMapping[index])
      );

      setImageMapping(newMapping);
      setDescriptionImages(newImages);

      // Blob URL 해제 (메모리 관리)
      deletedIds.forEach(id => {
        const index = imageMapping.indexOf(id);
        if (index >= 0) {
          const img = doc.querySelector(`img[data-image-id="${id}"]`);
          if (img) URL.revokeObjectURL(img.src);
        }
      });
    }
  }
};
```

### 3. 상품 등록 시 (FormData 전송)

```javascript
const handleSubmit = async () => {
  const formData = new FormData();

  // 1. 상품 기본 정보
  const productData = {
    productName: '상품명',
    categoryId: 1,
    price: 10000,
    stock: 100,
    description: quillRef.current.root.innerHTML, // HTML (data-image-id 포함)
    shippingInfo: '배송 정보',
    imageMapping: imageMapping // 이미지 ID 배열
  };
  formData.append('productData', new Blob([JSON.stringify(productData)], { type: 'application/json' }));

  // 2. 메인 이미지
  formData.append('mainImage', mainImageFile);

  // 3. 서브 이미지들
  subImages.forEach(file => {
    formData.append('subImages', file);
  });

  // 4. description 이미지들 (imageMapping 순서대로)
  descriptionImages.forEach(file => {
    formData.append('descriptionImages', file);
  });

  // 5. API 호출
  await axios.post('/api/v1/seller/product/create', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};
```

---

## 핵심 포인트

### 1. imageMapping의 역할
- **순서 보장**: imageMapping[0]의 ID가 descriptionImages[0]의 파일과 매칭됨
- **HTML 매칭**: HTML 내 `<img data-image-id="xxx">`의 xxx를 imageMapping에서 찾아 인덱스 확인
- **URL 교체**: 백엔드가 해당 인덱스의 실제 저장된 URL로 교체

### 2. Blob URL 사용 이유
- **미리보기용**: 사용자가 즉시 이미지를 볼 수 있도록
- **임시**: 등록 완료 후 `URL.revokeObjectURL()`로 해제

### 3. data-image-id 속성
- **고유성**: 각 이미지마다 고유한 ID 부여
- **추적**: 이미지 삭제 시 어떤 이미지가 삭제되었는지 추적
- **매칭**: imageMapping 배열과 HTML을 연결하는 키

### 4. 백엔드 처리 흐름
1. descriptionImages를 순서대로 저장 → URL 리스트 생성
2. HTML에서 `<img data-image-id="xxx">` 찾기
3. imageMapping에서 xxx의 인덱스 찾기
4. 해당 인덱스의 URL로 src 교체
5. data-image-id 속성 제거

---

## 예시 데이터 흐름

### 프론트엔드
```javascript
// 사용자가 이미지 3개 삽입 시
imageMapping = ['img_001', 'img_002', 'img_003']
descriptionImages = [File1, File2, File3]

description HTML:
<p>텍스트</p>
<img src="blob:http://..." data-image-id="img_001" />
<p>중간 텍스트</p>
<img src="blob:http://..." data-image-id="img_002" />
<img src="blob:http://..." data-image-id="img_003" />
```

### 백엔드 처리
```
1. File1, File2, File3 순서대로 저장
   → ['/product/1/description/uuid1.jpg', '/product/1/description/uuid2.jpg', '/product/1/description/uuid3.jpg']

2. HTML 파싱:
   - img_001 찾음 → imageMapping에서 인덱스 0 → URL[0]으로 교체
   - img_002 찾음 → imageMapping에서 인덱스 1 → URL[1]으로 교체
   - img_003 찾음 → imageMapping에서 인덱스 2 → URL[2]으로 교체

3. 최종 HTML:
<p>텍스트</p>
<img src="/product/1/description/uuid1.jpg" />
<p>중간 텍스트</p>
<img src="/product/1/description/uuid2.jpg" />
<img src="/product/1/description/uuid3.jpg" />
```

---

## 주의사항

1. **imageMapping과 descriptionImages는 항상 같은 순서 유지**
2. **이미지 삭제 시 두 배열 모두에서 제거**
3. **Blob URL은 컴포넌트 언마운트 시 해제 (useEffect cleanup)**
4. **UUID 사용 권장** (충돌 방지)
5. **상품 수정 시**: 기존 이미지는 유지, 새 이미지만 imageMapping에 추가

---

## 상품 수정 시 추가 구현 필요사항

상품 수정은 기존 이미지(이미 URL이 있음)와 새 이미지(Blob URL)를 구분해야 합니다:

```javascript
// 기존 이미지: data-image-id 없음, src는 실제 URL
// 새 이미지: data-image-id 있음, src는 Blob URL

const handleUpdate = () => {
  const currentHtml = quillRef.current.root.innerHTML;
  const parser = new DOMParser();
  const doc = parser.parseFromString(currentHtml, 'text/html');
  
  // 새로 추가된 이미지만 찾기
  const newImages = doc.querySelectorAll('img[data-image-id]');
  
  // 기존 로직과 동일하게 imageMapping과 descriptionImages 구성
  // ...
};
```

백엔드에서는 기존 description을 불러와서 새 이미지만 교체하면 됩니다.
