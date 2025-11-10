import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { qnaAPI } from "../api/qnaAPI";


export const useQnA = () =>{
    const qc = useQueryClient();


    const getQnAList = (params = {}) => {
        return useQuery({
            queryKey: ["qnaList", params],
            queryFn: () => qnaAPI.getList()
        });
    };

    const getQnADetail = (buyerInquiryId) => {
        return useQuery({
            queryKey: ["qnaDetail", buyerInquiryId],
            queryFn: () => qnaAPI.getDetail(buyerInquiryId)
        });
    };

    const createQnA = () => {
        return useMutation({
            mutationFn: (formData) => qnaAPI.create(formData),
            onSuccess: (res) => {
                qc.invalidateQueries(["qnaList"]);
                console.log("QnA 등록 성공", res);
                alert(res?.content?.content || "QnA 등록 성공");
            },
            onError: (err) => {
                console.error(err);
                alert(err.response?.data?.content || "QnA 등록 실패.");
            },

        });
    };

    const updateQnA = () => {
        return useMutation({
            mutationFn: ({buyerInquiryId, formData}) => qnaAPI.update({buyerInquiryId, formData}),
            onSuccess: (res) => {
                qc.invalidateQueries(["qnaDetail"], buyerInquiryId);
                console.log("QnA 수정 성공", res);
                alert(res?.content?.content || "QnA 수정 성공");
            },
            onError: (err) => {
                console.error(err);
                alert(err.response?.data?.content || "QnA 수정 실패.");

            }
        });
    };

    const deleteQnA = () => {
        return useMutation({
            mutationFn: (buyerInquiryId) => qnaAPI.delete(buyerInquiryId),
            onSuccess: (res) => {
                qc.invalidateQueries(["qnaList"]);
                console.log("QnA 삭제 성공", res);
                alert(res?.content || "QnA 삭제 성공");
            },
            onError: (err) => {
                console.log("QnA 삭제 실패", err);
                alert("QnA 삭제 실패" || err.response?.data?.content)
            }
        });
    };

    return{
        getQnAList,
        getQnADetail,
        createQnA,
        updateQnA,
        deleteQnA,
    }
}