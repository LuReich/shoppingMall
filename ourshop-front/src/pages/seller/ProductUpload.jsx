import React, { useMemo, useState, useRef, useEffect, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import ReactQuill from "react-quill-new";
import Quill from "quill";
import "quill/dist/quill.snow.css";
import { useProduct } from "../../hooks/useProduct";
import "../../assets/css/ProductUpload.css";
import { useCategory } from "../../hooks/useCategory";
import { useForm, Controller } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

function ProductUpload() {
  const navigate = useNavigate();
  const { productId } = useParams();

  const { getCategoryList } = useCategory();
  const { uploadTempDescriptionImage, createProduct, getProductDetail, getProductDescription } = useProduct();


  const { data: categoryData } = getCategoryList();
  const { mutateAsync: uploadTempImage } = uploadTempDescriptionImage();
  const { mutate: createMutate } = createProduct();
  const { data: productData } = getProductDetail(productId);
  const { data: productDescriptionData } = getProductDescription(productId);

  // 상품 수정 모드 구분 (예: 등록 vs 수정)
  const isEditMode = useMemo(() => !!productId, [productId]);

  // description 이미지 관리 (백엔드 data-image-id 방식)
  const [descriptionImages, setDescriptionImages] = useState([]); // {id, file, blobUrl}
  const imageIdCounter = useRef(0);

  // Yup 스키마 정의
  const productSchema = useMemo(() => yup.object({
    productName: yup
      .string()
      .required("상품명은 필수 항목입니다."),
    parentCategoryId: yup
      .string()
      .required("대분류 카테고리를 선택해주세요."),
    categoryId: yup
      .string()
      .required("소분류 카테고리를 선택해주세요."),
    price: yup
      .number()
      .typeError("가격은 숫자여야 합니다.")
      .positive("가격은 0보다 커야 합니다.")
      .required("가격은 필수 항목입니다."),
    stock: yup
      .number()
      .typeError("재고는 숫자여야 합니다.")
      .integer("재고는 정수여야 합니다.")
      .min(0, "재고는 0 이상이어야 합니다.")
      .required("재고는 필수 항목입니다."),
    shippingInfo: yup
      .string(),
    description: yup
      .string()
      .required("상품 상세 설명은 필수 항목입니다."),
    mainImage: yup
      .mixed()
      .test("required", "대표 이미지는 필수 항목입니다.", (value) => isEditMode || (value && value.length > 0)),
  }), [isEditMode]);

  const {
    register,
    handleSubmit,
    formState: { errors },
    control,
    watch,
    setValue,
    trigger,
    reset,
  } = useForm({
    resolver: yupResolver(productSchema),
    mode: "onChange",
  });

  const parentCategoryId = watch("parentCategoryId");

  // 카테고리 가공 
  const { parentCategories, subCategories } = useMemo(() => {
    if (!categoryData?.content) return { parentCategories: [], subCategories: [] };
    const all = categoryData.content;
    return {
      parentCategories: all.filter((cat) => cat.parentId === null),
      subCategories: all.filter((cat) => cat.parentId !== null),
    };
  }, [categoryData]);

  // 수정 모드 시 기본 값 세팅
  useEffect(() => {
    if (isEditMode && productData?.content && productDescriptionData?.content && subCategories.length > 0) {
      const p = productData.content;
      const pd = productDescriptionData.content;
      console.log("기존 상품 데이터:", p);

      const subCategory = subCategories.find(sc => sc.categoryId === p.categoryId);

      reset({
        productName: p.productName,
        price: p.price,
        stock: p.stock,
        shippingInfo: pd.shippingInfo,
        description: pd.description?.replaceAll('src="/temp/', 'src="http://localhost:9090/temp/')
          ?.replaceAll('src="/product/', 'src="http://localhost:9090/product/')
          || '',
        categoryId: p.categoryId,
        parentCategoryId: subCategory?.parentId,
      });

      setMainImageUrl(`http://localhost:9090${p.thumbnailUrl}`);
      // 기존 서브 이미지를 상태에 저장
      const existingImages = p.productImages?.map(img => ({
        url: `http://localhost:9090${img.imagePath}`,
        isNew: false,
        imageId: img.productImageId // 삭제 추적을 위한 ID
      })) || [];
      setSubImageUrls(existingImages);
    }
  }, [isEditMode, productData, productDescriptionData, subCategories, reset]);

  // 이미지 미리보기 상태
  const [mainImageUrl, setMainImageUrl] = useState(null);
  const [subImageUrls, setSubImageUrls] = useState([]);
  const [deletedImageIds, setDeletedImageIds] = useState([]); // 수정 시 삭제된 이미지 ID 추적

  const quillRef = useRef(null);



  // 대표 이미지 핸들러
  const handleMainImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setValue("mainImage", e.target.files, { shouldValidate: true });
      setMainImageUrl(URL.createObjectURL(file));
    } else {
      setValue("mainImage", null, { shouldValidate: true });
      setMainImageUrl(null);
    }
  };

  // 서브 이미지 핸들러
  const handleSubImagesChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    const newImageObjects = files.map(file => ({
      url: URL.createObjectURL(file),
      file: file,
      isNew: true
    }));

    // 기존 이미지와 새 이미지 합치기
    const updatedUrls = [...subImageUrls, ...newImageObjects];
    setSubImageUrls(updatedUrls);

    // react-hook-form에 File 객체들만 업데이트
    const newFiles = updatedUrls.filter(img => img.isNew).map(img => img.file);
    setValue("subImages", newFiles, { shouldValidate: true });
  };

  // 서브 이미지 삭제 핸들러
  const handleRemoveSubImage = (indexToRemove) => {
    const imageToRemove = subImageUrls[indexToRemove];

    // 수정 모드에서 기존 이미지를 삭제하는 경우, ID를 추적
    if (isEditMode && !imageToRemove.isNew) {
      setDeletedImageIds(prev => [...prev, imageToRemove.imageId]);
    }

    const updatedUrls = subImageUrls.filter((_, index) => index !== indexToRemove);
    setSubImageUrls(updatedUrls);
    const newFiles = updatedUrls.filter(img => img.isNew).map(img => img.file);
    setValue("subImages", newFiles, { shouldValidate: true });
  };

  //이미지 서버 업로드 (백엔드 data-image-id 방식으로 변경)
  const uploadFile = useCallback(async (file) => {
    try {
      console.log("uploadFile 호출:", file.name, file.type, file.size); // 디버깅용

      // 이미지를 Data URL로 변환 (에디터 표시용)
      const reader = new FileReader();
      const dataUrl = await new Promise((resolve, reject) => {
        reader.onload = (e) => resolve(e.target.result);
        reader.onerror = reject;
        reader.readAsDataURL(file);
      });

      console.log("Data URL 생성 완료"); // 디버깅용

      // 고유 ID 생성
      const imageId = `temp-${imageIdCounter.current++}`;
      console.log("Image ID 생성:", imageId); // 디버깅용

      // 이미지 정보 저장 (원본 파일 보관)
      setDescriptionImages(prev => {
        const updated = [...prev, { id: imageId, file, dataUrl }];
        console.log("descriptionImages 업데이트:", updated.length, "개"); // 디버깅용
        return updated;
      });

      return { imageId, dataUrl };
    } catch (err) {
      console.error("이미지 업로드 실패", err);
      alert("이미지 업로드 실패");
      throw err;
    }
  }, []);

  // 에디터에 이미지 삽입 (data-image-id 추가)
  const insertImage = useCallback((imageId, dataUrl) => {
    const editor = quillRef.current?.getEditor();
    if (!editor) return;
    const range = editor.getSelection(true);

    console.log("이미지 삽입:", imageId); // 디버깅용

    // 이미지를 삽입 (Data URL)
    editor.insertEmbed(range.index, "image", dataUrl);

    // 커서를 이미지 다음으로 이동
    editor.setSelection(range.index + 1);

    // 방금 삽입한 이미지에 data-image-id 속성 추가
    setTimeout(() => {
      const editorElement = editor.root;
      const images = editorElement.querySelectorAll('img');

      // 가장 최근에 추가된 이미지 찾기 (data-image-id가 없는 것)
      for (let i = images.length - 1; i >= 0; i--) {
        const img = images[i];
        if (!img.getAttribute('data-image-id') && img.src.startsWith('data:')) {
          img.setAttribute('data-image-id', imageId);
          console.log("data-image-id 설정 완료:", imageId);
          break;
        }
      }
    }, 100);
  }, []);

  // 드래그/붙여넣기 이미지 업로드 
  useEffect(() => {
    const editor = quillRef.current?.getEditor();
    if (!editor) return;
    const el = editor.root;

    const handleDrop = async (e) => {
      e.preventDefault();
      const file = e.dataTransfer?.files?.[0];
      if (file && file.type.startsWith("image/")) {
        const { imageId, dataUrl } = await uploadFile(file);
        insertImage(imageId, dataUrl);
      }
    };

    const handlePaste = async (e) => {
      const item = [...e.clipboardData.items].find((i) =>
        i.type.startsWith("image/")
      );
      if (item) {
        e.preventDefault();
        const file = item.getAsFile();
        const { imageId, dataUrl } = await uploadFile(file);
        insertImage(imageId, dataUrl);
      }
    };

    el.addEventListener("drop", handleDrop);
    el.addEventListener("paste", handlePaste);
    return () => {
      el.removeEventListener("drop", handleDrop);
      el.removeEventListener("paste", handlePaste);
    };
  }, [uploadFile, insertImage]);

  // 이미지 드래그/드롭 (위치 변경) + 리사이즈 (Shift + 드래그)
  useEffect(() => {
    const editor = quillRef.current?.getEditor();
    if (!editor) return;
    console.log("DND: useEffect init");

    const el = editor.root;
    let selectedImg = null;
    let isDragging = false;
    let isResizing = false;
    let startX, startWidth;

    const handleMouseDown = (e) => {
      const img = e.target.closest("img");
      if (!img) return;

      if (e.shiftKey) {
        console.log("DND: Start resizing", img);
        isResizing = true;
        selectedImg = img;
        startX = e.clientX;
        startWidth = img.offsetWidth;
        img.style.outline = "2px solid #2193b0";
        e.preventDefault();
      } else {
        console.log("DND: Start dragging", img);
        isDragging = true;
        selectedImg = img;
        e.preventDefault();
      }
    };

    const handleMouseMove = (e) => {
      if (isResizing && selectedImg) {
        const delta = e.clientX - startX;
        selectedImg.style.width = `${Math.max(50, startWidth + delta)}px`;
        e.preventDefault();
      } else if (isDragging && selectedImg) {
        e.preventDefault();
      }
    };

    const handleMouseUp = (e) => {
      if (isResizing && selectedImg) {
        console.log("DND: End resizing");
        selectedImg.style.outline = "none";
        isResizing = false;
        selectedImg = null;
      } else if (isDragging && selectedImg) {
        console.log("DND: End dragging / MouseUp event");
        isDragging = false;

        const editor = quillRef.current.getEditor();
        const blot = Quill.find(selectedImg, true);
        if (!blot) {
            console.error("DND: Could not find blot for selected image.", selectedImg);
            selectedImg = null;
            return;
        }
        const originalIndex = editor.getIndex(blot);
        console.log(`DND: Original index: ${originalIndex}`);

        // Find drop position by iterating through lines
        let targetIndex = editor.getLength(); // Default to the very end
        const lines = editor.getLines();
        for (const line of lines) {
            const bounds = line.domNode.getBoundingClientRect();
            if (e.clientY < bounds.top + bounds.height / 2) {
                targetIndex = editor.getIndex(line);
                break;
            }
        }
        
        console.log(`DND: Target index: ${targetIndex}`);

        const imageSrc = blot.domNode.src;
        const imageId = blot.domNode.getAttribute('data-image-id');
        console.log(`DND: Image ID: ${imageId}, Src: ${imageSrc.substring(0, 50)}...`);

        // Only move if the position is different
        if (targetIndex !== originalIndex && targetIndex !== originalIndex + 1) {
            console.log("DND: Moving image...");
            const newIndex = originalIndex < targetIndex ? targetIndex - 1 : targetIndex;
            editor.deleteText(originalIndex, 1, 'user');
            console.log(`DND: Deleting from ${originalIndex}, Inserting at ${newIndex}`);
            editor.insertEmbed(newIndex, 'image', imageSrc, 'user');
            
            setTimeout(() => {
                const [newBlot] = editor.getLeaf(newIndex);
                if (newBlot && newBlot.statics.blotName === 'image') {
                    newBlot.domNode.setAttribute('data-image-id', imageId);
                    console.log(`DND: Re-applied data-image-id '${imageId}' to new image blot.`);
                } else {
                    console.error(`DND: Failed to re-apply data-image-id. Blot at new index ${newIndex} is not the expected image.`, newBlot);
                }
            }, 100);
        } else {
            console.log("DND: No move needed, target is same as original or adjacent.");
        }
        selectedImg = null;
      }
    };

    // Use document for mousemove and mouseup to handle cases where the mouse leaves the editor
    el.addEventListener("mousedown", handleMouseDown);
    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);

    return () => {
      console.log("DND: useEffect cleanup");
      el.removeEventListener("mousedown", handleMouseDown);
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };
  }, []);

  const filteredSubCategories = useMemo(
    () => subCategories.filter((sub) => sub.parentId === Number(parentCategoryId)),
    [parentCategoryId, subCategories]
  );

  // 툴바 모듈 
  const modules = useMemo(
    () => ({
      toolbar: {
        container: [
          [{ header: [1, 2, false] }],
          ["bold", "italic", "underline", "strike"],
          [{ list: "ordered" }, { list: "bullet" }],
          [{ align: [] }, { color: [] }],
          ["link", "image"],
          ["clean"],
        ],
        handlers: {
          image: function () {
            const input = document.createElement("input");
            input.type = "file";
            input.accept = "image/*";
            input.click();

            input.onchange = async () => {
              const file = input.files?.[0];
              if (!file) return;

              console.log("파일 선택됨:", file.name, file.type); // 디버깅용

              const { imageId, dataUrl } = await uploadFile(file);
              console.log("업로드 완료:", imageId); // 디버깅용

              const quill = this.quill;
              const range = quill.getSelection(true);

              // 이미지 삽입 (Data URL)
              quill.insertEmbed(range.index, "image", dataUrl);
              quill.setSelection(range.index + 1);

              // data-image-id 속성 추가
              setTimeout(() => {
                const editorElement = quill.root;
                const images = editorElement.querySelectorAll('img');

                // 가장 최근에 추가된 이미지 찾기
                for (let i = images.length - 1; i >= 0; i--) {
                  const img = images[i];
                  if (!img.getAttribute('data-image-id') && img.src.startsWith('data:')) {
                    img.setAttribute('data-image-id', imageId);
                    console.log("툴바: data-image-id 설정 완료:", imageId);
                    break;
                  }
                }
              }, 100);
            };
          },
        },
      },
    }),
    [uploadFile]
  );

  // 상품 등록 
  const onSubmit = (data) => {
    const { mainImage, subImages, description, ...productDataFields } = data;

    if (!isEditMode && !mainImage) {
      alert("대표 이미지를 등록해주세요.");
      return;
    }

    // description HTML에서 data-image-id 추출하여 imageMapping 생성
    const parser = new DOMParser();
    const doc = parser.parseFromString(description, 'text/html');
    const images = doc.querySelectorAll('img[data-image-id]');
    const imageMapping = [];
    const descriptionFiles = [];

    images.forEach(img => {
      const imageId = img.getAttribute('data-image-id');
      imageMapping.push(imageId);

      // Data URL을 빈 문자열로 교체 (백엔드가 실제 URL로 채움)
      img.setAttribute('src', '');

      // descriptionImages에서 해당 파일 찾기
      const imageData = descriptionImages.find(item => item.id === imageId);
      if (imageData) {
        descriptionFiles.push(imageData.file);
      }
    });

    // 수정된 HTML 가져오기 (Data URL 제거됨)
    const cleanedDescription = doc.body.innerHTML;

    const productData = {
      ...productDataFields,
      categoryId: Number(productDataFields.categoryId),
      description: cleanedDescription, // Data URL 제거된 HTML 전송
      imageMapping: imageMapping, // 이미지 순서 배열
    };

    if (isEditMode) {
      productData.deletedImageIds = deletedImageIds;
    }

    console.log("전송할 productData:", productData); // 디버깅용
    console.log("전송할 이미지 파일:", descriptionFiles.length, "개"); // 디버깅용

    const formData = new FormData();
    formData.append(
      "productData",
      new Blob([JSON.stringify(productData)], { type: "application/json" })
    );

    if (mainImage && mainImage.length > 0) {
      formData.append("mainImage", mainImage[0]);
    }

    if (subImages && subImages.length > 0) {
      Array.from(subImages).forEach((img) => formData.append("subImages", img));
    }

    // description 이미지 파일들 추가
    descriptionFiles.forEach(file => {
      formData.append("description", file);
    });

    createMutate(formData, {
      onSuccess: () => {
        alert("상품이 성공적으로 등록되었습니다.");
        navigate("/");
      },
    });
  };

  return (
    <div className="product-upload-container">
      <h2>{isEditMode ? '상품 수정' : '상품 등록'}</h2>
      <form className="product-upload-form" onSubmit={handleSubmit(onSubmit)}>
        {/* 상품 기본 정보 */}
        <div className="form-group">
          <label>상품명</label>
          <input {...register("productName")} />
          {errors.productName && <p className="error-message">{errors.productName.message}</p>}
        </div>

        <div className="form-group">
          <label>카테고리</label>
          <div className="category-select-group">
            <select
              {...register("parentCategoryId", {
                onChange: () => setValue("categoryId", ""),
              })}
            >
              <option value="">대분류 선택</option>
              {parentCategories.map((cat) => (
                <option key={cat.categoryId} value={cat.categoryId}>
                  {cat.categoryName}
                </option>
              ))}
            </select>
            <select {...register("categoryId")} disabled={!parentCategoryId}>
              <option value="">소분류 선택</option>
              {filteredSubCategories.map((cat) => (
                <option key={cat.categoryId} value={cat.categoryId}>
                  {cat.categoryName}
                </option>
              ))}
            </select>
          </div>
          {errors.parentCategoryId && <p className="error-message">{errors.parentCategoryId.message}</p>}
          {errors.categoryId && !errors.parentCategoryId && <p className="error-message">{errors.categoryId.message}</p>}
        </div>

        <div className="form-group">
          <label>가격</label>
          <input type="number" {...register("price")} />
          {errors.price && <p className="error-message">{errors.price.message}</p>}
        </div>

        <div className="form-group">
          <label>재고</label>
          <input type="number" {...register("stock")} />
          {errors.stock && <p className="error-message">{errors.stock.message}</p>}
        </div>

        <div className="form-group">
          <label>배송 정보</label>
          <input {...register("shippingInfo")} />
        </div>

        {/* 이미지 업로드 */}
        <div className="form-group">
          <label>대표 이미지</label>
          {mainImageUrl ? (
            <div className="main-image-preview-container">
              <div className="main-image-preview-item">
                <img src={mainImageUrl} alt="대표 이미지 미리보기" />
                <button type="button" className="remove-image-btn" onClick={() => handleMainImageChange({ target: { files: [] } })}>
                  ×
                </button>
              </div>
            </div>
          ) : (
            <label htmlFor="main-image-input" className="file-input-group">
              <p>클릭하여 파일을 선택하세요</p>
              <input
                id="main-image-input"
                type="file"
                accept="image/*"
                onChange={handleMainImageChange}
              />
            </label>
          )}
          {errors.mainImage && <p className="error-message">{errors.mainImage.message}</p>}
        </div>

        {/* 서브 이미지 */}
        <div className="form-group">
          <label>미리보기 이미지 (여러 개 선택 가능)</label>
          <label htmlFor="sub-images-input" className="file-input-group">
            <p>클릭하여 파일을 추가하세요</p>
            <input
              id="sub-images-input"
              type="file"
              accept="image/*"
              multiple
              onChange={handleSubImagesChange}
            />
          </label>
          {subImageUrls?.length > 0 && (
            <div className="sub-image-preview-container">
              {subImageUrls.map((url, i) => (
                <div key={i} className="sub-image-preview-item">
                  <img src={url.url} alt={`미리보기 ${i + 1}`} />
                  <button type="button" className="remove-image-btn" onClick={() => handleRemoveSubImage(i)}>
                    ×
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* ReactQuill 에디터 */}
        <div className="form-group">
          <label>상품 상세 설명</label>
          <div className="quill-editor-container">
            <Controller
              name="description"
              control={control}
              render={({ field }) => (
                <ReactQuill
                  ref={quillRef}
                  value={field.value}
                  onChange={field.onChange}
                  modules={modules}
                  theme="snow"
                  style={{ height: "100%" }}
                />
              )}
            />
          </div>
          {errors.description && <p className="error-message">{errors.description.message}</p>}
        </div>

        <button type="submit" className="upload-btn">{isEditMode ? '수정하기' : '등록하기'}</button>
      </form>
    </div>
  );
}

export default ProductUpload;
