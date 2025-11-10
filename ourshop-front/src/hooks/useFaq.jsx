import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { faqAPI } from "../api/faqAPI";

export const useFaq = () => {

    const qc = useQueryClient(); 

    //FAQ 리스트 조회
    const getFaqList = (params = {}) => {
        return useQuery({
            queryKey: ["faqList",params],
            queryFn: () => faqAPI.getList(params),
        });
    };

    //FAQ 상세 조회
    const getFaqDetail = (faqId) => {
        return useQuery({
            queryKey: ["faqDetail", faqId],
            queryFn: () => faqAPI.getDetail(faqId),
        });
    };

    //FAQ 등록
    const createFaq = () => {
        return useMutation({
            mutationFn: (data) => faqAPI.create(data),
            onSuccess: (res) => {
                qc.invalidateQueries(["faqList"]);
                qc.invalidateQueries(["faqDetail"]);
                console.log("FAQ 등록 성공", res);
                alert(res?.content?.content || "FAQ 등록 성공");
            },
            onError: (err) => {
                console.error(err);
                alert(err.response?.data?.content || "FAQ 등록 실패.");
            },
           
        });
    };

    //FAQ 수정
    const updateFaq = () => {
        return useMutation({
            mutationFn: ({faqId, data}) => faqAPI.update(faqId, data),
            onSuccess: (res) => {
                qc.invalidateQueries(["faqList"]);
                qc.invalidateQueries(["faqDetail"]);
                console.log("FAQ 수정 성공", res);
                alert(res?.content?.content || "FAQ 수정 성공");
            },
            onError: (err) => {
                console.error(err);
                alert(err.response?.data?.content || "FAQ 수정 실패.");
            }
        });
    };

    //FAQ 삭제
    const deleteFaq = () => {
        return useMutation({
            mutationFn: (faqId) => faqAPI.delete(faqId),
            onSuccess: (res) => {
                qc.invalidateQueries(["faqList"]);
                console.log("FAQ 삭제 성공", res);
                alert(res?.content || "FAQ 삭제 성공");
            },
            onError: (err) => {
                console.error(err);
                alert(err.response?.data?.content || "FAQ 삭제 실패.");
            }
        });
    };


    return {
        getFaqList,
        getFaqDetail,
        createFaq,
        updateFaq,
        deleteFaq

    };

}