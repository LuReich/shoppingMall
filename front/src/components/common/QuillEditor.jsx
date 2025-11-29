import React, { useMemo, useState, useRef, useEffect, useCallback, Suspense } from "react";
const ReactQuill = React.lazy(() => import('react-quill-new'));
import Quill from "quill";
import "quill/dist/quill.snow.css";

// Note: quill-image-resize-module-react is currently disabled based on previous debugging steps.
// If re-enabled, its import and registration logic would go here.

const QuillEditor = ({ value, onChange, quillRef, uploadFile, insertImage, setIsVideoModalOpen }) => {
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    // This ensures the component only renders on the client side.
    // If ImageResize is re-enabled, its dynamic import and registration would go here.
    setIsClient(true);
  }, []);

  const getSafeEditor = useCallback(() => {
    if (!quillRef.current) return null;
    try {
      // More robust check: ensure the editor instance is created and has methods.
      const editor = quillRef.current.getEditor();
      if (editor && typeof editor.getSelection === 'function') {
        return editor;
      }
      return null;
    } catch (error) {
      // This catch is important if getEditor() itself throws.
      return null;
    }
  }, [quillRef]);

  // ==================================================================
  // [수정] 에디터 핸들러 설정: 콜백 ref 기반으로 변경
  // ==================================================================
  useEffect(() => {
    if (!isEditorMounted) return; // 에디터가 마운트될 때까지 실행하지 않음

    const editor = getSafeEditor();
    if (!editor) return;

    const el = editor.root;

    // 1. 드롭/붙여넣기 핸들러
    const handleDrop = async (e) => {
      e.preventDefault();
      const files = e.dataTransfer?.files;
      if (files && files.length > 0) {
        for (const file of files) {
          if (file.type.startsWith("image/")) {
            try {
              const { imageId, dataUrl } = await uploadFile(file);
              insertImage(imageId, dataUrl);
            } catch (error) {
              console.error("Drop image upload failed", error);
              break;
            }
          }
        }
      }
    };

    const handlePaste = async (e) => {
      const item = [...e.clipboardData.items].find((i) => i.type.startsWith("image/"));
      if (item) {
        e.preventDefault();
        const file = item.getAsFile();
        const { imageId, dataUrl } = await uploadFile(file);
        insertImage(imageId, dataUrl);
      }
    };

    el.addEventListener("drop", handleDrop);
    el.addEventListener("paste", handlePaste);

    // 2. 이미지 드래그 위치 이동 핸들러
    let selectedImg = null;
    let isDragging = false;
    let dragGhost = null;

    const handleMouseDown = (e) => {
      if (e.target.classList.contains('ql-resize-handle')) return;
      const img = e.target.closest("img");
      if (img) {
        isDragging = true;
        selectedImg = img;
        dragGhost = img.cloneNode();
        Object.assign(dragGhost.style, {
           position: 'absolute', opacity: '0.5', pointerEvents: 'none', zIndex: '9999'
        });
        document.body.appendChild(dragGhost);
        dragGhost.style.left = `${e.clientX - dragGhost.width / 2}px`;
        dragGhost.style.top = `${e.clientY - dragGhost.height / 2}px`;
        e.preventDefault();
      }
    };

    const handleMouseMove = (e) => {
      if (isDragging && selectedImg && dragGhost) {
        dragGhost.style.left = `${e.clientX - dragGhost.width / 2}px`;
        dragGhost.style.top = `${e.clientY - dragGhost.height / 2}px`;
        e.preventDefault();
      }
    };

    const handleMouseUp = (e) => {
      if (isDragging && selectedImg) {
        isDragging = false;
        if (dragGhost && dragGhost.parentNode) {
          dragGhost.parentNode.removeChild(dragGhost);
          dragGhost = null;
        }

        const currentEditor = getSafeEditor();
        if(!currentEditor) return;

        const blot = Quill.find(selectedImg, true);
        if (!blot) {
          selectedImg = null;
          return;
        }

        const originalIndex = currentEditor.getIndex(blot);
        const imageSrc = blot.domNode.src;
        const imageId = blot.domNode.getAttribute('data-image-id');

        let targetIndex = 0;
        let range = null;
        if (document.caretRangeFromPoint) {
          range = document.caretRangeFromPoint(e.clientX, e.clientY);
        } else if (document.caretPositionFromPoint) {
          const pos = document.caretPositionFromPoint(e.clientX, e.clientY);
          if (pos) {
             range = document.createRange();
             range.setStart(pos.offsetNode, pos.offset);
             range.collapse(true);
          }
        }

        if (range && currentEditor.root.contains(range.startContainer)) {
          const targetBlot = Quill.find(range.startContainer, true);
          if (targetBlot) {
            targetIndex = currentEditor.getIndex(targetBlot) + range.startOffset;
          }
        } else {
          selectedImg = null;
          return;
        }

        const newIndex = originalIndex < targetIndex ? targetIndex - 1 : targetIndex;
        if (newIndex !== originalIndex) {
          currentEditor.deleteText(originalIndex, 1, 'user');
          currentEditor.insertEmbed(newIndex, 'image', imageSrc, 'user');
          setTimeout(() => {
            const [newBlot] = currentEditor.getLeaf(newIndex);
            if (newBlot && newBlot.statics.blotName === 'image') {
              newBlot.domNode.setAttribute('data-image-id', imageId);
            }
          }, 100);
        }
        selectedImg = null;
      }
    };

    el.addEventListener("mousedown", handleMouseDown);
    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);

    return () => {
      el.removeEventListener("drop", handleDrop);
      el.removeEventListener("paste", handlePaste);
      el.removeEventListener("mousedown", handleMouseDown);
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isEditorMounted, uploadFile, insertImage, getSafeEditor]);

  const modules = useMemo(
    () => ({
      toolbar: {
        container: [
          [{ header: [1, 2, false] }],
          ["bold", "italic", "underline", "strike"],
          [{ list: "ordered" }, { list: "bullet" }],
          [{ align: [] }, { color: [] }],
          ["link", "image", "video"],
          ["clean"],
        ],
        handlers: {
          image: () => {
            const input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');
            input.setAttribute('multiple', true);
            input.click();
            input.onchange = async () => {
              const files = input.files;
              if (files) {
                for (const file of files) {
                  try {
                    const { imageId, dataUrl } = await uploadFile(file);
                    insertImage(imageId, dataUrl);
                  } catch (error) {
                    console.error("Toolbar image upload failed", error);
                    break;
                  }
                }
              }
            };
          },
          video: function () { setIsVideoModalOpen(true); },
        },
      },
      // imageResize module is disabled for now
    }),
    [uploadFile, insertImage, setIsVideoModalOpen]
  );

  const [isEditorMounted, setIsEditorMounted] = useState(false);

  // ... (other code) ...

  return (
    isClient ? (
      <Suspense fallback={<div>에디터를 불러오는 중입니다...</div>}>
        <ReactQuill
          ref={(el) => {
            quillRef.current = el;
            if (el && !isEditorMounted) {
              setIsEditorMounted(true);
            }
          }}
          value={value}
          onChange={onChange}
          modules={modules}
          theme="snow"
          style={{ height: "100%" }}
        />
      </Suspense>
    ) : (
      <div>에디터를 불러오는 중입니다...</div>
    )
  );
};

export default QuillEditor;