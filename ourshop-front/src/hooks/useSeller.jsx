import { useQuery } from "@tanstack/react-query"
import { sellerAPI } from "../api/sellerAPI"

export const useSeller = () => {

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
    }
    
        

    return {
        getShopInfo,
        getSellerProductList,
        getPublicSellerProductList
    }
}