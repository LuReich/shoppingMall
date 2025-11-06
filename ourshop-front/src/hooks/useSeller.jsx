import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { sellerAPI } from "../api/sellerAPI"

export const useSeller = () => {

    const qc = useQueryClient();

    //판매자 업체 정보 조회
    const getShopInfo = (sellerUid) => {
        return useQuery({
            queryKey: ['companyInfo'],
            queryFn: async () => sellerAPI.get(sellerUid)
        });
    };

    //판매자 상품 조회 
    const getSellerProductList = (params ={}) => {
        return useQuery({
            queryKey: ["sellerProducts", params],
            queryFn: () => sellerAPI.getProduct(params),
        });
    };

    //판매자 공개 상품 조회 (누구나 조회 가능)
    const getPublicSellerProductList = (sellerUid, params={}) => {
        return useQuery({
            queryKey: ["publicSellerProducts", sellerUid, params],
            queryFn: () => sellerAPI.getPublicProducts(sellerUid, params),
        });
    };

    //판매자 배송 상품 조회
    const getDeliverySellerProductList = (params={}) => {
        return useQuery({
            queryKey: ["deliverySellerProducts", params],
            queryFn: () => sellerAPI.getDeliveryProducts(params),
        });
    };


    //배송상태 수정
    const updateDeliveryStatus = () =>{
        return useMutation({
            mutationFn: ({orderDetailId, data}) => sellerAPI.update(orderDetailId, data),
            onSuccess: (res, variables) => {
                qc.invalidateQueries({ queryKey: ["deliverySellerProducts"] }); 
                console.log("배송상태 수정 성공:", res);
                alert("배송상태 수정되었습니다");
            },
            onError: (err) => {
                const msg = err.response?.data?.content || "배송상태 수정에 실패했습니다.";
                console.error("배송상태 수정 실패:", err);
                alert(msg);
            }
        });
    };
    
        

    return {
        getShopInfo,
        getSellerProductList,
        getPublicSellerProductList,
        getDeliverySellerProductList,
        updateDeliveryStatus
    }
}