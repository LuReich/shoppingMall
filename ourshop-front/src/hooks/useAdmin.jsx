import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { adminAPI } from "../api/adminAPI"
import { useNavigate } from "react-router";

export const useAdmin = () => {

    const qc = useQueryClient();
    const navigate = useNavigate();

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
        })
    }

    return { 
        getUserList,
        getUserDetail,
        updateUser,  
     };
}