import { useQuery } from "@tanstack/react-query"
import { adminAPI } from "../api/adminAPI"

export const useAdmin = () => {
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
            enabled: !!uid && uid > 0, // uid가 유효한 값일 때만 쿼리를 실행합니다.
        })
    }

    return { 
        getUserList,
        getUserDetail,
     };
}