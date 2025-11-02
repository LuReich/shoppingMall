import React, { useMemo, useState, useRef, useEffect, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import ReactQuill from "react-quill-new";
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
        description: pd.description ?.replaceAll('src="/temp/', 'src="http://localhost:9090/temp/')
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

  //이미지 서버 업로드 
  const uploadFile = useCallback(async (file) => {
    try {
      const res = await uploadTempImage(file);
      return `http://localhost:9090${res?.content?.imageUrl}`;
    } catch (err) {
      console.error("이미지 업로드 실패", err);
      alert("이미지 업로드 실패");
      throw err;
    }
  }, [uploadTempImage]);

  // 에디터에 이미지 삽입 
  const insertImage = useCallback((url) => {
    const editor = quillRef.current?.getEditor();
    if (!editor) return;
    const range = editor.getSelection(true);
    editor.insertEmbed(range.index, "image", url);
    editor.setSelection(range.index + 1);
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
        const url = await uploadFile(file);
        insertImage(url);
      }
    };

    const handlePaste = async (e) => {
      const item = [...e.clipboardData.items].find((i) =>
        i.type.startsWith("image/")
      );
      if (item) {
        e.preventDefault();
        const file = item.getAsFile();
        const url = await uploadFile(file);
        insertImage(url);
      }
    };

    el.addEventListener("drop", handleDrop);
    el.addEventListener("paste", handlePaste);
    return () => {
      el.removeEventListener("drop", handleDrop);
      el.removeEventListener("paste", handlePaste);
    };
  }, [uploadFile, insertImage]);

  // 이미지 리사이즈 + 순서 변경 (Shift + 드래그) 
  useEffect(() => {
    const editor = quillRef.current?.getEditor();
    if (!editor) return;
    const el = editor.root;

    let selectedImg = null;
    let startX, startY, startWidth, isResizing = false, isDragging = false;

    const handleMouseDown = (e) => {
      const img = e.target.closest("img");
      if (!img) return;

      if (e.shiftKey) {
        isDragging = true;
        selectedImg = img;
        startY = e.clientY;
        img.style.opacity = "0.5";
      } else {
        isResizing = true;
        selectedImg = img;
        startX = e.clientX;
        startWidth = img.offsetWidth;
        img.style.outline = "2px solid #2193b0";
      }
    };

    const handleMouseMove = (e) => {
      if (isResizing && selectedImg) {
        const delta = e.clientX - startX;
        selectedImg.style.width = `${Math.max(50, startWidth + delta)}px`;
      } else if (isDragging && selectedImg) {
        const deltaY = e.clientY - startY;
        selectedImg.style.transform = `translateY(${deltaY}px)`;
      }
    };

    const handleMouseUp = (e) => {
      if (isResizing && selectedImg) {
        selectedImg.style.outline = "none";
        selectedImg = null;
        isResizing = false;
      } else if (isDragging && selectedImg) {
        const imgs = [...el.querySelectorAll("img")];
        const index = imgs.indexOf(selectedImg);
        const deltaY = e.clientY - startY;
        const next = deltaY > 0 ? imgs[index + 1] : imgs[index - 1];
        if (next) {
          const parent = selectedImg.parentNode;
          parent.insertBefore(selectedImg, deltaY > 0 ? next.nextSibling : next);
        }
        selectedImg.style.opacity = "1";
        selectedImg.style.transform = "none";
        selectedImg = null;
        isDragging = false;
      }
    };

    el.addEventListener("mousedown", handleMouseDown);
    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);

    return () => {
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
              const url = await uploadFile(file);
              const quill = this.quill;
              const range = quill.getSelection(true);
              quill.insertEmbed(range.index, "image", url);
              quill.setSelection(range.index + 1);
            };
          },
        },
      },
    }),
    [uploadFile]
  );

  // 상품 등록 
  const onSubmit = (data) => {
    const { mainImage, subImages, ...productDataFields } = data;

    if (!isEditMode && !mainImage) {
      alert("대표 이미지를 등록해주세요.");
      return;
    }

    const productData = {
      ...productDataFields,
      categoryId: Number(productDataFields.categoryId),
    };
    if (isEditMode) {
      productData.deletedImageIds = deletedImageIds;
    }

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
