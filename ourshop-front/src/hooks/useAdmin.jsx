import { useQuery } from "@tanstack/react-query"
import { adminAPI } from "../api/adminAPI"

export const useAdmin = () => {
    //회원 리스트 조회
    const getUserList = (mode, params = {},  enabled = true) => 
        useQuery({
            queryKey: ["userList", mode, params],
            queryFn: () => adminAPI.get(mode, params),
            enabled, //검색 전은 false 로 나중에 fetch()로 호출

        })

    return { getUserList };
}