//zustand(immer + persist)로 상태 관리

import { create } from "zustand";
import { persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

export const authStore = create(

    persist(
        immer((set) => ({
            user: null,
            token: null,
            isLogin: false,

            //로그인
            setLogin: (user, token) =>{
                set((state)=>{
                    state.user = user;
                    state.token = token;
                    state.isLogin = true;
                });
            },

            //로그아웃
            setLogout: () =>{
                set((state)=> {
                    state.user = null;
                    state.token = null;
                    state.isLogin = false;
                });
            },

            //user 정보 업데이트
            updateUser: (updatedData) => {
                set((state) => {
                    if (!state.user) return;
                    state.user = { ...state.user, ...updatedData };
                });
            },  
        })),
        {
            name: "auth-storage", //로컬스토리지 키 이름
            partialize: (state) => ({
                token: state.token,
                user: state.user,
                isLogin: state.isLogin,
            })
        }
    )





)