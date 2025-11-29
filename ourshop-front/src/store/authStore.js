import { create } from "zustand";
import { persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

//zustand(immer + persist)로 상태 관리

export const authStore = create(

    persist(
        immer((set) => ({
            user: null,
            token: null,
            isLogin: false,
            role: null,

            //로그인
            setLogin: (user, token) =>{
                set((state)=>{
                    state.user = user;
                    state.role = user?.content.role
                    state.token = token;
                    state.isLogin = true;
                });
            },

            //로그아웃
            setLogout: () =>{
                set((state)=> {
                    state.user = null;
                    state.role = null;
                    state.token = null;
                    state.isLogin = false;
                });
            },

            //user 정보 업데이트
            updateUser: (updatedData) => {
                set((state) => {
                    if (!state.user?.content) return;
                    state.user.content = { ...state.user.content, ...updatedData };
                });
            },
        })),
        {
            name: "auth-storage", //로컬스토리지 키 이름
            partialize: (state) => ({
                token: state.token,
                user: state.user,
                role: state.role,
                isLogin: state.isLogin,
            })
        }
    )





)