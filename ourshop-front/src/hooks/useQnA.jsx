import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { qnaAPI } from "../api/qnaAPI";


export const useQnA = () =>{
    const qc = useQueryClient();


    const getQnAList = (mode, params = {}, uid) => {
        return useQuery({
            queryKey: ["qnaList", mode, params, uid],
            queryFn: () => qnaAPI.getList(mode, params)
        });
    };

    const getQnADetail = (mode, inquiryId) => {
        return useQuery({
            queryKey: ["qnaDetail", mode, inquiryId],
            queryFn: () => qnaAPI.getDetail(mode, inquiryId)
        });
    };

    const createQnA = () => {
        return useMutation({
            mutationFn: ({mode, formData}) => qnaAPI.create(mode, formData),
            onSuccess: (res, {mode}) => {
                qc.invalidateQueries(["qnaList", mode]);
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
            mutationFn: ({mode, inquiryId, formData}) => qnaAPI.update({mode, inquiryId, formData}),
            onSuccess: (res, {mode, inquiryId}) => {
                qc.invalidateQueries(["qnaDetail", mode, inquiryId]);
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
            mutationFn: ({mode, inquiryId}) => qnaAPI.delete(mode, inquiryId),
            onSuccess: (res, {mode}) => {
                qc.invalidateQueries(["qnaList", mode]);
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