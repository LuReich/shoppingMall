import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { adminAPI } from "../api/adminAPI"
import { useNavigate } from "react-router";

export const useAdmin = () => {

    const qc = useQueryClient();

    //회원 리스트 조회
    const getUserList = (mode, params = {},  enabled = true) => {
        return useQuery({
            queryKey: ["userList", mode, params],
            queryFn: () => adminAPI.get(mode, params),
            enabled, //검색 전은 false 로 나중에 fetch()로 호출

        });
    };
    
    //회원 상세 정보 조회
    const getUserDetail = (mode, uid) => {
        return useQuery({
            queryKey: ["userDetail", mode, uid],
            queryFn: () => adminAPI.getDetail(mode, uid),
        });
    };

    //전체 상품 리스트 조회
    const getProductList = (params = {}) => {
        return useQuery({
            queryKey: ["adminProducts", params],
            queryFn: () => adminAPI.getProduct(params),
        });
    };

      //문의 리스트 보기
    const getQnAList = (mode, params ={}) => {
        return useQuery({
            queryKey: ["qnaList", mode, params],
            queryFn: () => adminAPI.getQnaList(mode, params),
        });
    };

    //문의 상세 보기
    const getAdminQnADetail = (mode, inquiryId) => {
        return useQuery({
            queryKey: ["qnaDetail", mode, inquiryId],
            queryFn: () => adminAPI.getQnaDetail(mode, inquiryId),
        });
    };
    

    //회원 정보 수정
    const updateUser = () =>{
        return useMutation({
            mutationFn: ({mode, uid, data}) => adminAPI.update(mode, uid, data),
            onSuccess: (res, variables) => {
                const { mode, uid } = variables;
                qc.invalidateQueries({ queryKey: ["userDetail", mode, uid] }); 
                qc.invalidateQueries({ queryKey: ["userList", mode] });
                console.log("회원정보 수정 성공:", res);
                alert("회원정보가 수정되었습니다");
            },
            onError: (err) => {
                const msg = err.response?.data?.content || "회원정보 수정에 실패했습니다.";
                console.error("회원정보 수정 실패:", err);
                alert(msg);
            }
        });
    };

    //상품 소프트 삭제 및 사유 등록
    const deleteProduct = () => {
        return useMutation({
            mutationFn: ({productId, data}) => adminAPI.deleteProduct(productId, data),
            onSuccess: (res, variables) => {
                const {productId} = variables;
                qc.invalidateQueries({ queryKey: ["adminProducts"] });
                qc.invalidateQueries({ queryKey: ["product", productId] });
                console.log("상품 삭제 성공:", res);
                alert("상품이 상태가 성공적으로 변경되었습니다.");
            },
            onError: (err) => {
                const msg = err.response?.data?.content || "상품 삭제에 실패했습니다.";
                console.error("상품 삭제 실패:", err);
                alert(msg);
            }
        });
    };

    //문의 답변하기
    const answerQnA = () => {
        return useMutation({
            mutationFn: ({mode, inquiryId, data}) => adminAPI.answerQna(mode, inquiryId, data),
            onSuccess: (res, variables) => {
                const { inquiryId } = variables;
                qc.invalidateQueries({ queryKey: ["qnaDetail", inquiryId] });
                qc.invalidateQueries({ queryKey: ["qnaList"] });
                console.log("문의 답변 성공:", res);
                alert("문의 답변이 성공적으로 등록되었습니다.");
            },
            onError: (err) => {
                const msg = err.response?.data?.content || "문의 답변 등록에 실패했습니다.";
                console.error("문의 답변 등록 실패:", err);
                alert(msg);
            }
        });
    };

  

    return { 
        getUserList,
        getUserDetail,
        getProductList,
        getQnAList,
        getAdminQnADetail,
        updateUser,  
        deleteProduct,
        answerQnA
     };
}