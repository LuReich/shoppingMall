import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { reviewAPI } from "../api/reviewAPI"
import { useNavigate } from "react-router";

export const useReviews = () =>{

    const qc = useQueryClient();
    const navigate = useNavigate();
    
    // 리뷰 목록 조회
    const getReviews = (params ={}) => {
        return useQuery({
            queryKey: ["reviews", params],
            queryFn: () => reviewAPI.getMyReviews(params),
        });
    };


    //리뷰 등록
    const createReview = () => {
        return useMutation({
            mutationFn:(reviewData) => reviewAPI.create(reviewData),
            onSuccess: (res) => {
                console.log("리뷰 등록 성공 res", res);
                const msg = res?.content?.content || "리뷰가 성공적으로 등록되었습니다.";
                qc.invalidateQueries(["reviews"]);
                qc.invalidateQueries(["productList"]);
                qc.invalidateQueries(["product"]);
       
                alert(msg);
                navigate('/buyer/mypage/review');
            },
            onError: (err) => {
                console.error(err);
                const msg = err.response?.data?.content || "리뷰 등록에 실패했습니다.";
                
                //리뷰가 이미 등록되어 있으면 
                if(err.response?.status === 400){
                    alert(msg);
                    navigate('/buyer/mypage/review');
                    return;
                }
                
                alert(msg);
            },
        });
    };

    //리뷰 수정
    const updateReview = () => {
        return useMutation({
            mutationFn:({reviewId, reviewData}) => reviewAPI.update({reviewId, reviewData}),
            onSuccess: (res) => {
                const msg = res?.content || "리뷰가 성공적으로 수정되었습니다.";
                qc.invalidateQueries(["reviews"]);
                qc.invalidateQueries(['review', res.content.reviewId]);
                alert(msg);
                navigate('/buyer/mypage/review')
            },
            onError: (err) => {
                const msg = err.response?.data?.content || "리뷰 수정에 실패했습니다.";
                console.error(err);
                alert(msg);
            }
        });
    };

    //리뷰 삭제
    const deleteReview = () =>{
        return useMutation({
            mutationFn: (reviewId) => reviewAPI.delete(reviewId),
            onSuccess: (res) => {
                const msg = res?.content || "리뷰가 성공적으로 삭제되었습니다.";
                qc.invalidateQueries(["reviews"]);
                alert(msg);
                navigate('/buyer/mypage/review')
            },
            onError: (err) => {
                const msg = err.response?.data?.content || "리뷰 삭제에 실패했습니다.";
                console.error(err);
                alert(msg);
            }   
        });
    };
    
    return {
        getReviews,
        createReview,
        updateReview,
        deleteReview,
    }
}